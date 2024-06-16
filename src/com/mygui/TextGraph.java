package com.mygui;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.security.SecureRandom;
import java.util.Set;

/**
 * TextGraph.
 */
@SuppressFBWarnings("DM_DEFAULT_ENCODING")
public class TextGraph {

  /**
   * Node class representing a word and its count and color in the graph.
   */
  public static class Node {
    String word;
    int count;
    String color;

    /**
     * Constructs a new Node with the specified word.
     *
     * @param word the word of the node
     */
    public Node(String word) {
      this.word = word;
      count = 1;
      color = "";
    }

  }

  // ͼ���ݽṹ
  String root;
  private final Set<String> nodeSet; // ���нڵ����Ƶ��ֵ�
  private final Map<String, LinkedList<Node>> nodeInfo; // ���нڵ�������洢��Ӧ�ӽڵ������
  private static final int max = 9999;

  /**
   * Constructs a new TextGraph.
   */
  public TextGraph() {
    nodeInfo = new HashMap<>();
    nodeSet = new LinkedHashSet<>();
  }

  /**
   * Creates a TextGraph from an array of words.
   *
   * @param wordList the array of words to create the graph from
   * @return the created TextGraph
   */
  public TextGraph createTextGraph(String[] wordList) {
    for (int i = 0; i < wordList.length - 1; i++) {

      // �������ڵ�
      if (i == 0) {
        root = wordList[i];
      }

      // ���ʼ��뼯�� Node_set
      String word1 = wordList[i];
      String word2 = wordList[i + 1];
      boolean existed1 = nodeSet.add(word1);

      if (i == wordList.length - 2) {
        nodeSet.add(word2);
      }

      // ���ڵ�δ���ֹ�������Node_info������
      if (existed1) {
        LinkedList<Node> neighbour = new LinkedList<>();
        nodeInfo.put(word1, neighbour);
      }

      // ���ڵ�����ӱ�
      addEdge(word1, word2);
    }

    return this;
  }

  /**
   * Adds an edge between two nodes in the graph.
   *
   * @param word1 the first word
   * @param word2 the second word
   */
  public void addEdge(String word1, String word2) {
    LinkedList<Node> neighbour = nodeInfo.get(word1);
    boolean existed = false;

    if (neighbour != null) {
      for (Node node : neighbour) {

        // �� word2 �Ѿ��� word1 ���ھӣ�count + 1
        if (node.word.equals(word2)) {
          node.count += 1;
          existed = true;
        }
      }
    }

    // �� word2 ���� word1 ���ھӣ���������
    if (!existed) {
      Node node2 = new Node(word2);
      if (neighbour != null) {
        neighbour.add(node2);
      } else {
        System.out.print("neighbour pointer empty\n");
      }
    }
  }

  /**
   * Writes the graph in DOT format to a file.
   *
   * @param path  the path to save the DOT file
   * @param color whether to include color in the visualization
   * @throws IOException if an I/O error occurs
   */
  @SuppressFBWarnings("PATH_TRAVERSAL_OUT")
  public void showTextGraph(String path, boolean color) throws IOException {
    try(FileWriter fileWriter = new FileWriter(path)) {

      fileWriter.write("digraph TextGraph {\r\n");

      // д��ڵ���Ϣ
      for (String word : nodeSet) {
        fileWriter.write(word + ";\r\n");
      }

      // д�����Ϣ
      Set<String> visited = new HashSet<>();
      writeEdge(fileWriter, root, visited, color);

      fileWriter.write("}");
    } catch (FileNotFoundException e) {
      System.out.println("File not Found");
    }
  }

  /**
   * Writes edges for the node and its neighbors in DOT format to the file.
   *
   * @param fileWriter the FileWriter to write to
   * @param word       the current word
   * @param visited    the set of visited nodes
   * @param color      whether to include color in the visualization
   * @throws IOException if an I/O error occurs
   */
  public void writeEdge(FileWriter fileWriter, String word, Set<String> visited,
                        boolean color) throws IOException {
    // �ж��Ƿ��Ѿ����ʹ�
    if (visited.contains(word)) {
      return;
    } else {
      visited.add(word);
    }

    if (!color) {
      if (nodeSet.contains(word)) {
        LinkedList<Node> neighbour = nodeInfo.get(word);

        if (neighbour != null) {
          for (Node node : neighbour) {
            fileWriter.write(word + " -> " + node.word + " [label = "
                + node.count + "];\r\n");
            writeEdge(fileWriter, node.word, visited, false);
          }
        }
      }
    } else { // ��ʾ·��
      if (nodeSet.contains(word)) {
        LinkedList<Node> neighbour = nodeInfo.get(word);

        if (neighbour != null) {
          for (Node node : neighbour) {
            // ���ڵ���·����
            if (!node.color.isEmpty()) {
              fileWriter.write(word + " -> " + node.word + " [label = "
                  + node.count + ", color = " + node.color + "];\r\n");
            } else {
              fileWriter.write(word + " -> " + node.word + " [label = "
                  + node.count + "];\r\n");
            }

            writeEdge(fileWriter, node.word, visited, true);
          }
        }
      }
    }
  }

