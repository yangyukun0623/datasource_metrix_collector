package com.ikun.metrics.transaction.utils;

import java.util.ArrayList;
import java.util.List;

public class ExceptionUtils {

    private static final StackTraceElement[] UNASSIGNED_STACK = new StackTraceElement[0];


    public static StackTraceElement[] getCurrentStackTrace() {
        Thread currentThread = Thread.currentThread();
        return currentThread.getStackTrace();
    }

    public static List<StackTraceElement[]> getRecursiveStackTraces() {
        List<StackTraceElement[]> stackTraces = new ArrayList<>();
        Thread currentThread = Thread.currentThread();
    }

    private static void recursiveCollectStackTraces(Thread thread, List<StackTraceElement[]> stackTraces) {
        if (thread != null) {
            stackTraces.add(0, thread.getStackTrace());
        }
    }

}
