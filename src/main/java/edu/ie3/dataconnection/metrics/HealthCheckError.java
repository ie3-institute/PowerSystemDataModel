package edu.ie3.dataconnection.metrics;

public class HealthCheckError extends AssertionError {

    private long measuredTime = -1L;

    public HealthCheckError(String msg, long measuredTime) {
        super(msg);
        this.measuredTime = measuredTime;
    }

    public long getMeasuredTime() {
        return measuredTime;
    }

    public void setMeasuredTime(long measuredTime) {
        this.measuredTime = measuredTime;
    }
}
