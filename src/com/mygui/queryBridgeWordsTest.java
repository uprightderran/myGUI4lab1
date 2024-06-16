package com.mygui;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static com.mygui.Interface.queryBridgeWords;
import static com.mygui.Interface.showTextGraph;
import static org.junit.Assert.*;

public class queryBridgeWordsTest {

    private TextGraph graph;

    @Before
    public void setUp() throws IOException {
        graph = new TextGraph();
        String[] words = {"hello", "world", "is", "test", "hello", "test", "is", "world", "another", "test", "another", "example"};
        graph.createTextGraph(words);
        showTextGraph(graph, "src/file/test", "src/file/test", false);
    }

    @Test
    public void testQueryBridgeWordsExist() {
        // 测试用例编号1
        // 等价类：(1), (2), (5), (6), (14)
        String result = queryBridgeWords(graph, "test", "example");
        assertNotNull(result);
        assertEquals("The bridge word from \"test\" to \"example\" is: another.", result);
    }

    @Test
    public void testQueryBridgeWordsMixedCase() {
        // 测试用例编号2
        // 等价类：(1), (3), (4), (8), (13)
        String result = queryBridgeWords(graph, "Test", "Test");
        assertNotNull(result);
        assertEquals("The bridge words from \"test\" to \"test\" are: another, is, and hello.", result);
    }

    @Test
    public void testQueryBridgeWordsOneBridge() {
        // 测试用例编号3
        // 等价类：(1), (2), (5), (7), (14)
        String result = queryBridgeWords(graph, "hello", "another");
        assertNotNull(result);
        assertEquals("The bridge words from \"hello\" to \"another\" are: world, and test.", result);
    }

    @Test
    public void testQueryBridgeWordsNoBridge() {
        // 测试用例编号4
        // 等价类：(1), (2), (5), (9), (14)
        String result = queryBridgeWords(graph, "another", "example");
        assertNotNull(result);
        assertEquals("No bridge words from \"another\" to \"example\"!", result);
    }

    @Test
    public void testQueryBridgeWordsNonexistentWords() {
        // 测试用例编号5
        // 等价类：(10)
        String result = queryBridgeWords(graph, "hello", "nonexist_@  ,.");
        assertNotNull(result);
        assertEquals("No \"hello\" or \"nonexist_@  ,.\" in the graph!", result);
    }

}
