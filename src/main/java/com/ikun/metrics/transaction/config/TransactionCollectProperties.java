package com.ikun.metrics.transaction.config;

public class TransactionCollectProperties {

    /**
     * 事务的最大运行时间，超过这个时间会被标记为大事务，收集起来用于分析和预警，默认五分钟
     */
    private long maxTransactionRunningMillis = 5 * 60 * 1000;

    /**
     * 单个事务中执行的sql数量，超过这个数量会被标记为大事务
     */
    private int maxTransactionSql = 20;

    /**
     * 是否只收集未自动提交的事务
     */
    private boolean collectUnAutoCommit = true;

    /**
     * 是否只收集变更数据的sql
     */
    private boolean collectUpdateSql = false;

}
