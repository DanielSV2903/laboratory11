package domain;

import domain.list.List;
import domain.list.ListException;
import domain.queue.LinkedQueue;
import domain.queue.QueueException;
import domain.stack.LinkedStack;
import domain.stack.StackException;
import util.Utility;

public class AdjacencyListGraph implements Graph {
        private Vertex[] vertexList; //arreglo de objetos tupo vértice
        private int n; //max de elementos
        private int counter; //contador de vertices

        //para los recorridos dfs, bfs
        private LinkedStack stack;
        private LinkedQueue queue;

        //Constructor
        public AdjacencyListGraph(int n) {
            if (n <= 0) System.exit(1); //sale con status==1 (error)
            this.n = n;
            this.counter = 0;
            this.vertexList = new Vertex[n];
            this.stack = new LinkedStack();
            this.queue = new LinkedQueue();
        }



        @Override
        public int size() throws ListException {
            return counter;
        }

        @Override
        public void clear() {
            this.vertexList = new Vertex[n];
            this.counter = 0; //inicializo contador de vértices
        }

        @Override
        public boolean isEmpty() {
            return counter == 0;
        }

        @Override
        public boolean containsVertex(Object element) throws GraphException, ListException {
            if (isEmpty())
                throw new GraphException("Adjacency List Graph is empty");
            return indexOf(element)!=-1;
        }

        @Override
        public boolean containsEdge(Object a, Object b) throws GraphException, ListException {
            if (isEmpty())
                throw new GraphException("Adjacency List Graph is empty");

            return !vertexList[indexOf(a)].edgesList.isEmpty()
                    && vertexList[indexOf(a)].edgesList.contains(new EdgeWeight(b,null));
        }

        @Override
        public void addVertex(Object element) throws GraphException, ListException {
            if (counter>=vertexList.length)
                throw new GraphException("Adjacency List graph is full");
            vertexList[counter++]=new Vertex(element);

        }

        @Override
        public void addEdge(Object a, Object b) throws GraphException, ListException {
            if (!containsVertex(a)||!containsVertex(b))
                throw new GraphException("Cannot add edge between vertexes["+a+","+b+"]");
            vertexList[indexOf(a)].edgesList.add(new EdgeWeight(b,null));
            //grafo no dirigido
            vertexList[indexOf(b)].edgesList.add(new EdgeWeight(a,null));
        }

        private int indexOf(Object element) {
            for (int i=0;i<vertexList.length;i++){
                if (util.Utility.compare(vertexList[i].data,element)==0)
                    return i;
            }
            return -1;
//        int col=0;
//        int row=0;
//        for (int i=0;i<vertexList.length;i++)
//            for (int j=0;j<adjacencyList[0].length;j++) {
//                Vertex v=(Vertex) adjacencyList[i][j];
//                if (v.data.equals(element)){
//                    col=j;
//                    row=i;
//                }
//            }
//        return new int[]{row,col};
        }

        @Override
        public void addWeight(Object a, Object b, Object weight) throws GraphException, ListException {
            if (!containsEdge(a,b))
                throw new GraphException("Theres no edge between ["+a+","+b+"]");
            updateEdgeWeight(a,b,weight);

            updateEdgeWeight(b,a,weight);
        }

    public void updateEdgeWeight(Object a, Object b, Object weight) throws ListException {
        EdgeWeight edgeWeight=(EdgeWeight) vertexList[indexOf(a)].edgesList
                .getNode(new EdgeWeight(b,null)).getData();
        edgeWeight.setWeight(weight);
        //seteo el peso en su campo respectivo
        //actualizo la informacion en la lista de aristas correspondiente
        vertexList[indexOf(a)].edgesList.getNode(new EdgeWeight(b,null))
                .setData(edgeWeight);
        }

        @Override
        public void addEdgeWeight(Object a, Object b, Object weight) throws GraphException, ListException {
            if (!containsVertex(a)||!containsVertex(b))
                throw new GraphException("Cannot add edge between vertexes["+a+","+b+"]");
            if (!containsEdge(a,b)) {
                vertexList[indexOf(a)].edgesList.add(new EdgeWeight(b, weight));
                //grafo no dirigido
                vertexList[indexOf(b)].edgesList.add(new EdgeWeight(a, weight));
            }
        }

