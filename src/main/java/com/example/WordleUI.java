package com.example;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class WordleUI extends JFrame {

    private JLabel[][] grid = new JLabel[6][5];
    private HashMap<String, JLabel> keyMap = new HashMap<>();
    private int currentRow = 0;
    private int currentCol = 0;
    private StringBuilder currentInput = new StringBuilder();
    private logic gameLogic = new logic();
    private List<String> secretWord;

    public WordleUI() {
        setTitle("Wordle");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        getContentPane().setBackground(Color.BLACK);
        setLayout(new BorderLayout());

        JPanel gridWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER));
        gridWrapper.setBackground(Color.BLACK);
        gridWrapper.add(buildGrid());

        JPanel keyboardWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER));
        keyboardWrapper.setBackground(Color.BLACK);
        keyboardWrapper.add(buildKeyboard());

        add(gridWrapper, BorderLayout.CENTER);
        add(keyboardWrapper, BorderLayout.SOUTH);

        setupInput();

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private JPanel buildGrid() {
        JPanel panel = new JPanel(new GridLayout(6, 5, 5, 5));
        panel.setBackground(Color.BLACK);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setPreferredSize(new Dimension(340, 420));

        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 5; j++) {
                JLabel cell = new JLabel("", SwingConstants.CENTER);
                cell.setPreferredSize(new Dimension(60, 60));
                cell.setFont(new Font("Arial", Font.BOLD, 24));
                cell.setForeground(Color.WHITE);
                cell.setBackground(new Color(18, 18, 19));
                cell.setOpaque(true);
                cell.setBorder(BorderFactory.createLineBorder(new Color(58, 58, 60), 2));
                grid[i][j] = cell;
                panel.add(cell);
            }
        }
        return panel;
    }

    private JPanel buildKeyboard() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.BLACK);
        panel.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));

        String[] row1 = {"Q","W","E","R","T","Y","U","I","O","P"};
        String[] row2 = {"A","S","D","F","G","H","J","K","L"};
        String[] row3 = {"Z","X","C","V","B","N","M"};

        panel.add(buildKeyRow(row1));
        panel.add(Box.createVerticalStrut(5));
        panel.add(buildKeyRow(row2));
        panel.add(Box.createVerticalStrut(5));
        panel.add(buildKeyRow(row3));
        panel.add(Box.createVerticalStrut(10));

        return panel;
    }

    private JPanel buildKeyRow(String[] letters) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        row.setBackground(Color.BLACK);

        for (String letter : letters) {
            JLabel key = new JLabel(letter, SwingConstants.CENTER);
            key.setPreferredSize(new Dimension(40, 40));
            key.setFont(new Font("Arial", Font.BOLD, 14));
            key.setForeground(Color.WHITE);
            key.setBackground(new Color(129, 131, 132));
            key.setOpaque(true);
            key.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
            keyMap.put(letter, key);
            row.add(key);
        }
        return row;
    }

    private void setupInput() {
        gameLogic.loadWords(logic.lawords, gameLogic.lawordslist);
        gameLogic.loadWords(logic.tawords, gameLogic.tawordslist);
        secretWord = gameLogic.selectWord(gameLogic.lawordslist);

        addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {
                int key = e.getKeyCode();

                if (key == java.awt.event.KeyEvent.VK_BACK_SPACE) {
                    if (currentCol > 0) {
                        currentCol--;
                        currentInput.deleteCharAt(currentInput.length() - 1);
                        grid[currentRow][currentCol].setText("");
                    }
                    return;
                }

                if (key == java.awt.event.KeyEvent.VK_ENTER) {
                    if (currentCol == 5) {
                        processGuess();
                    }
                    return;
                }

                if (key >= java.awt.event.KeyEvent.VK_A && key <= java.awt.event.KeyEvent.VK_Z) {
                    if (currentCol < 5) {
                        String letter = String.valueOf((char) key);
                        currentInput.append(letter.toLowerCase());
                        grid[currentRow][currentCol].setText(letter);
                        currentCol++;
                    }
                }
            }
        });

        setFocusable(true);
        requestFocusInWindow();
    }

    private void processGuess() {
        String input = currentInput.toString();

        if (!gameLogic.verifyWord(input)) {
            JOptionPane.showMessageDialog(this, "Word not in list!");
            return;
        }

        List<String> letters = new ArrayList<>(Arrays.asList(input.split("")));

        for (int i = 0; i < 5; i++) {
            String letter = letters.get(i);
            Color cellColor;

            if (letter.equals(secretWord.get(i))) {
                cellColor = new Color(83, 141, 78);
            } else if (secretWord.contains(letter)) {
                cellColor = new Color(181, 159, 59);
            } else {
                cellColor = new Color(58, 58, 60);
            }

            grid[currentRow][i].setBackground(cellColor);
            grid[currentRow][i].setBorder(BorderFactory.createLineBorder(cellColor, 2));

            JLabel key = keyMap.get(letter.toUpperCase());
            if (key != null) {
                if (!key.getBackground().equals(new Color(83, 141, 78))) {
                    key.setBackground(cellColor);
                }
            }
        }

        if (letters.equals(secretWord)) {
            JOptionPane.showMessageDialog(this, "¡Correct! 🎉");
            return;
        }

        currentRow++;
        currentCol = 0;
        currentInput.setLength(0);

        if (currentRow == 6) {
            String answer = String.join("", secretWord);
            JOptionPane.showMessageDialog(this, "Game over! The word was: " + answer);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new WordleUI());
    }
}