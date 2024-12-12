package com.ikun.metrics.transaction.config;

import com.ikun.metrics.transaction.constant.SqlCommandType;
import com.ikun.metrics.transaction.utils.SQLUtils;

public class StatementMetrics extends AbstractMillisMetrics {

    private final String boundSql;

    private final SqlCommandType sqlCommandType;

    public StatementMetrics(String sql) {
        super();
        this.boundSql = sql;
        this.sqlCommandType = SQLUtils.resolveSqlCommandType(sql);
    }

    public String getBoundSql() {
        return boundSql;
    }

    public SqlCommandType getSqlCommandType() {
        return sqlCommandType;
    }

}
