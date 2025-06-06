package domain;

import domain.list.ListException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AdjacencyListGraphTest {

    @Test
    void test() {
        try {
            AdjacencyListGraph graph = new AdjacencyListGraph(50);
            for (int i=0;i<30;i++){
                graph.addVertex(util.Utility.random(10, 50));
            }

            System.out.println(graph);

            AdjacencyListGraph letterGraph=new AdjacencyListGraph(50);
            for (char i='a';i<='e';i++){
                letterGraph.addVertex(i);
            }

            letterGraph.addEdgeWeight('a','b',util.Utility.random(20)+2);
            letterGraph.addEdgeWeight('c','e',util.Utility.random(20)+2);
            letterGraph.addEdgeWeight('e','a',util.Utility.random(20)+2);

            System.out.println(letterGraph);

        } catch (GraphException e) {
                throw new RuntimeException(e);
            } catch (ListException e) {
                throw new RuntimeException(e);
            }
    }

}