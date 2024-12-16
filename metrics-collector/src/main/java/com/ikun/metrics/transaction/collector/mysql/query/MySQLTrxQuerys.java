package com.ikun.metrics.transaction.collector.mysql.query;

import com.ikun.metrics.transaction.collector.mysql.model.LockModel;
import com.ikun.metrics.transaction.collector.mysql.model.TransactionModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

public class MySQLTrxQuerys {

    private static final String TRX_LOCK_QUERY_SQL = " select * from information_schema.INNODB_TRX T1 left join information_schema.INNODB_LOCKS T2 on T1.trx_id = T2.lock_trx_id where T1.trx_id = ? ";

    public static final String TRX_MODEL_KEY = "TRX_MODEL";
    private static final Logger log = LoggerFactory.getLogger(MySQLTrxQuerys.class);

    public static TransactionModel queryRunningTrxModel(DataSource dataSource, Long trxId) {
        try(Connection connection = dataSource.getConnection()) {
            try(PreparedStatement ps = connection.prepareStatement(TRX_LOCK_QUERY_SQL, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)) {
                ps.setLong(1, trxId);
                try(ResultSet rs = ps.executeQuery(TRX_LOCK_QUERY_SQL)) {
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
                        lockModel.setLockId(rs.getString("lock_id"));
                        lockModel.setLockIndex(rs.getString("lock_index"));
                        lockModel.setLockData(rs.getString("lock_data"));
                        lockModel.setLockTable(rs.getString("lock_table"));
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

//    public static List<Map<String, Object>> convert(ResultSet rs) {
//
//    }

}
