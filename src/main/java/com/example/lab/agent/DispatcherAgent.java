package com.example.lab.agent;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class DispatcherAgent extends Agent {
    protected void setup() {
        System.out.println("Диспетчер агент " + getAID().getName() + " готов.");

        // Додати поведінки до агента
        addBehaviour(new RequestTicketBehaviour());
        addBehaviour(new ProvideScheduleBehaviour());
    }

    private class RequestTicketBehaviour extends CyclicBehaviour {
        public void action() {
            // Очікувати повідомлення від агента-покупця
            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
            ACLMessage message = receive(mt);

            if (message != null) {
                // Отримати інформацію про необхідний квиток від агента-покупця
                String ticketInfo = message.getContent();
                System.out.println("Диспетчер отримав запит на квиток: " + ticketInfo);

                // Відправити запит на бронювання квитка агенту-продавцю
                ACLMessage requestMsg = new ACLMessage(ACLMessage.REQUEST);
                requestMsg.setContent(ticketInfo);
                requestMsg.addReceiver(getSellerAgentAID());
                send(requestMsg);
            } else {
                block();
            }
        }
    }

    private class ProvideScheduleBehaviour extends OneShotBehaviour {
        public void action() {
            // Передати розклад рейсів агентам-продавцям та агентам-покупцям
            ACLMessage scheduleMsg = new ACLMessage(ACLMessage.INFORM);
            scheduleMsg.setContent(getSchedule());
            scheduleMsg.addReceiver(getSellerAgentAID());
            scheduleMsg.addReceiver(getBuyerAgentAID());
            send(scheduleMsg);
        }
    }

    public String getSchedule() {
        // Повернення розкладу рейсів агента-диспетчера
        String schedule = "Розклад рейсів:\n";
        schedule += "Рейс 1: Київ - Лондон, 10:00\n";
        schedule += "Рейс 2: Київ - Париж, 14:30\n";
        schedule += "Рейс 3: Київ - Рим, 18:15\n";
        schedule += "Рейс 4: Київ - Нью-Йорк, 21:45\n";
        schedule += "Рейс 5: Київ - Токіо, 07:30\n";
        schedule += "Рейс 6: Київ - Сідней, 23:00\n";
        schedule += "Рейс 7: Київ - Москва, 15:45\n";
        schedule += "Рейс 8: Київ - Берлін, 12:20\n";
        schedule += "Рейс 9: Київ - Мадрид, 17:10\n";
        schedule += "Рейс 10: Київ - Стамбул, 19:55\n";

        return schedule;
    }

    private jade.core.AID getSellerAgentAID() {
        return new jade.core.AID("SellerAgent1", jade.core.AID.ISLOCALNAME);
    }

    private jade.core.AID getBuyerAgentAID() {
        return new jade.core.AID("BuyerAgent1", jade.core.AID.ISLOCALNAME);
    }
}
