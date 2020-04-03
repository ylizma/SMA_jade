package containers;

import agents.AcheteurAgent;
import jade.core.ProfileImpl;
import jade.core.Runtime;
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

public class AcheteurGui extends Application {

    public AcheteurAgent acheteurAgent;
    ListView<String> stringListView ;
    ObservableList<String> stringObservableList;

    public static void main(String[] args) {
        launch(args);
    }

    void initContainer() throws Exception {
        Runtime runtime = Runtime.instance();
        ProfileImpl profile = new ProfileImpl();
        profile.setParameter(ProfileImpl.MAIN_HOST, "localhost");
        AgentContainer agentContainer = runtime.createAgentContainer(profile);
        AgentController agentController = agentContainer.createNewAgent("acheteur", "agents.AcheteurAgent", new Object[]{this});
        agentController.start();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        initContainer();
        VBox vBox=new VBox();
        stringObservableList = FXCollections.observableArrayList();
        stringListView = new ListView<>(stringObservableList);
        vBox.getChildren().add(stringListView);
        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(vBox);
        Scene scene = new Scene(borderPane, 600, 600);
        primaryStage.setTitle("Acheteur");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void logMessage(ACLMessage message){
        stringObservableList.add(message.getContent());
    }
}
