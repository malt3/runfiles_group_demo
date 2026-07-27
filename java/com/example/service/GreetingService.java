package com.example.service;

import com.example.model.Greeting;

public class GreetingService {
    public Greeting greet(String name) {
        return new Greeting(name, "Hello, " + name + "!");
    }
}
