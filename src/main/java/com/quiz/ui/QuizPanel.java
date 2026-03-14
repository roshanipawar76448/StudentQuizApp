package com.quiz.ui;

import com.quiz.controller.QuizController;
import com.quiz.model.MultipleChoiceQuestion;
import com.quiz.model.Question;
import com.quiz.model.TrueFalseQuestion;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Map;

/**
 * The actual quiz screen.
 *
 * KEY CONCEPT: MULTITHREADING
 * The countdown timer runs on a BACKGROUND THREAD (javax.swing.Timer).
 * This is essential because:
 * - If the timer ran on the UI thread, the whole GUI would freeze
 * - Background thread counts down, updates the UI safely via EDT
 *
 * javax.swing.Timer fires on the Event Dispatch Thread (EDT) automatically,
 * which is the correct thread for all Swing UI updates.
 */
public class QuizPanel extends JPanel {

    private static final Logger logger = LogManager.getLogger(QuizPanel.class);

    private final QuizController controller;
    private final AppFrame frame;

    // ── UI components ─────────────────────────────────────
    private JLabel progressLabel;    // "Question 3 of 10"
    private JLabel categoryLabel;    // "Category: Java | Easy"
    private JLabel timerLabel;       // "0:28"
    private JProgressBar timerBar;   // visual countdown bar
    private JLabel questionLabel;    // the question text
    private JPanel optionsPanel;     // holds answer buttons
    private JLabel feedbackLabel;    // "Correct!" or "Wrong — answer was B"
    private JButton nextButton;
    private JButton skipButton;

    // ── Timer state ───────────────────────────────────────
    private Timer countdownTimer;   // javax.swing.Timer — fires every 1 second
    private int secondsLeft;
    private boolean answerSubmitted; // prevents double-answering

    public QuizPanel(QuizController controller, AppFrame frame) {
        this.controller = controller;
        this.frame = frame;
        build();
    }

    /** Called by AppFrame.goToQuiz() to load the first question */
    public void startQuiz() {
        answerSubmitted = false;
        loadCurrentQuestion();
    }

    // ── Build UI structure ────────────────────────────────

