package com.ikun.metrics.transaction.utils;

import com.ikun.metrics.transaction.constant.SqlCommandType;

public class SQLUtils {


    public static SqlCommandType resolveSqlCommandType(String sql) {
        return SqlCommandType.UNKNOWN;
    }

}
