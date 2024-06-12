package myGUI4lab1;

import java.io.*;
import java.util.*;

// created 2024/5/17

// modify B1

// change C4

// modify B2

// IDE modify

public class Interface {

	// ����ͼ
	public static TextGraph createTextGraph(String filename) {
		// ��ȡ�ļ�
		File file = new File(filename);
		BufferedReader fin = null;
		try {
			fin = new BufferedReader(new FileReader(file));
		} catch (FileNotFoundException e) {
			System.out.println("File not found");
			e.printStackTrace();
		}

		// �ļ����ݶ���data�ַ���
		String data = "";
		String line = null;
		try {
			while ((line = fin.readLine()) != null) {
				data = data + line + " ";
			}
		} catch (IOException e) {
			System.out.println("Read file failed");
			e.printStackTrace();
		}

		// Ԥ������ţ������ַ�
		for (int i = 0; i < data.length() - 1; i++) {
			if (!(data.charAt(i) >= 'a' && data.charAt(i) <= 'z')
					&& !(data.charAt(i) >= 'A' && data.charAt(i) <= 'Z')) {
				data = data.substring(0, i) + " " + data.substring(i + 1);
			}
			// ��д����Сд
			else if (data.charAt(i) >= 'A' && data.charAt(i) <= 'Z') {
				char lower_case = (char) (data.charAt(i) + ('a' - 'A'));
				data = data.substring(0, i) + lower_case + data.substring(i + 1);
			}
		}

		// ���ʷ����ַ������� word_List
		String[] word_List = data.split("\\s+");

		// �ر��ļ�
		try {
			if (fin != null) {
				fin.close();
			}
		} catch (IOException e) {
			System.out.println("File close failed");
			e.printStackTrace();
		}

		// ʹ�õ������鴴��ͼ
		TextGraph graph = new TextGraph();
		return graph.createTextGraph(word_List);
	}

