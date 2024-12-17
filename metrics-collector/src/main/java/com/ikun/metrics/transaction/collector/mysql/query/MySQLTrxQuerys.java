package com.ikun.metrics.transaction.collector.mysql.query;

import com.ikun.metrics.transaction.collector.mysql.model.LockModel;
import com.ikun.metrics.transaction.collector.mysql.model.TransactionModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class MySQLTrxQuerys {

    private static final String TRX_LOCK_QUERY_SQL_5 = " select * from information_schema.INNODB_TRX T1 left join information_schema.INNODB_LOCKS T2 on T1.trx_id = T2.lock_trx_id where T1.trx_id = ? ";
    private static final String TRX_LOCK_QUERY_SQL_8 = " select * from information_schema.INNODB_TRX T1 left join performance_schema.data_locks T2 on T1.trx_id = T2.ENGINE_LOCK_ID where T1.trx_id = ? ";

    public static final String TRX_MODEL_KEY = "TRX_MODEL";
    private static final Logger log = LoggerFactory.getLogger(MySQLTrxQuerys.class);

    private static final Map<DataSource, MySQLVersion> MY_SQL_VERSION_MAP = new HashMap<>();

    public static TransactionModel queryRunningTrxModel(DataSource dataSource, Long trxId) {
        try(Connection connection = dataSource.getConnection()) {
            MySQLVersion version = getMySQLVersion(dataSource);
            try(PreparedStatement ps = connection.prepareStatement(determineSql(dataSource), ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)) {
                ps.setLong(1, trxId);
                try(ResultSet rs = ps.executeQuery()) {
                    Map<Object, TransactionModel> joinMergeMap = new HashMap<>();
                    while (rs.next()) {
                        Object trxIdVal = rs.getObject("trx_id");
                        if (trxIdVal == null) continue;
                        if (!joinMergeMap.containsKey(trxIdVal)) {
                            TransactionModel model = new TransactionModel();
                            model.setTransactionId(Long.parseLong(trxIdVal.toString()));
                            Timestamp trxStarted = rs.getTimestamp("trx_started");
                            if (trxStarted != null) {
                                model.setStartTimeMillis(trxStarted.getTime());
                            }
                            Timestamp trxWaitStarted = rs.getTimestamp("trx_wait_started");
                            if (trxWaitStarted != null) {
                                model.setWaitStartTimeMillis(trxWaitStarted.getTime());
                            }
                            model.setRequestLockId(rs.getString("trx_requested_lock_id"));
                            model.setMysqlThreadId(rs.getLong("trx_mysql_thread_id"));
                            model.setTrxQuery(rs.getString("trx_query"));
                            model.setTrxOperationState(rs.getString("trx_operation_state"));
                            joinMergeMap.put(trxIdVal, model);
                        }
                        LockModel lockModel = new LockModel();
                        lockModel.setLockId(rs.getString(version == MySQLVersion.VERSION_5 ? "lock_id" : "ENGINE_LOCK_ID"));
                        lockModel.setLockIndex(rs.getString(version == MySQLVersion.VERSION_5 ? "lock_index" : "INDEX_NAME"));
                        lockModel.setLockData(rs.getString(version == MySQLVersion.VERSION_5 ? "lock_data" : "LOCK_DATA"));
                        lockModel.setLockTable(rs.getString(version == MySQLVersion.VERSION_5 ? "lock_table" : "OBJECT_NAME"));
                        lockModel.setTransactionId(Long.parseLong(trxIdVal.toString()));
                        joinMergeMap.get(trxIdVal).getLocks().add(lockModel);
                    }
                    return joinMergeMap.get(trxId);
                }
            }
        } catch (Exception e) {
            log.error("获取连接异常", e);
        }
        return null;
    }

    public static String determineSql(DataSource dataSource) {
        MySQLVersion version = getMySQLVersion(dataSource);
        if (version == MySQLVersion.VERSION_5) {
            return TRX_LOCK_QUERY_SQL_5;
        } else {
            return TRX_LOCK_QUERY_SQL_8;
        }
    }

    public static MySQLVersion getMySQLVersion(DataSource dataSource) {
        MySQLVersion version = MY_SQL_VERSION_MAP.get(dataSource);
        if (version == null) {
            try(Connection conn = dataSource.getConnection()) {
                try(Statement st = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)) {
                    try(ResultSet rs = st.executeQuery(" select version() ")) {
                        while (rs.next()) {
                            String v = rs.getString(1);
                            if (v != null && v.startsWith("5")) {
                                version = MySQLVersion.VERSION_5;
                            } else if (v != null && v.startsWith("8")) {
                                version = MySQLVersion.VERSION_8;
                            }
                            if (version != null) {
                                MY_SQL_VERSION_MAP.put(dataSource, version);
                            }
                        }
                        log.info(" current datasource version: {}", version);
                    }
                }
            } catch (SQLException e) {
                log.error("获取连接异常", e);
                version = MySQLVersion.VERSION_5;
            }
        }
        return version;
    }

    public enum MySQLVersion {
        VERSION_5, VERSION_8
    }

}
