package com.ikun.metrics.transaction.utils;

public class ExceptionUtils {

    private static final StackTraceElement[] UNASSIGNED_STACK = new StackTraceElement[0];

    public static StackTraceElement[] getCurrentStackTrace() {
        Thread currentThread = Thread.currentThread();
        return currentThread.getStackTrace();
    }

}
