package com.quiz.controller;

import com.quiz.model.Question;
import com.quiz.model.QuizScore;
import com.quiz.service.QuizService;

import java.util.List;
import java.util.Map;

/**
 * Controller bridges the UI panels and the QuizService.
 *
 * MVC PATTERN:
 * UI panels NEVER call QuizService directly.
 * They call QuizController, which calls QuizService.
 * If we ever swap QuizService for a database-backed version,
 * only this class changes — UI is untouched.
 */
public class QuizController {

    private final QuizService service;

    public QuizController(QuizService service) {
        this.service = service;
    }

    // Delegates every call to service

    public void startQuiz(String playerName, String category,
                          String difficulty, int questionCount) {
        service.startQuiz(playerName, category, difficulty, questionCount);
    }

    public Question getCurrentQuestion()     { return service.getCurrentQuestion(); }
    public int getCurrentQuestionNumber()    { return service.getCurrentQuestionNumber(); }
    public int getTotalQuestions()           { return service.getTotalQuestions(); }
    public boolean isQuizFinished()          { return service.isQuizFinished(); }
    public boolean submitAnswer(String ans)  { return service.submitAnswer(ans); }
    public void timeOut()                    { service.timeOut(); }
    public QuizScore finishQuiz()            { return service.finishQuiz(); }
    public int getCorrectCount()             { return service.getCorrectCount(); }
    public Map<String, Boolean> getAnswerResults() { return service.getAnswerResults(); }
    public List<QuizScore> getTopScores(int n)     { return service.getTopScores(n); }
    public List<String> getAvailableCategories()   { return service.getAvailableCategories(); }
}