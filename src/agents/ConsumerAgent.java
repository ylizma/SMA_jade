package agents;

import containers.ConsumerContainer;
import jade.core.AID;
import jade.core.behaviours.*;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;

public class ConsumerAgent extends GuiAgent {

    transient ConsumerContainer gui;

    @Override
    protected void setup() {
        if (getArguments().length > 0){
            gui = (ConsumerContainer) getArguments()[0];
            gui.consumerAgent=this;
        }


        ParallelBehaviour parallelBehaviour = new ParallelBehaviour();
        addBehaviour(parallelBehaviour);
        parallelBehaviour.addSubBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                ACLMessage message = receive();
                if (message != null) {
                    if (message.getPerformative()==ACLMessage.CONFIRM){
                        gui.logMessage(message);
                    }
                } else block();
            }
        });
    }

    @Override
    public void onGuiEvent(GuiEvent params) {
        if (params.getType()==1){
            String livre= (String) params.getParameter(0);
            ACLMessage message=new ACLMessage(ACLMessage.REQUEST);
            message.setContent(livre);
            message.addReceiver(new AID("acheteur",AID.ISLOCALNAME));
            send(message);

        }
    }
}
