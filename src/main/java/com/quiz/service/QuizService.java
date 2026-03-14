package com.quiz.service;

import com.quiz.exception.QuestionsNotFoundException;
import com.quiz.model.Question;
import com.quiz.model.QuizScore;
import com.quiz.repository.QuestionRepository;
import com.quiz.repository.ScoreRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.stream.Collectors;

/**
 * All quiz business logic lives here.
 *
 * CONCEPTS:
 * Collections — ArrayList, List for questions and results
 * Lambdas     — filter, map, sort using stream API
 * Streams     — filter questions by category/difficulty
 * State       — tracks current question index, score, timer
 */
public class QuizService {

    private static final Logger logger = LogManager.getLogger(QuizService.class);

    private final QuestionRepository questionRepo;
    private final ScoreRepository scoreRepo;

    // ── Quiz session state ────────────────────────────────
    private List<Question> sessionQuestions; // questions for this session
    private int currentIndex;                // which question we're on
    private int correctCount;                // running score
    private long startTimeMs;               // when quiz started (for timing)
    private String playerName;

    // Tracks which questions were answered correctly — for results screen
    private final Map<String, Boolean> answerResults = new LinkedHashMap<>();

    public QuizService(QuestionRepository questionRepo, ScoreRepository scoreRepo) {
        this.questionRepo = questionRepo;
        this.scoreRepo = scoreRepo;
    }

    // ── Quiz setup ────────────────────────────────────────

    /**
     * Starts a new quiz session.
     * Loads questions, filters by category/difficulty, shuffles, limits count.
     *
     * LAMBDA examples throughout this method.
     */
    public void startQuiz(String playerName, String category,
                          String difficulty, int questionCount) {
        this.playerName    = playerName;
        this.currentIndex  = 0;
        this.correctCount  = 0;
        this.answerResults.clear();

        // Load all questions from XML
        List<Question> all = questionRepo.loadAllQuestions();

        if (all.isEmpty()) {
            throw new QuestionsNotFoundException("XML file has no questions");
        }

        // LAMBDA + STREAM: filter by category and difficulty
        List<Question> filtered = all.stream()
                // filter by category — "All" means skip this filter
                .filter(q -> category.equals("All")
                        || q.getCategory().equalsIgnoreCase(category))
                // filter by difficulty — "All" means skip
                .filter(q -> difficulty.equals("All")
                        || q.getDifficulty().equalsIgnoreCase(difficulty))
                .collect(Collectors.toList());

        if (filtered.isEmpty()) {
            throw new QuestionsNotFoundException(
                    "No questions match: category=" + category +
                            ", difficulty=" + difficulty);
        }

        // Shuffle randomly so each quiz is different
        Collections.shuffle(filtered);

        // Take only the requested number of questions
        int limit = Math.min(questionCount, filtered.size());
        sessionQuestions = new ArrayList<>(filtered.subList(0, limit));

        startTimeMs = System.currentTimeMillis();
        logger.info("Quiz started for '{}': {} questions, cat={}, diff={}",
                playerName, sessionQuestions.size(), category, difficulty);
    }

    // ── During quiz ───────────────────────────────────────

    /** Returns the current question object */
    public Question getCurrentQuestion() {
        if (currentIndex >= sessionQuestions.size()) return null;
        return sessionQuestions.get(currentIndex);
    }

    /** Returns current question number (1-based for display) */
    public int getCurrentQuestionNumber() { return currentIndex + 1; }

    /** Total questions in this session */
    public int getTotalQuestions() {
        return sessionQuestions == null ? 0 : sessionQuestions.size();
    }

    /** True if there are no more questions */
    public boolean isQuizFinished() {
        return sessionQuestions == null
                || currentIndex >= sessionQuestions.size();
    }

    /**
     * Submits an answer for the current question.
     * Advances to next question and returns whether it was correct.
     */
    public boolean submitAnswer(String userAnswer) {
        Question current = getCurrentQuestion();
        if (current == null) return false;

        // POLYMORPHISM in action: isCorrect() works differently
        // for MCQ vs TrueFalse but we don't need to know which type
        boolean correct = current.isCorrect(userAnswer);

        if (correct) correctCount++;

        // Store result for the results/review screen
        answerResults.put(current.getId(), correct);

        logger.debug("Q{}: answer='{}' correct={}", currentIndex + 1,
                userAnswer, correct);

        currentIndex++; // advance to next question
        return correct;
    }

    /** Called when timer runs out — counts as wrong answer */
    public void timeOut() {
        Question current = getCurrentQuestion();
        if (current != null) {
            answerResults.put(current.getId(), false);
            logger.debug("Q{}: TIMEOUT", currentIndex + 1);
            currentIndex++;
        }
    }

    // ── End of quiz ───────────────────────────────────────

    /** Builds and saves the final QuizScore for the leaderboard */
    public QuizScore finishQuiz() {
        long elapsed = (System.currentTimeMillis() - startTimeMs) / 1000;
        QuizScore result = new QuizScore(playerName, correctCount,
                sessionQuestions.size(), elapsed);
        scoreRepo.saveScore(result);
        logger.info("Quiz finished: {}", result);
        return result;
    }

    public int getCorrectCount()              { return correctCount; }
    public Map<String, Boolean> getAnswerResults() { return answerResults; }

    // ── Leaderboard ───────────────────────────────────────

    /**
     * Returns top 10 scores, sorted best first.
     * Uses Comparable we implemented in QuizScore.
     */
    public List<QuizScore> getTopScores(int limit) {
        List<QuizScore> all = scoreRepo.loadAllScores();
        Collections.sort(all); // uses QuizScore.compareTo()

        // LAMBDA: take only first 'limit' items
        return all.stream()
                .limit(limit)
                .collect(Collectors.toList());
    }

    /**
     * Returns all unique categories from loaded questions.
     * Used to populate the category dropdown on Welcome screen.
     */
    public List<String> getAvailableCategories() {
        List<Question> all = questionRepo.loadAllQuestions();

        // STREAM + LAMBDA: extract unique category names
        List<String> categories = all.stream()
                .map(Question::getCategory)        // extract category field
                .distinct()                         // remove duplicates
                .sorted()                           // alphabetical order
                .collect(Collectors.toList());

        categories.add(0, "All"); // "All" option at the top
        return categories;
    }
}