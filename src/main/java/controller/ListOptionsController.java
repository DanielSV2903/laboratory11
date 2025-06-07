package controller;

import domain.AdjacencyListGraph;
import domain.GraphException;
import domain.list.ListException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.paint.Color;
import util.Utility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListOptionsController {
    @javafx.fxml.FXML
    private Canvas canvas;
    @javafx.fxml.FXML
    private TextArea txtArea;
    @javafx.fxml.FXML
    private Label labelEdges;

    private AdjacencyListGraph graph;
    private List<Map<String, Object>> drawnEdges;
    private List<Object> availableLetters;

    @FXML
    public void initialize() {
        graph=new AdjacencyListGraph(10);
        txtArea.clear();
        labelEdges.setText("");
        drawnEdges = new ArrayList<>();
        availableLetters = new ArrayList<>();
        for (char i = 'A'; i <= 'Z'; i++) {
            availableLetters.add(i);
        }
    }

    private void clear(){
        graph=new AdjacencyListGraph(10);
        txtArea.clear();
        labelEdges.setText("");
        drawnEdges = new ArrayList<>();
        availableLetters = new ArrayList<>();
        for (char i = 'A'; i <= 'Z'; i++) {
            availableLetters.add(i);
        }
    }

    private List<Object> getVertexData(){
        try {
            List<Object> vertices = new ArrayList<>();
            for (int i = 0; i < graph.size(); i++) {
                vertices.add(graph.getVertexList()[i].data); // Obtenemos los datos de cada vértice
            }
            return vertices;
        } catch (ListException e) {
            throw new RuntimeException(e);
        }
    }

    @javafx.fxml.FXML
    public void addVertexOnAction(ActionEvent actionEvent) {
        try {

            Object element = availableLetters.get(Utility.random(availableLetters.size()));
            graph.addVertex(element);
            availableLetters.remove(element);

            List<Object> vertices = getVertexData();

            drawGraph(vertices);

        } catch (GraphException | ListException e) {
            throw new RuntimeException(e);
        }
    }

    @javafx.fxml.FXML
    public void removeVertexOnAction(ActionEvent actionEvent) {
        try{
            Object element = graph.getVertexList()[Utility.random(graph.size())].data;
            graph.removeVertex(element);
            availableLetters.add(element);

            List<Object> vertices = getVertexData();

            drawGraph(vertices);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @javafx.fxml.FXML
    public void randomizeOnAction(ActionEvent actionEvent) {
        clear();
        try {
            // Llenar el grafo con 10 vértices aleatorios
            for (int i = 0; i < 10; i++) {
                Object element = availableLetters.get(Utility.random(availableLetters.size()));
                graph.addVertex(element);
                availableLetters.remove(element);
            }

            List<Object> vertices = getVertexData();

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
                            

                            // Si la arista no está ya registrada, la agregamos
                            if (drawnEdges.stream().noneMatch(edge -> edge.get("vertex1").equals(vertex1) && edge.get("vertex2").equals(vertex2))) {
                                // Guardamos las aristas dibujadas


                                Map<String, Object> edge = new HashMap<>();
                                edge.put("vertex1", vertex1);
                                edge.put("vertex2", vertex2);
                                edge.put("pos1", pos1);
                                edge.put("pos2", pos2);
                                edge.put("selected", false); // No seleccionada por defecto
                                drawnEdges.add(edge);
                            }

                            // Dibujar la arista (por defecto en negro o verde si está seleccionada)
                            Map<String, Object> edge = drawnEdges.stream()
                                    .filter(e -> e.get("vertex1").equals(vertex1) && e.get("vertex2").equals(vertex2))
                                    .findFirst()
                                    .orElse(null);

                            if (edge != null) {
                                // Si la arista está seleccionada, dibujarla en verde y aumentar el grosor
                                if ((Boolean) edge.get("selected")) {
                                    gc.setStroke(Color.RED);      // Color verde
                                    gc.setLineWidth(3.5);           // Grosor mayor para la arista seleccionada
                                    Object edgeWeight = edge.get("weight");
                                    labelEdges.setText("Edge between the vertexes: " + vertex1 + " and " + vertex2 + ": " + graph);
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
                }
            }

            canvas.setOnMouseClicked(event -> {
                double x = event.getX();
                double y = event.getY();

                // Primero, desmarcamos todas las aristas
                for (Map<String, Object> edge : drawnEdges) {
                    edge.put("selected", false);  // Restablecemos el estado de todas las aristas
                }

                drawGraph(vertices);

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
    public void clearOnAction(ActionEvent actionEvent) {
        clear();
        drawGraph(new ArrayList<>());
    }

    @javafx.fxml.FXML
    public void addEdgeNWeightOnAction(ActionEvent actionEvent) {
        try {
            List<Object> vertices = getVertexData();

            boolean added = false;
            while (!added) {
                int weight = util.Utility.random(50) + 1;
                Object v1 = vertices.get(Utility.random(vertices.size()));  // Vértice 1 aleatorio
                Object v2 = vertices.get(Utility.random(vertices.size()));  // Vértice 2 aleatorio

                if (!graph.containsEdge(v1, v2)) {
                    graph.addEdgeWeight(v1, v2, weight);
                    added = true;
                }
            }
            drawGraph(vertices);

        } catch (GraphException | ListException e) {
            throw new RuntimeException(e);
        }
    }

    @javafx.fxml.FXML
    public void removeEdgeNWeightOnAction(ActionEvent actionEvent) {
        try {
            List<Object> vertices = getVertexData();

            boolean erased = false;
            while (!erased) {
                Object v1 = vertices.get(Utility.random(vertices.size()));  // Vértice 1 aleatorio
                Object v2 = vertices.get(Utility.random(vertices.size()));  // Vértice 2 aleatorio
                if (graph.containsEdge(v1, v2)) {
                    graph.removeEdge(v1, v2);
                    erased = true;
                }
            }
            drawGraph(vertices);

        } catch (GraphException | ListException e) {
            throw new RuntimeException(e);
        }
    }
}
