package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import ucr.lab.HelloApplication;

import java.io.IOException;

public class HelloController {

    @FXML
    private BorderPane bp;
    @FXML
    private AnchorPane ap;
    @FXML
    private Text txtMessage;

    private void loadPage(String page) {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource(page));
        try {
            this.bp.setCenter(fxmlLoader.load());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void Exit(ActionEvent event) {
        System.exit(0);
    }

    @FXML
    void Home(ActionEvent event) {
        this.txtMessage.setText("Laboratory No. 11");
        this.bp.setCenter(ap);
    }

    @FXML
    public void matrixOperationsOnAction(ActionEvent actionEvent) {loadPage("matrixGraphOperations.fxml");
    }

    @FXML
    public void matrixGraphOnAction(ActionEvent actionEvent) {loadPage("matrixGraph.fxml");
    }

    @FXML
    public void listGraphOnAction(ActionEvent actionEvent) {loadPage("adjacency-list-graph.fxml");}

    @FXML
    public void listOperationsOnAction(ActionEvent actionEvent) {
        loadPage("list-graph-ops.fxml");
    }

    @FXML
    public void linkedGraphOnAction(ActionEvent actionEvent) { loadPage("singlyLinkedListGraph.fxml");
    }

    @FXML
    public void linkedListOperationsOnAction(ActionEvent actionEvent) {loadPage("singlyLinkedListGraphOperations.fxml");
    }
}