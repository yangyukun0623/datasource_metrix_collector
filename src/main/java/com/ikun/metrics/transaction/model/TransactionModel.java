package com.ikun.metrics.transaction.model;

import java.util.List;

public class TransactionModel {

    private long transactionId;

    private long startTimeMillis;

    private List<LockModel> locks;

}
