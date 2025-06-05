package domain;

import domain.list.ListException;
import domain.queue.QueueException;
import domain.stack.StackException;
import org.junit.jupiter.api.Test;
import util.Utility;

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

            letterGraph.addEdgeWeight('a','b', Utility.random(20)+2);
            assertTrue(letterGraph.containsEdge('a','b'));
            assertTrue(letterGraph.containsEdge('b','a'));
            letterGraph.addEdgeWeight('a','c', Utility.random(20)+2);
            letterGraph.addEdgeWeight('a','d', Utility.random(20)+2);
            letterGraph.addEdgeWeight('b','e', Utility.random(20)+2);
            letterGraph.addEdgeWeight('c','e', Utility.random(20)+2);
            System.out.println(letterGraph);
            System.out.println("DFS "+letterGraph.dfs());
            System.out.println("BFS "+letterGraph.bfs());
            System.out.println("Vertex deleted a");
            letterGraph.removeVertex('a');
            System.out.println("Removing Edges e-----b");
            letterGraph.removeEdge('e','b');

            System.out.println(letterGraph);
        } catch (GraphException | ListException | StackException | QueueException e) {
            throw new RuntimeException(e);
        }
    }

}