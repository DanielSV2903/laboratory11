package controller;

import domain.AdjacencyMatrixGraph;
import domain.GraphException;
import domain.Vertex;
import domain.list.ListException;
import domain.list.Node;
import domain.list.SinglyLinkedList;
import domain.queue.QueueException;
import domain.stack.StackException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
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

public class SinglyLinkedListGraphController {

    @FXML
    private Pane graphPane;

    @FXML
    private Label label;

    @FXML
    private TextArea tArea;

    private SinglyLinkedList singlyLinkedList;
    private AdjacencyMatrixGraph graph;
    private List<Vertex> vertex;
    @FXML
    private BorderPane bp;


    @javafx.fxml.FXML
    public void initialize() {
        singlyLinkedList = new SinglyLinkedList();
        singlyLinkedList.add("Mahoma");
        singlyLinkedList.add("R. Isabel");
        singlyLinkedList.add("Marie Curie");
        singlyLinkedList.add("M. L. King");
        singlyLinkedList.add("Darwin");
        singlyLinkedList.add("Sócrates");
        singlyLinkedList.add("Abraham Lincoln");
        singlyLinkedList.add("M. Gandi");
        singlyLinkedList.add("J. César");
        singlyLinkedList.add("C. Colón");
    }

    @FXML
    void bfsTourOnAction(ActionEvent event) {
        tArea.clear();
        try {
            tArea.setText("BFS Tour order:\n"+graph.bfs());
        } catch (GraphException | ListException | QueueException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void containsEdgeOnAction(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        if (!graph.isEmpty()) {
            try {
                alert.setTitle("Contains Edge");
                alert.setHeaderText(null);
                TextField tfV1 = new TextField();
                TextField tfV2 = new TextField();
                GridPane gp = new GridPane();
                gp.add(new Label("Vertex 1"), 0, 0);
                gp.add(new Label("Vertex 2"), 0, 1);
                gp.add(tfV1, 1, 0);
                gp.add(tfV2, 1, 1);
                alert.getDialogPane().setContent(gp);
                alert.showAndWait();
                String v1 = tfV1.getText().trim();
                String v2 = tfV2.getText().trim();

                if (graph.containsEdge(v1, v2))
                    label.setText("Theres an edge between " + v1 +" and "+v2+ " in the graph");
                else
                    label.setText("Theres no edge between " + v1 + " and " + v2 + " in the graph");
            } catch (GraphException | ListException e) {
                throw new RuntimeException(e);
            }
        }else{
            alert.setAlertType(Alert.AlertType.ERROR);
            alert.setTitle("Is empty");
            alert.setHeaderText(null);
            alert.setContentText("Graph is empty");
            alert.showAndWait();
        }

    }

    @FXML
    void containsVertexOnAction(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        if (!graph.isEmpty()) {
            try {
                alert.setTitle("Contains Vertex");
                alert.setHeaderText(null);
                TextField tf = new TextField();
                GridPane gp = new GridPane();
                gp.add(new Label("Vertex"), 0, 0);
                gp.add(tf, 1, 0);
                alert.getDialogPane().setContent(gp);
                alert.showAndWait();
                String v1 = tf.getText().trim();
                label.setText(graph.containsVertex(v1) ? "Vertex " + v1 + " is in the graph" : "Vertex " + v1 + " is not in the graph");
            } catch (GraphException | ListException e) {
                throw new RuntimeException(e);
            }
        }else{
            alert.setAlertType(Alert.AlertType.ERROR);
            alert.setTitle("Is empty");
            alert.setHeaderText(null);
            alert.setContentText("Graph is empty");
            alert.showAndWait();
        }
    }

    @FXML
    void dfsTourOnAction(ActionEvent event) {
        tArea.clear();
        try {
            this.tArea.setText("DFS Tour order:\n"+graph.dfs());
        } catch (GraphException | StackException | ListException e) {
            throw new RuntimeException(e);
        }
    }

    private void clear(){
        vertex =new ArrayList<>();
        graph=new AdjacencyMatrixGraph(10);
    }

    @FXML
    void toStringOnAction(ActionEvent event) {
        tArea.clear();
        this.tArea.setText(graph.toString());
    }

    @FXML
    void randomizeOnAction(ActionEvent event) {
        clear();

        List<String> vertexNames = new ArrayList<>();
        try {
            for (int i = 1; i <= singlyLinkedList.size(); i++) {
                Object name = singlyLinkedList.getNode(i).data;
                vertexNames.add(name.toString());
            }
        } catch (ListException e) {
            label.setText("Error al obtener elementos de la lista: " + e.getMessage());
            return;
        }

        for (String name : vertexNames) {
            try {
                graph.addVertex(name);
            } catch (GraphException e) {
                throw new RuntimeException(e);
            } catch (ListException e) {
                throw new RuntimeException(e);
            }
        }

        int edgeQtty = Utility.random(20) + 1;

        try {
            for (int i = 0; i < edgeQtty; i++) {
                int weight = Utility.random(1000, 2000);
                int index1 = Utility.random(1, singlyLinkedList.size());
                int index2 = Utility.random(1, singlyLinkedList.size());

                Node node1 = singlyLinkedList.getNode(index1);
                Node node2 = singlyLinkedList.getNode(index2);
                if (node1 != null && node2 != null) {
                    String v1 = node1.data.toString();
                    String v2 = node2.data.toString();
                    graph.addEdgeWeight(v1, v2, weight);
                }

            }
        } catch (Exception e) {
            label.setText("Error al generar las conexiones: " + e.getMessage());
            System.out.println(e.getMessage());
        }

        int[][] matrix = new int[10][10];
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                matrix[i][j] = (int) graph.getAdjacencyMatrix()[i][j];
            }
        }

        drawGraph(vertexNames, matrix);
        tArea.setText(graph.toString());
    }

