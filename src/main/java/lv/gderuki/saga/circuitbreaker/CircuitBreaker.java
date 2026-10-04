package lv.gderuki.saga.circuitbreaker;

import lombok.Getter;

public class CircuitBreaker {

    public enum State {
        CLOSED, OPEN, HALF_OPEN
    }

    @Getter
    private State state = State.CLOSED;
    private int failureCount = 0;

    private final int failureThreshold;
    private final long resetTimeoutMs;
    private long lastStateChangeTime = 0;

    public CircuitBreaker(int failureThreshold, long resetTimeoutMs) {
        this.failureThreshold = failureThreshold;
        this.resetTimeoutMs = resetTimeoutMs;
    }

    public synchronized boolean allowRequest() {
        if (state == State.OPEN) {
            // time passed is more than resetTimeoutMs
            if (System.currentTimeMillis() - lastStateChangeTime >= resetTimeoutMs) {
                state = State.HALF_OPEN;
                return true; // probe call
            }
            return false; // block call
        }
        return true; // allow call in CLOSED and HALF_OPEN states
    }

    public synchronized void recordSuccess() {
        failureCount = 0;
        state = State.CLOSED;
    }

    public synchronized void recordFailure() {
        failureCount++;
        if (failureCount >= failureThreshold || state == State.HALF_OPEN) {
            state = State.OPEN;
            lastStateChangeTime = System.currentTimeMillis();
        }
    }

}
