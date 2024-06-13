package com.mygui;

import java.io.*;
import java.util.*;

public class Interface {

    public static TextGraph createTextGraph(String filename) {
        File file = new File(filename);
        try (BufferedReader fin = new BufferedReader(new FileReader(file))) {
            StringBuilder dataBuilder = new StringBuilder();
            String line;
            while ((line = fin.readLine()) != null) {
                dataBuilder.append(line).append(" ");
            }

            String data = dataBuilder.toString();

            // 预处理符号和特殊字符
            data = preprocessData(data);

            String[] wordList = data.split("\\s+");
            TextGraph graph = new TextGraph();
            return graph.createTextGraph(wordList);
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Read file failed");
            e.printStackTrace();
        }

        return null;
    }

    private static String preprocessData(String data) {
        StringBuilder processedData = new StringBuilder();
        for (int i = 0; i < data.length(); i++) {
            char c = data.charAt(i);
            if (!((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z'))) {
                processedData.append(' ');
            } else if (c >= 'A' && c <= 'Z') {
                char lowerCase = (char) (c + ('a' - 'A'));
                processedData.append(lowerCase);
            } else {
                processedData.append(c);
            }
        }
        return processedData.toString();
    }

    public static void showTextGraph(TextGraph G, String path, String outputpath, boolean color) {
        long stime = System.currentTimeMillis();

        String gvPath = path + "/Graph.dot";
        String outputfilePath = outputpath + "/Graph.png";

        try {
			G.showTextGraph(gvPath, color);
		} catch (IOException e1) {
			e1.printStackTrace();
		}

        long etime = System.currentTimeMillis();
        System.out.printf("calculated in %d ms.\n", (etime - stime));

        try {
            String command = "dot -Tpng -o " + outputfilePath + " " + gvPath;
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }

    public static String queryBridgeWords(TextGraph G, String rawWord1, String rawWord2) {
        long stime = System.currentTimeMillis();

        String word1 = rawWord1.toLowerCase();
        String word2 = rawWord2.toLowerCase();

        Set<String> bridgeWords = G.queryBridgeWords(word1, word2);
        if (bridgeWords == null || bridgeWords.isEmpty()) {
            System.out.println("No bridge words from \"" + word1 + "\" to \"" + word2 + "\"!");
            return "No bridge words from \"" + word1 + "\" to \"" + word2 + "\"!";
        }

        StringBuilder result = new StringBuilder();
        if (bridgeWords.size() == 1) {
        	result.append("The bridge words from \"").append(word1).append("\" to \"").append(word2).append("\" is: ");
        	for (String word : bridgeWords) {
                    result.append(word).append(".");
            }
        }
        else {
        	result.append("The bridge words from \"").append(word1).append("\" to \"").append(word2).append("\" are: ");
            int counter = 0;
            for (String word : bridgeWords) {
                if (counter == bridgeWords.size() - 1) {
                    result.append("and ").append(word).append(".");
                } else {
                    result.append(word).append(", ");
                }
                counter++;
            }
        }
        

        long etime = System.currentTimeMillis();
        System.out.printf("calculated in %d ms.\n", (etime - stime));

        return result.toString();
    }

    public static String generateNewText(TextGraph G, String inputText) {
        long stime = System.currentTimeMillis();

        inputText = preprocessData(inputText);
        String[] wordList = inputText.split("\\s+");
        List<String> wordListFiltered = new ArrayList<>(Arrays.asList(wordList));

        String newText = G.generateNewText(wordListFiltered);

        long etime = System.currentTimeMillis();
        System.out.printf("calculated in %d ms.\n", (etime - stime));

        System.out.println(newText);
        return newText;
    }

    public static String calcShortestPath(TextGraph G, String rawWord1, String rawWord2) {
        long stime = System.currentTimeMillis();

        String word1 = rawWord1.toLowerCase();
        String word2 = rawWord2.toLowerCase();

        String result = G.dijkstra(word1, word2);

        long etime = System.currentTimeMillis();
        System.out.printf("calculated in %d ms.\n", (etime - stime));

        return result;
    }

    public static String calcShortestPath(TextGraph G, String rawWord) {
        long stime = System.currentTimeMillis();

        String word = rawWord.toLowerCase();
        String result = G.dijkstraSingleWord(word);

        long etime = System.currentTimeMillis();
        System.out.printf("calculated in %d ms.\n", (etime - stime));

        return result;
    }

    public static String randomWalk(TextGraph G, String rawWord) {
        long stime = System.currentTimeMillis();

        String word = rawWord.toLowerCase();
        String result = null;
		try {
			result = G.random(word);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        long etime = System.currentTimeMillis();
        System.out.printf("calculated in %d ms.\n", (etime - stime));

        return result;
    }

    public static void main(String[] args) {
        TextGraph graph = createTextGraph("src/file/GraphData.txt");

        showTextGraph(graph, "src/file", "src/file", false);

        queryBridgeWords(graph, "strange", "worlds");

        generateNewText(graph, "Seek to explore new and exciting synergies");

        calcShortestPath(graph, "new", "and");

        showTextGraph(graph, "src/file/normal", "src/file/normal", true);

        calcShortestPath(graph, "to");

        randomWalk(graph, "to");
    }
}
