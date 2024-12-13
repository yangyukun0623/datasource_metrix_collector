package com.ikun.metrics.transaction.collector;

import com.alibaba.druid.filter.FilterChain;
import com.alibaba.druid.filter.FilterEventAdapter;
import com.alibaba.druid.proxy.jdbc.ConnectionProxy;
import com.alibaba.druid.proxy.jdbc.ResultSetProxy;
import com.alibaba.druid.proxy.jdbc.StatementProxy;
import com.ikun.metrics.transaction.config.StatementMetrics;
import com.ikun.metrics.transaction.holder.TransactionMetricsHolder;

import java.sql.SQLException;
import java.sql.Savepoint;

public class DruidTransactionNoneCommitCollector extends FilterEventAdapter {

    private final TransactionMetricsHolder metricsHolder;

    private final ThreadLocal<StatementMetrics> STATEMENT_INFO_RECORD = new ThreadLocal<>();

    public DruidTransactionNoneCommitCollector(TransactionMetricsHolder metricsHolder) {
        this.metricsHolder = metricsHolder;
    }

    @Override
    public void connection_setAutoCommit(FilterChain chain, ConnectionProxy connection, boolean autoCommit) throws SQLException {
        super.connection_setAutoCommit(chain, connection, autoCommit);
        // 只监控非自动提交的
        // 如果设置了非自动提交，当动手提交后会再次调用改方法改回自动提交
        if (!autoCommit) {
            metricsHolder.transactionReady(false, connection.getTransactionInfo() == null ? null : connection.getTransactionInfo().getId());
        }
    }

    @Override
    public void connection_commit(FilterChain chain, ConnectionProxy connection) throws SQLException {
        try {
            super.connection_commit(chain, connection);
            metricsHolder.transactionFinish();
        } finally {
            metricsHolder.clearTransactionMetrics();
        }
    }

    @Override
    public void connection_rollback(FilterChain chain, ConnectionProxy connection) throws SQLException {
        try {
            super.connection_rollback(chain, connection);
            metricsHolder.transactionFinish();
        } finally {
            metricsHolder.clearTransactionMetrics();
        }
    }

    @Override
    public void connection_rollback(FilterChain chain, ConnectionProxy connection, Savepoint savepoint) throws SQLException {
        try {
            super.connection_rollback(chain, connection, savepoint);
            metricsHolder.transactionFinish();
        } finally {
            metricsHolder.clearTransactionMetrics();
        }
    }

    @Override
    protected void statementExecuteBefore(StatementProxy statement, String sql) {
        StatementMetrics statementMetrics = new StatementMetrics(sql, statement);
        STATEMENT_INFO_RECORD.set(statementMetrics);
        metricsHolder.collectStatement(statementMetrics);
    }

    @Override
    protected void statementExecuteAfter(StatementProxy statement, String sql, boolean result) {
        if (STATEMENT_INFO_RECORD.get() != null) {
            STATEMENT_INFO_RECORD.get().terminal(result ? 0 : -1);
            STATEMENT_INFO_RECORD.remove();
        }
    }

    @Override
    protected void statementExecuteQueryBefore(StatementProxy statement, String sql) {
        if (!metricsHolder.getProperties().isCollectUpdateSql()) {
            StatementMetrics statementMetrics = new StatementMetrics(sql, statement);
            STATEMENT_INFO_RECORD.set(statementMetrics);
            metricsHolder.collectStatement(statementMetrics);
        }
    }

    @Override
    protected void statementExecuteQueryAfter(StatementProxy statement, String sql, ResultSetProxy resultSet) {
        if (!metricsHolder.getProperties().isCollectUpdateSql()) {
            STATEMENT_INFO_RECORD.get().terminal(0);
            STATEMENT_INFO_RECORD.remove();
        }
    }

    @Override
    protected void statementExecuteUpdateBefore(StatementProxy statement, String sql) {
        StatementMetrics statementMetrics = new StatementMetrics(sql, statement);
        STATEMENT_INFO_RECORD.set(statementMetrics);
        metricsHolder.collectStatement(statementMetrics);
    }

    @Override
    protected void statementExecuteUpdateAfter(StatementProxy statement, String sql, int updateCount) {
        if (STATEMENT_INFO_RECORD.get() != null) {
            STATEMENT_INFO_RECORD.get().terminal(0);
            STATEMENT_INFO_RECORD.get().setUpdateCount(updateCount);
            STATEMENT_INFO_RECORD.remove();
        }
    }
}
