package com.shopify.testify.plugin;

public class HelloWorld {
    private String greeting;

    public HelloWorld(String greeting) {
        this.greeting = greeting;
    }

    public String greet() {
        return greeting;
    }
}