	// ���ӻ�ͼ
	public static void showTextGraph(TextGraph G, String path, String outputpath, boolean color) throws IOException {
		long stime = System.currentTimeMillis();

		String gv_path = path + "/Graph.dot";
		String outputfile_path = outputpath + "/Graph.png";

		// ����dot�ļ�
		G.showTextGraph(gv_path, color);

		long etime = System.currentTimeMillis();
		System.out.printf("calculated in %d ms.\n", (etime - stime));

		// ���������png
		try {
			String Command = "dot -Tpng -o " + outputfile_path + " " + gv_path;
			Process process = Runtime.getRuntime().exec(Command);
			try {
				process.waitFor();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} catch (IOException e) {
			System.out.println("IOException");
		}
	}

	// ��ѯ�ŽӴ�
	public static String queryBridgeWords(TextGraph G, String raw_word1, String raw_word2) {

		long stime = System.currentTimeMillis();

		// ת��ΪСд
		String word1 = raw_word1.toLowerCase();
		String word2 = raw_word2.toLowerCase();

		String result = "";

		Set<String> bridgeWords = G.queryBridgeWords(word1, word2);
		if (bridgeWords == null) {
			System.out.println("No \"" + word1 + "\" or \"" + word2 + "\" in the graph!");
			result = "No \"" + word1 + "\" or \"" + word2 + "\" in the graph!";

			long etime = System.currentTimeMillis();
			System.out.printf("calculated in %d ms.\n", (etime - stime));

		} else if (bridgeWords.isEmpty()) {
			System.out.println("No bridge words from \"" + word1 + "\" to \"" + word2 + "\"!");
			result = "No bridge words from \"" + word1 + "\" to \"" + word2 + "\"!";

			long etime = System.currentTimeMillis();
			System.out.printf("calculated in %d ms.\n", (etime - stime));

		} else {
			String bridge_String = "";
			if (bridgeWords.size() == 1) {
				bridge_String = "\" is: ";
				for (String word : bridgeWords) {
					bridge_String += word;
				}
				bridge_String += ".";
				System.out.println("The bridge word from \"" + word1 + "\" to \"" + word2 + bridge_String);
				result = "The bridge word from \"" + word1 + "\" to \"" + word2 + bridge_String;

				long etime = System.currentTimeMillis();
				System.out.printf("calculated in %d ms.\n", (etime - stime));

			} else {
				bridge_String = "\" are: ";
				int counter = 0;
				for (String word : bridgeWords) {
					// ������һ�� word
					if (counter == bridgeWords.size() - 1) {
						bridge_String = bridge_String + "and " + word + ".";
					} else {
						bridge_String = bridge_String + word + ", ";
						counter++;
					}
				}
				System.out.println("The bridge words from \"" + word1 + "\" to \"" + word2 + bridge_String);
				result = "The bridge words from \"" + word1 + "\" to \"" + word2 + bridge_String;

				long etime = System.currentTimeMillis();
				System.out.printf("calculated in %d ms.\n", (etime - stime));

			}
		}
		return result;
	}

	public static String generateNewText(TextGraph G, String inputText) {
		long stime = System.currentTimeMillis();
		// Ԥ�������ʷ����ַ�������
		for (int i = 0; i < inputText.length() - 1; i++) {
			if (!(inputText.charAt(i) >= 'a' && inputText.charAt(i) <= 'z')
					&& !(inputText.charAt(i) >= 'A' && inputText.charAt(i) <= 'Z')) {
				inputText = inputText.substring(0, i) + " " + inputText.substring(i + 1);
			}
			// ��д����Сд
			else if (inputText.charAt(i) >= 'A' && inputText.charAt(i) <= 'Z') {
				char lower_case = (char) (inputText.charAt(i) + ('a' - 'A'));
				inputText = inputText.substring(0, i) + lower_case + inputText.substring(i + 1);
			}
		}
		String[] word_List = inputText.split("\\s+");
		ArrayList<String> wordList = new ArrayList<>(Arrays.asList(word_List));

		String newText = G.generateNewText(wordList);

		long etime = System.currentTimeMillis();
		System.out.printf("calculated in %d ms.\n", (etime - stime));

		System.out.println(newText);
		return newText;
	}

	public static String calcShortestPath(TextGraph G, String raw_word1, String raw_word2) {
		long stime = System.currentTimeMillis();
		// ת����Сд
		String word1 = raw_word1.toLowerCase();
		String word2 = raw_word2.toLowerCase();

		String result = G.dijkstra(word1, word2);

		long etime = System.currentTimeMillis();
		System.out.printf("calculated in %d ms.\n", (etime - stime));

		return result;
	}

	public static String calcShortestPath(TextGraph G, String raw_word) {
		long stime = System.currentTimeMillis();

		String word = raw_word.toLowerCase();
		String result = G.dijkstraSingleWord(word);

		long etime = System.currentTimeMillis();
		System.out.printf("calculated in %d ms.\n", (etime - stime));

		return result;
	}

	public static String randomWalk(TextGraph G, String raw_word) throws IOException {
		long stime = System.currentTimeMillis();

		String word = raw_word.toLowerCase();
		String result = G.random(word);

		long etime = System.currentTimeMillis();
		System.out.printf("calculated in %d ms.\n", (etime - stime));

		return result;
	}

	public static void main(String[] args) {
//    	System.out.println(System.getProperty("user.dir"));
		TextGraph graph = createTextGraph("src/file/GraphData.txt");

		try {// չʾ����ͼ
			showTextGraph(graph, "src/file", "src/file", false);
		} catch (IOException e) {
			System.out.println("IOException");
		}

		// ��ѯbridge word
		queryBridgeWords(graph, "strange", "worlds");

		// ����bridge word�������ı�
		generateNewText(graph, "Seek to explore new and exciting synergies");

		// ������������֮������·��
		calcShortestPath(graph, "new", "and");
		try {
			showTextGraph(graph, "src/file/normal", "src/file/normal", true);
		} catch (IOException e) {
			System.out.println("IOException");
		}

		// ����һ�����ʵ����пɴ�ڵ�����·��
		calcShortestPath(graph, "to");

		try {// �������
			randomWalk(graph, "to");
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}