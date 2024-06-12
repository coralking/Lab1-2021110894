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
    public void testCalcShortestPath_case1() {
        textGraph.readFile("src/test/java/org/example/test-b1.txt");
        String result = textGraph.calcShortestPath("seek", "and");
        System.out.println("case1-Result: " + result);
        assertEquals("seek->out->new->life->and", result);
    }

    @Test
    public void testCalcShortestPath_case2() {
        textGraph.readFile("src/test/java/org/example/test-b2.txt");
        String result = textGraph.calcShortestPath("eating", "studying");
        System.out.println("case2-Result: " + result);
        assertEquals("No path from \"eating\" to \"studying\"!", result);
    }

    @Test
    public void testCalcShortestPath_case3() {
        textGraph.readFile("src/test/java/org/example/test-b2.txt");
        String result = textGraph.calcShortestPath("", "");
        System.out.println("case3-Result: " + result);
        assertEquals("Both words are empty!", result);
    }

    @Test
    public void testCalcShortestPath_case4() {
        textGraph.readFile("src/test/java/org/example/test-b2.txt");
        String result = textGraph.calcShortestPath("", "new");
        System.out.println("case4-Result: " + result);
        assertEquals("One word is empty!", result);
    }

    @Test
    public void testCalcShortestPath_case5() {
        textGraph.readFile("src/test/java/org/example/test-b1.txt");
        String result = textGraph.calcShortestPath("start", "new");
        System.out.println("case5-Result: " + result);
        assertEquals("No start or new in the graph!", result);
    }

    @Test
    public void testCalcShortestPath_case6() {
        textGraph.readFile("src/test/java/org/example/test-b1.txt");
        String result = textGraph.calcShortestPath("start", "end");
        System.out.println("case6-Result: " + result);
        assertEquals("No start or end in the graph!", result);
    }
}