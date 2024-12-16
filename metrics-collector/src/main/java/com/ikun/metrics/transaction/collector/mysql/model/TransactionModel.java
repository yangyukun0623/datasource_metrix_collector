package com.ikun.metrics.transaction.collector.mysql.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TransactionModel {

    private long transactionId;

    private long startTimeMillis;

    private Long waitStartTimeMillis;

    private String requestLockId;

    private Long mysqlThreadId;

    private String trxQuery;

    private String trxOperationState;

    private List<LockModel> locks;

    private List<Map<String, Object>> trxSnapShot = new ArrayList<>();

    private List<Map<String, Object>> lockSnapShot = new ArrayList<>();

    private List<Map<String, Object>> lockWaitSnapShot = new ArrayList<>();

    public long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(long transactionId) {
        this.transactionId = transactionId;
    }

    public List<Map<String, Object>> getLockWaitSnapShot() {
        return lockWaitSnapShot;
    }

    public void setLockWaitSnapShot(List<Map<String, Object>> lockWaitSnapShot) {
        this.lockWaitSnapShot = lockWaitSnapShot;
    }

    public long getStartTimeMillis() {
        return startTimeMillis;
    }

    public void setStartTimeMillis(long startTimeMillis) {
        this.startTimeMillis = startTimeMillis;
    }

    public Long getWaitStartTimeMillis() {
        return waitStartTimeMillis;
    }

    public void setWaitStartTimeMillis(Long waitStartTimeMillis) {
        this.waitStartTimeMillis = waitStartTimeMillis;
    }

    public String getRequestLockId() {
        return requestLockId;
    }

    public void setRequestLockId(String requestLockId) {
        this.requestLockId = requestLockId;
    }

    public Long getMysqlThreadId() {
        return mysqlThreadId;
    }

    public void setMysqlThreadId(Long mysqlThreadId) {
        this.mysqlThreadId = mysqlThreadId;
    }

    public String getTrxQuery() {
        return trxQuery;
    }

    public void setTrxQuery(String trxQuery) {
        this.trxQuery = trxQuery;
    }

    public String getTrxOperationState() {
        return trxOperationState;
    }

    public void setTrxOperationState(String trxOperationState) {
        this.trxOperationState = trxOperationState;
    }

    public List<LockModel> getLocks() {
        return locks;
    }

    public void setLocks(List<LockModel> locks) {
        this.locks = locks;
    }

    public List<Map<String, Object>> getTrxSnapShot() {
        return trxSnapShot;
    }

    public void setTrxSnapShot(List<Map<String, Object>> trxSnapShot) {
        this.trxSnapShot = trxSnapShot;
    }

    public List<Map<String, Object>> getLockSnapShot() {
        return lockSnapShot;
    }

    public void setLockSnapShot(List<Map<String, Object>> lockSnapShot) {
        this.lockSnapShot = lockSnapShot;
    }
}
