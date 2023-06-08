package com.example.lab.agent;
import com.example.lab.ui.UserInterface;
import jade.core.Agent;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
public class MainContainerAgent extends Agent {
    protected void setup() {
        System.out.println("Агент головного контейнера готов.");
        // Ініціалізуємо контейнер JADE
        Runtime runtime = Runtime.instance();
        Profile profile = new ProfileImpl();
        ContainerController container = runtime.createMainContainer(profile);
        try {
            // Створюємо та запускаємо агентів-продавців
            AgentController sellerAgent1 = container.createNewAgent("SellerAgent1", SellerAgent.class.getName(), null);
            sellerAgent1.start();
            AgentController sellerAgent2 = container.createNewAgent("SellerAgent2", SellerAgent.class.getName(), null);
            sellerAgent2.start();
            // Створюємо та запускаємо агентів-покупців
            AgentController buyerAgent1 = container.createNewAgent("BuyerAgent1", com.example.lab.agent.BuyerAgent.class.getName(), null);
            buyerAgent1.start();
            AgentController buyerAgent2 = container.createNewAgent("BuyerAgent2", com.example.lab.agent.BuyerAgent.class.getName(), null);
            buyerAgent2.start();
            // Створюємо та запускаємо агента-диспетчера
            AgentController dispatcherAgent = container.createNewAgent("DispatcherAgent", DispatcherAgent.class.getName(), null);
            dispatcherAgent.start();
            // Створюємо та запускаємо агента-інтерфейсу користувача
            AgentController userInterfaceAgent = container.createNewAgent("UserInterfaceAgent", UserInterface.class.getName(), null);
            userInterfaceAgent.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
