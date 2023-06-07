package com.example.lab;

import jade.core.Agent;

public class Test extends Agent {
    @Override
    protected void setup() {
        System.out.println(getAID().getName());
    }
}
