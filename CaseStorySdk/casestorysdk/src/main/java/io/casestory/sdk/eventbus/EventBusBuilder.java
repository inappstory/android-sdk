package io.casestory.sdk.eventbus;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EventBusBuilder {

    private final static ExecutorService DEFAULT_EXECUTOR_SERVICE = Executors.newCachedThreadPool();
    ExecutorService executorService = DEFAULT_EXECUTOR_SERVICE;
    boolean eventInheritance = true;
    boolean strictMethodVerification;
    EventBusBuilder() {
    }

    public EventBusBuilder strictMethodVerification(boolean strictMethodVerification) {
        this.strictMethodVerification = strictMethodVerification;
        return this;
    }

    public EventBus build() {
        return new EventBus(this);
    }
}