  /**
   * Queries bridge words between two given words in the graph.
   *
   * @param word1 the first word
   * @param word2 the second word
   * @return a set of bridge words between the two words
   */
  public Set<String> queryBridgeWords(String word1, String word2) {

    Set<String> bridgeWords = new HashSet<>();

    // �����ʲ���ͼ�� ���� null
    if (!nodeSet.contains(word1) || !nodeSet.contains(word2)) {
      return null;
    }

    ArrayList<String> bridges = getNeighbour(word1);
    if (!bridges.isEmpty()) {
      // �ж��ŵ��ھ��Ƿ��� word2
      for (String bridge : bridges) {
        ArrayList<String> nextNeighbour = getNeighbour(bridge);
        if (!nextNeighbour.isEmpty()) {
          for (String word : nextNeighbour) {
            if (word.equals(word2)) {
              bridgeWords.add(bridge);
            }
          }
        }
      }
    }
    return bridgeWords;
  }

  /**
   * Gets the neighbors of a node.
   *
   * @param word the word of the node
   * @return a list of neighbor words
   */
  public ArrayList<String> getNeighbour(String word) {
    ArrayList<String> neighbours = new ArrayList<>();
    LinkedList<Node> neighbourList = nodeInfo.get(word);
    if (neighbourList != null && !neighbourList.isEmpty()) {
      for (Node neighbour : neighbourList) {
        neighbours.add(neighbour.word);
      }
    }
    return neighbours;
  }

  /**
   * Generates new text by inserting bridge words between consecutive words.
   *
   * @param wordList the list of words to process
   * @return the generated new text
   */
  public String generateNewText(ArrayList<String> wordList) {
    String newText = "";
    for (int i = 0; i < wordList.size() - 1; i++) {
      newText = newText + wordList.get(i) + " ";
      Set<String> bridgeWords = queryBridgeWords(wordList.get(i), wordList.get(i + 1));
      if (bridgeWords != null && !bridgeWords.isEmpty()) {
        for (String word : bridgeWords) {
          newText = newText + word + " ";
        }
      }
      if (i == wordList.size() - 2) {
        newText += wordList.get(i + 1);
      }
    }

    return newText;
  }

  /**
   * Calculates the shortest path between two words using Dijkstra's algorithm.
   *
   * @param word the starting word
   * @param f    the ending word
   * @return the shortest path and its distance
   */
  public String dijkstra(String word, String f) {
    // ����Ƿ���ͼG��
    if (!nodeSet.contains(word) || !nodeSet.contains(f)) {
      return null;
    }

    int size = nodeSet.size();

    // ʹ������mapά��һ����ŵ��ַ�����˫��
    int cnt = 0;
    HashMap<String, Integer> stoi = new HashMap<>();
    HashMap<Integer, String> itos = new HashMap<>();
    for (String str : nodeSet) {
      stoi.put(str, cnt);
      itos.put(cnt, str);
      cnt++;
    }

    int[][] matrix = new int[size][size];

    // ��ʼ��
    for (int i = 0; i < size; i++) {
      for (int j = 0; j < size; j++) {
        matrix[i][j] = (i == j) ? 0 : max;
      }
    }

    // ���matrix
    for (int i = 0; i < size; i++) {
      // ����set��ÿ��String������
      LinkedList<Node> neighbours = nodeInfo.get(itos.get(i));
      if (neighbours == null) {
        continue;
      }
      for (Node n : neighbours) {
        matrix[i][stoi.get(n.word)] = n.count;
      }
    }

    // �������нڵ���г�ʼ��
    // ��¼�Ƿ��Ѿ��ҵ����·��
    int[] flag = new int[size];
    // ��¼·����ǰһ���ڵ�
    int[] prev = new int[size];
    // ��¼��̾���
    int[] dist = new int[size];
    for (int i = 0; i < size; i++) {
      flag[i] = 0;
      prev[i] = stoi.get(word);
      dist[i] = matrix[stoi.get(word)][i];
    }

    flag[stoi.get(word)] = 1; // ��word�����ʼ��
    dist[stoi.get(word)] = 0;

    // ����size-2�Σ�ÿ���ҳ�һ����word����Ľڵ�
    int min;
    int tmp;
    int record = 0;
    for (int i = 0; i < size - 1; i++) {
      min = max;
      for (int j = 0; j < size; j++) {
        if (flag[j] == 0 && dist[j] < min) {
          min = dist[j];
          record = j;
        }
      }

      flag[record] = 1;
      if (record == stoi.get(f)) {
        break;
      }

      for (int j = 0; j < size; j++) {
        tmp = (matrix[record][j] == max) ? max : min + matrix[record][j];
        if (flag[j] == 0 && tmp < dist[j]) {
          prev[j] = record;
          dist[j] = tmp;
        }
      }
    }

    // ���·���ͳ���
    String outputstr = "";
    String tmpstr = f;
    while (!tmpstr.equals(word)) {
      String preword = itos.get(prev[stoi.get(tmpstr)]);
      LinkedList<Node> preNeighbour = nodeInfo.get(preword);
      if (preNeighbour != null && !preNeighbour.isEmpty()) {
        // ��Ӧ�߸���ɫ
        for (Node node : preNeighbour) {
          if (node.word.equals(tmpstr)) {
            node.color = "blue";
            break;
          }
        }
      }

      outputstr = " -> " + tmpstr + outputstr;
      tmpstr = preword;
    }
    if (dist[stoi.get(f)] == max) {
      return "";
    }
    System.out.println(word + outputstr);
    System.out.println("Distance is " + dist[stoi.get(f)]);
    return word + outputstr + "\nDistance is " + dist[stoi.get(f)];
  }