        @Override
        public void removeVertex(Object element) throws GraphException, ListException {
            if (isEmpty())
                throw new GraphException("Adjacency List Graph is empty");
            if (containsVertex(element)) {
                for (int i=0;i<counter;i++){
                    if (util.Utility.compare(vertexList[i].data,element)==0){
                        //se debe suprimir el vertice a eliminar de todas las listas enlazadas de los otros vertices
                        for (int j=0;j<counter;j++)
                            if (containsEdge(vertexList[j].data,element))
                                removeEdge(vertexList[j].data,element);
                    for (int j = i; j < counter - 1; j++)
                        vertexList[j] = vertexList[j + 1];
                    counter--;
                    }
                }
            }
        }

        @Override
        public void removeEdge(Object a, Object b) throws GraphException, ListException {
            if (!containsVertex(a)||!containsVertex(b))
                throw new GraphException("Theres no edge between ["+a+","+b+"]");
            int indexOfA=indexOf(a);
            if(!vertexList[indexOfA].edgesList.isEmpty()){
                vertexList[indexOfA].edgesList.remove(new EdgeWeight(b,null));
                //grafo no dirigido
                vertexList[indexOf(b)].edgesList.remove(new EdgeWeight(a,null));
            }
        }

        // Recorrido en profundidad
        @Override
        public String dfs() throws GraphException, StackException, ListException {
            setVisited(false);//marca todos los vertices como no vistados
            // inicia en el vertice 0
            String info = vertexList[0].data + ", ";
            vertexList[0].setVisited(true); // lo marca
            stack.clear();
            stack.push(0); //lo apila
            while (!stack.isEmpty()) {
                // obtiene un vertice adyacente no visitado,
                //el que esta en el tope de la pila
                int index = adjacentVertexNotVisited((int) stack.top());
                if (index == -1) // no lo encontro
                    stack.pop();
                else {
                    vertexList[index].setVisited(true); // lo marca
                    info += vertexList[index].data + ", "; //lo muestra
                    stack.push(index); //inserta la posicion
                }
            }
            return info;
        }

        //Recorrido en amplitud
        @Override
        public String bfs() throws GraphException, QueueException, ListException {
            setVisited(false);//marca todos los vertices como no visitados
            // inicia en el vertice 0
            String info = vertexList[0].data + ", ";
            vertexList[0].setVisited(true); // lo marca
            queue.clear();
            queue.enQueue(0); // encola el elemento
            int v2;
            while (!queue.isEmpty()) {
                int v1 = (int) queue.deQueue(); // remueve el vertice de la cola
                // hasta que no tenga vecinos sin visitar
                while ((v2 = adjacentVertexNotVisited(v1)) != -1) {
                    // obtiene uno
                    vertexList[v2].setVisited(true); // lo marca
                    info += vertexList[v2].data + ", "; //lo muestra
                    queue.enQueue(v2); // lo encola
                }
            }
            return info;
        }

        //setteamos el atributo visitado del vertice respectivo
        private void setVisited(boolean value) {
            for (int i = 0; i < counter; i++) {
                vertexList[i].setVisited(value); //value==true o false
            }//for
        }

        private int adjacentVertexNotVisited(int index) throws ListException {
            Object vertexData=vertexList[index].data;
            for (int i = 0; i < counter; i++) {
                if (!vertexList[i].edgesList.isEmpty()
                        && !vertexList[i].edgesList.contains(new EdgeWeight(vertexData,null))&&
                !vertexList[i].isVisited())
                    return i;//retorna la posicion del vertice adyacente no visitado
            }//for i
            return -1;
        }

        @Override
        public String toString() {
            String result = "Adjacency List Graph Content...";
            //se muestran todos los vértices del grafo
            for (int i = 0; i < counter; i++) {
                result += "\nThe vextex in the position " + i + " is: " + vertexList[i].data;
            }
            for (int i = 0; i < counter; i++) {
                for (int j = 0; j < counter; j++) {
                    try {
                        if (containsEdge(vertexList[i].data, vertexList[j].data)) {
                            //si existe una arista (Edge)
                            result += "\nThere is an edge between the vertexes:" + vertexList[i].data + "...."
                                    + vertexList[j].data;

                            //si existe un peso (Weight) en la arista (Edge), se muestra
                            EdgeWeight newEW;
                            for (int k = 0; k < vertexList[i].edgesList.size(); k++) {
                                newEW = (EdgeWeight) vertexList[i].edgesList.getNode(k).data;
                                if (Utility.compare(newEW, vertexList[j])==0){
                                    result += "____WEIGHT: " + newEW.getWeight();
                                }
                            }
                        }
                    } catch (GraphException e) {
                        throw new RuntimeException(e);
                    } catch (ListException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            return result;
        }
    }

