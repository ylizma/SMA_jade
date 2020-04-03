package agents;

import containers.AcheteurGui;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;

import java.util.*;

public class AcheteurAgent extends GuiAgent {

    AcheteurGui acheteurGui;
    AID[] sellers;
    List<ACLMessage> allmessages = new ArrayList<>();

    @Override
    protected void setup() {
        if (getArguments().length > 0) {
            acheteurGui = (AcheteurGui) getArguments()[0];
            acheteurGui.acheteurAgent = this;
        }

        ParallelBehaviour parallelBehaviour = new ParallelBehaviour();
        addBehaviour(parallelBehaviour);
        parallelBehaviour.addSubBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {

                int count = 0;
                ACLMessage message = receive();
                if (message != null) {
                    acheteurGui.logMessage(message);
                    switch (message.getPerformative()) {
                        case ACLMessage.REQUEST:
                            ACLMessage cfp = new ACLMessage(ACLMessage.CFP);
                            cfp.setContent(message.getContent());
                            for (AID aid : sellers) {
                                cfp.addReceiver(aid);
                            }
                            send(cfp);
                            break;
                        case ACLMessage.PROPOSE:
                            System.out.println("before condition");
                            allmessages.add(message);
                            System.out.println(allmessages.size() + " " + sellers.length + "  " + count);
                            if (allmessages.size() == sellers.length) {
                                System.out.println("after condition");
                                ACLMessage best= allmessages.stream().min(Comparator.comparing(ACLMessage::getContent)).get();
                                System.out.println(best.getContent());
                                ACLMessage reply=best.createReply();
                                reply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
                                reply.setContent(best.getContent());
                                send(reply);
                            }
                            break;
                        case ACLMessage.AGREE:
                            ACLMessage agreemsg = new ACLMessage(ACLMessage.CONFIRM);
                            agreemsg.setContent(message.getContent());
                            agreemsg.addReceiver(new AID("consumer", AID.ISLOCALNAME));
                            send(agreemsg);
                            break;
                    }

                } else block();
            }
        });

        parallelBehaviour.addSubBehaviour(new TickerBehaviour(this, 2000) {
            @Override
            protected void onTick() {
                DFAgentDescription agentDescription = new DFAgentDescription();
                ServiceDescription serviceDescription = new ServiceDescription();
                serviceDescription.setType("transaction");
                serviceDescription.setName("vente-livres");
                agentDescription.addServices(serviceDescription);
                try {
                    DFAgentDescription[] results = DFService.search(myAgent, agentDescription);
                    sellers = new AID[results.length];
                    for (int i = 0; i < results.length; i++) {
                        sellers[i] = results[i].getName();
                    }
                } catch (FIPAException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onGuiEvent(GuiEvent guiEvent) {

    }
}
