import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

////////////////////////////////////////////////////////////////////////////
// Semester:         CS400 Spring 2018
// PROJECT:          cs400_p3_201801
// FILES:            Graph.java
// GraphProcessor.java
// GraphTest.java
// WordProcessor.java
//
// USER:             ateng@wisc.edu
//                   tfiedler2@wisc.edu
//                   cdedrick@wisc.edu
//                   yfang57@wisc.edu
//                   mdespe@wisc.edu
//
// Instructor:       Deb Deppeler (deppeler@cs.wisc.edu)
// Bugs:             no known bugs, but not complete either
//
// 2018 Apr 16, 2018 GraphProcessor.java 
////////////////////////////80 columns wide //////////////////////////////////

/**
 * This class adds additional functionality to the graph as a whole.
 * 
 * Contains an instance variable, {@link #graph}, which stores information for all the vertices and edges.
 * 
 * @see #populateGraph(String)
 *      - loads a dictionary of words as vertices in the graph.
 *      - finds possible edges between all pairs of vertices and adds these edges in the graph.
 *      - returns number of vertices added as Integer.
 *      - every call to this method will add to the existing graph.
 *      - this method needs to be invoked first for other methods on shortest path computation to work.
 * @see #shortestPathPrecomputation()
 *      - applies a shortest path algorithm to precompute data structures (that store shortest path data)
 *      - the shortest path data structures are used later to
 *      to quickly find the shortest path and distance between two vertices.
 *      - this method is called after any call to populateGraph.
 *      - It is not called again unless new graph information is added via populateGraph().
 * @see #getShortestPath(String, String)
 *      - returns a list of vertices that constitute the shortest path between two given vertices,
 *      computed using the precomputed data structures computed as part of {@link #shortestPathPrecomputation()}.
 *      - {@link #shortestPathPrecomputation()} must have been invoked once before invoking this method.
 * @see #getShortestDistance(String, String)
 *      - returns distance (number of edges) as an Integer for the shortest path between two given vertices
 *      - this is computed using the precomputed data structures computed as part of {@link #shortestPathPrecomputation()}.
 *      - {@link #shortestPathPrecomputation()} must have been invoked once before invoking this method.
 * 
 * @author sapan (sapan@cs.wisc.edu)
 * 
 */
public class GraphProcessor {
    
    /**
     * Graph which stores the dictionary words and their associated connections
     */
    private GraphADT<String> graph;
    // index i = Start word, j = End word
    private List<String>[][] shortestPaths;
    
    /**
     * Constructor for this class. Initializes instances variables to set the starting state of the object
     */
    public GraphProcessor() {
        this.graph = new Graph<>();
    }
    
