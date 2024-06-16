package com.mygui;

<<<<<<< HEAD
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
=======
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;

/**
 * An Interface which provides several means to deal with the graph.
 */
public class Interface {

  /**
   * Creates a TextGraph from the content of the specified file.
   *
   * @param filename the name of the file to read from
   * @return the created TextGraph
   */
  public static TextGraph createTextGraph(String filename) {
    // 读取文件
    File file = new File(filename);
    BufferedReader fin = null;
    try {
      fin = new BufferedReader(new FileReader(file));
    } catch (FileNotFoundException e) {
      System.out.println("File not found");
      e.printStackTrace();
    }

    // 文件内容读入data字符串
    StringBuilder data = new StringBuilder();
    String line;
    try {
      if (fin != null) {
        while ((line = fin.readLine()) != null) {
          data.append(line).append(" ");
        }
      }
    } catch (IOException e) {
      System.out.println("Read file failed");
      e.printStackTrace();
    }

    // 预处理符号，特殊字符
    for (int i = 0; i < data.length() - 1; i++) {
      if (!(data.charAt(i) >= 'a' && data.charAt(i) <= 'z')
          && !(data.charAt(i) >= 'A' && data.charAt(i) <= 'Z')) {
        data = new StringBuilder(data.substring(0, i) + " " + data.substring(i + 1));
      } else if (data.charAt(i) >= 'A' && data.charAt(i) <= 'Z') { // 大写换成小写
        char lowerCase = (char) (data.charAt(i) + ('a' - 'A'));
        data = new StringBuilder(data.substring(0, i) + lowerCase + data.substring(i + 1));
      }
    }

    // 单词放入字符串数组 wordList
    String[] wordList = data.toString().split("\\s+");

    // 关闭文件
    try {
      if (fin != null) {
        fin.close();
      }
    } catch (IOException e) {
      System.out.println("File close failed");
      e.printStackTrace();
    }

    // 使用单词数组创建图
    TextGraph graph = new TextGraph();
    return graph.createTextGraph(wordList);
  }

