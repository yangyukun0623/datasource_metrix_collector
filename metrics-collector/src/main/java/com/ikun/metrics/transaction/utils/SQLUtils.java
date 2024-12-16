package com.ikun.metrics.transaction.utils;

import com.ikun.metrics.transaction.constant.SqlCommandType;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.alter.Alter;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.grant.Grant;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.update.Update;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class SQLUtils {


    private static final Logger log = LoggerFactory.getLogger(SQLUtils.class);

    public static SqlCommandType resolveSqlCommandType(String sql) {
        Objects.requireNonNull(sql, " sql must not be null ");
        try {
            Statement statement = CCJSqlParserUtil.parse(sql);
            if (statement instanceof Select) {
                return SqlCommandType.SELECT;
            }
            else if (statement instanceof Insert) {
                return SqlCommandType.INSERT;
            } else if (statement instanceof Update) {
                return SqlCommandType.UPDATE;
            } else if (statement instanceof Delete) {
                return SqlCommandType.DELETE;
            } else if (statement instanceof Alter) {
                return SqlCommandType.ALTER;
            } else if (statement instanceof Grant) {
                return SqlCommandType.GRANT;
            }
        } catch (JSQLParserException e) {
            log.error(" sql parse error ", e);
        }
        return SqlCommandType.UNKNOWN;
    }

}
