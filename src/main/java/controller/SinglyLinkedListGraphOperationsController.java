package controller;

import domain.AdjacencyMatrixGraph;
import domain.GraphException;
import domain.Vertex;
import domain.list.ListException;
import domain.list.Node;
import domain.list.SinglyLinkedList;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
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

public class SinglyLinkedListGraphOperationsController
{
    @javafx.fxml.FXML
    private TextArea tArea;
    @javafx.fxml.FXML
    private Label label;
    @javafx.fxml.FXML
    private BorderPane bp;
    @javafx.fxml.FXML
    private Pane graphPane;
    private SinglyLinkedList singlyLinkedList;
    private AdjacencyMatrixGraph graph;
    private List<Vertex> vertex;
    private int sLLSize;
    private int firstSLLSize;
    private String[] historicalFigures;
    private int [][] matrix;
    private List<String> vertexNames;

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

        try {
            sLLSize = singlyLinkedList.size();
            firstSLLSize = sLLSize;

        } catch (ListException e) {
            throw new RuntimeException(e);
        }

        historicalFigures = new String[]{
                "A. Einstein", "I. Newton", "Galileo", "N. Mandela", "W. Churchill",
                "B. Franklin", "L. Da Vinci", "W. Shakespeare", "P. Picasso", "L. Pasteur",
                "S. Freud", "J. F. Kennedy", "A. Fleming", "A. Magno", "Mozart",
                "Platón", "Cleopatra", "Beethoven", "A. Bell", "H. Tubman",
                "J. Washington", "R. Descartes", "Florence N.", "S. Jobs", "A. Tesla"
        };

    }

    @javafx.fxml.FXML
    public void clearOnAction(ActionEvent actionEvent) {
        graphPane.getChildren().clear();
        tArea.clear();
        label.setText("");

        vertexNames.clear();
        for (int i = 0; i < firstSLLSize; i++) {
            vertexNames.add("Vertex " + (i + 1));
        }


        sLLSize = firstSLLSize;
        matrix = new int[firstSLLSize][firstSLLSize];
        for (int i = 0; i < firstSLLSize; i++) {
            for (int j = 0; j < firstSLLSize; j++) {
                matrix[i][j] = 0;
            }
        }

        tArea.setText("Graph has reset");
    }

    @javafx.fxml.FXML
    public void addEWOnAction(ActionEvent actionEvent) {
        try {
            boolean added = false;

            for (int i = 0; i < sLLSize; i++) {
                for (int j = i + 1; j < sLLSize; j++) {
                    if (matrix[i][j] == 0) {
                        int edgeWeight = Utility.random(1000, 2000);

                        //crear arista
                        matrix[i][j] = edgeWeight;
                        matrix[j][i] = edgeWeight;

                        String vertex1 = vertexNames.get(i);
                        String vertex2 = vertexNames.get(j);

                        drawGraph(vertexNames, matrix);

                        label.setText("Added edge between " + vertex1 + " and " + vertex2 + " with weight: " + edgeWeight);

                        added = true;
                        break;
                    }
                }

                if (added) break;
            }

            if (!added) {
                label.setText("No available spots to add an edge. All edges are filled.");
            }

        } catch (Exception e) {
            label.setText("Error adding edge: " + e.getMessage());
        }
    }

    @javafx.fxml.FXML
    public void addVertexOnAction(ActionEvent actionEvent) {
        try {
            List<String> available = new ArrayList<>();
            for (String name : historicalFigures) {
                if (graph == null || !graph.containsVertex(name)) {
                    available.add(name);
                }
            }

            String name = available.get(Utility.random(available.size() - 1));
            sLLSize++;
            AdjacencyMatrixGraph newGraph = new AdjacencyMatrixGraph(sLLSize);

            //copiar vertices
            if (graph != null) {
                for (int i = 0; i < graph.size(); i++) {
                    String existingName = graph.getVertexList()[i].data.toString();
                    newGraph.addVertex(existingName);
                }

                //copiar aristas
                Object[][] oldMatrix = graph.getAdjacencyMatrix();
                Vertex[] oldVertices = graph.getVertexList();
                int oldSize = graph.size();

                for (int i = 0; i < oldSize; i++) {
                    for (int j = 0; j < oldSize; j++) {
                        if (!oldMatrix[i][j].equals(0)) {
                            String v1 = oldVertices[i].data.toString();
                            String v2 = oldVertices[j].data.toString();
                            Object weight = oldMatrix[i][j];
                            newGraph.addEdgeWeight(v1, v2, weight);
                        }
                    }
                }
            }

            newGraph.addVertex(name);
            singlyLinkedList.add(name);
            vertexNames.add(name);
            graph = newGraph;

            matrix = new int[sLLSize][sLLSize];
            for (int i = 0; i < sLLSize; i++) {
                for (int j = 0; j < sLLSize; j++) {
                    matrix[i][j] = (int) graph.getAdjacencyMatrix()[i][j];
                }
            }

            drawGraph(vertexNames, matrix);
            tArea.setText(graph.toString());

        } catch (GraphException | ListException e) {
            label.setText("Error: " + e.getMessage());
        }
    }


    @javafx.fxml.FXML
    public void removeVertexOnAction(ActionEvent actionEvent) {
        try {
            int index = Utility.random(sLLSize - 1);
            graph.removeVertex(index);
            for (String name : vertexNames) {
                if (graph.containsVertex(name)) {
                    vertexNames.remove(name);
                    break;
                }
            }

            drawGraph(vertexNames, matrix);

        } catch (GraphException | ListException e) {
            label.setText("Error: " + e.getMessage());
        }
    }

    @javafx.fxml.FXML
    public void removeEWOnAction(ActionEvent actionEvent) {
        try {
            boolean found = false;

            for (int i = 0; i < sLLSize; i++) {
                for (int j = i + 1; j < sLLSize; j++) {
                    if (matrix[i][j] > 0) {
                        //eliminar aristas
                        matrix[i][j] = 0;
                        matrix[j][i] = 0;

                        String vertex1 = vertexNames.get(i);
                        String vertex2 = vertexNames.get(j);

                        drawGraph(vertexNames, matrix);

                        vertexNames.add(vertex1);
                        vertexNames.add(vertex2);

                        label.setText("Removed edge between " + vertex1 + " and " + vertex2);

                        found = true;
                        break;
                    }
                }

                if (found) break;
            }

            if (!found) {
                label.setText("No edges to remove. The graph is empty.");
            }

        } catch (Exception e) {
            label.setText("Error removing edge: " + e.getMessage());
        }
    }

    private void clear(){
        vertex =new ArrayList<>();
        graph=new AdjacencyMatrixGraph(sLLSize);
    }

    @javafx.fxml.FXML
    public void randomizeOnAction(ActionEvent actionEvent) throws ListException {
        clear();
        vertexNames = new ArrayList<>();
        try {
            for (int i = 1; i <= singlyLinkedList.size(); i++) {
                Object name = singlyLinkedList.getNode(i).data;
                vertexNames.add(name.toString());
            }
        } catch (ListException e) {
            label.setText("Error al obtener elementos de la lista: " + e.getMessage());
            return;
        }

        // AGREGAR VÉRTICES AL GRAFO
        for (String name : vertexNames) {
            try {
                graph.addVertex(name);
            } catch (GraphException e) {
                throw new RuntimeException(e);
            } catch (ListException e) {
                throw new RuntimeException(e);
            }
        }

        // Agregar conexiones (aristas) aleatorias
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

        // Copiar matriz
        matrix = new int[sLLSize][sLLSize];
        for (int i = 0; i < sLLSize; i++) {
            for (int j = 0; j < sLLSize; j++) {
                matrix[i][j] = (int) graph.getAdjacencyMatrix()[i][j];
            }
        }

        drawGraph(vertexNames, matrix);
        tArea.setText(graph.toString());
    }


    private void drawGraph(List<String> vertexNames, int[][] matrix) {
        graphPane.getChildren().clear();

        // Crear círculos para cada vértice
        int centerX = 300;
        int centerY = 300;
        int radius = 200;
        int n = vertexNames.size();

        Map<Integer, Circle> nodeMap = new HashMap<>();//uso un mapa para mayor eficiencia
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

            // Agregar el nombre del vértice como texto
            Text text = new Text(x - 15, y + 5, (String) vertexNames.get(i));

            nodeMap.put(i, circle);
            dataMap.put(i, vertexNames.get(i));
            graphPane.getChildren().addAll(circle, text);
        }

        /// Dibujar las líneas/aristas basadas en la matriz de adyacencia
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (matrix[i][j] > 0 && i < j) { // Si hay conexión entre los vértices i y j
                    Circle startVertex = nodeMap.get(i);
                    Circle endVertex = nodeMap.get(j);

                    // Dibujar una línea entre los dos vértices
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

                    //poner el peso en el medio de la linea
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