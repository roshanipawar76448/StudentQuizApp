package com.quiz.ui;

import com.quiz.controller.QuizController;
import com.quiz.model.QuizScore;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.List;

/**
 * Shows the top 10 scores in a table.
 * Scores are loaded from scores.xml and sorted best-first.
 */
public class LeaderboardPanel extends JPanel {

    private final QuizController controller;
    private final AppFrame frame;
    private DefaultTableModel tableModel;

    private static final String[] COLUMNS =
            {"Rank", "Player", "Score", "Percentage", "Time (s)", "Date"};

    public LeaderboardPanel(QuizController controller, AppFrame frame) {
        this.controller = controller;
        this.frame = frame;
        build();
    }

    private void build() {
        setLayout(new BorderLayout());
        setBackground(new Color(44, 62, 80));

        // ── Header ────────────────────────────────────────
        JPanel header = new JPanel();
        header.setBackground(new Color(243, 156, 18)); // gold
        header.setBorder(new EmptyBorder(18, 0, 18, 0));
        JLabel title = new JLabel("Leaderboard", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(Color.WHITE);
        header.add(title);
        add(header, BorderLayout.NORTH);

        // ── Table ─────────────────────────────────────────
        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        JTable table = new JTable(tableModel);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(32);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBackground(new Color(52, 73, 94));
        table.getTableHeader().setForeground(Color.WHITE);
        table.setGridColor(new Color(80, 100, 120));
        table.setBackground(new Color(52, 73, 94));
        table.setForeground(Color.WHITE);
        table.setSelectionBackground(new Color(243, 156, 18, 100));

        // Gold for rank 1, silver for rank 2, bronze for rank 3
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val,
                                                           boolean sel, boolean foc, int row, int col) {
                Component c = super.getTableCellRendererComponent(
                        t, val, sel, foc, row, col);
                c.setForeground(Color.WHITE);
                if (!sel) {
                    if (row == 0) c.setBackground(new Color(180, 140, 20));
                    else if (row == 1) c.setBackground(new Color(100, 110, 120));
                    else if (row == 2) c.setBackground(new Color(140, 80, 40));
                    else c.setBackground(new Color(52, 73, 94));
                }
                return c;
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(new EmptyBorder(15, 20, 15, 20));
        scroll.getViewport().setBackground(new Color(52, 73, 94));
        add(scroll, BorderLayout.CENTER);

        // ── Buttons ───────────────────────────────────────
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        btnRow.setBackground(new Color(44, 62, 80));

        JButton homeBtn = makeBtn("Home", new Color(127, 140, 141));
        JButton playBtn = makeBtn("Play Quiz", new Color(39, 174, 96));

        homeBtn.addActionListener(e -> frame.showScreen("WELCOME"));
        playBtn.addActionListener(e -> frame.showScreen("WELCOME"));

        btnRow.add(homeBtn);
        btnRow.add(playBtn);
        add(btnRow, BorderLayout.SOUTH);
    }

    /** Reloads and displays the latest scores — called every time we navigate here */
    public void refresh() {
        tableModel.setRowCount(0); // clear table
        List<QuizScore> scores = controller.getTopScores(10);

        for (int i = 0; i < scores.size(); i++) {
            QuizScore s = scores.get(i);
            tableModel.addRow(new Object[]{
                    "#" + (i + 1),
                    s.getPlayerName(),
                    s.getScore() + "/" + s.getTotalQuestions(),
                    String.format("%.0f%%", s.getPercentage()),
                    s.getTimeTakenSeconds(),
                    s.getPlayedAt().toLocalDate().toString()
            });
        }
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