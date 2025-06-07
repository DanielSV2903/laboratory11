package controller;

import domain.EdgeWeight;
import domain.GraphException;
import domain.SinglyLinkedListGraph;
import domain.Vertex;
import domain.list.ListException;
import domain.list.SinglyLinkedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import util.Utility;

import java.util.*;

public class SinglyLinkedListGraphOperationsController {
    @FXML private TextArea tArea;
    @FXML private Label label;
    @FXML private BorderPane bp;
    @FXML private Pane graphPane;

    private SinglyLinkedListGraph graph;
    private SinglyLinkedList names;
    private List<String> vertexNames;
    private Map<Integer, Integer> edges;
    private final String[] historicalFigures = {
            "Mahoma", "R. Isabel", "Marie Curie", "M. L. King", "Darwin",
            "Sócrates", "Abraham Lincoln", "M. Gandi", "J. César", "C. Colón",
            "A. Einstein", "I. Newton", "Galileo", "N. Mandela", "W. Churchill",
            "B. Franklin", "L. Da Vinci", "W. Shakespeare", "P. Picasso", "L. Pasteur"
    };

    @FXML
    public void initialize() {
        graph = new SinglyLinkedListGraph();
        names = new SinglyLinkedList();
        vertexNames = new ArrayList<>();
        edges = new HashMap<>();
    }

    @FXML
    public void clearOnAction(ActionEvent e) {
        try {
            graph.clear();
            names = new SinglyLinkedList();
            vertexNames.clear();
            graphPane.getChildren().clear();
            tArea.clear();
            edges.clear();
            label.setText("Graph reset.");
        } catch (Exception ex) {
            label.setText("Error clearing: " + ex.getMessage());
        }
    }

    @FXML
    public void randomizeOnAction(ActionEvent e) {
        clearOnAction(e);
        try {
            Set<String> used = new HashSet<>();
            while (vertexNames.size() < 10) {
                String name = historicalFigures[Utility.random(historicalFigures.length - 1)];
                if (used.add(name)) {
                    graph.addVertex(name);
                    names.add(name);
                    vertexNames.add(name);
                }
            }
            // Agrega aristas aleatorias
            for (int i = 0; i < 15; i++) {
                int i1 = Utility.random(vertexNames.size());
                int i2 = Utility.random(vertexNames.size());
                if (i1 != i2 && !graph.containsEdge(vertexNames.get(i1), vertexNames.get(i2))) {
                    int w = Utility.random(100, 500);
                    edges.put(i1,w);
                    graph.addEdgeWeight(vertexNames.get(i1), vertexNames.get(i2), w);
                }
            }
            drawGraph();
            tArea.setText(graph.toString());
        } catch (Exception ex) {
            label.setText("Error randomizing: " + ex.getMessage());
        }
    }

    @FXML
    public void addVertexOnAction(ActionEvent e) {
        try {
            for (String name : historicalFigures) {
                if (!vertexNames.contains(name)) {
                    graph.addVertex(name);
                    names.add(name);
                    vertexNames.add(name);
                    label.setText("Added vertex: " + name);
                    drawGraph();
                    return;
                }
            }
            label.setText("No more historical figures available.");
        } catch (Exception ex) {
            label.setText("Error adding vertex: " + ex.getMessage());
        }
    }

    @FXML
    public void removeVertexOnAction(ActionEvent e) {
        try {
            if (vertexNames.isEmpty()) {
                label.setText("No vertices to remove.");
                return;
            }
            String name = vertexNames.remove(Utility.random(vertexNames.size()));
            graph.removeVertex(name);
            label.setText("Removed vertex: " + name);
            drawGraph();
        } catch (Exception ex) {
            label.setText("Error removing vertex: " + ex.getMessage());
        }
    }

    @FXML
    public void addEWOnAction(ActionEvent e) {
        try {
            for (int i = 0; i < 10; i++) {
                int i1 = Utility.random(vertexNames.size());
                int i2 = Utility.random(vertexNames.size());
                if (i1 != i2 && !graph.containsEdge(vertexNames.get(i1), vertexNames.get(i2))) {
                    int w = Utility.random(100, 500);
                    graph.addEdgeWeight(vertexNames.get(i1), vertexNames.get(i2), w);
                    label.setText("Added edge: " + vertexNames.get(i1) + " - " + vertexNames.get(i2));
                    drawGraph();
                    return;
                }
            }
            label.setText("No new edge could be added.");
        } catch (Exception ex) {
            label.setText("Error adding edge: " + ex.getMessage());
        }
    }

    @FXML
    public void removeEWOnAction(ActionEvent e) {
        try {
            for (int i = 0; i < vertexNames.size(); i++) {
                for (int j = i + 1; j < vertexNames.size(); j++) {
                    if (graph.containsEdge(vertexNames.get(i), vertexNames.get(j))) {
                        graph.removeEdge(vertexNames.get(i), vertexNames.get(j));
                        label.setText("Removed edge: " + vertexNames.get(i) + " - " + vertexNames.get(j));
                        drawGraph();
                        return;
                    }
                }
            }
            label.setText("No edge found to remove.");
        } catch (Exception ex) {
            label.setText("Error removing edge: " + ex.getMessage());
        }
    }

    private void drawGraph() throws ListException {
        try {
        graphPane.getChildren().clear();
        int centerX = 300, centerY = 300, radius = 200;
        int n = vertexNames.size();
        Map<String, Circle> circles = new HashMap<>();
        Map<String, Double[]> coords = new HashMap<>();
        Map<Integer, String> dataMap = new HashMap<>();

        for (int i = 0; i < n; i++) {
            double angle = 2 * Math.PI * i / n;
            double x = centerX + radius * Math.cos(angle);
            double y = centerY + radius * Math.sin(angle);

            Circle c = new Circle(x, y, 20, Color.AQUA);
            int finalI = i;
            c.setOnMouseClicked(event -> {
                label.setText("Vertex "+vertexNames.get(finalI)+" is on index "+finalI);
            });
            Text t = new Text(x - 15, y + 5, vertexNames.get(i));

            circles.put(vertexNames.get(i), c);
            dataMap.put(i, vertexNames.get(i));
            coords.put(vertexNames.get(i), new Double[]{x, y});

            graphPane.getChildren().addAll(c, t);
        }

        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                String v1 = vertexNames.get(i);
                String v2 = vertexNames.get(j);
                if (graph.containsEdge(v1, v2)) {
                    Double[] p1 = coords.get(v1);
                    Double[] p2 = coords.get(v2);
                    Line line = new Line(p1[0], p1[1], p2[0], p2[1]);
                    line.setStrokeWidth(3);
                    int finalI = i;
                    int finalJ = j;
                    line.setOnMouseClicked(event -> {
                        String txt = "Edge between " + dataMap.get(finalI) + " and " + dataMap.get(finalJ) + " weight: "+edges.get(finalI) ;
                        label.setText(txt);
                        line.setStroke(Color.GREEN);
                        tArea.appendText(txt + "\n");
                    });

                    graphPane.getChildren().addAll(line);
                }
            }
        }
        } catch (GraphException e) {
            throw new RuntimeException(e);
        }
    }
}