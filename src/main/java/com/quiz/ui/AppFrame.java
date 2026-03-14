package com.quiz.ui;

import com.quiz.controller.QuizController;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;

/**
 * The outer window that holds all 4 screens.
 *
 * CardLayout = like a deck of cards.
 * Only one card (screen) is visible at a time.
 * showScreen("QUIZ") flips to the quiz card.
 *
 * SCREENS:
 *  "WELCOME"     → WelcomePanel
 *  "QUIZ"        → QuizPanel
 *  "RESULT"      → ResultPanel
 *  "LEADERBOARD" → LeaderboardPanel
 */
public class AppFrame extends JFrame {

    private static final Logger logger = LogManager.getLogger(AppFrame.class);

    // CardLayout manages which panel is visible
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel cardPanel = new JPanel(cardLayout);

    private final QuizController controller;

    // All 4 screen panels
    private WelcomePanel welcomePanel;
    private QuizPanel quizPanel;
    private ResultPanel resultPanel;
    private LeaderboardPanel leaderboardPanel;

    public AppFrame(QuizController controller) {
        this.controller = controller;
        setupWindow();
        buildPanels();
        showScreen("WELCOME");
        logger.info("AppFrame ready");
    }

    private void setupWindow() {
        setTitle("Student Quiz App");
        setSize(700, 520);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // center on screen
        setResizable(false);
        setLayout(new BorderLayout());
        add(cardPanel, BorderLayout.CENTER);
    }

    private void buildPanels() {
        // Create all panels, passing 'this' so they can call showScreen()
        welcomePanel     = new WelcomePanel(controller, this);
        quizPanel        = new QuizPanel(controller, this);
        resultPanel      = new ResultPanel(controller, this);
        leaderboardPanel = new LeaderboardPanel(controller, this);

        // Add each panel to the card deck with a name
        cardPanel.add(welcomePanel,     "WELCOME");
        cardPanel.add(quizPanel,        "QUIZ");
        cardPanel.add(resultPanel,      "RESULT");
        cardPanel.add(leaderboardPanel, "LEADERBOARD");
    }

    /**
     * Flips to the named screen.
     * Called by every panel when it wants to navigate away.
     */
    public void showScreen(String name) {
        cardLayout.show(cardPanel, name);
        logger.debug("Navigated to screen: {}", name);
    }

    // ── Navigation helpers called by panels ───────────────

    /** Transition to quiz screen — triggers panel to prepare itself */
    public void goToQuiz() {
        quizPanel.startQuiz();
        showScreen("QUIZ");
    }

    /** Transition to result screen with the final score */
    public void goToResult() {
        resultPanel.showResult(controller.finishQuiz(), controller.getAnswerResults());
        showScreen("RESULT");
    }

    /** Transition to leaderboard */
    public void goToLeaderboard() {
        leaderboardPanel.refresh();
        showScreen("LEADERBOARD");
    }
}