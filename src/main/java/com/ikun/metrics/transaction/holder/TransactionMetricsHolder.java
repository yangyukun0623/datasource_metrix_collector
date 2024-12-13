package com.ikun.metrics.transaction.holder;

import com.ikun.metrics.transaction.config.StatementMetrics;
import com.ikun.metrics.transaction.config.TransactionCollectProperties;
import com.ikun.metrics.transaction.config.TransactionMetrics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;

public class TransactionMetricsHolder {

    private static final Logger log = LoggerFactory.getLogger(TransactionMetricsHolder.class);

    private final ThreadLocal<TransactionMetrics> TRX_INFO_RECORD = new ThreadLocal<>();

    private final TransactionCollectProperties properties;

    private DataSource currentDataSource;

    public TransactionMetricsHolder(TransactionCollectProperties props, DataSource dataSource) {
        this.properties = props;
        this.currentDataSource = dataSource;
    }

    public void transactionReady(boolean isAutoCommit, Long transactionId) {
        TransactionMetrics metrics = new TransactionMetrics(isAutoCommit, transactionId);
        TRX_INFO_RECORD.set(metrics);
        log.info(" trx {} ready ... ", transactionId);
    }

    public void transactionFinish() {
        Long trxId = null;
        if (TRX_INFO_RECORD.get() != null) {
            TransactionMetrics metrics = TRX_INFO_RECORD.get();
            metrics.terminal(0);
            trxId = metrics.getTransactionId();
            if (metrics.getTotalTimeMillis() > properties.getMaxTransactionRunningMillis()
            || metrics.getStatementSize() > properties.getMaxTransactionSql()) {
                log.warn("trx {} running over {} ms", metrics.getTransactionId(), metrics.getTotalTimeMillis());
                log.warn(" trx {} contains {} sql ", metrics.getTransactionId(), metrics.getStatementSize());
            }
            log.info(" trx {} contains sql: {}", metrics.getTransactionId(), metrics.getStatementCollect());
        }
        log.info(" trx {} end ... ", trxId);
    }

    public void clearTransactionMetrics() {
        if (TRX_INFO_RECORD.get() != null) {
            TRX_INFO_RECORD.remove();
        }
    }

    public void collectStatement(StatementMetrics statementMetrics) {
        if (TRX_INFO_RECORD.get() != null) {
            TRX_INFO_RECORD.get().collectStatementMetrics(statementMetrics);
        }
    }

    public TransactionCollectProperties getProperties() {
        return this.properties;
    }

    public DataSource getCurrentDataSource() {
        return this.currentDataSource;
    }

}