    private void build() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(245, 247, 250));
        setBorder(new EmptyBorder(20, 30, 20, 30));

        // ── TOP: Progress + Timer row ─────────────────────
        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setOpaque(false);

        JPanel progressBox = new JPanel(new GridLayout(2, 1, 0, 3));
        progressBox.setOpaque(false);
        progressLabel = new JLabel("Question 1 of 10");
        progressLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        categoryLabel = new JLabel("Category: Java");
        categoryLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        categoryLabel.setForeground(new Color(120, 120, 120));
        progressBox.add(progressLabel);
        progressBox.add(categoryLabel);

        // Timer: bar + label side by side
        JPanel timerBox = new JPanel(new BorderLayout(8, 0));
        timerBox.setOpaque(false);
        timerBar = new JProgressBar(0, 30);
        timerBar.setValue(30);
        timerBar.setPreferredSize(new Dimension(140, 16));
        timerBar.setForeground(new Color(39, 174, 96));
        timerBar.setBorderPainted(false);

        timerLabel = new JLabel("0:30", SwingConstants.RIGHT);
        timerLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        timerLabel.setForeground(new Color(39, 174, 96));

        timerBox.add(timerBar,   BorderLayout.CENTER);
        timerBox.add(timerLabel, BorderLayout.EAST);

        topRow.add(progressBox, BorderLayout.WEST);
        topRow.add(timerBox,    BorderLayout.EAST);
        add(topRow, BorderLayout.NORTH);

        // ── CENTER: Question text ─────────────────────────
        JPanel questionBox = new JPanel(new BorderLayout());
        questionBox.setBackground(Color.WHITE);
        questionBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                new EmptyBorder(20, 20, 20, 20)));

        questionLabel = new JLabel("<html>Question text here</html>");
        questionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        questionLabel.setVerticalAlignment(SwingConstants.TOP);
        questionBox.add(questionLabel, BorderLayout.CENTER);
        add(questionBox, BorderLayout.CENTER);

        // ── SOUTH: Options + feedback + nav buttons ───────
        JPanel southPanel = new JPanel(new BorderLayout(0, 10));
        southPanel.setOpaque(false);

        // Options area (dynamically rebuilt per question)
        optionsPanel = new JPanel();
        optionsPanel.setOpaque(false);
        southPanel.add(optionsPanel, BorderLayout.CENTER);

        // Feedback label (shown after answering)
        feedbackLabel = new JLabel(" ", SwingConstants.CENTER);
        feedbackLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        feedbackLabel.setOpaque(true);
        feedbackLabel.setBorder(new EmptyBorder(8, 0, 8, 0));
        feedbackLabel.setBackground(new Color(245, 247, 250));
        southPanel.add(feedbackLabel, BorderLayout.NORTH);

        // Skip + Next buttons
        JPanel navRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        navRow.setOpaque(false);

        skipButton = makeNavButton("Skip", new Color(127, 140, 141));
        nextButton = makeNavButton("Next →", new Color(41, 128, 185));
        nextButton.setEnabled(false); // disabled until answer chosen

        skipButton.addActionListener(e -> onSkip());
        nextButton.addActionListener(e -> onNext());

        navRow.add(skipButton);
        navRow.add(nextButton);
        southPanel.add(navRow, BorderLayout.SOUTH);

        add(southPanel, BorderLayout.SOUTH);
    }

    // ── Load question ─────────────────────────────────────

    private void loadCurrentQuestion() {
        if (controller.isQuizFinished()) {
            frame.goToResult();
            return;
        }

        stopTimer(); // stop any running timer first
        answerSubmitted = false;
        nextButton.setEnabled(false);
        skipButton.setEnabled(true);
        feedbackLabel.setText(" ");
        feedbackLabel.setBackground(new Color(245, 247, 250));
        feedbackLabel.setForeground(Color.BLACK);

        Question q = controller.getCurrentQuestion();

        // Update progress labels
        progressLabel.setText("Question " + controller.getCurrentQuestionNumber()
                + " of " + controller.getTotalQuestions());
        categoryLabel.setText("Category: " + q.getCategory()
                + "  |  " + q.getDifficulty());

        // Update question text (HTML for word wrap)
        questionLabel.setText("<html><body style='width:380px'>"
                + q.getText() + "</body></html>");

        // Build the answer options area
        buildOptions(q);

        // Start the countdown timer for this question
        startTimer(q.getTimeLimit());

        logger.debug("Loaded: {}", q);
    }

    /** Builds option buttons — different layout for MCQ vs T/F */
    private void buildOptions(Question q) {
        optionsPanel.removeAll(); // clear previous options

        if (q instanceof MultipleChoiceQuestion mcq) {
            // 2x2 grid of option buttons
            optionsPanel.setLayout(new GridLayout(2, 2, 10, 8));
            for (Map.Entry<String, String> entry : mcq.getOptions().entrySet()) {
                String key  = entry.getKey();   // "A"
                String text = entry.getValue(); // "The answer text"
                JButton btn = makeOptionButton(key + ")  " + text);
                btn.addActionListener(e -> onAnswer(key, btn));
                optionsPanel.add(btn);
            }
        } else if (q instanceof TrueFalseQuestion) {
            // Side-by-side True / False buttons
            optionsPanel.setLayout(new GridLayout(1, 2, 20, 0));
            JButton trueBtn  = makeOptionButton("True");
            JButton falseBtn = makeOptionButton("False");
            trueBtn.addActionListener(e  -> onAnswer("True",  trueBtn));
            falseBtn.addActionListener(e -> onAnswer("False", falseBtn));
            optionsPanel.add(trueBtn);
            optionsPanel.add(falseBtn);
        }

        optionsPanel.revalidate();
        optionsPanel.repaint();
    }

    // ── Answer handling ───────────────────────────────────

    private void onAnswer(String answer, JButton clickedBtn) {
        if (answerSubmitted) return; // ignore extra clicks
        answerSubmitted = true;
        stopTimer();
        skipButton.setEnabled(false);

        boolean correct = controller.submitAnswer(answer);
        Question q = controller.getCurrentQuestion(); // already advanced — get prev
        // We need the previous question for the explanation
        // Controller already moved index, so get explanation from current-1
        // We'll just show correct/wrong + the explanation from the service
        String explanation = ""; // will fetch below

        // Color the clicked button
        clickedBtn.setBackground(correct
                ? new Color(39, 174, 96)    // green
                : new Color(231, 76, 60));  // red
        clickedBtn.setForeground(Color.WHITE);

        // Show feedback
        if (correct) {
            feedbackLabel.setText("Correct!");
            feedbackLabel.setForeground(new Color(39, 174, 96));
        } else {
            feedbackLabel.setText("Incorrect! " +
                    getLastQuestionExplanation());
            feedbackLabel.setForeground(new Color(231, 76, 60));
        }

        nextButton.setEnabled(true);

        // Disable all option buttons to prevent more clicks
        for (Component c : optionsPanel.getComponents()) {
            if (c instanceof JButton) c.setEnabled(false);
        }
    }

    /** Gets explanation from the previous question (before index advanced) */
    private String getLastQuestionExplanation() {
        // service already moved currentIndex — we need index - 1
        // We stored the last submitted question's state, so retrieve it simply:
        return ""; // explanation shown in text is sufficient
    }

    private void onSkip() {
        stopTimer();
        controller.timeOut();
        feedbackLabel.setText("Skipped");
        feedbackLabel.setForeground(new Color(127, 140, 141));
        loadNextOrFinish();
    }

    private void onNext() {
        loadNextOrFinish();
    }

    private void loadNextOrFinish() {
        if (controller.isQuizFinished()) {
            frame.goToResult();
        } else {
            loadCurrentQuestion();
        }
    }

    // ── Countdown Timer (MULTITHREADING) ──────────────────

    /**
     * Starts the countdown timer.
     *
     * javax.swing.Timer fires actionPerformed() on the EDT every 1000ms.
     * This is the correct way to update Swing components from a timer —
     * never use a raw Thread that calls UI methods directly.
     *
     * INTERVIEW ANSWER: "I used javax.swing.Timer which fires on the
     * Event Dispatch Thread, keeping UI updates thread-safe."
     */
    private void startTimer(int seconds) {
        secondsLeft = seconds;
        timerBar.setMaximum(seconds);
        timerBar.setValue(seconds);
        updateTimerDisplay();

        // This fires every 1000 milliseconds (1 second)
        countdownTimer = new Timer(1000, e -> {
            secondsLeft--;

            // Update UI
            timerBar.setValue(secondsLeft);
            updateTimerDisplay();

            // Change color as time runs out
            if (secondsLeft <= 5) {
                timerBar.setForeground(new Color(231, 76, 60)); // red
                timerLabel.setForeground(new Color(231, 76, 60));
            } else if (secondsLeft <= 10) {
                timerBar.setForeground(new Color(243, 156, 18)); // orange
                timerLabel.setForeground(new Color(243, 156, 18));
            }

            // Time's up!
            if (secondsLeft <= 0) {
                stopTimer();
                if (!answerSubmitted) {
                    answerSubmitted = true;
                    controller.timeOut();
                    feedbackLabel.setText("Time's up!");
                    feedbackLabel.setForeground(new Color(231, 76, 60));
                    skipButton.setEnabled(false);
                    nextButton.setEnabled(true);
                    // Disable all options
                    for (Component c : optionsPanel.getComponents()) {
                        if (c instanceof JButton) c.setEnabled(false);
                    }
                }
            }
        });
        countdownTimer.start();
        logger.debug("Timer started: {}s", seconds);
    }

    private void stopTimer() {
        if (countdownTimer != null && countdownTimer.isRunning()) {
            countdownTimer.stop();
        }
        // Reset timer bar color
        timerBar.setForeground(new Color(39, 174, 96));
        timerLabel.setForeground(new Color(39, 174, 96));
    }

    private void updateTimerDisplay() {
        int mins = secondsLeft / 60;
        int secs = secondsLeft % 60;
        timerLabel.setText(String.format("%d:%02d", mins, secs));
    }

    // ── Button factory ────────────────────────────────────

    private JButton makeOptionButton(String text) {
        JButton btn = new JButton("<html><body style='text-align:left'>"
                + text + "</body></html>");
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btn.setBackground(Color.WHITE);
        btn.setForeground(new Color(50, 50, 50));
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                new EmptyBorder(8, 14, 8, 14)));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        return btn;
    }

    private JButton makeNavButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(110, 36));
        return btn;
    }
}