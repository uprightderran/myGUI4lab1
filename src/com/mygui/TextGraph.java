package com.mygui;

import java.io.*;
import java.util.*;

public class TextGraph {

    // 节点定义，包括节点名称和节点出现次数
    public static class Node {
        String word;
        int count;
        String color;

        public Node(String word) {
            this.word = word;
            count = 1;
            color = "";
        }
    }

    // 图数据结构
    String root;
    private Set<String> nodeSet; // 所有节点名称的字典
    private Map<String, LinkedList<Node>> nodeInfo; // 所有节点名称与存储对应子节点的链表
    private static final int MAX = 9999;
    volatile boolean running = true; // 控制游走是否继续

    // 构造函数
    public TextGraph() {
        nodeInfo = new HashMap<String, LinkedList<Node>>();
        nodeSet = new LinkedHashSet<String>();
    }

    // 根据单词链表创建图
    public TextGraph createTextGraph(String[] wordList) {
        for (int i = 0; i < wordList.length - 1; i++) {

            // 创建根节点
            if (i == 0) {
                root = wordList[i];
            }

            // 单词加入集合 nodeSet
            String word1 = wordList[i];
            String word2 = wordList[i + 1];
            boolean existed1 = nodeSet.add(word1);

            if (i == wordList.length - 2) {
                nodeSet.add(word2);
            }

            // 若节点未出现过，加入nodeInfo集合中
            if (existed1) {
                LinkedList<Node> neighbour = new LinkedList<>();
                nodeInfo.put(word1, neighbour);
            }

            // 相邻单词添加边
            addEdge(word1, word2);
        }

        return this;
    }

    // 在两个节点之间添加边
    public void addEdge(String word1, String word2) {
        LinkedList<Node> neighbour = nodeInfo.get(word1);
        boolean existed = false;

        if (neighbour != null) {
            for (Node node : neighbour) {

                // 若 word2 已经是 word1 的邻居，count + 1
                if (node.word.equals(word2)) {
                    node.count += 1;
                    existed = true;
                }
            }
        }

        // 若 word2 不是 word1 的邻居，加入链表
        if (!existed) {
            Node node2 = new Node(word2);
            neighbour.add(node2);
        }
    }

    public void showTextGraph(String path, boolean color) throws IOException {
        try {
            FileWriter fileWriter = new FileWriter(new File(path));
            fileWriter.write("digraph TextGraph {\r\n");

            // 写入节点信息
            for (String word : nodeSet) {
                fileWriter.write(word + ";\r\n");
            }

            // 写入边信息
            Set<String> visited = new HashSet<>();
            if (color == false) {
                writeEdge(fileWriter, root, visited, false);
            } else {
                writeEdge(fileWriter, root, visited, true);
            }

            fileWriter.write("}");
            fileWriter.close();

        } catch (FileNotFoundException e) {
            System.out.println("File not Found");
        }
    }

    // 对节点的邻接节点写入边信息
    public void writeEdge(FileWriter fileWriter, String word, Set<String> visited, boolean color) throws IOException {
        // 判断是否已经访问过
        if (visited.contains(word)) {
            return;
        } else {
            visited.add(word);
        }

        if (color == false) {
            if (nodeSet.contains(word)) {
                LinkedList<Node> neighbour = nodeInfo.get(word);

                if (neighbour != null) {
                    for (Node node : neighbour) {
                        fileWriter.write(
                                word + " -> " + node.word + " [label = " + Integer.toString(node.count) + "];\r\n");
                        writeEdge(fileWriter, node.word, visited, false);
                    }
                } else {
                    return;
                }
            }
        }

        // 显示路径
        else {
            if (nodeSet.contains(word)) {
                LinkedList<Node> neighbour = nodeInfo.get(word);

                if (neighbour != null) {
                    for (Node node : neighbour) {
                        // 若节点在路径上
                        if (!node.color.isEmpty()) {
                            fileWriter.write(word + " -> " + node.word + " [label = " + Integer.toString(node.count)
                                    + ", color = " + node.color + "];\r\n");
                        } else {
                            fileWriter.write(
                                    word + " -> " + node.word + " [label = " + Integer.toString(node.count) + "];\r\n");
                        }

                        writeEdge(fileWriter, node.word, visited, true);
                    }
                } else {
                    return;
                }
            }
        }
    }

