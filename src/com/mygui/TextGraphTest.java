package com.mygui;

import org.junit.Test;
import org.junit.Before;

import java.io.IOException;

import static com.mygui.Interface.showTextGraph;
import static org.junit.Assert.*;

public class TextGraphTest {

    private TextGraph graph;

    @Before
    public void setUp() throws IOException {
        graph = new TextGraph();
        String[] words = {"hello", "world", "is", "test", "hello", "test", "is", "world", "another", "test", "another", "example"};
        graph.createTextGraph(words);
        showTextGraph(graph, "src/file/test2", "src/file/test2", false);
    }


    @Test
    public void CommonTest() {
        String result = graph.dijkstra("hello", "another");
        assertNotNull(result);
        assertEquals("hello -> world -> another\n" + "Distance is 2", result);
    }

    @Test
    public void NotExistTest() {
        String result = graph.dijkstra("hello", "notexist");
        assertNull(result);
    }

    @Test
    public void NoPathTest() {
        String result = graph.dijkstra("example", "another");
        assertNotNull(result);
        assertEquals("", result);
    }
}