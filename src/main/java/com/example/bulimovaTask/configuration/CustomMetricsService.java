package com.example.bulimovaTask.configuration;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Service;
import io.micrometer.core.instrument.Counter;
@Service
public class CustomMetricsService {

    private final Counter taskCreated;
    private final Counter taskUpdated;
    private final Counter taskDeleted;

    private final Counter userCreated;
    private final Counter userUpdated;
    private final Counter userDeleted;

    public CustomMetricsService(MeterRegistry registry) {
        this.taskCreated = registry.counter("task.created.count");
        this.taskUpdated = registry.counter("task.updated.count");
        this.taskDeleted = registry.counter("task.deleted.count");

        this.userCreated = registry.counter("user.created.count");
        this.userUpdated = registry.counter("user.updated.count");
        this.userDeleted = registry.counter("user.deleted.count");
    }


    public void taskCreated() {
        taskCreated.increment();
    }

    public void taskUpdated() {
        taskUpdated.increment();
    }

    public void taskDeleted() {
        taskDeleted.increment();
    }


    public void userCreated() {
        userCreated.increment();
    }

    public void userUpdated() {
        userUpdated.increment();
    }

    public void userDeleted() {
        userDeleted.increment();
    }
}