  /**
   * Calculates the shortest paths from a word to all reachable nodes.
   *
   * @param word the starting word
   * @return the shortest paths to all reachable nodes
   */
  public String dijkstraSingleWord(String word) {
    StringBuilder result = new StringBuilder();
    if (!nodeSet.contains(word)) {
      return null;
    } else {
      for (String str : nodeSet) {
        if (!word.equals(str)) {
          result.append(dijkstra(word, str)).append("\n");
        }
      }
      return result.toString();
    }
  }

  /**
   * Interface defining the observer for random walks  �ӿڶ��壬���ڸ���GUI.
   */
  public interface RandomWalkObserver {
    void update(String message);

    void finish();

    void stopped();  // ����ֹ֪ͣͨ
  }

  /**
   * Add an instance variable of the Random object
   */
  private SecureRandom random = new SecureRandom();

  /**
   * Performs a random walk in the graph starting from the given word.
   *
   * @param startWord the word to start from
   * @param observer  the observer to update during the walk
   * @throws IOException if an I/O error occurs
   */
  public void random(String startWord, RandomWalkObserver observer) throws IOException {
    HashMap<String, LinkedList<Node>> record = new HashMap<>();
    StringBuilder route = new StringBuilder(startWord + " -> ");
    String tmp = startWord;
    String pre;

    observer.update(route.toString());
    try {
      Thread.sleep(1000);  // ��ͣ1��
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();  // �����ж�״̬
    }

    while (nodeInfo.containsKey(tmp) && !Thread.currentThread().isInterrupted()) { // ����߳��Ƿ��ж�
      pre = tmp;
      LinkedList<String> minus = new LinkedList<>();

      for (Node n : nodeInfo.get(tmp)) {
        minus.push(n.word);
      }

      if (record.containsKey(tmp)) {
        for (Node n : record.get(tmp)) {
          minus.remove(n.word);
        }
      }

      if (minus.isEmpty()) {
        break;
      }

      int num = random.nextInt(minus.size());
      tmp = minus.get(num);
      route.append(tmp).append(" -> ");

      if (!record.containsKey(pre)) {
        LinkedList<Node> list = new LinkedList<>();
        list.push(new Node(tmp));
        record.put(pre, list);
      } else {
        record.get(pre).push(new Node(tmp));
      }

      observer.update(tmp + " -> ");
      try {
        Thread.sleep(1000);  // ��ͣ1��
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();  // �����ж�״̬
      }
    }

    route.append("Finish");
    try (FileWriter fileWriter = new FileWriter("src/file/randomWalk.txt", false)) {
      if (Thread.currentThread().isInterrupted()) {
        observer.stopped();  // ����ֹ֪ͣͨ
        fileWriter.write(route.toString() + " (Stopped)\n");
      } else {
        observer.finish();  // �������֪ͨ
        fileWriter.write(route.toString() + "\n");
      }
    }
  }

  /**
   * Performs a random walk in the graph starting from the given word.
   * Interface version.
   *
   * @param word the word to start from
   * @throws IOException if an I/O error occurs
   */
  public void random(String word) throws IOException {

    HashMap<String, LinkedList<Node>> record = new HashMap<>();
    StringBuilder route = new StringBuilder();
    String tmp = word;
    String pre;

    while (nodeInfo.containsKey(tmp)) {
      pre = tmp;
      LinkedList<String> minus = new LinkedList<>(); // minus���Node_info-record������

      for (Node n : nodeInfo.get(tmp)) {
        minus.push(n.word);
      }

      if (record.containsKey(tmp)) {
        for (Node n : record.get(tmp)) {
          minus.remove(n.word);
        }
      }

      if (minus.isEmpty()) {
        break;
      }

      System.out.printf(tmp + " -> ");
      route.append(tmp).append(" -> ");

      // ���ѡ��minus�е�һ��word
      int num = random.nextInt(minus.size());
      tmp = minus.get(num);


      // ����record
      if (!record.containsKey(pre)) {
        LinkedList<Node> list = new LinkedList<>();
        list.push(new Node(tmp));
        record.put(pre, list);
      } else {
        record.get(pre).push(new Node(tmp));
      }

      try {
        Thread.sleep(1000); // ��ͣ1��
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }

    try (FileWriter fileWriter = new FileWriter("src/file/randomWalk.txt", false)) {
      System.out.printf(tmp);
      route.append(tmp);
      fileWriter.write(route + "\n");  // д��·�����ļ�
    }
  }
}
