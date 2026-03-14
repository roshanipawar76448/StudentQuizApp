package com.quiz.model;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A question with 4 options (A, B, C, D).
 *
 * OOP: INHERITANCE
 * Extends Question — gets ALL Question fields for free.
 * Only adds what's unique to MCQ: the options map and correctAnswer.
 *
 * OOP: POLYMORPHISM
 * Overrides isCorrect(), getCorrectAnswer(), getQuestionType()
 * The parent says "you MUST have these methods"
 * This class says "here's HOW mine work"
 */
public class MultipleChoiceQuestion extends Question {

    // LinkedHashMap preserves insertion order (A before B before C before D)
    // Key = "A", "B", "C", "D"   Value = the option text
    private Map<String, String> options;
    private String correctAnswer; // "A", "B", "C", or "D"

    // ── Constructors ──────────────────────────────────────

    public MultipleChoiceQuestion() {
        super(); // calls Question()
        this.options = new LinkedHashMap<>(); // preserves A→B→C→D order
    }

    public MultipleChoiceQuestion(String id, String text, String category,
                                  String difficulty, int timeLimit,
                                  String explanation, Map<String, String> options,
                                  String correctAnswer) {
        super(id, text, category, difficulty, timeLimit, explanation);
        this.options = options;
        this.correctAnswer = correctAnswer;
    }

    // ── Getters / Setters ─────────────────────────────────
    public Map<String, String> getOptions() { return options; }
    public void setOptions(Map<String, String> options) { this.options = options; }
    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    // ── Abstract method implementations ───────────────────

    @Override
    public String getCorrectAnswer() {
        return correctAnswer;
    }

    /**
     * POLYMORPHISM: Our version of isCorrect
     * Trims whitespace and ignores upper/lowercase
     * so "a" and "A" both count as correct
     */
    @Override
    public boolean isCorrect(String userAnswer) {
        if (userAnswer == null) return false;
        return correctAnswer.equalsIgnoreCase(userAnswer.trim());
    }

    @Override
    public String getQuestionType() {
        return "Multiple Choice";
    }
}