  /**
   * Visualizes the given TextGraph and saves the output to a specified path.
   *
   * @param g          the TextGraph to visualize
   * @param path       the path to save the Graph.dot file
   * @param outputPath the path to save the generated PNG file
   * @param color      whether to use color in the visualization
   * @throws IOException if an I/O error occurs
   */
  public static void showTextGraph(TextGraph g, String path, String outputPath,
                                   boolean color) throws IOException {
    long stime = System.currentTimeMillis();

    String gvPath = path + "/Graph.dot";
    String outputfilePath = outputPath + "/Graph.png";

    // 生成dot文件
    g.showTextGraph(gvPath, color);

    long etime = System.currentTimeMillis();
    System.out.printf("calculated in %d ms.%n", (etime - stime));

    // 命令行输出png
    try {
      String command =
          "dot -Tpng -o " + outputfilePath + " " + gvPath;
      Process process = Runtime.getRuntime().exec(command);
      try {
        process.waitFor();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    } catch (IOException e) {
      System.out.println("IOException");
    }
  }

  /**
   * Queries bridge words between two given words in the graph.
   *
   * @param g         the TextGraph to query
   * @param rawWord1 the first word
   * @param rawWord2 the second word
   * @return the bridge words between the two words
   */
  public static String queryBridgeWords(TextGraph g, String rawWord1, String rawWord2) {

    long stime = System.currentTimeMillis();


    // 转换为小写
    String word1 = rawWord1.toLowerCase();
    String word2 = rawWord2.toLowerCase();

    String result;

    Set<String> bridgeWords = g.queryBridgeWords(word1, word2);
    if (bridgeWords == null) {
      System.out.println("No \"" + word1 + "\" or \"" + word2 + "\" in the graph!");
      result = "No \"" + word1 + "\" or \"" + word2 + "\" in the graph!";

      long etime = System.currentTimeMillis();
      System.out.printf("calculated in %d ms.%n", (etime - stime));

    } else if (bridgeWords.isEmpty()) {
      System.out.println("No bridge words from \"" + word1 + "\" to \"" + word2 + "\"!");
      result = "No bridge words from \"" + word1 + "\" to \"" + word2 + "\"!";

      long etime = System.currentTimeMillis();
      System.out.printf("calculated in %d ms.%n", (etime - stime));

    } else {
      StringBuilder bridgeString;
      if (bridgeWords.size() == 1) {
        bridgeString = new StringBuilder("\" is: ");
        for (String word : bridgeWords) {
          bridgeString.append(word);
        }
        bridgeString.append(".");
        System.out.println("The bridge word from \"" + word1 + "\" to \"" + word2 + bridgeString);
        result = "The bridge word from \"" + word1 + "\" to \"" + word2 + bridgeString;

        long etime = System.currentTimeMillis();
        System.out.printf("calculated in %d ms.%n", (etime - stime));

      } else {
        bridgeString = new StringBuilder("\" are: ");
        int counter = 0;
        for (String word : bridgeWords) {
          // 输出最后一个 word
          if (counter == bridgeWords.size() - 1) {
            bridgeString.append("and ").append(word).append(".");
          } else {
            bridgeString.append(word).append(", ");
            counter++;
          }
        }
        System.out.println("The bridge words from \"" + word1 + "\" to \"" + word2 + bridgeString);
        result = "The bridge words from \"" + word1 + "\" to \"" + word2 + bridgeString;

        long etime = System.currentTimeMillis();
        System.out.printf("calculated in %d ms.%n", (etime - stime));

      }
    }
    return result;
  }

  /**
     * Generates a new text based on the input text using the TextGraph.
     *
     * @param g the TextGraph to use
     * @param inputText the input text to process
     * @return the generated new text
     */
  public static String generateNewText(TextGraph g, String inputText) {
    long stime = System.currentTimeMillis();
    // 预处理，单词放入字符串数组
    for (int i = 0; i < inputText.length() - 1; i++) {
      if (!(inputText.charAt(i) >= 'a' && inputText.charAt(i) <= 'z')
          && !(inputText.charAt(i) >= 'A' && inputText.charAt(i) <= 'Z')) {
        inputText = inputText.substring(0, i) + " " + inputText.substring(i + 1);
      } else if (inputText.charAt(i) >= 'A' && inputText.charAt(i) <= 'Z') { /* 大写换成小写 */
        char lowerCase = (char) (inputText.charAt(i) + ('a' - 'A'));
        inputText = inputText.substring(0, i) + lowerCase + inputText.substring(i + 1);
      }
    }
    String[] split = inputText.split("\\s+");
    ArrayList<String> wordList = new ArrayList<>(Arrays.asList(split));

    String newText = g.generateNewText(wordList);

    long etime = System.currentTimeMillis();
    System.out.printf("calculated in %d ms.%n", (etime - stime));

    System.out.println(newText);
    return newText;
  }

  /**
     * Calculates the shortest path between two words in the graph.
     *
     * @param g the TextGraph to use
     * @param rawWord1 the first word
     * @param rawWord2 the second word
     * @return the shortest path between the two words
     */
  public static String calcShortestPath(TextGraph g, String rawWord1, String rawWord2) {
    long stime = System.currentTimeMillis();
    //转换成小写
    String word1 = rawWord1.toLowerCase();
    String word2 = rawWord2.toLowerCase();

    String result = g.dijkstra(word1, word2);

    long etime = System.currentTimeMillis();
    System.out.printf("calculated in %d ms.%n", (etime - stime));

    return result;
  }

  /**
     * Calculates the shortest path from a word to all reachable nodes in the graph.
     *
     * @param g the TextGraph to use
     * @param rawWord the word to start from
     * @return the shortest path from the word to all reachable nodes
     */
  public static String calcShortestPath(TextGraph g, String rawWord) {
    long stime = System.currentTimeMillis();

    String word = rawWord.toLowerCase();
    String result = g.dijkstraSingleWord(word);

    long etime = System.currentTimeMillis();
    System.out.printf("calculated in %d ms.%n", (etime - stime));

    return result;
  }

  /**
   * Performs a random walk in the graph starting from the given word.
   *
   * @param g       the TextGraph to use
   * @param rawWord the word to start from
   * @throws IOException if an I/O error occurs
   */
  public static void randomWalk(TextGraph g, String rawWord) throws IOException {
    long stime = System.currentTimeMillis();

    String word = rawWord.toLowerCase();
    g.random(word);

    long etime = System.currentTimeMillis();
    System.out.printf("calculated in %d ms.%n", (etime - stime));

  }

  /**
     * The main method to demonstrate the functionality of the TextGraphUtils class.
     *
     * @param args command line arguments
     */
  public static void main(String[] args) {
    TextGraph graph = createTextGraph("src/file/GraphData.txt");

    try { // 展示有向图
      showTextGraph(graph, "src/file", "src/file", false);
    } catch (IOException e) {
      System.out.println("IOException");
    }

    // 查询bridge word
    queryBridgeWords(graph, "strange", "worlds");

    // 根据bridge word生成新文本
    generateNewText(graph, "Seek to explore new and exciting synergies");

    // 计算两个单词之间的最短路径
    calcShortestPath(graph, "new", "and");
    try {
      showTextGraph(graph, "src/file/normal", "src/file/normal", true);
    } catch (IOException e) {
      System.out.println("IOException");
    }

    // 计算一个单词到所有可达节点的最短路径
    calcShortestPath(graph, "to");


    try { // 随机游走
      randomWalk(graph, "to");
    } catch (IOException e) {
      e.printStackTrace();
    }

  }
}
>>>>>>> SpotBugFinish
