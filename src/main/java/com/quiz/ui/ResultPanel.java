package com.quiz.ui;

import com.quiz.controller.QuizController;
import com.quiz.model.QuizScore;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Map;

/**
 * Shows the final score after the quiz ends.
 * Displays: score, percentage, correct/wrong count, and a grade.
 */
public class ResultPanel extends JPanel {

    private final QuizController controller;
    private final AppFrame frame;

    private JLabel scoreLabel;
    private JLabel percentLabel;
    private JLabel gradeLabel;
    private JLabel correctLabel;
    private JLabel wrongLabel;
    private JLabel timeLabel;
    private JLabel messageLabel;

    public ResultPanel(QuizController controller, AppFrame frame) {
        this.controller = controller;
        this.frame = frame;
        build();
    }

    private void build() {
        setLayout(new BorderLayout());
        setBackground(new Color(44, 62, 80));

        // ── Header ────────────────────────────────────────
        JPanel header = new JPanel();
        header.setBackground(new Color(41, 128, 185));
        header.setBorder(new EmptyBorder(20, 0, 20, 0));
        JLabel title = new JLabel("Quiz Complete!", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(Color.WHITE);
        header.add(title);
        add(header, BorderLayout.NORTH);

        // ── CENTER: Score display ─────────────────────────
        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBackground(new Color(44, 62, 80));
        center.setBorder(new EmptyBorder(20, 60, 20, 60));

        // Big score
        scoreLabel = new JLabel("0 / 0", SwingConstants.CENTER);
        scoreLabel.setFont(new Font("Segoe UI", Font.BOLD, 56));
        scoreLabel.setForeground(Color.WHITE);
        scoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        percentLabel = new JLabel("0%", SwingConstants.CENTER);
        percentLabel.setFont(new Font("Segoe UI", Font.PLAIN, 22));
        percentLabel.setForeground(new Color(200, 220, 240));
        percentLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        gradeLabel = new JLabel("Grade: A", SwingConstants.CENTER);
        gradeLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        gradeLabel.setForeground(new Color(243, 156, 18));
        gradeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        messageLabel = new JLabel("", SwingConstants.CENTER);
        messageLabel.setFont(new Font("Segoe UI", Font.ITALIC, 15));
        messageLabel.setForeground(new Color(180, 200, 220));
        messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Stats row: Correct | Wrong | Time
        JPanel statsRow = new JPanel(new GridLayout(1, 3, 15, 0));
        statsRow.setOpaque(false);
        statsRow.setMaximumSize(new Dimension(400, 70));
        statsRow.setAlignmentX(Component.CENTER_ALIGNMENT);

        correctLabel = makeStatCard("Correct", "0", new Color(39, 174, 96));
        wrongLabel   = makeStatCard("Wrong",   "0", new Color(231, 76, 60));
        timeLabel    = makeStatCard("Time",    "0s", new Color(52, 152, 219));

        statsRow.add(correctLabel.getParent());
        statsRow.add(wrongLabel.getParent());
        statsRow.add(timeLabel.getParent());

        center.add(Box.createVerticalStrut(10));
        center.add(scoreLabel);
        center.add(Box.createVerticalStrut(5));
        center.add(percentLabel);
        center.add(Box.createVerticalStrut(5));
        center.add(gradeLabel);
        center.add(Box.createVerticalStrut(5));
        center.add(messageLabel);
        center.add(Box.createVerticalStrut(20));
        center.add(statsRow);

        add(center, BorderLayout.CENTER);

        // ── SOUTH: Buttons ────────────────────────────────
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        btnRow.setBackground(new Color(44, 62, 80));

        JButton lbBtn      = makeBtn("Leaderboard", new Color(41, 128, 185));
        JButton retryBtn   = makeBtn("Play Again",  new Color(39, 174, 96));
        JButton homeBtn    = makeBtn("Home",         new Color(127, 140, 141));

        homeBtn.addActionListener(e  -> frame.showScreen("WELCOME"));
        retryBtn.addActionListener(e -> frame.showScreen("WELCOME"));
        lbBtn.addActionListener(e    -> frame.goToLeaderboard());

        btnRow.add(homeBtn);
        btnRow.add(retryBtn);
        btnRow.add(lbBtn);
        add(btnRow, BorderLayout.SOUTH);
    }

    /** Populates the result screen with actual quiz data */
    public void showResult(QuizScore score, Map<String, Boolean> results) {
        int correct = score.getScore();
        int total   = score.getTotalQuestions();
        int wrong   = total - correct;
        double pct  = score.getPercentage();

        scoreLabel.setText(correct + " / " + total);
        percentLabel.setText(String.format("%.0f%%", pct));
        timeLabel.setText(score.getTimeTakenSeconds() + "s");
        correctLabel.setText(String.valueOf(correct));
        wrongLabel.setText(String.valueOf(wrong));

        // Grade and message
        String grade, message;
        if      (pct >= 90) { grade = "A+"; message = "Outstanding!"; }
        else if (pct >= 80) { grade = "A";  message = "Excellent work!"; }
        else if (pct >= 70) { grade = "B";  message = "Good job!"; }
        else if (pct >= 60) { grade = "C";  message = "Keep practicing!"; }
        else if (pct >= 50) { grade = "D";  message = "You can do better!"; }
        else                { grade = "F";  message = "Study more and try again!"; }

        gradeLabel.setText("Grade: " + grade);
        messageLabel.setText(message);
    }

    private JLabel makeStatCard(String label, String value, Color color) {
        JPanel card = new JPanel(new GridLayout(2, 1));
        card.setBackground(color);
        card.setBorder(new EmptyBorder(10, 15, 10, 15));

        JLabel lbl = new JLabel(label, SwingConstants.CENTER);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lbl.setForeground(new Color(255, 255, 255, 200));

        JLabel val = new JLabel(value, SwingConstants.CENTER);
        val.setFont(new Font("Segoe UI", Font.BOLD, 20));
        val.setForeground(Color.WHITE);

        card.add(lbl);
        card.add(val);
        return val; // return value label so we can update it
    }

    private JButton makeBtn(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setPreferredSize(new Dimension(130, 38));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }
}