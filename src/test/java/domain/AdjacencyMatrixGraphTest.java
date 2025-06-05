package domain;

import domain.list.ListException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AdjacencyMatrixGraphTest {

    @Test
    void addVertex() {
        try {
            AdjacencyMatrixGraph graph=new AdjacencyMatrixGraph(50);
            for (int i=0;i<30;i++){
                graph.addVertex(util.Utility.random(50));
            }
            AdjacencyMatrixGraph letterGraph=new AdjacencyMatrixGraph(50);
            for (char i='a';i<='e';i++){
                letterGraph.addVertex(i);
            }
            letterGraph.addEdge('a','b');
            letterGraph.addEdge('a','c');
            letterGraph.addEdge('a','d');
            letterGraph.addEdge('b','e');
            letterGraph.addEdge('c','e');

            System.out.println(graph);
            System.out.println(letterGraph);
        } catch (GraphException | ListException e) {
            throw new RuntimeException(e);
        }
    }

}