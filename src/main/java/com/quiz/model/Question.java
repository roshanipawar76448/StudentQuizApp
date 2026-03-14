package com.quiz.model;

    /**
     * ABSTRACT BASE CLASS for all question types.
     *
     * WHY ABSTRACT?
     * A plain "Question" doesn't make sense on its own — is it
     * multiple choice? True/False? You can never write: new Question()
     * You must use a subclass: new MultipleChoiceQuestion()
     *
     * OOP CONCEPTS:
     * Abstraction  — abstract class can't be instantiated directly
     * Encapsulation — all fields private, accessed via getters/setters
     */
    public abstract class Question {

        // private = ONLY this class can access directly
        // Even subclasses must use getters — that's strict encapsulation
        private String id;
        private String text;           // The question text shown to the user
        private String category;       // e.g. "Java", "OOP", "Data Structures"
        private String difficulty;     // "EASY", "MEDIUM", "HARD"
        private int timeLimit;         // seconds the user has to answer
        private String explanation;    // shown after answering

        // ── Constructors ─────────────────────────────────────

        // Used when loading from XML (fields set one by one via setters)
        public Question() {}

        // Used when creating a question in code
        public Question(String id, String text, String category,
                        String difficulty, int timeLimit, String explanation) {
            this.id = id;
            this.text = text;
            this.category = category;
            this.difficulty = difficulty;
            this.timeLimit = timeLimit;
            this.explanation = explanation;
        }

        // ── Getters ───────────────────────────────────────────
        public String getId()          { return id; }
        public String getText()        { return text; }
        public String getCategory()    { return category; }
        public String getDifficulty()  { return difficulty; }
        public int getTimeLimit()      { return timeLimit; }
        public String getExplanation() { return explanation; }

        // ── Setters ───────────────────────────────────────────
        public void setId(String id)                   { this.id = id; }
        public void setText(String text)               { this.text = text; }
        public void setCategory(String category)       { this.category = category; }
        public void setDifficulty(String difficulty)   { this.difficulty = difficulty; }
        public void setTimeLimit(int timeLimit)        { this.timeLimit = timeLimit; }
        public void setExplanation(String explanation) { this.explanation = explanation; }

        // ── Abstract methods — subclasses MUST implement ──────

        /**
         * Returns the correct answer as a String.
         * MCQ returns "A", "B", "C" or "D"
         * TrueFalse returns "True" or "False"
         */
        public abstract String getCorrectAnswer();

        /**
         * Checks if the given answer is correct.
         * Each subclass decides what "correct" means for its type.
         */
        public abstract boolean isCorrect(String userAnswer);

        /**
         * Returns a label describing the question type.
         * Used in the UI to show "Multiple Choice" or "True / False"
         */
        public abstract String getQuestionType();

        // ── toString — useful for logging ────────────────────
        @Override
        public String toString() {
            return String.format("Question{id='%s', type='%s', difficulty='%s', text='%s'}",
                    id, getQuestionType(), difficulty, text);
        }
    }