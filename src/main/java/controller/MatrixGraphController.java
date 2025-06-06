package controller;

import domain.AdjacencyMatrixGraph;
import domain.GraphException;
import domain.Vertex;
import domain.list.ListException;
import domain.queue.QueueException;
import domain.stack.StackException;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
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

public class MatrixGraphController
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

    @javafx.fxml.FXML
    public void initialize() {
        vertices = new ArrayList<>();
        graph=new AdjacencyMatrixGraph(10);
    }
    private void clear(){
        vertices=new ArrayList<>();
        graph=new AdjacencyMatrixGraph(10);
    }
    @javafx.fxml.FXML
    public void randomOnAction(ActionEvent actionEvent) {
        clear();
        try {
            //lleno el grafo
        for (int i = 0; i < 10; i++) {
                graph.addVertex(util.Utility.random(99));
        }
            vertices.clear();
            for (int i = 0; i < graph.getVertexList().length; i++) {
                vertices.add(graph.getVertexList()[i]);
            }
        //creo aristas aleatorias
            int edgeQtty=util.Utility.random(20)+1;
            for (int i = 0; i < edgeQtty; i++) {
                int weight=util.Utility.random(50)+1;
                int v1= (int) vertices.get(Utility.random(10)).data;
                int v2=(int) vertices.get(Utility.random(10)).data;
                graph.addEdgeWeight(v1,v2,weight);
            }

            int[][] matix=new int[10][10];

            //copio la matiz
            for (int i = 0; i < 10; i++) {
                for (int j = 0; j < 10; j++) {
                    matix[i][j]= (int) graph.getAdjacencyMatrix()[i][j];
                }
            }
            //copio la lista de vertices
            List<Integer> intVertexes=new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                intVertexes.add((Integer) vertices.get(i).data);
            }
            drawGraph(intVertexes,matix);
        } catch (GraphException | ListException e) {
            throw new RuntimeException(e);
        }
    }

    @javafx.fxml.FXML
    public void toStringOnAction(ActionEvent actionEvent) {
        tArea.clear();
        this.tArea.setText(graph.toString());
    }

    @javafx.fxml.FXML
    public void dfsTourOnAction(ActionEvent actionEvent) {
        tArea.clear();
        try {
            this.tArea.setText("DFS Tour order:\n"+graph.dfs());
        } catch (GraphException | StackException | ListException e) {
            throw new RuntimeException(e);
        }
    }



    @javafx.fxml.FXML
    public void bfsTourOnAction(ActionEvent actionEvent) {
        tArea.clear();
        try {
            tArea.setText("BFS Tour order:\n"+graph.bfs());
        } catch (GraphException | ListException | QueueException e) {
            throw new RuntimeException(e);
        }
    }

    @javafx.fxml.FXML
    public void containsVertexOnAction(ActionEvent actionEvent) {
    }

    @javafx.fxml.FXML
    public void containsEdgeOnAction(ActionEvent actionEvent) {
    }

    private void drawGraph(List<Integer> vertices, int[][] matrix) {
        graphPane.getChildren().clear(); // limpiar anterior

        //tamaños del nodo
        int centerX = 300;
        int centerY = 300;
        int radius = 200;
        int n = vertices.size();

        Map<Integer, Circle> nodeMap = new HashMap<>();//uso un mapa para mayor eficiencia

        //creo los nodos del grafo
        for (int i = 0; i < n; i++) {
            double angle = 2 * Math.PI * i / n;
            double x = centerX + radius * Math.cos(angle);
            double y = centerY + radius * Math.sin(angle);

            Circle node = new Circle(x, y, 20, Color.AQUA);
            Text text = new Text(x - 10, y + 5, String.valueOf(vertices.get(i)));

            nodeMap.put(i, node);//asocio el circulo para llegar a el mas facilmente
            graphPane.getChildren().addAll(node, text);
        }

        //Crear aristas
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                if (matrix[i][j] > 0) {
                    Circle origin = nodeMap.get(i);
                    Circle destiny = nodeMap.get(j);

                    Line edge = new Line(origin.getCenterX(), origin.getCenterY(),
                            destiny.getCenterX(), destiny.getCenterY());
                    edge.setStrokeWidth(2);
                    edge.setStroke(Color.GRAY);

                    //poner el peso en el medio de la linea
                    double x = (origin.getCenterX() + destiny.getCenterX()) / 2;
                    double y = (origin.getCenterY() + destiny.getCenterY()) / 2;
                    Text weight = new Text(x, y,
                            String.valueOf(matrix[i][j]));

                    graphPane.getChildren().addAll(edge, weight);
                }
            }
        }
    }

}