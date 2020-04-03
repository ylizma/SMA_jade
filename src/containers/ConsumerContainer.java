package containers;

import agents.ConsumerAgent;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ConsumerContainer extends Application {
    public ConsumerAgent consumerAgent;
    ObservableList<String> stringObservableList;
    public static void main(String[] args) {
        launch();
    }

    void initContainer() throws Exception {
        Runtime runtime = Runtime.instance();
        ProfileImpl profile = new ProfileImpl();
        profile.setParameter(ProfileImpl.MAIN_HOST, "localhost");
        AgentContainer agentContainer = runtime.createAgentContainer(profile);
        AgentController agentController = agentContainer.createNewAgent("consumer", "agents.ConsumerAgent", new Object[]{this});
        agentController.start();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        initContainer();
        HBox hBox = new HBox();
        hBox.setPadding(new Insets(20));
        hBox.setSpacing(10);
        Label label = new Label("title : ");
        TextField bookname = new TextField();
        Button buyBtn = new Button("Buy...");
        hBox.getChildren().addAll(label, bookname, buyBtn);
        VBox vBox = new VBox();
        stringObservableList = FXCollections.observableArrayList();
        ListView<String> stringListView = new ListView<>(stringObservableList);
        vBox.getChildren().add(stringListView);
        BorderPane borderPane = new BorderPane();
        borderPane.setTop(hBox);
        borderPane.setCenter(vBox);
        Scene scene = new Scene(borderPane, 600, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("consumateur");
        primaryStage.show();

        buyBtn.setOnMouseClicked(ev -> {
            String livre = bookname.getText();
            GuiEvent event = new GuiEvent(this, 1);
            event.addParameter(livre);
            consumerAgent.onGuiEvent(event);
        });
    }

    public void logMessage(ACLMessage message) {
        stringObservableList.add(message.getContent());
    }
}
