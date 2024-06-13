package com.mygui;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

/**
 * MyFrame.
 */
public class MyFrame extends JFrame {
  private static final long sserialVersionUID = 1L;

  /**
   * A text field used to display and enter the file path selected by the user.
   * The user can either type in the file path directly or use the "View" button
   * to open a file chooser dialog to select the file. The selected file path
   * will be displayed in this text field.
   *
   * <p>用户用来显示和输入所选文件路径的文本框。用户可以直接输入文件路径，也可以使用“查看”按钮
   * 打开文件选择对话框选择文件。所选文件路径将显示在此文本框中。
   */
  private JTextField textField;

  /**
   * A button that opens a file chooser dialog to allow the user to select a file.
   *
   * <p>打开文件选择对话框以允许用户选择文件的按钮。
   */
  private JButton viewButton;

  /**
   * A button that confirms the file path entered in the text field and proceeds to the next step.
   *
   * <p>确认文本框中输入的文件路径并进行下一步操作的按钮。
   */
  private JButton confirmButton;

  /**
   * A secondary frame used to display various options after the file path has been confirmed.
   *
   * <p>确认文件路径后用于显示各种选项的第二个窗口。
   */
  private JFrame optionsFrame;

  /**
   * A combo box that allows the user to select a function to perform on the file.
   *
   * <p>允许用户选择对文件执行的功能的下拉框。
   */
  private JComboBox<String> functionBox;

  /**
   * A button within the options frame that confirms the selected function.
   *
   * <p>选项窗口内确认所选功能的按钮。
   */
  private JButton optionsConfirmButton;

  /**
   * Stores the file path selected by the user.
   *
   * <p>存储用户选择的文件路径。
   */
  private String filePath;

  /**
   * Constructor.
   */
  public MyFrame() {
    setTitle("program");
    setSize(500, 120);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLayout(new FlowLayout());

    textField = new JTextField(20);
    viewButton = new JButton("View");
    confirmButton = new JButton("Confirm");

    add(textField);
    add(viewButton);
    add(confirmButton);

    // View鎸夐挳鐨勭洃鍚簨浠?
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

    // Confirm鎸夐挳鐨勭洃鍚簨浠?
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

    String[] functions = {"Show directed graph", "Search bridge word",
        "Generate new text", "Calculate the shortest route from word1 to word2",
        "Calculate all the shortest route from word", "Random walk"};
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
      TextGraph graph = Interface.createTextGraph(path); // 使锟斤拷锟睫改猴拷锟铰凤拷锟斤拷锟斤拷锟酵?
      switch (index) {
        case 0:  // Show directed graph

          Interface.showTextGraph(graph, path.replace("GraphData.txt", ""),
              path.replace("GraphData.txt", ""), false);
          showImage(path.replace("GraphData.txt", "") + "Graph.png");

          break;
        case 1:  // Search bridge world

          String input = JOptionPane.showInputDialog("Please input two words separated with space");
          if (input != null) {
            String[] words = input.split("\\s+");
            if (words.length >= 2) {

              JOptionPane.showMessageDialog(null,
                  Interface.queryBridgeWords(graph, words[0], words[1]));

            }
          }
          break;
        case 2:  // Generate new text
          String text = JOptionPane.showInputDialog("Please input text");
          if (text != null) {
            JOptionPane.showMessageDialog(null, Interface.generateNewText(graph, text));
          }
          break;
        case 3:  // Calculate the shortest route from word1 to word2
          input = JOptionPane.showInputDialog("Please input two words separated with space");
          if (input != null) {
            String[] words = input.split("\\s+");
            if (words.length == 2) {
              if (Interface.calcShortestPath(graph, words[0], words[1]) == null) {
                JOptionPane.showMessageDialog(null, "\""
                    + words[0] + "\" or \"" + words[1] + "\" doesn't exist!");
              } else {
                JOptionPane.showMessageDialog(null,
                    Interface.calcShortestPath(graph, words[0], words[1]));
                Interface.showTextGraph(graph, path.replace("GraphData.txt", "") + "normal",
                    path.replace("GraphData.txt", "") + "normal", true);
                showImage(path.replace("GraphData.txt", "") + "normal/Graph.png");
              }
            } else {
              JOptionPane.showMessageDialog(null, "You should input two words, try again!");
            }

          }
          break;
        case 4:  // Calculate all the shortest route from word
          String word = JOptionPane.showInputDialog("Please input a word");
          if (word != null) {
            if (Interface.calcShortestPath(graph, word) == null) {
              JOptionPane.showMessageDialog(null, "\"" + word + "\" doesn't exist!");
            } else {
              JOptionPane.showMessageDialog(null, Interface.calcShortestPath(graph, word));
            }
          }
          break;
        case 5:  // Random Walk
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
                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(null,
                    "error when random walk: " + e.getMessage()));
              }
            });
            walkThread.start();

            stopButton.addActionListener(event -> {
              walkThread.interrupt();  // 锟斤拷锟斤拷锟叫讹拷锟脚猴拷
            });
          }
          break;
        default:
          throw new IllegalStateException("Unexpected value: " + index);
      }
    } catch (IOException e) {
      JOptionPane.showMessageDialog(null, "Process error: " + e.getMessage());
    }
  }

  private void showImage(String imagePath) {
    try {
      BufferedImage image = ImageIO.read(new File(imagePath));  // 浣跨敤ImageIO璇诲彇鍥剧墖
      ImageIcon icon = new ImageIcon(image);  // 鍒涘缓涓?涓柊鐨処mageIcon
      JLabel label = new JLabel(icon);
      JScrollPane scrollPane = new JScrollPane(label);  // 浣跨敤JScrollPane鍖呰９JLabel
      scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
      scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

      JFrame frame = new JFrame("Show Graph");
      frame.getContentPane().removeAll();  // 绉婚櫎鏃х粍浠?
      frame.add(scrollPane);  // 灏嗘粴鍔ㄩ潰鏉挎坊鍔犲埌绐楀彛涓?
      frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
      frame.pack();  // 璋冩暣绐楀彛澶у皬浠ラ?傚簲鍐呭
      frame.setVisible(true);
    } catch (IOException e) {
      JOptionPane.showMessageDialog(null, "Error loading image: " + e.getMessage());
    }
  }

  /**
   * main.
   */
  public static void main(String[] args) {
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        new MyFrame().setVisible(true);
      }
    });
  }
}
