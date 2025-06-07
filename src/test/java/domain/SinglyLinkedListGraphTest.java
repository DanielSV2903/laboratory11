package domain;

import domain.list.ListException;
import domain.queue.QueueException;
import domain.stack.StackException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SinglyLinkedListGraphTest {

    @Test
    void test() {
        try {
            Graph graph = new SinglyLinkedListGraph();

            //Crear el grafo
            for (char i= 'A';i<='J';i++) {
                graph.addVertex(i);
            }
            //aristas
            graph.addEdgeWeight('A', 'B', "Luis");
            graph.addEdgeWeight('A', 'C', "Ana");
            graph.addEdgeWeight('A', 'D', "Carlos");
            graph.addEdgeWeight('B', 'E', "Laura");
            graph.addEdgeWeight('C', 'F', "Pablo");
            graph.addEdgeWeight('C', 'G', "Sofía");
            graph.addEdgeWeight('D', 'H', "Pedro");
            graph.addEdgeWeight('E', 'F', "Lucía");
            graph.addEdgeWeight('F', 'G', "Mario");
            graph.addEdgeWeight('G', 'H', "María");
            graph.addEdgeWeight('H', 'I', "Andrés");
            graph.addEdgeWeight('G', 'J', "Paula");

            System.out.println(graph);

            //RECORRIDOS
            System.out.println("\nDFS:");
            System.out.println(graph.dfs());

            System.out.println("\nBFS:");
            System.out.println(graph.bfs());

            //e
            graph.removeVertex("F");
            graph.removeVertex("H");
            graph.removeVertex("J");
            //f
            System.out.println("\nDFS sin F, H, J:");
            System.out.println(graph.dfs());

            System.out.println("\nBFS sin F, H, J:");
            System.out.println(graph.bfs());
            //g
            System.out.println(graph);

        } catch (GraphException | ListException | QueueException | StackException  e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}