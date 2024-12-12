package com.ikun.metrics.transaction.config;

import java.util.ArrayList;
import java.util.List;

public class TransactionMetrics extends AbstractMillisMetrics {

    /**
     * 是否自动提交事务
     */
    private final boolean isAutoCommit;

    /**
     * 当前事务执行sql
     */
    private List<StatementMetrics> statementCollect;


    public TransactionMetrics(boolean isAutoCommit) {
        this.isAutoCommit = isAutoCommit;
        this.statementCollect = new ArrayList<>();
    }

    public boolean isAutoCommit() {
        return this.isAutoCommit;
    }

}
