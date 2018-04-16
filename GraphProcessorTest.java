import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

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
    private WordProcessor wProc;
    private String f1, f2, f3;
    
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }
    
    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }
    
    @Before
    public void setUp() throws Exception {
        gProc = new GraphProcessor();
        wProc = new WordProcessor();
        f1 = "exDict.txt";
        f2 = "largeDict.txt";
        f3 = "exDict_needTrim.txt";
    }
    
    @After
    public void tearDown() throws Exception {
        gProc = null;
        f1 = null;
        f2 = null;
    }
    
    // INSERT TESTS HERE:
    /*
     * Lists of tests to be conducted (Please add your own stuff to it!):
     * 
     */
    @Test
    public final void fullGraphTest() {
        gProc.populateGraph(f1);
        String[] exPath;
        List<String> acPath;
        int exLen;
        int acLen;
        
        String[][][] paths = {
                {
                        null,
                        null,
                        { "AT", "BAT", "BIT" },
                        { "AT", "BAT" },
                        { "AT", "BAT", "BAIT" },
                        { "AT", "BAT", "BIT", "BITS" },
                        null
                },
                {
                        null,
                        null,
                        { "AT", "BAT", "BIT" },
                        { "AT", "BAT" },
                        { "AT", "BAT", "BAIT" },
                        { "AT", "BAT", "BIT", "BITS" },
                        null
                },
                {
                        { "BIT", "BAT", "AT" },
                        { "BIT", "BAT", "AT" },
                        null,
                        { "BIT", "BAT" },
                        { "BIT", "BAIT" },
                        { "BIT", "BITS" },
                        null
                },
                {
                        { "BAT", "AT" },
                        { "BAT", "AT" },
                        { "BAT", "BIT" },
                        null,
                        { "BAT", "BAIT" },
                        { "BAT", "BIT", "BITS" },
                        null
                },
                {
                        { "BAIT", "BAT", "AT" },
                        { "BAIT", "BAT", "AT" },
                        { "BAIT", "BIT" },
                        { "BAIT", "BAT" },
                        null,
                        { "BAIT", "BIT", "BITS" },
                        null
                },
                {
                        { "BITS", "BIT", "BAT", "AT" },
                        { "BITS", "BIT", "BAT", "AT" },
                        { "BITS", "BIT" },
                        { "BITS", "BIT", "BAT" },
                        { "BITS", "BIT", "BAIT" },
                        null,
                        null
                },
                {
                        null,
                        null,
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
                        fail("Expected Size: " + exLen + "\nGot: " + acLen + "\n" + words.get(i) + " " + words.get(j));
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
    public final void popMultiGraph() {
        gProc.populateGraph(f1);
        gProc.populateGraph(f2);
        
        String[] expect = { "BAT", "AT", "AD", "AID", "AVID" };
        List<String> act = gProc.getShortestPath("bat", "avid");
        if (expect.length != act.size())
            fail("Did not return the same path");
        for (int i = 0; i < expect.length; i++) {
            assertEquals("Dif word in path: (" + act.get(i) + " vs " + expect[i] + ")", act.get(i), expect[i]);
        }
    }
    
    @Test
    public final void addDup() {
        gProc.populateGraph(f1);
        assertEquals("Allowed Duplicates", gProc.getShortestPath("at", "at"), null);
    }
    
    
    @Test
    public final void getEachElement() {
    	try { 
    		String[] expect = { "AT", "AT", "BIT", "BAT", "BAIT", "BITS", "DOG" };
    		Stream<String> stream = wProc.getWordStream(f1);
    		String[] result = stream.toArray(String[]::new);
    		// Check length
    		assertEquals("Length not match, expected is "+ expect.length + ", actual is " + result.length,
    				expect.length, result.length);
    		//  Check elements
    		for ( int i = 0 ; i < expect.length ; i++) {
    			assertEquals("Element "+i+" is not equal. ("+expect[i]+","+result[i]+")",
    					expect[i], result[i]);
    		}
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    
    @Test
    public final void wProcTrim() {
    	try {
        	String[] expect = { "AT", "BIT", "BAT", "BAIT", "BITS", "DOG" };
        	Stream<String> stream = wProc.getWordStream(f3);
    		String[] result = stream.toArray(String[]::new);
    		assertEquals("Length not match, expected is "+ expect.length + ", actual is " + result.length,
    				expect.length, result.length);
    		for ( int i = 0 ; i < expect.length ; i++) {
    			assertEquals("Element "+i+" is not equal. ("+expect[i]+","+result[i]+")",
    					expect[i], result[i]);
    		}
    	} catch(IOException e) {
    		e.printStackTrace();
    	}	
    }
    /*
    @Test
    public final void sameWordPath() {
    	gProc.populateGraph(f1);
    	List<String> actual = gProc.getShortestPath("bat", "bat");
    	String[] expected = null;
    	assertEquals("Different word in path: (" + actual + "vs" + expected + ")", expected, actual);
    }
    
    @Test
    public final void sameWordDistcance() {
    	gProc.populateGraph(f1);
    	int actual = gProc.getShortestDistance("bat", "bat");
    	int expected = -1;
    	assertEquals("Different distance: " + actual + "vs" + expected, expected, actual);
    }
    */
}