package com.ikun.metrics.transaction.config;

import com.ikun.metrics.MetricsTerminalException;
import com.ikun.metrics.transaction.utils.ExceptionUtils;

import java.util.concurrent.atomic.AtomicBoolean;

public abstract class AbstractMillisMetrics implements MillisMetrics {

    private final long startTimeMillis;

    private Long endTimeMillis;

    private final AtomicBoolean finish = new AtomicBoolean(false);

    private int exitCode;

    private StackTraceElement[] stackTrace;

    public AbstractMillisMetrics() {
        this.startTimeMillis = System.currentTimeMillis();
    }

    @Override
    public long getStartTimeMillis() {
        return this.startTimeMillis;
    }

    @Override
    public Long getEndTimeMillis() {
        return this.endTimeMillis;
    }

    @Override
    public long getTotalTimeMillis() {
        if (finish.get()) {
            return this.endTimeMillis - startTimeMillis;
        } else {
            return System.currentTimeMillis() - this.startTimeMillis;
        }
    }

    @Override
    public boolean finish() {
        return this.finish.get();
    }

    @Override
    public int getExitCode() {
        return this.exitCode;
    }

    @Override
    public boolean terminal(int exitCode) throws MetricsTerminalException {
        if (this.finish.compareAndSet(false, true)) {
            this.exitCode = 0;
            this.endTimeMillis = System.currentTimeMillis();
            this.stackTrace = ExceptionUtils.getCurrentStackTrace();
            return true;
        }
        this.exitCode = exitCode;
        return false;
    }

    public StackTraceElement[] getCurrentStackTrace() {
        return this.stackTrace;
    }

}
