package com.quiz;

import com.quiz.controller.QuizController;
import com.quiz.repository.QuestionRepository;
import com.quiz.repository.ScoreRepository;
import com.quiz.service.QuizService;
import com.quiz.ui.AppFrame;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;

/**
 * Entry point — wires the entire app together.
 *
 * STARTUP ORDER:
 * 1. Repositories (data access)
 * 2. Service (business logic, gets repos injected)
 * 3. Controller (gets service injected)
 * 4. GUI on the EDT (never build Swing on the main thread)
 *
 * WHY invokeLater?
 * Swing is single-threaded. All GUI must run on the
 * Event Dispatch Thread (EDT). invokeLater() schedules
 * our GUI startup on that thread safely.
 * This is the correct multithreading approach for Swing.
 */
public class Main {

    private static final Logger logger = LogManager.getLogger(Main.class);

    public static void main(String[] args) {
        logger.info("=== Student Quiz App Starting ===");

        // Use system look and feel for native appearance
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            logger.warn("Could not set system look and feel");
        }

        // ── Wire up layers (Manual Dependency Injection) ──
        QuestionRepository questionRepo = new QuestionRepository();
        ScoreRepository    scoreRepo    = new ScoreRepository("data/scores.xml");
        QuizService        service      = new QuizService(questionRepo, scoreRepo);
        QuizController     controller   = new QuizController(service);

        // ── Launch GUI on the Event Dispatch Thread ────────
        SwingUtilities.invokeLater(() -> {
            AppFrame app = new AppFrame(controller);
            app.setVisible(true);
            logger.info("GUI launched successfully");
        });
    }
}