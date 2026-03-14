package com.quiz.ui;

import com.quiz.controller.QuizController;
import com.quiz.exception.QuizException;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

/**
 * The first screen users see.
 * Collects: player name, category, difficulty, question count
 * Then calls controller.startQuiz() and navigates to QuizPanel.
 */
public class WelcomePanel extends JPanel {

    private final QuizController controller;
    private final AppFrame frame;

    private JTextField nameField;
    private JComboBox<String> categoryCombo;
    private JComboBox<String> difficultyCombo;
    private JComboBox<Integer> countCombo;

    public WelcomePanel(QuizController controller, AppFrame frame) {
        this.controller = controller;
        this.frame = frame;
        build();
    }

    private void build() {
        setLayout(new BorderLayout());
        setBackground(new Color(44, 62, 80)); // dark blue-gray

        // ── TOP: App title banner ─────────────────────────
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(41, 128, 185));
        header.setBorder(new EmptyBorder(30, 40, 30, 40));

        JLabel title = new JLabel("Student Quiz App", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 32));
        title.setForeground(Color.WHITE);

        JLabel subtitle = new JLabel("Test your Java knowledge!", SwingConstants.CENTER);
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subtitle.setForeground(new Color(200, 230, 255));

        header.add(title, BorderLayout.CENTER);
        header.add(subtitle, BorderLayout.SOUTH);
        add(header, BorderLayout.NORTH);

        // ── CENTER: Options form ──────────────────────────
        JPanel formCard = new JPanel();
        formCard.setLayout(new BoxLayout(formCard, BoxLayout.Y_AXIS));
        formCard.setBackground(Color.WHITE);
        formCard.setBorder(new EmptyBorder(30, 60, 30, 60));

        // Player Name
        addFormLabel(formCard, "Your Name");
        nameField = new JTextField();
        styleTextField(nameField);
        nameField.setText("Player 1");
        formCard.add(nameField);
        formCard.add(Box.createVerticalStrut(15));

        // Category selector — populated from XML categories
        addFormLabel(formCard, "Category");
        List<String> categories = controller.getAvailableCategories();
        categoryCombo = new JComboBox<>(categories.toArray(new String[0]));
        styleCombo(categoryCombo);
        formCard.add(categoryCombo);
        formCard.add(Box.createVerticalStrut(15));

        // Difficulty selector
        addFormLabel(formCard, "Difficulty");
        difficultyCombo = new JComboBox<>(
                new String[]{"All", "EASY", "MEDIUM", "HARD"});
        styleCombo(difficultyCombo);
        formCard.add(difficultyCombo);
        formCard.add(Box.createVerticalStrut(15));

        // Number of questions
        addFormLabel(formCard, "Number of Questions");
        countCombo = new JComboBox<>(new Integer[]{5, 10, 15});
        countCombo.setSelectedItem(10);
        styleCombo(countCombo);
        formCard.add(countCombo);

        // Center the form card
        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setBackground(new Color(44, 62, 80));
        centerWrapper.add(formCard);
        add(centerWrapper, BorderLayout.CENTER);

        // ── BOTTOM: Buttons ───────────────────────────────
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        btnPanel.setBackground(new Color(44, 62, 80));

        JButton startBtn = makeButton("Start Quiz", new Color(39, 174, 96));
        JButton lbBtn    = makeButton("Leaderboard", new Color(41, 128, 185));

        startBtn.addActionListener(e -> onStart());
        lbBtn.addActionListener(e -> frame.goToLeaderboard());

        btnPanel.add(startBtn);
        btnPanel.add(lbBtn);
        add(btnPanel, BorderLayout.SOUTH);
    }

    private void onStart() {
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter your name.", "Missing Name",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            String category   = (String)  categoryCombo.getSelectedItem();
            String difficulty = (String)  difficultyCombo.getSelectedItem();
            int    count      = (Integer) countCombo.getSelectedItem();

            controller.startQuiz(name, category, difficulty, count);
            frame.goToQuiz();

        } catch (QuizException e) {
            JOptionPane.showMessageDialog(this,
                    e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ── UI helpers ────────────────────────────────────────

    private void addFormLabel(JPanel panel, String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setForeground(new Color(60, 60, 60));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(label);
        panel.add(Box.createVerticalStrut(4));
    }

    private void styleTextField(JTextField field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                new EmptyBorder(7, 10, 7, 10)));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
    }

    private void styleCombo(JComboBox<?> combo) {
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        combo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        combo.setAlignmentX(Component.LEFT_ALIGNMENT);
    }

    private JButton makeButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(150, 40));
        return btn;
    }
}