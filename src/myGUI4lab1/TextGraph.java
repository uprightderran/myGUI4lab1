package myGUI4lab1;

import java.awt.desktop.SystemSleepEvent;
import java.io.*;
import java.util.*;

import javax.swing.JTextArea;

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
    private Set<String> Node_set;// ���нڵ����Ƶ��ֵ�
    private Map<String, LinkedList<Node>> Node_info;// ���нڵ�������洢��Ӧ�ӽڵ������
    private static final int max = 9999;
    volatile boolean running = true;  // ���������Ƿ����
    
    // ���캯��
    public TextGraph() {
        Node_info = new HashMap<String, LinkedList<Node>>();
        Node_set = new LinkedHashSet<String>();
    }

    // ���ݵ���������ͼ
    public TextGraph createTextGraph(String[] word_List) {
        for (int i = 0; i < word_List.length - 1; i ++ ) {

            // �������ڵ�
            if (i == 0) {
                root = word_List[i];
            }
            
            // ���ʼ��뼯�� Node_set
            String word1 = word_List[i];
            String word2 = word_List[i + 1];
            boolean existed1 = Node_set.add(word1);

            if (i == word_List.length - 2) {
                Node_set.add(word2);
            }

            // ���ڵ�δ���ֹ�������Node_info������
            if (existed1) {
                LinkedList<Node> neighbour = new LinkedList<>();
                Node_info.put(word1, neighbour);
            }

            // ���ڵ�����ӱ�
            addEdge(word1, word2);
        }

        return this;
    }

    // �������ڵ�֮����ӱ�
    public void addEdge(String word1, String word2) {
        LinkedList<Node> neighbour = Node_info.get(word1);
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
            for (String word : Node_set) {
                fileWriter.write(word + ";\r\n");
            }

            // д�����Ϣ
            Set<String> Visited = new HashSet<>();
            if (color == false) {
                WriteEdge(fileWriter, root, Visited, false);
            }
            else {
                WriteEdge(fileWriter, root, Visited, true);
            }
            
            
            fileWriter.write("}");
            fileWriter.close();

        } catch (FileNotFoundException e) {
            System.out.println("File not Found");
        }
    }

    // �Խڵ���ڽӽڵ�д�����Ϣ
    public void WriteEdge(FileWriter fileWriter, String word, Set<String> Visited, boolean color) throws IOException {
        // �ж��Ƿ��Ѿ����ʹ�
        if (Visited.contains(word)) {
            return ;
        }
        else {
            Visited.add(word);
        }

        if (color == false) {
            if (Node_set.contains(word)) {
                LinkedList<Node> neighbour = Node_info.get(word);
    
                if (neighbour != null) {
                    for (Node node : neighbour) {
                        fileWriter.write(word + " -> " + node.word + " [label = " 
                                        + Integer.toString(node.count) + "];\r\n");
                        WriteEdge(fileWriter, node.word, Visited, false);
                    }
                }
                else {
                    return ;
                }
            }
        }

        // ��ʾ·��
        else {
            if (Node_set.contains(word)) {
                LinkedList<Node> neighbour = Node_info.get(word);
    
                if (neighbour != null) {
                    for (Node node : neighbour) {
                        // ���ڵ���·����
                        if (!node.color.isEmpty()) {
                            fileWriter.write(word + " -> " + node.word + " [label = " 
                                        + Integer.toString(node.count) + ", color = " + node.color + "];\r\n");
                        }
                        else {
                            fileWriter.write(word + " -> " + node.word + " [label = " 
                                        + Integer.toString(node.count) + "];\r\n");
                        }
                        
                        WriteEdge(fileWriter, node.word, Visited, true);
                    }
                }
                else {
                    return ;
                }
            }
        }
    }

    // ��ѯ�ŽӴ�
    public Set<String> queryBridgeWords(String word1, String word2) {

        Set<String> bridgeWords = new HashSet<>();

        // �����ʲ���ͼ�� ���� null
        if (!Node_set.contains(word1) || !Node_set.contains(word2)) {
            return null;
        }

        ArrayList<String> bridges = getNeighbour(word1);
        if (bridges.size() != 0) {
            // �ж��ŵ��ھ��Ƿ��� word2
            for (String bridge : bridges) {
                ArrayList<String> next_neighbour = getNeighbour(bridge);
                if (next_neighbour.size() != 0) {
                    for (String word : next_neighbour) {
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
        LinkedList<Node> neighbourList = Node_info.get(word);
        if (neighbourList != null && !neighbourList.isEmpty()) {
            for (Node neighbour : neighbourList) {
                neighbours.add(neighbour.word);
            }
        }
        return neighbours;
    }

    // �������ı�
    public String generateNewText(ArrayList<String> wordList) {
        String newText = "";
        for (int i = 0; i < wordList.size() - 1; i ++ ) {
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
        if (!Node_set.contains(word) || !Node_set.contains(f)) {
            return null;
        }

    	int size = Node_set.size();
    	int[] flag = new int[size];// ��¼�Ƿ��Ѿ��ҵ����·��
    	int[] prev = new int[size];// ��¼·����ǰһ���ڵ�
    	int[] dist = new int[size];// ��¼��̾���
    	
    	// ʹ������mapά��һ����ŵ��ַ�����˫��
    	int cnt = 0;
    	HashMap<String, Integer> stoi = new HashMap<>();
    	HashMap<Integer, String> itos = new HashMap<>();
    	for(String str : Node_set)
    	{
    		stoi.put(str, cnt);
    		itos.put(cnt, str);
    		cnt++;
    	}
    	
    	int[][] matrix = new int[size][size];
    	
    	// ��ʼ��
    	for(int i = 0; i < size; i++)
    		for(int j = 0; j < size; j++) 
    			matrix[i][j] = (i == j) ? 0 : max;

    	// ���matrix
    	for(int i = 0; i < size; i++)
    	{
    		// ����set��ÿ��String������
    		LinkedList<Node> neighbours = Node_info.get(itos.get(i));
    		if (neighbours == null)
    			continue;
    		for (Node n : neighbours)
    		{
    			matrix[i][stoi.get(n.word)] = n.count;
    		}
    	}
    	
    	// �������нڵ���г�ʼ��
    	for(int i = 0; i < size; i++)
    	{
    		flag[i] = 0;
    		prev[i] = stoi.get(word);
    		dist[i] = matrix[stoi.get(word)][i];
    	}
    	
    	flag[stoi.get(word)] = 1;// ��word�����ʼ��
    	dist[stoi.get(word)] = 0;
    	
    	// ����size-2�Σ�ÿ���ҳ�һ����word����Ľڵ�
    	int min, tmp, record = 0;
    	for(int i = 0; i < size-1; i++)
    	{
    		min = max;
    		for(int j = 0; j < size; j++)
    		{
    			if(flag[j] == 0 && dist[j] < min)
    			{
    				min = dist[j];
    				record = j;
    			}
    		}
    		

    		flag[record] = 1;
    		if (record == stoi.get(f)) break;
    		
    		for (int j = 0; j < size; j++)
    		{
    			tmp = (matrix[record][j] == max) ? max : min + matrix[record][j];
    			if(flag[j] == 0 && tmp < dist[j])
    			{
    				prev[j] = record;
    				dist[j] = tmp;
    			}
    		}
    	}
    	
    	// ���·���ͳ���
    	String outputstr = "";
    	String tmpstr = f;
    	while (!tmpstr.equals(word))
    	{
            String preword = itos.get(prev[stoi.get(tmpstr)]);
            LinkedList<Node> pre_neighbour = Node_info.get(preword);
            if (pre_neighbour != null && !pre_neighbour.isEmpty()) {
                // ��Ӧ�߸���ɫ
                for (Node node : pre_neighbour) {
                    if (node.word.equals(tmpstr)) {
                        node.color = "blue";
                        break;
                    }
                }
            }

    		outputstr = " -> "+ tmpstr + outputstr;
    		tmpstr = preword;
    	}
    	if (dist[stoi.get(f)] == max) {
    		return "";
    	}
    	System.out.println(word + outputstr);
    	System.out.println("Distance is "+dist[stoi.get(f)]);
    	return word + outputstr + "\nDistance is "+dist[stoi.get(f)];
    }

    public String dijkstraSingleWord(String word) {
    	String result = "";
        if (!Node_set.contains(word)) {
            return null;
        }
        else {
            for(String str : Node_set)
    	{
    		if(!word.equals(str))
    		{
        		result += dijkstra(word,str)+"\n";
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
        void stopped();  // ����ֹ֪ͣͨ
    }

    public String random(String startWord, RandomWalkObserver observer) throws IOException {
        FileWriter fileWriter = new FileWriter("src/file/randomWalk.txt", false);
        HashMap<String, LinkedList<Node>> record = new HashMap<>();
        String route = startWord + " -> ";
        String tmp = startWord;
        String pre = "";

        observer.update(route);
        try {
            Thread.sleep(1000);  // ��ͣ1��
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();  // �����ж�״̬
        }

        while (Node_info.containsKey(tmp) && !Thread.currentThread().isInterrupted()) { // ����߳��Ƿ��ж�
            pre = tmp;
            LinkedList<String> minus = new LinkedList<>();

            for (Node n : Node_info.get(tmp)) {
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
                Thread.sleep(1000);  // ��ͣ1��
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();  // �����ж�״̬
            }
        }

        route += "Finish";
        if (Thread.currentThread().isInterrupted()) {
            observer.stopped();  // ����ֹ֪ͣͨ
            fileWriter.write(route + " (Stopped)\n");
        } else {
            observer.finish();  // �������֪ͨ
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

        while (Node_info.containsKey(tmp)) {
            pre = tmp;
            LinkedList<String> minus = new LinkedList<>();// minus���Node_info-record������

            for (Node n : Node_info.get(tmp)) {
                minus.push(n.word);
            }

            if (record.containsKey(tmp)) {
                for (Node n : record.get(tmp)) {
                    minus.remove(n.word);
                }
            }

            if (minus.isEmpty()) 
            	break;

            System.out.printf(tmp+" -> ");
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
        fileWriter.write(route + "\n");  // д��·�����ļ�
        fileWriter.close();  // �ر��ļ�
        return route;
    }

    public void stop() {
        running = false;  // �������б�־Ϊfalse��ֹͣ����
    }

}
