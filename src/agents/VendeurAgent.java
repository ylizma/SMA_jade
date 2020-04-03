package agents;

import containers.VendeurGui;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;

import java.util.Random;

public class VendeurAgent extends GuiAgent {
    VendeurGui gui;

    @Override
    protected void setup() {
        if (getArguments().length > 0) {
            gui = (VendeurGui) getArguments()[0];
            gui.vendeurAgent = this;
        }

        ParallelBehaviour parallelBehaviour = new ParallelBehaviour();
        addBehaviour(parallelBehaviour);
        parallelBehaviour.addSubBehaviour(new OneShotBehaviour() {
            @Override
            public void action() {
                DFAgentDescription agentDescription = new DFAgentDescription();
                agentDescription.setName(getAID());
                ServiceDescription serviceDescription = new ServiceDescription();
                serviceDescription.setType("transaction");
                serviceDescription.setName("vente-livres");
                agentDescription.addServices(serviceDescription);
                try {
                    DFService.register(myAgent,agentDescription);
                } catch (FIPAException e) {
                    e.printStackTrace();
                }
            }
        });
        parallelBehaviour.addSubBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                ACLMessage message = receive();
                if (message != null) {
                    switch (message.getPerformative()){
                        case ACLMessage.CFP:
                            ACLMessage reply=message.createReply();
                            reply.setPerformative(ACLMessage.PROPOSE);
                            reply.setContent(String.valueOf(new Random().nextInt(1000)+500));
                            send(reply);
                            break;
                        case ACLMessage.ACCEPT_PROPOSAL:
                            ACLMessage acceptreply=message.createReply();
                            acceptreply.setPerformative(ACLMessage.AGREE);
                            send(acceptreply);
                            break;
                    }
                } else block();
            }
        });
    }

    @Override
    protected void onGuiEvent(GuiEvent event) {

    }

    @Override
    protected void takeDown() {
        try {
            DFService.deregister(this);
        } catch (FIPAException e) {
            e.printStackTrace();
        }
    }
}
