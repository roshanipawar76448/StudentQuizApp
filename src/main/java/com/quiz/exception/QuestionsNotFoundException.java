package com.quiz.exception;

public class QuestionsNotFoundException extends QuizException {

    public QuestionsNotFoundException(String detail) {
        super("NO_QUESTIONS",
                "No questions found. Details: " + detail);
    }
}