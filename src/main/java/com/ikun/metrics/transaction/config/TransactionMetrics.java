package com.ikun.metrics.transaction.config;

import java.util.ArrayList;
import java.util.List;

public class TransactionMetrics extends AbstractMillisMetrics {

    /**
     * 事务ID
     */
    private Long transactionId;

    /**
     * 是否自动提交事务
     */
    private final boolean isAutoCommit;

    private boolean timeoutKill = false;

    /**
     * 当前事务执行sql
     */
    private List<StatementMetrics> statementCollect;

    public TransactionMetrics(boolean isAutoCommit, Long transactionId) {
        this.isAutoCommit = isAutoCommit;
        this.transactionId = transactionId;
        this.statementCollect = new ArrayList<>();
    }

    public boolean isAutoCommit() {
        return this.isAutoCommit;
    }

    public List<StatementMetrics> getStatementCollect() {
        return this.statementCollect;
    }

    public int getStatementSize() {
        return this.statementCollect.size();
    }

    public void collectStatementMetrics(StatementMetrics statementMetrics) {
        if (this.statementCollect == null) {
            this.statementCollect = new ArrayList<>();
        }
        this.statementCollect.add(statementMetrics);
    }

    public Long getTransactionId() {
        return this.transactionId;
    }

    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
    }

    public boolean isTimeoutKill() {
        return timeoutKill;
    }

    public void setTimeoutKill(boolean timeoutKill) {
        this.timeoutKill = timeoutKill;
    }

}
