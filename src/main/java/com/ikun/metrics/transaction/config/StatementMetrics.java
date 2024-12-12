package com.ikun.metrics.transaction.config;

public class StatementMetrics extends AbstractMillisMetrics {

    private final String boundSql;

    public StatementMetrics(String sql) {
        super();
        this.boundSql = sql;
    }

    public String getBoundSql() {
        return boundSql;
    }

}
