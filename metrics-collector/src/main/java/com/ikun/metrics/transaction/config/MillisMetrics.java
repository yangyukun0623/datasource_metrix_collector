package com.ikun.metrics.transaction.config;

import com.ikun.metrics.MetricsTerminalException;

public interface MillisMetrics {

    long getStartTimeMillis();

    Long getEndTimeMillis();

    long getTotalTimeMillis();

    boolean finish();

    int getExitCode();

    boolean terminal(int exitCode) throws MetricsTerminalException;

    StackTraceElement[] getCurrentStackTrace();

}
