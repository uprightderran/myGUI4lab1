package com.mygui;

import java.io.*;
import java.util.*;

public class TextGraph {

    // �ڵ㶨�壬�����ڵ����ƺͽڵ���ִ���
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

    // ͼ���ݽṹ
    String root;
    private Set<String> nodeSet; // ���нڵ����Ƶ��ֵ�
    private Map<String, LinkedList<Node>> nodeInfo; // ���нڵ�������洢��Ӧ�ӽڵ������
    private static final int MAX = 9999;
    volatile boolean running = true; // ���������Ƿ����

    // ���캯��
    public TextGraph() {
        nodeInfo = new HashMap<String, LinkedList<Node>>();
        nodeSet = new LinkedHashSet<String>();
    }

    // ���ݵ���������ͼ
    public TextGraph createTextGraph(String[] wordList) {
        for (int i = 0; i < wordList.length - 1; i++) {

            // �������ڵ�
            if (i == 0) {
                root = wordList[i];
            }

            // ���ʼ��뼯�� nodeSet
            String word1 = wordList[i];
            String word2 = wordList[i + 1];
            boolean existed1 = nodeSet.add(word1);

            if (i == wordList.length - 2) {
                nodeSet.add(word2);
            }

            // ���ڵ�δ���ֹ�������nodeInfo������
            if (existed1) {
                LinkedList<Node> neighbour = new LinkedList<>();
                nodeInfo.put(word1, neighbour);
            }

            // ���ڵ�����ӱ�
            addEdge(word1, word2);
        }

        return this;
    }

    // �������ڵ�֮����ӱ�
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
            neighbour.add(node2);
        }
    }

    public void showTextGraph(String path, boolean color) throws IOException {
        try {
            FileWriter fileWriter = new FileWriter(new File(path));
            fileWriter.write("digraph TextGraph {\r\n");

            // д��ڵ���Ϣ
            for (String word : nodeSet) {
                fileWriter.write(word + ";\r\n");
            }

            // д�����Ϣ
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

    // �Խڵ���ڽӽڵ�д�����Ϣ
    public void writeEdge(FileWriter fileWriter, String word, Set<String> visited, boolean color) throws IOException {
        // �ж��Ƿ��Ѿ����ʹ�
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

        // ��ʾ·��
        else {
            if (nodeSet.contains(word)) {
                LinkedList<Node> neighbour = nodeInfo.get(word);

                if (neighbour != null) {
                    for (Node node : neighbour) {
                        // ���ڵ���·����
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

    // ��ѯ�ŽӴ�
    public Set<String> queryBridgeWords(String word1, String word2) {

        Set<String> bridgeWords = new HashSet<>();

        // �����ʲ���ͼ�� ���� null
        if (!nodeSet.contains(word1) || !nodeSet.contains(word2)) {
            return null;
        }

        ArrayList<String> bridges = getNeighbour(word1);
        if (bridges.size() != 0) {
            // �ж��ŵ��ھ��Ƿ��� word2
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

    // ��ȡ�ڵ���ھӽڵ��б� ArrayList<String>
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

    // �������ı�
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

    // Dijkstra�㷨
    public String dijkstra(String word, String f) {
        // ����Ƿ���ͼG��
        if (!nodeSet.contains(word) || !nodeSet.contains(f)) {
            return null;
        }

        int size = nodeSet.size();
        int[] flag = new int[size]; // ��¼�Ƿ��Ѿ��ҵ����·��
        int[] prev = new int[size]; // ��¼·����ǰһ���ڵ�
        int[] dist = new int[size]; // ��¼��̾���

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
        for (int i = 0; i < size; i++)
            for (int j = 0; j < size; j++)
                matrix[i][j] = (i == j) ? 0 : MAX;

        // ���matrix
        for (int i = 0; i < size; i++) {
            // ����set��ÿ��String������
            LinkedList<Node> neighbours = nodeInfo.get(itos.get(i));
            if (neighbours == null)
                continue;
            for (Node n : neighbours) {
                matrix[i][stoi.get(n.word)] = n.count;
            }
        }

        // �������нڵ���г�ʼ��
        for (int i = 0; i < size; i++) {
            flag[i] = 0;
            prev[i] = stoi.get(word);
            dist[i] = matrix[stoi.get(word)][i];
        }

        flag[stoi.get(word)] = 1; // ��word�����ʼ��
        dist[stoi.get(word)] = 0;

        // ����size-2�Σ�ÿ���ҳ�һ����word����Ľڵ�
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

    // random Walk����
    // �ӿڶ��壬���ڸ���GUI
    public interface RandomWalkObserver {
        void update(String message);

        void finish();

        void stopped(); // ����ֹ֪ͣͨ
    }

    public String random(String startWord, RandomWalkObserver observer) throws IOException {
        FileWriter fileWriter = new FileWriter("src/file/randomWalk.txt", false);
        HashMap<String, LinkedList<Node>> record = new HashMap<>();
        String route = startWord + " -> ";
        String tmp = startWord;
        String pre = "";

        observer.update(route);
        try {
            Thread.sleep(1000); // ��ͣ1��
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // �����ж�״̬
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
                Thread.sleep(1000); // ��ͣ1��
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // �����ж�״̬
            }
        }

        route += "Finish";
        if (Thread.currentThread().isInterrupted()) {
            observer.stopped(); // ����ֹ֪ͣͨ
            fileWriter.write(route + " (Stopped)\n");
        } else {
            observer.finish(); // �������֪ͨ
            fileWriter.write(route + "\n");
        }

        fileWriter.close();
        return route;
    }

    // interface��
    public String random(String word) throws IOException {
        FileWriter fileWriter = new FileWriter("src/file/randomWalk.txt", false);

        HashMap<String, LinkedList<Node>> record = new HashMap<>();
        String route = "";
        String tmp = word;
        String pre = "";

        while (nodeInfo.containsKey(tmp)) {
            pre = tmp;
            LinkedList<String> minus = new LinkedList<>(); // minus���nodeInfo-record������

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

            // ���ѡ��minus�е�һ��word
            Random random = new Random();
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

        System.out.printf(tmp);
        route += tmp;
        fileWriter.write(route + "\n"); // д��·�����ļ�
        fileWriter.close(); // �ر��ļ�
        return route;
    }

    public void stop() {
        running = false; // �������б�־Ϊfalse��ֹͣ����
    }
}
