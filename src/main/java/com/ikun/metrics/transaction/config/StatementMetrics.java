package com.ikun.metrics.transaction.config;

import com.ikun.metrics.MetricsTerminalException;
import com.ikun.metrics.transaction.constant.SqlCommandType;
import com.ikun.metrics.transaction.utils.SQLUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.sql.Statement;

public class StatementMetrics extends AbstractMillisMetrics {

    private static final Logger log = LoggerFactory.getLogger(StatementMetrics.class);
    private final String boundSql;

    private SqlCommandType sqlCommandType;

    private int updateCount = -1;

    private Statement statement;

    public StatementMetrics(String sql) {
        super();
        this.boundSql = sql;
    }

    public StatementMetrics(String sql, Statement statement) {
        this(sql);
        this.statement = statement;
    }

    @Override
    public boolean terminal(int exitCode) throws MetricsTerminalException {
        boolean terminal = super.terminal(exitCode);
        if (this.sqlCommandType == null) {
            this.sqlCommandType = SQLUtils.resolveSqlCommandType(this.boundSql);
        }
        if (this.statement != null) {
            try {
                this.updateCount = this.statement.getUpdateCount();
            } catch (Throwable e) {
                log.error(" statement get updateCount error ", e);
            }
            this.statement = null;
        }
        return terminal;
    }

    public String getBoundSql() {
        return boundSql;
    }

    public SqlCommandType getSqlCommandType() {
        if (this.sqlCommandType == null) {
            this.sqlCommandType = SQLUtils.resolveSqlCommandType(this.boundSql);
        }
        return sqlCommandType;
    }

    public int getUpdateCount() {
        return this.updateCount;
    }

    public void setUpdateCount(int updateCount) {
        this.updateCount = updateCount;
    }

    public Statement getStatement() {
        return this.statement;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() +
                "{" +
                " sql = " + getBoundSql() + "," +
                " sqlCommandType = " + getSqlCommandType() + "," +
                " exitCode = " + getExitCode() + "," +
                " totalTimeMillis = " + getTotalTimeMillis() + "," +
                " updateCount = " + getUpdateCount() +
                "}";
    }
}