    private void drawGraph(List<String> vertexNames, int[][] matrix) {
        graphPane.getChildren().clear();

        int centerX = 300;
        int centerY = 300;
        int radius = 200;
        int n = vertexNames.size();

        Map<Integer, Circle> nodeMap = new HashMap<>();
        Map<Integer, String> dataMap = new HashMap<>();

        for (int i = 0; i < n; i++) {
            double angle = 2 * Math.PI * i / n;
            double x = centerX + radius * Math.cos(angle);
            double y = centerY + radius * Math.sin(angle);

            Circle circle = new Circle(x, y, 20, Color.AQUA);
            int finalI = i;
            circle.setOnMouseClicked(event -> {
                label.setText("Vertex "+ vertexNames.get(finalI)+" is on index "+finalI);
            });

            Text text = new Text(x - 15, y + 5, (String) vertexNames.get(i));

            nodeMap.put(i, circle);
            dataMap.put(i, vertexNames.get(i));
            graphPane.getChildren().addAll(circle, text);
        }

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (matrix[i][j] > 0 && i < j) {
                    Circle startVertex = nodeMap.get(i);
                    Circle endVertex = nodeMap.get(j);

                    Line edge = new Line(startVertex.getCenterX(),
                            startVertex.getCenterY()-10,
                            endVertex.getCenterX(),
                            endVertex.getCenterY()+10);
                    edge.setStrokeWidth(4);
                    edge.setStroke(Color.BLACK);

                    int finalI = i;
                    int finalJ = j;
                    edge.setOnMouseClicked(event -> {
                        String txt="Edege between "+dataMap.get(finalI)+" and "+dataMap.get(finalJ)+" weight: "+matrix[finalI][finalJ];
                        label.setText(txt);
                        edge.setStroke(Color.GREEN);
                        tArea.appendText(txt+"\n");
                    });

                    double x = (startVertex.getCenterX() + endVertex.getCenterX()) / 2;
                    double y = (startVertex.getCenterY() + endVertex.getCenterY()) / 2;
                    Text weight = new Text(x, y,
                            String.valueOf(matrix[i][j]));

                    graphPane.getChildren().add(edge);
                }
            }
        }

    }


}