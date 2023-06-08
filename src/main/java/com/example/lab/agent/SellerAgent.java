package com.example.lab.agent;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
public class SellerAgent extends Agent {
    private DispatcherAgent dispatcherAgent;
    public SellerAgent(DispatcherAgent dispatcherAgent) {
        this.dispatcherAgent = dispatcherAgent;
    }
    protected void setup() {
        System.out.println("SellerAgent " + getAID().getName() + " is ready.");
        // Додати поведінку для обробки запитів покупців
        addBehaviour(new HandleBuyerRequestsBehaviour());
    }
    private class HandleBuyerRequestsBehaviour extends CyclicBehaviour {
        public void action() {
            // Визначити шаблон повідомлення для отримання запитів від покупців
            MessageTemplate template = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
            // Очікувати повідомлення від покупців
            ACLMessage message = myAgent.receive(template);
            if (message != null) {
                try {
                    // Отримати інформацію про необхідний квиток в повідомленні
                    String ticketInfo = message.getContent();
                    // Виконати бронювання квитка
                    boolean success = reserveTicket(ticketInfo);
                    // Створити відповідь покупцю
                    ACLMessage reply = message.createReply();
                    if (success) {
                        reply.setPerformative(ACLMessage.CONFIRM);
                        reply.setContent("Ticket reserved successfully");
                    } else {
                        reply.setPerformative(ACLMessage.FAILURE);
                        reply.setContent("Failed to reserve ticket");
                    }
                    // Надіслати відповідь покупцю
                    myAgent.send(reply);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                block();
            }
        }
    }
    private boolean reserveTicket(String ticketInfo) {
        // Перевіряємо доступність квитка
        if (isTicketAvailable(ticketInfo)) {
            System.out.println("Квиток успішно заброньовано: " + ticketInfo);
            return true;
        } else {
            // Квиток недоступний
            System.out.println("Квиток недоступний: " + ticketInfo);
            return false;
        }
    }
    private boolean isTicketAvailable(String ticketInfo) {
        // Перевірка доступності квитка на основі розкладу рейсів
        String schedule = dispatcherAgent.getSchedule();
        // Квиток не знайдено в розкладі
        return schedule.contains(ticketInfo);
    }
}