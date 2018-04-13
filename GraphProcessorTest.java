import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
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
//                   GraphProcessorTest.java
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
// 2018 Apr 16, 2018 GraphProcessorTest.java 
////////////////////////////80 columns wide //////////////////////////////////

/**
 * Junit test class to test class @see GraphProcessor
 *
 * @author
 */
public class GraphProcessorTest {
    
    private GraphProcessor gProc;
    private WordProcessor wdProc;
    private String file;
    
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }
    
    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }
    
    @Before
    public void setUp() throws Exception {
        gProc = new GraphProcessor();
        wdProc = new WordProcessor();
        file = "exDict.txt";
    }
    
    @After
    public void tearDown() throws Exception {
        gProc = null;
        wdProc = null;
        file = null;
    }
    
    // INSERT TESTS HERE:
    /*
     * Lists of tests to be conducted (Please add your own stuff to it!):
     * 
     */
    @Test
    public final void fullGraphTest() {
        gProc.populateGraph(file);
        String[] exPath;
        List<String> acPath;
        int exLen;
        int acLen;
        
        String[][][] paths = {
                {
                        null,
                        { "AT", "BAT", "BIT" },
                        { "AT", "BAT" },
                        { "AT", "BAT", "BAIT" },
                        null
                },
                {
                        { "BIT", "BAT", "AT" },
                        null,
                        { "BIT", "BAT" },
                        { "BIT", "BAIT" },
                        null
                },
                {
                        { "BAT", "AT" },
                        { "BAT", "BIT" },
                        null,
                        { "BAT", "BAIT" },
                        null
                },
                {
                        { "BAIT", "BAT", "AT" },
                        { "BAIT", "BIT" },
                        { "BAIT", "BAT" },
                        null,
                        null
                },
                {
                        null,
                        null,
                        null,
                        null,
                        null
                }
        };
        
        try {
            List<String> words = Files.readAllLines(Paths.get("exDict.txt"));
            for (int i = 0; i < words.size(); i++) {
                for (int j = 0; j < words.size(); j++) {
                    exPath = paths[i][j];
                    acPath = gProc.getShortestPath(words.get(i), words.get(j));
                    if (paths[i][j] != null)
                        exLen = paths[i][j].length - 1;
                    else
                        exLen = -1;
                    acLen = gProc.getShortestDistance(words.get(i), words.get(j));
                    if (exLen != acLen)
                        fail("Expected Size: " + exLen + "\nGot: " + acLen);
                    if ((acPath == null && exPath != null) || (acPath != null && exPath == null))
                        fail("Paths don't match nulls");
                    else if (acPath != null && exPath != null) {
                        for (int k = 0; k < exPath.length; k++) {
                            assertEquals("Index: " + k + "\nExpected item: " + exPath[k] + "\nGot: " + acPath.get(k), exPath[k], acPath.get(k));
                        }
                    }
                    
                }
            }
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            fail("Could not read file");
        }
        
    }
    
    @Test
    public final void popGraph() {
        gProc.populateGraph(file);
    }
    
    @Test
    public final void addDup() {
        gProc.populateGraph(file);
        gProc.populateGraph(file);
        
        assertEquals("Allowed Duplicates", gProc.getShortestPath("at", "at"), null);
    }
    
    @Test
    public final void test() {
        
    }
}