package org.example;

import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

public class TextGraphTest {

    private TextGraph textGraph;

    @Before
    public void setUp() {
        textGraph = new TextGraph();
    }

    @Test
    public void testQueryBridgeWords_case1() {
        textGraph.readFile("test-w.txt", true);
        String result = textGraph.queryBridgeWords("start", "end");
        System.out.println("case1-Result: " + result);
        assertEquals("No " + "\"" + "start" + "\"" + " and " + "\"" + "end" + "\"" + " in the graph!", result);
    }

    @Test
    public void testQueryBridgeWords_case2() {
        textGraph.readFile("test-w.txt", true);
        String result = textGraph.queryBridgeWords("start", "new");
        System.out.println("case2-Result: " + result);
        assertEquals("No " + "\"" + "start" + "\"" + " in the graph!", result);
    }

    @Test
    public void testQueryBridgeWords_case3() {
        textGraph.readFile("test-w.txt", true);
        String result = textGraph.queryBridgeWords("new", "end");
        System.out.println("case3-Result: " + result);
        assertEquals("No " + "\"" + "end" + "\"" + " in the graph!", result);
    }

    @Test
    public void testQueryBridgeWords_case4() {
        textGraph.readFile("test-w.txt", true);
        String result = textGraph.queryBridgeWords("seek", "to");
        System.out.println("case4-Result: " + result);
        assertEquals("No bridge words from " + "\"" + "seek" + "\"" + " to " + "\"" + "to" + "\"" + "!", result);
    }

    @Test
    public void testQueryBridgeWords_case5() {
        textGraph.readFile("test-w.txt", true);
        String result = textGraph.queryBridgeWords("to", "strange");
        System.out.println("case5-Result: " + result);
        assertTrue(result.equals("The bridge words from " + "\"" + "to" + "\"" + " to " + "\"" + "strange" + "\"" + " are: explore, seek")
                || result.equals("The bridge words from " + "\"" + "to" + "\"" + " to " + "\"" + "strange" + "\"" + " are: seek, explore"));
    }
}
