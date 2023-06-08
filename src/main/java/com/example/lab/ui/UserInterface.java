package com.example.lab.ui;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import java.util.Scanner;
public class UserInterface extends Agent {
    private AID buyerAgent;
    protected void setup() {
        System.out.println("Агент інтерфейсу користувача готов.");
        // Реєстрація агента-інтерфейсу
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        try {
            DFService.register(this, dfd);
        } catch (FIPAException e) {
            e.printStackTrace();
        }
        // Пошук агента-покупця
        DFAgentDescription template = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setType("buyer");
        template.addServices(sd);
        try {
            DFAgentDescription[] result = DFService.search(this, template);
            if (result.length > 0) {
                buyerAgent = result[0].getName();
                System.out.println("Знайдено агента-покупця: " + buyerAgent.getName());
            } else {
                System.out.println("Агент-покупець не знайдений.");
                doDelete();
            }
        } catch (FIPAException e) {
            e.printStackTrace();
        }
        // Додати поведінку до агента
        addBehaviour(new RequestTicketBehaviour());
    }
    private class RequestTicketBehaviour extends CyclicBehaviour {
        public void action() {
            // Запитати користувача про необхідний квиток
            System.out.println("Введіть опис необхідного квитка:");
            Scanner scanner = new Scanner(System.in);
            String ticketInfo = scanner.nextLine();
            // Створити повідомлення для агента-покупця
            ACLMessage message = new ACLMessage(ACLMessage.REQUEST);
            message.addReceiver(buyerAgent);
            message.setContent(ticketInfo);
            send(message);
            // Очікувати відповіді від агента-покупця
            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
            ACLMessage reply = receive(mt);
            if (reply != null) {
                // Отримати результат бронювання
                boolean reservationSuccess = Boolean.parseBoolean(reply.getContent());
                if (reservationSuccess) {
                    System.out.println("Квиток заброньовано успішно.");
                } else {
                    System.out.println("Не вдалося забронювати квиток.");
                }
            } else {
                block();
            }
        }
    }
}
