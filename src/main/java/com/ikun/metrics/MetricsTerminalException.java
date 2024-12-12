package com.ikun.metrics;

public class MetricsTerminalException extends RuntimeException {

    private final int exitCode;

    private final String reason;

    public MetricsTerminalException(int exitCode, String reason) {
        super(reason);
        this.exitCode = exitCode;
        this.reason = reason;
    }

    public MetricsTerminalException(String reason) {
        super(reason);
        this.exitCode = -1;
        this.reason = reason;
    }

    public MetricsTerminalException(String reason, Throwable cause) {
        super(reason, cause);
        this.exitCode = -1;
        this.reason = reason;
    }

    public int getExitCode() {
        return exitCode;
    }

    public String getReason() {
        return reason;
    }


}
