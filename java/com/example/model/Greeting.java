package com.example.model;

import java.time.Instant;

public class Greeting {
    private final String name;
    private final String message;
    private final String timestamp;

    public Greeting(String name, String message) {
        this.name = name;
        this.message = message;
        this.timestamp = Instant.now().toString();
    }

    public String getName() {
        return name;
    }

    public String getMessage() {
        return message;
    }

    public String getTimestamp() {
        return timestamp;
    }
}
