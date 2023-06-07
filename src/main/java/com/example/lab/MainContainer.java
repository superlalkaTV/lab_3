package com.example.lab;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;

public class MainContainer {
    public static void main(String[] args) {
        // Ініціалізувати робочий середовище JADE
        Runtime rt = Runtime.instance();
        Profile p = new ProfileImpl();
        ContainerController container = rt.createMainContainer(p);

        try {
            // Створити агента
            AgentController agentController = container.createNewAgent("TestAgent", "com.example.lab.Test", null);
            agentController.start();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