    /**
     * Builds a graph from the words in a file. Populate an internal graph, by adding words from the dictionary as vertices
     * and finding and adding the corresponding connections (edges) between
     * existing words.
     * 
     * Reads a word from the file and adds it as a vertex to a graph.
     * Repeat for all words.
     * 
     * For all possible pairs of vertices, finds if the pair of vertices is adjacent {@link WordProcessor#isAdjacent(String, String)}
     * If a pair is adjacent, adds an undirected and unweighted edge between the pair of vertices in the graph.
     * 
     * @param filepath
     *            file path to the dictionary
     * @return Integer the number of vertices (words) added
     */
    public Integer populateGraph(String filepath) {
        int size = 0;
        try {
            String[] words = (String[]) WordProcessor.getWordStream(filepath).toArray();
            for (int i = 0; i < words.length; i++) { // For each word
                graph.addVertex(words[i]); // Add word into the graph
                for (int j = 0; j < i; j++) { // Then compare with other words in graph
                    if (WordProcessor.isAdjacent(words[i], words[j])) { // If adjacent, add an edge
                        graph.addEdge(words[i], words[j]);
                    }
                }
            }
            size = ((ArrayList) graph.getAllVertices()).size();
            shortestPaths = (List<String>[][]) (new Object[size][size]);
            shortestPathPrecomputation();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return size;
        
    }
    
    /**
     * Gets the list of words that create the shortest path between word1 and word2
     * 
     * Example: Given a dictionary,
     * cat
     * rat
     * hat
     * neat
     * wheat
     * kit
     * shortest path between cat and wheat is the following list of words:
     * [cat, hat, heat, wheat]
     * 
     * @param word1
     *            first word
     * @param word2
     *            second word
     * @return List<String> list of the words
     */
    public List<String> getShortestPath(String word1, String word2) {
        ArrayList<String> vertices = ((ArrayList<String>) graph.getAllVertices());
        int startPoint = vertices.indexOf(word1);
        int endPoint = vertices.indexOf(word2);
        return shortestPaths[startPoint][endPoint];
        
    }
    
    /**
     * Gets the distance of the shortest path between word1 and word2
     * 
     * Example: Given a dictionary,
     * cat
     * rat
     * hat
     * heat
     * wheat
     * kit
     * distance of the shortest path between cat and wheat, [cat, hat, heat, wheat]
     * = 3 (the number of edges in the shortest path)
     * 
     * @param word1
     *            first word
     * @param word2
     *            second word
     * @return Integer distance
     */
    public Integer getShortestDistance(String word1, String word2) {
        List<String> shortestPath = getShortestPath(word1, word2);
        if (shortestPath == null)
            return -1;
        return shortestPath.size() - 1;
    }
    
    /**
     * Computes shortest paths and distances between all possible pairs of vertices.
     * This method is called after every set of updates in the graph to recompute the path information.
     * Any shortest path algorithm can be used (Djikstra's or Floyd-Warshall recommended).
     */
    public void shortestPathPrecomputation() {
        ArrayList<String> vertices = ((ArrayList<String>) graph.getAllVertices());
        for (int i = 0; i < vertices.size(); i++) {
            for (int j = 0; j < vertices.size(); j++) {
                if (!vertices.get(i).equals(vertices.get(j)))
                    shortestPaths[i][j] = shortestPathComp(vertices.get(i), vertices.get(j), vertices);
            }
        }
    }
    
    private List<String> shortestPathComp(String word1, String word2, ArrayList<String> vertices) {
        /*
         * For Dijkstra's Algorithm to work:
         * 1. Calculate the weight of all current neighbors or change optimal weight
         * 2. Mark current path visited
         * 3. Choose path with smallest weight
         * 4. Repeat until destination is in visited
         */
        String curr = word1; // Current word
        boolean empty = false;
        List<String> visited = new ArrayList<String>(); // Optimal Path
        List<Integer> inPos = new ArrayList<Integer>();
        PriorityQueue<Node> lowCostPath = new PriorityQueue<Node>();
        
        int pathWeight = 0; // Starting path weight
        
        while (!empty) {
            ArrayList<String> neighbors = (ArrayList<String>) graph.getNeighbors(curr);
            
            // Add all unchecked neighbors
            for (String neighbor : neighbors) {
                int index = vertices.indexOf(neighbor);
                // Fist found index will always be shortest path to that index,
                // So no need to check for other indexes or update the path
                if (!inPos.contains(index)) {
                    inPos.add(index); // Add to checked
                    Node newNode = new Node(neighbor, pathWeight + 1);
                    lowCostPath.add(newNode); // Add to priority queue
                }
            }
            visited.add(curr); // Add to visited after all neighbors are put in
            if (curr.equals(word2)) {
                return visited;
            }
            if (!lowCostPath.isEmpty()) {
                curr = lowCostPath.poll().node;
                pathWeight++;
            }
            // Iterate at end to guarantee one run through of loop
            if (lowCostPath.isEmpty())
                empty = true;
        }
        return null; // Impossible to reach
    }
    
    private class Node implements Comparable<Node> {
        String node;
        int cost;
        
        public Node(String node, int cost) {
            this.node = node;
            this.cost = cost;
        }
        
        @Override
        public int compareTo(Node o) {
            return cost - o.cost;
        }
    }
}
