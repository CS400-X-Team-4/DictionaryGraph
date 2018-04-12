import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

////////////////////////////////////////////////////////////////////////////
// Semester:         CS400 Spring 2018
// PROJECT:          cs400_p3_201801
// FILES:            Graph.java
//                   GraphProcessor.java
//                   GraphTest.java
//                   WordProcessor.java
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
// 2018 Apr 16, 2018 GraphTest.java 
////////////////////////////80 columns wide //////////////////////////////////

/**
 * Junit test class to test class @see Graph that implements @see GraphADT interface
 *
 * @author sapan (sapan@cs.wisc.edu)
 */
public class GraphProcessorTest {
    
    private GraphProcessor gProc;
    private WordProcessor wdProc;
    
    private static List<String> vertices;
    
    private static int numOfVertices = 0;
    
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        vertices = new ArrayList<>();
        vertices.add("at");
        vertices.add("it");
        vertices.add("cat");
        vertices.add("hat");
        vertices.add("hot");
        vertices.add("rat");
        vertices.add("dog");
        for (String vertex : vertices)
            numOfVertices++;
    }
    
    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        vertices = null;
        numOfVertices = 0;
    }
    
    @Before
    public void setUp() throws Exception {
        gProc = new GraphProcessor();
        wdProc = new WordProcessor();
    }
    
    @After
    public void tearDown() throws Exception {
        gProc = null;
        wdProc = null;
    }
    
    // INSERT TESTS HERE:
    /*
     * Lists of tests to be conducted (Please add your own stuff to it!):
     * 
     */
    @Test
    public final void buildGraph() {
        gProc.populateGraph("exDict.txt");
        String[] expected1 = { "it", "at", "cat", "cut", "cute", "cuter" };
        int expected2 = 5;
        
        List<String> actual1 = gProc.getShortestPath("it", "cuter");
        int actual2 = gProc.getShortestDistance("it", "cuter");
        
        assertEquals("Expected path distance: " + expected2 + "\nActual path distance: " + actual2, expected2, actual2);
        for (int i = 0; i < actual1.size(); i++) {
            assertEquals("Expected: " + expected1[i].toUpperCase() + "\nActual: " + actual1.get(i), expected1[i].toUpperCase(), actual1.get(i));
        }
    }
    
    @Test
    public final void test() {
        assertEquals("0 != 0", 0, 0);
    }
}