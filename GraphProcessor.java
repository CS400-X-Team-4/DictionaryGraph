import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Stack;

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
//// Other Source:     (1) Floyd-Warshall algorithm: 
//                       - https://goo.gl/Pnr7bg (Wikipedia)
//                       - https://goo.gl/7p6LSL 
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
    Hashtable<String, Integer> vMap;
    Hashtable<Integer, String> iMap;
    Integer graphSize;
    Integer MaxDistance;
    Integer[][] distanceMatrix;  // store the shortest distance between two vertices
    Integer[][] predecessor;     // store the predecessor index
    
    /**
     * Constructor for this class. Initializes instances variables to set the starting state of the object
     */
    public GraphProcessor() {
        this.graph = new Graph<>();
        vMap = new Hashtable<String, Integer>();
        iMap = new Hashtable<Integer, String>();
        MaxDistance = Integer.MAX_VALUE;
        distanceMatrix = new Integer[graphSize][graphSize];
        predecessor = new Integer[graphSize][graphSize];
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
        return 0;
        
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
        // Look up the predecessor matrix to find the shortest path
    	int i = this.vMap.get(word1);
    	int j = this.vMap.get(word2);
    	Stack<String> st = new Stack<String>();
    	List<String> pathList = new ArrayList<String>();
    	st.add(word2);  // destination
    	while (predecessor[i][j] != -1) {
    		j = predecessor[i][j];
    		String v = this.iMap.get(j);
    		st.add(v);
    	}
    	pathList.add(st.pop());
        return pathList; 
    }
    
    /**
     * Gets the distance of the shortest path between word1 and word2
     * 
     * Example: Given a dictionary,
     * cat
     * rat
     * hat
     * neat
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
        // Look up distanceMatrix to get shortest distance
    	int i = this.vMap.get(word1);
    	int j = this.vMap.get(word2);
        return this.distanceMatrix[i][j];
    }
    
    /**
     * Get all vertices, and map each vertex to an index
     */
    private void buildVerticesMapping() {
    	Iterable<String> allVertices = this.graph.getAllVertices();
    	int size = 0;
    	while ( allVertices.iterator().hasNext() ) {
    		allVertices.iterator().next();
    		this.vMap.put(allVertices.iterator().toString(), size);  // [key, index]
    		this.iMap.put(size, allVertices.iterator().toString());  // [index, key]
    		size++;
    	}
    	this.graphSize = size;
    }
    
    /**
     * Computes shortest paths and distances between all possible pairs of vertices.
     * This method is called after every set of updates in the graph to recompute the path information.
     * Any shortest path algorithm can be used (Djikstra's or Floyd-Warshall recommended).
     */
    public void shortestPathPrecomputation() {
        // Using Floyd-Warshall algorithm
    	buildVerticesMapping();
    	int len = this.graphSize;
    	
    	// Set up: d to self is 0, to adjacent vertex is 1, unreachable is infinity
    	//         pred to self and unreachable is -1, otherwise is i 
    	for (int i = 0; i < len; i ++) {
    		for (int j = 0; j < len; j ++) {
    			if (i == j ) {
    				distanceMatrix[i][j] = 0;
    				predecessor[i][j] = -1;
    			} else if (graph.isAdjacent(this.iMap.get(i), this.iMap.get(j))){
    				distanceMatrix[i][j] = 1;
    				distanceMatrix[j][i] = 1;
    				predecessor[i][j] = i; 
    			} else { // not adjacent vertex
    				distanceMatrix[i][j] = MaxDistance;
    				distanceMatrix[j][i] = MaxDistance;
    				predecessor[i][j] = -1; 
    			}
    		}
    	}
    	
    	// Main loop: if find the shorter path, update d to distanceMatrix and update predecessor
    	for (int k = 0; k < len; k++) {
    		for (int i = 0; i < len; i++) {
    			for (int j = 0; j < len; j++) {
    				if ((distanceMatrix[i][k] == MaxDistance) 
    					    || (distanceMatrix[k][j] == MaxDistance)) {
    					continue;
    				}
    				if ((distanceMatrix[i][j] > distanceMatrix[i][k] + distanceMatrix[k][j])
    						&& (distanceMatrix[i][k] != MaxDistance)) {
    					distanceMatrix[i][j] = distanceMatrix[i][k] + distanceMatrix[k][j];
    					predecessor[i][j] = predecessor[k][j];
    				}
    			}
    		}
    	}
    }
}
