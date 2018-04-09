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
            Object[] words = WordProcessor.getWordStream(filepath).toArray();
            for (int i = 0; i < words.length; i++) { // For each word
                graph.addVertex((String) words[i]); // Add word into the graph
                for (int j = 0; j < i; j++) { // Then compare with other words in graph
                    if (WordProcessor.isAdjacent((String) words[i], (String) words[j])) { // If adjacent, add an edge
                        graph.addEdge((String) words[i], (String) words[j]);
                    }
                }
            }
            for (String vertex : graph.getAllVertices()) {
                size++;
            }
            shortestPaths = (List<String>[][]) (new List<?>[size][size]);
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
        word1 = word1.toUpperCase();
        word2 = word2.toUpperCase();
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
        if (shortestPath == null) // If no path, return -1
            return -1;
        // Always one less edge compared to nodes in this path
        return shortestPath.size() - 1;
    }
    
    /**
     * Computes shortest paths and distances between all possible pairs of vertices.
     * This method is called after every set of updates in the graph to recompute the path information.
     * Any shortest path algorithm can be used (Djikstra's or Floyd-Warshall recommended).
     */
    public void shortestPathPrecomputation() {
        ArrayList<String> vertices = ((ArrayList<String>) graph.getAllVertices());
        // For each vertex, calculate the shortest distance between it and all other vertices
        for (int i = 0; i < vertices.size(); i++) {
            for (int j = 0; j < vertices.size(); j++) {
                // If not the same vertex
                if (!vertices.get(i).equals(vertices.get(j))) // Add into array
                    shortestPaths[i][j] = shortestPathComp(vertices.get(i), vertices.get(j), vertices);
            }
        }
    }
    
    /**
     * Private method that uses Dijkstra's Algorithm
     * To calculate the shortest path from A to B
     * Will be calculating for shortestPathPrecomputation()
     * 
     * @param word1
     *            Starting point
     * @param word2
     *            Ending Point
     * @param vertices
     *            To use to check against if it's been visited or not
     * @return
     */
    private List<String> shortestPathComp(String word1, String word2, ArrayList<String> vertices) {
        // System.out.println("Finding shortest path between " + word1 + " and " + word2);
        /*
         * For Dijkstra's Algorithm to work:
         * 1. Calculate the weight of all current neighbors or change optimal weight
         * 2. Mark current path visited
         * 3. Choose path with smallest weight
         * 4. Repeat until destination is in visited
         */
        Node curr = new Node(word1, 0);
        boolean empty = false;
        List<Node> visited = new ArrayList<Node>(); // Optimal Path
        // Checks if already visited
        List<Integer> inPos = new ArrayList<Integer>(); // Integer Position
        // Gets the node with the highest priority
        PriorityQueue<Node> lowCostPath = new PriorityQueue<Node>();
        
        int pathWeight = 0; // Starting path weight
        
        while (!empty) {
            // Get the node's neighbors
            ArrayList<String> neighbors = (ArrayList<String>) graph.getNeighbors(curr.node);
            
            // Add all unchecked neighbors
            for (String neighbor : neighbors) {
                int index = vertices.indexOf(neighbor);
                // Fist found index will always be shortest path to that index,
                // So no need to check for other indexes or update the path
                if (!inPos.contains(index)) {
                    inPos.add(index); // Add to checked
                    Node newNode = new Node(neighbor, curr, pathWeight + 1);
                    lowCostPath.add(newNode); // Add to priority queue
                }
            }
            visited.add(curr); // Add to visited after all neighbors are put in
            // for (Node n : visited) {
            // System.out.println(n.toString());
            // }
            // If the destination was added to the list
            if (curr.node.equals(word2)) {
                // Generates path starting with last node
                // Visited may have some unnecessary nodes,
                // So we need to make the correct path
                List<String> path = generatePath(visited.get(visited.size() - 1));
                // System.out.println("Found path\n");
                return path;
            }
            // If it isn't empty
            if (!lowCostPath.isEmpty()) {
                // Get next best node
                curr = lowCostPath.poll();
                // System.out.println("Next Node: " + curr.node + "\n");
                // Increment path weight (since all are unweighed)
                pathWeight++;
            }
            // Iterate at end to guarantee one run through of loop
            else
                empty = true;
        }
        return null; // Impossible to reach
    }
    
    /**
     * Generates the list of nodes inside the shortest path
     * 
     * @param visited
     *            The destination node
     * @return
     *         The shortest path
     */
    private List<String> generatePath(Node visited) {
        List<String> path = new ArrayList<String>();
        addNodeToPath(path, visited); // Start Recursion
        return path;
    }
    
    /**
     * Recursive method to add nodes to the path
     * 
     * @param path
     *            The shortest path from A to B
     * @param visited
     *            The current node
     */
    private void addNodeToPath(List<String> path, Node visited) {
        if (visited != null) {
            // Work "bottom" up to build path correctly
            addNodeToPath(path, visited.parent);
            // Then add the node
            path.add(visited.node);
        }
    }
    
    /**
     * Private class for Dijkstra's Algorithm
     * 
     * @author andreweng
     *
     */
    private class Node implements Comparable<Node> {
        String node; // Name of the vertex
        Node parent; // The parent of the vertex; where it came from
        int cost; // The cost to reach this node
        
        public Node(String node, int cost) {
            this.node = node;
            parent = null;
            this.cost = cost;
        }
        
        public Node(String node, Node parent, int cost) {
            this.node = node;
            this.parent = parent;
            this.cost = cost;
        }
        
        @Override
        public int compareTo(Node o) {
            return cost - o.cost;
        }
        
        @Override
        public String toString() {
            if (parent != null)
                return "Node: " + node + "\nParent: " + parent.node + "\nCost: " + cost + "\n";
            else
                return "Node: " + node + "\nParent: null\nCost: " + cost + "\n";
        }
    }
}
