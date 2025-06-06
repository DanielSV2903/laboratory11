package domain;

import domain.list.ListException;
import domain.queue.LinkedQueue;
import domain.queue.QueueException;
import domain.stack.LinkedStack;
import domain.stack.StackException;

public class AdjacencyMatrixGraph implements Graph {
    private Vertex[] vertexList; //arreglo de objetos tupo vértice
    private Object[][] adjacencyMatrix; //arreglo bidimensional
    private int n; //max de elementos
    private int counter; //contador de vertices

    //para los recorridos dfs, bfs
    private LinkedStack stack;
    private LinkedQueue queue;

    //Constructor
    public AdjacencyMatrixGraph(int n) {
        if (n <= 0) System.exit(1); //sale con status==1 (error)
        this.n = n;
        this.counter = 0;
        this.vertexList = new Vertex[n];
        this.adjacencyMatrix = new Object[n][n];
        this.stack = new LinkedStack();
        this.queue = new LinkedQueue();
        initMatrix(); //inicializa matriz de objetos con cero
    }

    private void initMatrix() {

        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                this.adjacencyMatrix[i][j] = 0; //init con ceros
    }

    @Override
    public int size() throws ListException {
        return counter;
    }

    @Override
    public void clear() {
        this.vertexList = new Vertex[n];
        this.adjacencyMatrix = new Object[n][n];
        this.counter = 0; //inicializo contador de vértices
        this.initMatrix();
    }

    @Override
    public boolean isEmpty() {
        return counter == 0;
    }

    @Override
    public boolean containsVertex(Object element) throws GraphException, ListException {
        if (isEmpty())
            throw new GraphException("Adjacency Matrix Graph is empty");
        return indexOf(element)!=-1;
    }

    @Override
    public boolean containsEdge(Object a, Object b) throws GraphException, ListException {
        if (isEmpty())
            throw new GraphException("Adjacency Matrix Graph is empty");
//        int i= (int) adjacencyMatrix[indexOf(a)][indexOf(b)];
//        int j=(int) adjacencyMatrix[indexOf(b)][indexOf(a)];
//        if (i>0||j>0)
//            return true;
//        return false;
        return !(util.Utility.compare(adjacencyMatrix[indexOf(a)][indexOf(b)],0)==0);
    }

    @Override
    public void addVertex(Object element) throws GraphException, ListException {
        if (counter>=vertexList.length)
            throw new GraphException("Adjacency matrix graph is full");
        vertexList[counter++]=new Vertex(element);

    }

    @Override
    public void addEdge(Object a, Object b) throws GraphException, ListException {
        if (!containsVertex(a)||!containsVertex(b))
            throw new GraphException("Cannot ad edge between vertexes["+a+","+b+"]");
        adjacencyMatrix[indexOf(a)][indexOf(b)]=1;
        //grafo no dirigido
        adjacencyMatrix[indexOf(b)][indexOf(a)]=1;
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
//            for (int j=0;j<adjacencyMatrix[0].length;j++) {
//                Vertex v=(Vertex) adjacencyMatrix[i][j];
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
        adjacencyMatrix[indexOf(a)][indexOf(b)]=weight;
        adjacencyMatrix[indexOf(b)][indexOf(a)]=weight;
    }

    @Override
    public void addEdgeWeight(Object a, Object b, Object weight) throws GraphException, ListException {
        if (!containsVertex(a)||!containsVertex(b))
            throw new GraphException("Cannot add edge between vertexes["+a+","+b+"]");
        adjacencyMatrix[indexOf(a)][indexOf(b)]=weight;
        adjacencyMatrix[indexOf(b)][indexOf(a)]=weight;
    }

    @Override
    public void removeVertex(Object element) throws GraphException, ListException {
        if (isEmpty())
            throw new GraphException("Adjacency Matrix Graph is empty");
        int index=indexOf(element);
        if (index!=-1){
            //movemos todas las filas una posicion hacia arriba
            for (int i=index;i<counter-1;i++){
                vertexList[i]=vertexList[i+1];
                for (int j=0;j<counter;j++)
                    adjacencyMatrix[i][j]=adjacencyMatrix[i+1][j];
            }
            //mover una columna a la izquierda
            for (int i=0;i<counter;i++){
                for (int j=index;j<counter-1;j++)
                    adjacencyMatrix[i][j]=adjacencyMatrix[i][j+1];
            }
            counter--;
        }//que pasa si ya no quedan vertices
         if (counter==0) initMatrix();
    }

    @Override
    public void removeEdge(Object a, Object b) throws GraphException, ListException {
        if (!containsVertex(a)||!containsVertex(b))
            throw new GraphException("Theres no edge between ["+a+","+b+"]");
        int i=indexOf(a);
        int j=indexOf(b);
        if (i!=-1&&j!=-1){
            adjacencyMatrix[j][i]=0;
            adjacencyMatrix[i][j]=0;
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

    private int adjacentVertexNotVisited(int index) {
        for (int i = 0; i < counter; i++) {
            if (!adjacencyMatrix[index][i].equals(0)
                    && !vertexList[i].isVisited())
                return i;//retorna la posicion del vertice adyacente no visitado
        }//for i
        return -1;
    }

    @Override
    public String toString() {
        String result = "Adjacency Matrix Graph Content...";
        //se muestran todos los vértices del grafo
        for (int i = 0; i < counter; i++) {
            result+="\nThe vextex in the position: "+i+" is: "+vertexList[i].data;
        }
        //agrega la informacion de los pesos
        for (int i = 0; i < counter; i++) {
            for (int j=0;j<counter;j++){
                if (util.Utility.compare(adjacencyMatrix[i][j],0)!=0){
                    result+="\n There is edge between the vertexes: "+vertexList[i].data+" and "+vertexList[j].data;
                    if (util.Utility.compare(adjacencyMatrix[i][j],1)!=0)
                        result+="_____WEIGHT "+adjacencyMatrix[i][j];
                }
            }
        }
            return result;
    }
}
