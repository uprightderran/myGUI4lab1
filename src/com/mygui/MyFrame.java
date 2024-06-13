package com.mygui;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class MyFrame extends JFrame {
    
	private static final long serialVersionUID = 1L;
	private JTextField textField; // 文本框显示的文件路径
    private JButton viewButton, confirmButton; // View按钮和Confirm按钮
    private JFrame optionsFrame; // 用于显示选项的第二个窗口
    private JComboBox<String> functionBox; // 功能选择下拉框
    private JButton optionsConfirmButton; // 选项窗口中的Confirm按钮
    private String filePath; // 存储文件路径
    
    public MyFrame() {
        setTitle("Program");
        setSize(500, 120);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new FlowLayout());

        textField = new JTextField(20);
        viewButton = new JButton("View");
        confirmButton = new JButton("Confirm");

        add(textField);
        add(viewButton);
        add(confirmButton);

        // View按钮的监听事件
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

        // Confirm按钮的监听事件
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

        String[] functions = {
            "Show directed graph", "Search bridge word", "Generate new text",
            "Calculate the shortest route from word1 to word2", 
            "Calculate all the shortest route from word", "Random walk"
        };
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
        path = path.replace("D:\\\\eclipse-workspace\\\\lab1\\\\", "").replace("\\\\", "/");
		TextGraph graph = Interface.createTextGraph(path); // 使用接口创建文本图
		switch (index) {
		    case 0: // Show directed graph
		        Interface.showTextGraph(
		            graph, path.replace("GraphData.txt", ""), 
		            path.replace("GraphData.txt", ""), false
		        );
		        showImage(path.replace("GraphData.txt", "") + "Graph.png");
		        break;
		    case 1: // Search bridge word
		        String input = JOptionPane.showInputDialog("Please input two words separated with space");
		        if (input != null) {
		            String[] words = input.split("\\s+");
		            if (words.length >= 2) {
		                JOptionPane.showMessageDialog(null, Interface.queryBridgeWords(graph, words[0], words[1]));
		            }
		        }
		        break;
		    case 2: // Generate new text
		        String text = JOptionPane.showInputDialog("Please input text");
		        if (text != null) {
		            JOptionPane.showMessageDialog(null, Interface.generateNewText(graph, text));
		        }
		        break;
		    case 3: // Calculate the shortest route from word1 to word2
		        input = JOptionPane.showInputDialog("Please input two words separated with space");
		        if (input != null) {
		            String[] words = input.split("\\s+");
		            if (words.length == 2) {
		                if (Interface.calcShortestPath(graph, words[0], words[1]) == null) {
		                    JOptionPane.showMessageDialog(
		                        null, "\"" + words[0] + "\" or \"" + words[1] + "\" doesn't exist!"
		                    );
		                } else {
		                    JOptionPane.showMessageDialog(
		                        null, Interface.calcShortestPath(graph, words[0], words[1])
		                    );
		                    Interface.showTextGraph(
		                        graph, path.replace("GraphData.txt", "") + "normal",
		                        path.replace("GraphData.txt", "") + "normal", true
		                    );
		                    showImage(path.replace("GraphData.txt", "") + "normal/Graph.png");
		                }
		            } else {
		                JOptionPane.showMessageDialog(null, "You should input two words, try again!");
		            }
		        }
		        break;
		    case 4: // Calculate all the shortest route from word
		        String word = JOptionPane.showInputDialog("Please input a word");
		        if (word != null) {
		            if (Interface.calcShortestPath(graph, word) == null) {
		                JOptionPane.showMessageDialog(null, "\"" + word + "\" doesn't exist!");
		            } else {
		                JOptionPane.showMessageDialog(null, Interface.calcShortestPath(graph, word));
		            }
		        }
		        break;
		    case 5: // Random Walk
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
		                    SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(
		                        null, "Error when random walk: " + e.getMessage()
		                    ));
		                }
		            });
		            walkThread.start();

		            stopButton.addActionListener(event -> {
		                walkThread.interrupt(); // 停止线程
		            });
		        }
		        break;
		}
    }

    private void showImage(String imagePath) {
        try {
            BufferedImage image = ImageIO.read(new File(imagePath)); // 使用ImageIO读取图片
            ImageIcon icon = new ImageIcon(image); // 创建一个新的ImageIcon
            JLabel label = new JLabel(icon);
            JScrollPane scrollPane = new JScrollPane(label); // 使用JScrollPane包装JLabel
            scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

            JFrame frame = new JFrame("Show Graph");
            frame.getContentPane().removeAll(); // 移除所有组件
            frame.add(scrollPane); // 将滚动面板添加到窗口中
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.pack(); // 调整窗口大小以适应内容
            frame.setVisible(true);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error loading image: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new MyFrame().setVisible(true);
            }
        });
    }
}
