package com.quiz.model;

/**
 * A True/False question — the simplest question type.
 *
 * OOP: INHERITANCE + POLYMORPHISM
 * Same pattern: extends Question, overrides the three abstract methods.
 * Notice how LITTLE code we need — inheritance handles everything else.
 */
public class TrueFalseQuestion extends Question {

    // Only "True" or "False"
    private String correctAnswer;

    // ── Constructors ──────────────────────────────────────

    public TrueFalseQuestion() {
        super();
    }

    public TrueFalseQuestion(String id, String text, String category,
                             String difficulty, int timeLimit,
                             String explanation, String correctAnswer) {
        super(id, text, category, difficulty, timeLimit, explanation);
        this.correctAnswer = correctAnswer;
    }

    // ── Getter / Setter ───────────────────────────────────
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
     * "true", "True", "TRUE" — all accepted
     */
    @Override
    public boolean isCorrect(String userAnswer) {
        if (userAnswer == null) return false;
        return correctAnswer.equalsIgnoreCase(userAnswer.trim());
    }

    @Override
    public String getQuestionType() {
        return "True / False";
    }
}