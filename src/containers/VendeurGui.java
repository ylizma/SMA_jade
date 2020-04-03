package containers;

import agents.AcheteurAgent;
import agents.VendeurAgent;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.lang.acl.ACLMessage;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import javax.xml.soap.Text;

public class VendeurGui extends Application {

    public VendeurAgent vendeurAgent;
    ListView<String> stringListView;
    ObservableList<String> stringObservableList;
    AgentContainer agentContainer;


    public static void main(String[] args) {
        launch(args);
    }

    void initContainer() throws Exception {
        Runtime runtime = Runtime.instance();
        ProfileImpl profile = new ProfileImpl();
        profile.setParameter(ProfileImpl.MAIN_HOST, "localhost");
        agentContainer = runtime.createAgentContainer(profile);
        agentContainer.start();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        initContainer();
        VBox vBox = new VBox();
        HBox hBox = new HBox(14);
        TextField agentName = new TextField();
        Button deploybtn = new Button("Deploy");
        hBox.getChildren().addAll(new Label("Agent Name"), agentName, deploybtn);
        stringObservableList = FXCollections.observableArrayList();
        stringListView = new ListView<>(stringObservableList);
        vBox.getChildren().add(stringListView);
        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(vBox);
        borderPane.setTop(hBox);
        Scene scene = new Scene(borderPane, 600, 600);
        primaryStage.setTitle("vendeur");
        primaryStage.setScene(scene);
        primaryStage.show();

        deploybtn.setOnMouseClicked(event -> {
            try {
                String name=agentName.getText();
                AgentController agentController = agentContainer.createNewAgent(name, "agents.VendeurAgent", new Object[]{this});
                agentController.start();
            } catch (StaleProxyException e) {
                e.printStackTrace();
            }

        });
    }

    public void logMessage(ACLMessage message) {
        stringObservableList.add(message.getContent());
    }
}
