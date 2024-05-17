package myGUI4lab1;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

public class MyFrame extends JFrame {
    private JTextField textField;  // �ı�����ʾ�ļ�·��
    private JButton viewButton, confirmButton;  // View��ť��Confirm��ť
    private JFrame optionsFrame;  // ������ʾѡ��ĵڶ�����
    private JComboBox<String> functionBox;  // ����ѡ��������
    private JButton optionsConfirmButton;  // ѡ����е�Confirm��ť
    private String filePath;  // �洢�ļ�·��
    private JButton stopButton;  // ֹͣ��ť

    public MyFrame() {
        setTitle("�ļ�ѡ��");
        setSize(500, 120);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new FlowLayout());

        textField = new JTextField(20);
        viewButton = new JButton("View");
        confirmButton = new JButton("Confirm");

        add(textField);
        add(viewButton);
        add(confirmButton);

        // View��ť����¼�
        viewButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                int returnValue = fileChooser.showOpenDialog(null);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    filePath = fileChooser.getSelectedFile().getPath();
                    textField.setText(filePath);
                }
            }
        });

        // Confirm��ť����¼�
        confirmButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String modifiedPath = textField.getText().replace("\\", "\\\\");
                openOptionsWindow(modifiedPath);
            }
        });
    }

    private void openOptionsWindow(String path) {
        optionsFrame = new JFrame("Options");
        optionsFrame.setSize(500, 250);
        optionsFrame.setLayout(new FlowLayout());
        optionsFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        String[] functions = {"Show directed graph", "Search bridge word", "Generate new text", 
        		"Calculate the shortest route from word1 to word2", "Calculate all the shortest route from word", "Random walk"};
        functionBox = new JComboBox<>(functions);
        optionsConfirmButton = new JButton("Confirm");

        optionsFrame.add(functionBox);
        optionsFrame.add(optionsConfirmButton);

        optionsFrame.setVisible(true);

        optionsConfirmButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                performFunction(functionBox.getSelectedIndex(), path);
            }
        });
    }

    private void performFunction(int index, String path) {
        try {
        	path = path.replace("D:\\\\eclipse-workspace\\\\myGUI4lab1\\\\", "").replace("\\\\", "/");
            TextGraph graph = Interface.createTextGraph(path); // ʹ���޸ĺ��·������ͼ
            switch (index) {
                case 0:  // չʾ����ͼ
                    Interface.showTextGraph(graph, path.replace("GraphData.txt", ""), path.replace("GraphData.txt", ""), false);
                    showImage(path.replace("GraphData.txt","") + "Graph.png");
                    break;
                case 1:  // ��ѯ�ŽӴ�
                    String input = JOptionPane.showInputDialog("Please input two words separated with space");
                    if (input != null) {
                        String[] words = input.split("\\s+");
                        if (words.length >= 2) {
                            JOptionPane.showMessageDialog(null, Interface.queryBridgeWords(graph, words[0], words[1]));
                        }
                    }
                    break;
                case 2:  // �������ı�
                    String text = JOptionPane.showInputDialog("Please input text");
                    if (text != null) {
                        JOptionPane.showMessageDialog(null, Interface.generateNewText(graph, text));
                    }
                    break;
                case 3:  // ������������֮������·��
                    input = JOptionPane.showInputDialog("Please input two words separated with space");
                    if (input != null) {
                        String[] words = input.split("\\s+");
                        if (words.length == 2) {
                            if (Interface.calcShortestPath(graph, words[0], words[1]) == null) {
                                JOptionPane.showMessageDialog(null,"\"" + words[0] + "\" or \"" + words[1] + "\" doesn't exist!");
                            }
                            else {
                                JOptionPane.showMessageDialog(null, Interface.calcShortestPath(graph, words[0], words[1]));
                                Interface.showTextGraph(graph, path.replace("GraphData.txt","") + "normal", 
                                        path.replace("GraphData.txt","") + "normal", true);
                                showImage(path.replace("GraphData.txt","") + "normal/Graph.png");
                            }
                        }
                        else {
                            JOptionPane.showMessageDialog(null,"You should input two words, try again!");
                        }
                        
                    }
                    break;
                case 4:  // ����һ�����ʵ����пɴ�ڵ�����·��
                    String word = JOptionPane.showInputDialog("Please input a word");
                    if (word != null) {
                        if (Interface.calcShortestPath(graph, word) == null) {
                            JOptionPane.showMessageDialog(null, "\"" + word + "\" doesn't exist!");
                        }
                        else {
                            JOptionPane.showMessageDialog(null, Interface.calcShortestPath(graph, word));
                        }    
                    }
                    break;
                case 5:  // �������
                	String word1 = JOptionPane.showInputDialog("Please input the first word");
                    if (word1 != null) {
                        JFrame randomWalkFrame = new JFrame("Random Walk");
                        JTextArea textArea = new JTextArea(10, 30);
                        JButton stopButton = new JButton("Stop");
                        randomWalkFrame.setLayout(new FlowLayout());
                        randomWalkFrame.add(new JScrollPane(textArea));
                        randomWalkFrame.add(stopButton);
                        randomWalkFrame.pack();
                        randomWalkFrame.setVisible(true);

                        Thread walkThread = new Thread(() -> {
                            try {
                                graph.random(word1, new TextGraph.RandomWalkObserver() {
                                    @Override
                                    public void update(String message) {
                                        SwingUtilities.invokeLater(() -> textArea.append(message));
                                    }

                                    @Override
                                    public void finish() {
                                        SwingUtilities.invokeLater(() -> {
                                            textArea.append("Finish\n");
                                            stopButton.setEnabled(false);
                                        });
                                    }

                                    @Override
                                    public void stopped() {
                                        SwingUtilities.invokeLater(() -> {
                                            textArea.append("Stopped\n");
                                            stopButton.setEnabled(false);
                                        });
                                    }
                                });
                            } catch (IOException e) {
                                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(null, "error when random walk: " + e.getMessage()));
                            }
                        });
                        walkThread.start();

                        stopButton.addActionListener(event -> {
                            walkThread.interrupt();  // �����ж��ź�
                        });
                    }
                    break;
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Process error: " + e.getMessage());
        }
    }

    private void showImage(String imagePath) {
        ImageIcon icon = new ImageIcon(imagePath);
        JLabel label = new JLabel(icon);
        JFrame frame = new JFrame("Show Graph");
        frame.add(label);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new MyFrame().setVisible(true);
            }
        });
    }
}
