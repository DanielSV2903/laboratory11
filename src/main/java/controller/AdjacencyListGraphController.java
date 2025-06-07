package controller;

import domain.AdjacencyListGraph;
import domain.EdgeWeight;
import domain.GraphException;
import domain.list.ListException;
import domain.list.SinglyLinkedList;
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
    private List<Map<String, Object>> drawnEdges = new ArrayList<>();

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

            // Llenar el grafo con 10 vértices aleatorios
            for (int i = 0; i < 10; i++) {
                graph.addVertex(availableLetters.get(util.Utility.random(availableLetters.size() - 1)));
            }

            List<Object> vertices = new ArrayList<>();
            for (int i = 0; i < graph.size(); i++) {
                vertices.add(graph.getVertexList()[i].data); // Obtenemos los datos de cada vértice
            }

            // Crear aristas aleatorias
            int maxEdges = util.Utility.random(5, 10);
            for (int i = 0; i < maxEdges; i++) {
                int weight = util.Utility.random(50) + 1;
                Object v1 = vertices.get(Utility.random(vertices.size()));  // Vértice 1 aleatorio
                Object v2 = vertices.get(Utility.random(vertices.size()));  // Vértice 2 aleatorio
                graph.addEdgeWeight(v1, v2, weight);  // Crear la arista entre los dos vértices
            }

            // Pasar los vértices y la matriz de adyacencia a drawGraph
            drawGraph(vertices);

        } catch (ListException | GraphException e) {
            throw new RuntimeException(e);
        }
    }

    private void drawGraph(List<Object> vertices) {
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

            Map<Object, double[]> vertexPositions = new HashMap<>();

            // Recolectar las posiciones de los vértices
            for (int i = 0; i < vertices.size(); i++) {
                Object vertex = vertices.get(i);

                // Obtener la lista de aristas del vértice
                SinglyLinkedList vertexEdges = graph.getVertexList()[i].edgesList;

                double angle = 2 * Math.PI * i / vertices.size();
                double x = centerX + radius * Math.cos(angle);
                double y = centerY + radius * Math.sin(angle);

                vertexPositions.put(vertex, new double[]{x, y});

                // Dibujar el círculo del vértice
                gc.setFill(Color.WHITE);
                gc.setStroke(Color.BLACK);
                gc.strokeOval(x - vertexRadius, y - vertexRadius, vertexRadius * 2, vertexRadius * 2);
                gc.fillOval(x - vertexRadius, y - vertexRadius, vertexRadius * 2, vertexRadius * 2);

                // Dibujar la etiqueta del vértice
                gc.setFill(Color.RED);
                gc.fillText(vertex.toString(), x - 5, y + 5);
            }

            // Dibujar las aristas y almacenarlas solo una vez
            for (Object vertex1 : vertices) {
                for (Object vertex2 : vertices) {
                    try {
                        if (!vertex1.equals(vertex2) && graph.containsEdge(vertex1, vertex2)) {
                            double[] pos1 = vertexPositions.get(vertex1);
                            double[] pos2 = vertexPositions.get(vertex2);

                            // Obtener el peso de la arista
                            Object weight = new Object();
                            SinglyLinkedList vertexEdges = graph.getVertexList()[vertices.indexOf(vertex1)].edgesList;
                            for (int i = 0; i < vertexEdges.size(); i++) {
                                // Comprobar si el nodo en la posición 'i' es nulo
                                if (vertexEdges.getNode(i) != null) {
                                    EdgeWeight edgeWeight = (EdgeWeight) vertexEdges.getNode(i).data;
                                    if (Utility.compare(edgeWeight.getEdge(), vertex2) == 0) {
                                        weight = edgeWeight.getWeight();  // Asignar el peso a la variable
                                    }
                                }
                            }

                            // Si la arista no está ya registrada, la agregamos
                            if (drawnEdges.stream().noneMatch(edge -> edge.get("vertex1").equals(vertex1) && edge.get("vertex2").equals(vertex2))) {
                                // Guardamos las aristas dibujadas
                                Map<String, Object> edge = new HashMap<>();
                                edge.put("vertex1", vertex1);
                                edge.put("vertex2", vertex2);
                                edge.put("pos1", pos1);
                                edge.put("pos2", pos2);
                                edge.put("selected", false); // No seleccionada por defecto
                                edge.put("weight", weight);  // Guardamos el peso de la arista
                                drawnEdges.add(edge);
                            }

                            // Dibujar la arista (por defecto en negro o rojo si está seleccionada)
                            Map<String, Object> edge = drawnEdges.stream()
                                    .filter(e -> e.get("vertex1").equals(vertex1) && e.get("vertex2").equals(vertex2))
                                    .findFirst()
                                    .orElse(null);

                            if (edge != null) {
                                // Si la arista está seleccionada, dibujarla en rojo y aumentar el grosor
                                if ((Boolean) edge.get("selected")) {
                                    gc.setStroke(Color.RED);      // Color rojo
                                    gc.setLineWidth(3.5);           // Grosor mayor para la arista seleccionada
                                    labelEdges.setText("Edge between the vertexes: " + vertex1 + " and " + vertex2 + ". Weight: " + edge.get("weight"));
                                } else {
                                    gc.setStroke(Color.BLACK);      // Color negro para las no seleccionadas
                                    gc.setLineWidth(1.5);           // Grosor normal para las no seleccionadas
                                }
                                gc.strokeLine(pos1[0], pos1[1], pos2[0], pos2[1]);  // Dibuja la línea
                            }
                        }
                    } catch (GraphException e) {
                        // Ignorar errores
                    }
                    txtArea.setText(graph.toString());
                }
            }

            // Evento de clic en el canvas
            canvas.setOnMouseClicked(event -> {
                double x = event.getX();
                double y = event.getY();

                // Primero, desmarcamos todas las aristas
                for (Map<String, Object> edge : drawnEdges) {
                    edge.put("selected", false);  // Restablecemos el estado de todas las aristas
                }

                // Verificar si el clic está cerca de alguna arista
                for (Map<String, Object> edge : drawnEdges) {
                    double[] pos1 = (double[]) edge.get("pos1");
                    double[] pos2 = (double[]) edge.get("pos2");

                    if (isPointNearLine(x, y, pos1[0], pos1[1], pos2[0], pos2[1])) {
                        edge.put("selected", true);  // Marca la arista como seleccionada
                        break;  // Solo seleccionamos la primera arista encontrada
                    }
                }

                // Redibujar el gráfico para aplicar el cambio de color
                drawGraph(vertices);
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private boolean isPointNearLine(double px, double py, double x1, double y1, double x2, double y2) {
        final double tolerance = 5.0; // Tolerancia de distancia para seleccionar la línea
        double dx = x2 - x1;
        double dy = y2 - y1;

        if (dx == 0 && dy == 0) {
            return Math.hypot(px - x1, py - y1) <= tolerance;
        }

        double t = ((px - x1) * dx + (py - y1) * dy) / (dx * dx + dy * dy);
        t = Math.max(0, Math.min(1, t));

        double closestX = x1 + t * dx;
        double closestY = y1 + t * dy;

        return Math.hypot(px - closestX, py - closestY) <= tolerance;
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