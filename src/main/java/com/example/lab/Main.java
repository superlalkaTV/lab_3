package com.example.lab;

import com.example.lab.agent.MainContainerAgent;
import jade.core.Runtime;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;

public class Main {
    public static void main(String[] args) {
        // Ініціалізувати рантайм JADE
        Runtime runtime = Runtime.instance();

        // Створити профіль для контейнера
        Profile profile = new ProfileImpl();

        // Створити контейнер
        AgentContainer container = runtime.createMainContainer(profile);

        try {
            // Створити агента MainContainerAgent
            AgentController agentController = container.createNewAgent("MainContainer", MainContainerAgent.class.getName(), null);

            // Запустити агента
            agentController.start();

            // Зупинити рантайм JADE після закінчення виконання агента
            runtime.shutDown();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