    // 查询桥接词
    public Set<String> queryBridgeWords(String word1, String word2) {

        Set<String> bridgeWords = new HashSet<>();

        // 若单词不在图中 返回 null
        if (!nodeSet.contains(word1) || !nodeSet.contains(word2)) {
            return null;
        }

        ArrayList<String> bridges = getNeighbour(word1);
        if (bridges.size() != 0) {
            // 判断桥的邻居是否是 word2
            for (String bridge : bridges) {
                ArrayList<String> nextNeighbour = getNeighbour(bridge);
                if (nextNeighbour.size() != 0) {
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

    // 获取节点的邻居节点列表 ArrayList<String>
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

    // 生成新文本
    public String generateNewText(List<String> wordList) {
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

    // Dijkstra算法
    public String dijkstra(String word, String f) {
        // 检查是否在图G中
        if (!nodeSet.contains(word) || !nodeSet.contains(f)) {
            return null;
        }

        int size = nodeSet.size();
        int[] flag = new int[size]; // 记录是否已经找到最短路径
        int[] prev = new int[size]; // 记录路径的前一个节点
        int[] dist = new int[size]; // 记录最短距离

        // 使用两个map维护一个序号到字符串的双射
        int cnt = 0;
        HashMap<String, Integer> stoi = new HashMap<>();
        HashMap<Integer, String> itos = new HashMap<>();
        for (String str : nodeSet) {
            stoi.put(str, cnt);
            itos.put(cnt, str);
            cnt++;
        }

        int[][] matrix = new int[size][size];

        // 初始化
        for (int i = 0; i < size; i++)
            for (int j = 0; j < size; j++)
                matrix[i][j] = (i == j) ? 0 : MAX;

        // 完成matrix
        for (int i = 0; i < size; i++) {
            // 遍历set中每个String的链表
            LinkedList<Node> neighbours = nodeInfo.get(itos.get(i));
            if (neighbours == null)
                continue;
            for (Node n : neighbours) {
                matrix[i][stoi.get(n.word)] = n.count;
            }
        }

        // 遍历所有节点进行初始化
        for (int i = 0; i < size; i++) {
            flag[i] = 0;
            prev[i] = stoi.get(word);
            dist[i] = matrix[stoi.get(word)][i];
        }

        flag[stoi.get(word)] = 1; // 对word自身初始化
        dist[stoi.get(word)] = 0;

        // 遍历size-2次，每次找出一个到word最近的节点
        int min, tmp, record = 0;
        for (int i = 0; i < size - 1; i++) {
            min = MAX;
            for (int j = 0; j < size; j++) {
                if (flag[j] == 0 && dist[j] < min) {
                    min = dist[j];
                    record = j;
                }
            }

            flag[record] = 1;
            if (record == stoi.get(f))
                break;

            for (int j = 0; j < size; j++) {
                tmp = (matrix[record][j] == MAX) ? MAX : min + matrix[record][j];
                if (flag[j] == 0 && tmp < dist[j]) {
                    prev[j] = record;
                    dist[j] = tmp;
                }
            }
        }

        // 输出路径和长度
        String outputstr = "";
        String tmpstr = f;
        while (!tmpstr.equals(word)) {
            String preword = itos.get(prev[stoi.get(tmpstr)]);
            LinkedList<Node> preNeighbour = nodeInfo.get(preword);
            if (preNeighbour != null && !preNeighbour.isEmpty()) {
                // 对应边赋颜色
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
        if (dist[stoi.get(f)] == MAX) {
            return "";
        }
        System.out.println(word + outputstr);
        System.out.println("Distance is " + dist[stoi.get(f)]);
        return word + outputstr + "\nDistance is " + dist[stoi.get(f)];
    }

    public String dijkstraSingleWord(String word) {
        String result = "";
        if (!nodeSet.contains(word)) {
            return null;
        } else {
            for (String str : nodeSet) {
                if (!word.equals(str)) {
                    result += dijkstra(word, str) + "\n";
                }
            }
            return result;
        }
    }

    // random Walk方法
    // 接口定义，用于更新GUI
    public interface RandomWalkObserver {
        void update(String message);

        void finish();

        void stopped(); // 增加停止通知
    }

    public String random(String startWord, RandomWalkObserver observer) throws IOException {
        FileWriter fileWriter = new FileWriter("src/file/randomWalk.txt", false);
        HashMap<String, LinkedList<Node>> record = new HashMap<>();
        String route = startWord + " -> ";
        String tmp = startWord;
        String pre = "";

        observer.update(route);
        try {
            Thread.sleep(1000); // 暂停1秒
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // 保持中断状态
        }

        while (nodeInfo.containsKey(tmp) && !Thread.currentThread().isInterrupted()) { // 检查线程是否被中断
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

            Random random = new Random();
            int num = random.nextInt(minus.size());
            tmp = minus.get(num);
            route += (tmp + " -> ");

            if (!record.containsKey(pre)) {
                LinkedList<Node> list = new LinkedList<>();
                list.push(new Node(tmp));
                record.put(pre, list);
            } else {
                record.get(pre).push(new Node(tmp));
            }

            observer.update(tmp + " -> ");
            try {
                Thread.sleep(1000); // 暂停1秒
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // 保持中断状态
            }
        }

        route += "Finish";
        if (Thread.currentThread().isInterrupted()) {
            observer.stopped(); // 发出停止通知
            fileWriter.write(route + " (Stopped)\n");
        } else {
            observer.finish(); // 发出完成通知
            fileWriter.write(route + "\n");
        }

        fileWriter.close();
        return route;
    }

    // interface版
    public String random(String word) throws IOException {
        FileWriter fileWriter = new FileWriter("src/file/randomWalk.txt", false);

        HashMap<String, LinkedList<Node>> record = new HashMap<>();
        String route = "";
        String tmp = word;
        String pre = "";

        while (nodeInfo.containsKey(tmp)) {
            pre = tmp;
            LinkedList<String> minus = new LinkedList<>(); // minus存放nodeInfo-record的内容

            for (Node n : nodeInfo.get(tmp)) {
                minus.push(n.word);
            }

            if (record.containsKey(tmp)) {
                for (Node n : record.get(tmp)) {
                    minus.remove(n.word);
                }
            }

            if (minus.isEmpty())
                break;

            System.out.printf(tmp + " -> ");
            route += (tmp + " -> ");

            // 随机选择minus中的一个word
            Random random = new Random();
            int num = random.nextInt(minus.size());
            tmp = minus.get(num);

            // 更新record
            if (!record.containsKey(pre)) {
                LinkedList<Node> list = new LinkedList<>();
                list.push(new Node(tmp));
                record.put(pre, list);
            } else {
                record.get(pre).push(new Node(tmp));
            }

            try {
                Thread.sleep(1000); // 暂停1秒
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.printf(tmp);
        route += tmp;
        fileWriter.write(route + "\n"); // 写入路径到文件
        fileWriter.close(); // 关闭文件
        return route;
    }

    public void stop() {
        running = false; // 设置运行标志为false，停止游走
    }
}
