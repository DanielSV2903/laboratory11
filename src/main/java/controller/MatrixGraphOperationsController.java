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

import java.util.*;

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
    private AdjacencyMatrixGraph graph;
    private List<Vertex> vertices;
    private int vertexCount = 10;
    private Set<Integer> addedValues;

    @javafx.fxml.FXML
    public void initialize() {
        vertices=new ArrayList<>();
        graph=new AdjacencyMatrixGraph(vertexCount);
        addedValues = new HashSet<>();
    }

    @javafx.fxml.FXML
    public void clearOnAction(ActionEvent actionEvent) {
        graph.clear();
        vertices.clear();
        addedValues.clear();
        graphPane.getChildren().clear();
        tArea.clear();
        label.setText("");
    }

    @javafx.fxml.FXML
    public void addEWOnAction(ActionEvent actionEvent) {
        if (vertices.size() < 2) return;//si no hay dos vertex, no hay arista
        try {
            int index1 = Utility.random(vertices.size());
            int index2 = Utility.random(vertices.size());
            int v1 = (int) vertices.get(index1).data;
            int v2 = (int) vertices.get(index2).data;
            while (v1==v2)
                v2=(int)vertices.get(util.Utility.random(vertices.size())).data;
            if (v1 != v2 && !graph.containsEdge(v1, v2) ) {
                int weight = Utility.random(50) + 1;
                graph.addEdgeWeight(v1, v2, weight);
            }
            updateGraphView();
        } catch (GraphException | ListException e) {
            throw new RuntimeException(e);
        }
    }

    @javafx.fxml.FXML
    public void addVertexOnAction(ActionEvent actionEvent) {
        try {
        if (graph.size() < vertexCount) {
            int value;
            do {
                value = Utility.random(99);
            } while (!addedValues.add(value));

            graph.addVertex(value);
            refreshVertices();
            updateGraphView();
        }
    } catch (GraphException | ListException e) {
        throw new RuntimeException(e);
    }

    }

    @javafx.fxml.FXML
    public void removeVertexOnAction(ActionEvent actionEvent) {
        if (addedValues.isEmpty()) return;//si no hay dos vertex, no hay arista
        int index = Utility.random(addedValues.size());
        Integer[] values = addedValues.toArray(new Integer[0]);
        int toRemove = values[index];
        try {
            addedValues.remove(toRemove);
            graph.removeVertex(toRemove);
            refreshVertices();
            updateGraphView();

        } catch (GraphException | ListException e) {
            throw new RuntimeException(e);
        }
    }

    @javafx.fxml.FXML
    public void removeEWOnAction(ActionEvent actionEvent) {
        try {
            List<Integer> edgeList = new ArrayList<>();
            int size = graph.size();
            for (int i = 0; i < size; i++) {
                for (int j = i + 1; j < size; j++) {
                    if (util.Utility.compare(graph.getAdjacencyMatrix()[i][j],0) > 0) {
                        edgeList.add(i * size + j);//si encuentra un edge añade la posicion fila columna para descomponerla luego
                    }
                }
            }
            if (!edgeList.isEmpty()) {//por si acaso
                int randomEdge = edgeList.get(Utility.random(edgeList.size()));
                //descomponer la posicion en fila columna
                int i = randomEdge / size;//fila
                int j = randomEdge % size;//columna
                int v1 = (int) graph.getVertexList()[i].data;
                int v2 = (int) graph.getVertexList()[j].data;
                graph.removeEdge(v1, v2);
                updateGraphView();
            }
        } catch (GraphException | ListException e) {
            throw new RuntimeException(e);
        }
    }
    private void clear(){
        vertices=new ArrayList<>();
        graph=new AdjacencyMatrixGraph(vertexCount);
    }
    @javafx.fxml.FXML
    public void randomOnAction(ActionEvent actionEvent) {
        clear();
        try {
            addedValues = new HashSet<>();//uso el set para evitar repetidos
            while (graph.size() < vertexCount) {
                int value = Utility.random(99);
                if (addedValues.add(value)) {
                    graph.addVertex(value);
                }
            }
            //llenar lista de vértices
            vertices.clear();
            for (Vertex v : graph.getVertexList())
                vertices.add(v);

            //creo las aristas
            int edgeQtty = Utility.random(vertexCount * 2) + 1;
            for (int i = 0; i < edgeQtty; i++) {
                int index1 = Utility.random(vertices.size());
                int index2 = Utility.random(vertices.size());
                int v1 = (int) vertices.get(index1).data;
                int v2 = (int) vertices.get(index2).data;

                if (v1 != v2 && !graph.containsEdge(v1, v2)) {
                    int weight = Utility.random(50) + 1;
                    graph.addEdgeWeight(v1, v2, weight);
                }
            }

            int[][] matrix = new int[10][10];

            //copio la matriz
            for (int i = 0; i < 10; i++) {
                for (int j = 0; j < 10; j++) {
                    matrix[i][j] = (int) graph.getAdjacencyMatrix()[i][j];
                }
            }
            //copiar la lista
            List<Integer> intVertexes = new ArrayList<>();
            for (Vertex v : vertices) {
                intVertexes.add((Integer) v.data);
            }
            drawGraph(intVertexes, matrix);

        } catch (GraphException | ListException e) {
            throw new RuntimeException(e);
        }
    }
    private void refreshVertices() {
        vertices.clear();
        for (Vertex v : graph.getVertexList()) {
            if (v != null) vertices.add(v);
        }
    }

    private void updateGraphView() throws ListException {
        int size = graph.size(); // cantidad actual de vértices
        int[][] matrix = new int[size][size];

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                matrix[i][j] = (int) graph.getAdjacencyMatrix()[i][j];
            }
        }

        List<Integer> intVertexes = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            intVertexes.add((Integer) graph.getVertexList()[i].data);
        }
        drawGraph(intVertexes, matrix);
    }

    private void drawGraph(List<Integer> vertices, int[][] matrix) {
        graphPane.getChildren().clear(); // limpiar anterior

        //tamaños del nodo
        int centerX = 300;
        int centerY = 300;
        int radius = 200;
        int n = vertices.size();

        Map<Integer, Circle> nodeMap = new HashMap<>();//uso un mapa para mayor eficiencia
        Map<Integer, Integer> dataMap = new HashMap<>();//uso un mapa para mayor eficiencia


        //creo los nodos del grafo
        for (int i = 0; i < n; i++) {
            double angle = 2 * Math.PI * i / n;
            double x = centerX + radius * Math.cos(angle);
            double y = centerY + radius * Math.sin(angle);

            Circle node = new Circle(x, y, 20, Color.AQUA);
            int finalI = i;
            node.setOnMouseClicked(event -> {
                label.setText("Vertex "+vertices.get(finalI)+" is on index "+finalI);
            });
            Text text = new Text(x - 10, y + 5, String.valueOf(vertices.get(i)));

            nodeMap.put(i, node);//asocio el circulo para llegar a el mas facilmente
            dataMap.put(i,vertices.get(i));//meto la data del nodo para recuperarla despues
            graphPane.getChildren().addAll(node, text);
        }

        //Crear aristas
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                if (matrix[i][j] > 0) {
                    Circle origin = nodeMap.get(i);
                    Circle destiny = nodeMap.get(j);

                    Line edge = new Line(origin.getCenterX(), origin.getCenterY()-10,
                            destiny.getCenterX(), destiny.getCenterY()+10);
                    edge.setStrokeWidth(4);
                    edge.setStroke(Color.GRAY);
                    //Auxiliares para el lamda
                    int finalI = i;
                    int finalJ = j;
                    edge.setOnMouseClicked(event -> {
                        String txt="Edege between "+dataMap.get(finalI)+" and "+dataMap.get(finalJ)+" weight: "+matrix[finalI][finalJ];
                        label.setText(txt);
                        edge.setStroke(Color.GREEN);
                        tArea.appendText(txt+"\n");
                    });

                    //poner el peso en el medio de la linea
                    double x = (origin.getCenterX() + destiny.getCenterX()) / 2;
                    double y = (origin.getCenterY() + destiny.getCenterY()) / 2;
                    Text weight = new Text(x, y,
                            String.valueOf(matrix[i][j]));
                    graphPane.getChildren().addAll(edge, weight);//añadir el grafo al pane
                }
            }
        }
    }
}