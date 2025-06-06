package controller;


import domain.AdjacencyMatrixGraph;
import domain.GraphException;
import domain.Vertex;
import domain.list.ListException;
import domain.queue.QueueException;
import domain.stack.StackException;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import util.Utility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class MatrixGraphOperationsController
{

    @javafx.fxml.FXML
    private TextArea tArea;
    @javafx.fxml.FXML
    private Label label;
    @javafx.fxml.FXML
    private BorderPane bp;
    @javafx.fxml.FXML
    private Pane graphPane;

    @javafx.fxml.FXML
    public void initialize() {
    }

    @javafx.fxml.FXML
    public void clearOnAction(ActionEvent actionEvent) {
    }

    @javafx.fxml.FXML
    public void addEWOnAction(ActionEvent actionEvent) {
    }

    @javafx.fxml.FXML
    public void addVertexOnAction(ActionEvent actionEvent) {
    }

    @javafx.fxml.FXML
    public void removeVertexOnAction(ActionEvent actionEvent) {
    }

    @javafx.fxml.FXML
    public void removeEWOnAction(ActionEvent actionEvent) {
    }

    @javafx.fxml.FXML
    public void randomOnAction(ActionEvent actionEvent) {
    }
}