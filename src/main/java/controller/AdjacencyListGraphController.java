package controller;

import domain.AdjacencyListGraph;
import domain.GraphException;
import domain.list.ListException;
import domain.queue.QueueException;
import domain.stack.StackException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import util.FXUtility;
import util.Utility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdjacencyListGraphController {
    @javafx.fxml.FXML
    private Canvas canvas;
    @javafx.fxml.FXML
    private TextArea txtArea;
    @javafx.fxml.FXML
    private Label labelEdges;

    private AdjacencyListGraph graph;

    @FXML
    public void initialize() {
        graph = new AdjacencyListGraph(10);
    }
    private void clear(){
        graph=new AdjacencyListGraph(10);
        txtArea.clear();
        labelEdges.setText("");
    }
    @javafx.fxml.FXML
    public void toStringOnAction(ActionEvent actionEvent) {
        txtArea.setText(graph.toString());
    }

    @javafx.fxml.FXML
    public void dfsTourOnAction(ActionEvent actionEvent) {
        txtArea.clear();
        try {
            txtArea.setText(graph.dfs());
        } catch (GraphException | ListException | StackException e) {
            throw new RuntimeException(e);
        }
    }

    @javafx.fxml.FXML
    public void randomizeOnAction(ActionEvent actionEvent) {
        clear();
        try {

            // Creamos una lista con todas las letras disponibles
            List<Character> availableLetters = new ArrayList<>();
            for (char i = 'A'; i <= 'Z'; i++) {
                availableLetters.add(i);
            }

            // Creamos 10 vértices aleatorios sin repetición
            int verticesNeeded = 10;
            while (verticesNeeded > 0 && !availableLetters.isEmpty()) {
                // Seleccionamos un índice aleatorio de las letras disponibles
                int randomIndex = Utility.random(availableLetters.size() - 1);
                char selectedLetter = availableLetters.get(randomIndex);

                try {
                    // Añadimos el vértice al grafo
                    graph.addVertex(selectedLetter);

                    // Removemos la letra usada de las disponibles
                    availableLetters.remove(randomIndex);
                    verticesNeeded--;

                } catch (GraphException e) {
                    // Si hay algún error al añadir el vértice, continuamos con el siguiente
                    e.printStackTrace();
                }
            }

            drawGraph();

        } catch (ListException e) {
            throw new RuntimeException(e);
        }
    }

    private void drawGraph() throws ListException {
        var gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        double centerX = canvas.getWidth() / 2;
        double centerY = canvas.getHeight() / 2;
        double radius = Math.min(centerX, centerY) - 50;
        double vertexRadius = 20;

        try {
            if (graph.isEmpty()) {
                return;
            }

            List<Object> vertices = new ArrayList<>();
            Map<Object, double[]> vertexPositions = new HashMap<>();

            // Recolectar vértices existentes usando toString()
            String graphContent = graph.toString();
            String[] lines = graphContent.split("\n");
            for (String line : lines) {
                if (line.contains("The vextex in the position:")) {
                    String[] parts = line.split("is: ");
                    if (parts.length > 1) {
                        Object vertex = parts[1].trim();
                        vertices.add(vertex);
                    }
                }
            }

            // Crear aristas aleatorias
            if (vertices.size() >= 2) {
                // Limpiamos las aristas existentes recreando el grafo
                AdjacencyListGraph newGraph = new AdjacencyListGraph(10);
                for (Object vertex : vertices) {
                    newGraph.addVertex(vertex);
                }
                graph = newGraph;

                // Calculamos el número de aristas a crear (entre 5 y 10, o el máximo posible)
                int totalEdges = Utility.random(5, 10);

                // Creamos las aristas aleatorias
                int edgesCreated = 0;
                int attempts = 0;
                int maxAttempts = 100; // Para evitar bucles infinitos

                while (edgesCreated < totalEdges && attempts < maxAttempts) {
                    int index1 = Utility.random(vertices.size());
                    int index2 = Utility.random(vertices.size());

                    Object vertex1 = vertices.get(index1);
                    Object vertex2 = vertices.get(index2);

                    if (!vertex1.equals(vertex2)) {
                        try {
                            if (!graph.containsEdge(vertex1, vertex2)) {
                                graph.addEdgeWeight(vertex1, vertex2, Utility.random(1, 50));
                                edgesCreated++;
                            }
                        } catch (GraphException e) {
                            // Ignorar errores y continuar
                        }
                    }
                    attempts++;
                }
            }

            // Dibujar los vértices en formación circular
            for (int i = 0; i < vertices.size(); i++) {
                Object vertex = vertices.get(i);
                double angle = 2 * Math.PI * i / vertices.size();
                double x = centerX + radius * Math.cos(angle);
                double y = centerY + radius * Math.sin(angle);

                vertexPositions.put(vertex, new double[]{x, y});

                // Dibujar círculo
                gc.setFill(Color.WHITE);
                gc.setStroke(Color.BLACK);
                gc.strokeOval(x - vertexRadius, y - vertexRadius, vertexRadius * 2, vertexRadius * 2);
                gc.fillOval(x - vertexRadius, y - vertexRadius, vertexRadius * 2, vertexRadius * 2);

                // Dibujar etiqueta del vértice
                gc.setFill(Color.RED);
                gc.fillText(vertex.toString(), x - 5, y + 5);
            }

            // Dibujar las aristas
            for (Object vertex1 : vertices) {
                for (Object vertex2 : vertices) {
                    try {
                        if (!vertex1.equals(vertex2) && graph.containsEdge(vertex1, vertex2)) {
                            double[] pos1 = vertexPositions.get(vertex1);
                            double[] pos2 = vertexPositions.get(vertex2);

                            // Dibujar línea entre vértices
                            gc.setStroke(Color.BLACK);
                            gc.setLineWidth(1.5);
                            gc.strokeLine(pos1[0], pos1[1], pos2[0], pos2[1]);
                        }
                    } catch (GraphException e) {
                        // Ignorar errores y continuar
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @javafx.fxml.FXML
    public void bfsTourOnAction(ActionEvent actionEvent) {
        txtArea.clear();
        try {
            txtArea.setText(graph.bfs());
        } catch (GraphException | QueueException | ListException e) {
            throw new RuntimeException(e);
        }
    }

    @javafx.fxml.FXML
    public void containsVertexOnAction(ActionEvent actionEvent) {
        try {
            TextInputDialog txt = FXUtility.dialog("Contains Vertex", "Ingrese el valor a comprobar");
            txt.showAndWait();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);

            if (txt.getResult() != null) {
                String result = txt.getResult();
                if (graph.containsVertex(result)) {
                    alert.setTitle("Contains Vertex");
                    alert.setHeaderText("El vértice si se encuentra en el grafo");
                } else {
                    alert.setAlertType(Alert.AlertType.ERROR);
                    alert.setTitle("Vertex not found");
                    alert.setHeaderText("No se ha podido encontrar el vértice en el grafo");
                }
                alert.showAndWait();
            }

        } catch (GraphException | ListException e) {
            throw new RuntimeException(e);
        }
    }

    @javafx.fxml.FXML
    public void containsEdgeOnAction(ActionEvent actionEvent) {
        try {
            TextInputDialog txt = FXUtility.dialog("Contains Edge", "Ingrese el valor a comprobar");
            TextField tfV1 = new TextField();
            TextField tfV2 = new TextField();
            GridPane gp = new GridPane();
            gp.add(new Label("Vertex 1"), 0, 0);
            gp.add(new Label("Vertex 2"), 0, 1);
            gp.add(tfV1, 1, 0);
            gp.add(tfV2, 1, 1);
            txt.getDialogPane().setContent(gp);
            txt.showAndWait();

            String v1 = tfV1.getText().trim();
            String v2 = tfV2.getText().trim();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);

            if (!v1.isBlank() && !v2.isBlank()) {
                if (graph.containsEdge(v1, v2)) {
                    alert.setTitle("Contains Edge");
                    alert.setHeaderText("La arista si se encuentra entre los vértices " + v1 + " y " + v2 + " en el grafo" );
                } else {
                    alert.setAlertType(Alert.AlertType.ERROR);
                    alert.setTitle("Edge not found");
                    alert.setHeaderText("No se ha podido encontrar la arista en el grafo");
                }
                alert.showAndWait();
            } else {
                alert.setAlertType(Alert.AlertType.ERROR);
                alert.setTitle("Invalid data");
                alert.setHeaderText("Entrada inválida aségurese de haber llenado ambas casillas");
            }

        } catch (GraphException | ListException e) {
            throw new RuntimeException(e);
        }
    }
}