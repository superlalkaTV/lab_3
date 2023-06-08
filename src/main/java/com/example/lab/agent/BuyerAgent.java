package com.example.lab.agent;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.util.Base64;
import java.util.Random;
public class BuyerAgent extends Agent {
    protected void setup() {
        System.out.println("Агент-покупець " + getAID().getName() + " готов.");
        addBehaviour(new RequestTicketBehaviour());
    }
    private class RequestTicketBehaviour extends CyclicBehaviour {
        public void action() {
            // Очікувати повідомлення від диспетчера
            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
            ACLMessage message = receive(mt);
            if (message != null) {
                // Отримати інформацію про необхідний квиток
                String ticketInfo = message.getContent();
                System.out.println("Агент-покупець отримав запит на квиток: " + ticketInfo);
                // Здійснити бронювання квитка
                boolean reservationSuccess = reserveTicket(ticketInfo);
                // Надіслати повідомлення диспетчеру про результат бронювання
                ACLMessage reply = message.createReply();
                reply.setPerformative(ACLMessage.INFORM);
                reply.setContent(Boolean.toString(reservationSuccess));
                send(reply);
            } else {
                block();
            }
        }

        private AID getAvailableSellerAgent() {
            // Створити запит до диспетчера для отримання доступних агентів-продавців
            ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
            request.setContent("GetAvailableSellerAgent");
            send(request);
            // Очікувати відповідь від диспетчера
            ACLMessage reply = blockingReceive(MessageTemplate.MatchPerformative(ACLMessage.INFORM));
            if (reply != null) {
                // Отримати список доступних агентів-продавців з вмісту відповіді
                String content = reply.getContent();
                AID[] availableAgents = extractAgents(content);
                if (availableAgents != null && availableAgents.length > 0) {
                    // Вибрати випадкового агента-продавця зі списку
                    Random random = new Random();
                    int index = random.nextInt(availableAgents.length);
                    return availableAgents[index];
                }
            }
            return null;
        }
        private AID[] extractAgents(String content) {
            // Розпакувати список агентів з рядка
            try {
                AID[] agents = (AID[]) new ObjectInputStream(new ByteArrayInputStream(Base64.getDecoder().decode(content))).readObject();
                return agents;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
        private boolean reserveTicket(String ticketInfo) {
            // Отримати агента-продавця з диспетчера
            AID sellerAgent = getAvailableSellerAgent();
            if (sellerAgent != null) {
                // Створити запит на бронювання квитка
                ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
                request.addReceiver(sellerAgent);
                request.setContent(ticketInfo);
                // Відправити запит агенту-продавцю
                send(request);
                // Очікувати відповідь
                ACLMessage reply = blockingReceive(MessageTemplate.MatchPerformative(ACLMessage.INFORM));
                if (reply != null) {
                    // Отримати вміст відповіді
                    String content = reply.getContent();
                    if (content.equals("TicketReserved")) {
                        // Квиток успішно заброньовано
                        System.out.println("Квиток успішно заброньовано.");
                        return true;
                    } else if (content.equals("NoAvailableTickets")) {
                        // Немає вільних квитків
                        System.out.println("На жаль, немає вільних квитків.");
                        return false;
                    }
                } else {
                    // Відповідь не отримано
                    System.out.println("Не отримано відповідь від агента-продавця.");
                }
            } else {
                // Немає доступних агентів-продавців
                System.out.println("На жаль, немає доступних агентів-продавців.");
            }
            return false;
        }
    }
}
