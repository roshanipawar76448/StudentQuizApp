package com.quiz.exception;

public class InvalidAnswerException extends QuizException {
    public InvalidAnswerException(String answer) {
        super("INVALID_ANSWER",
                "Answer '" + answer + "' is not valid for this question type.");
    }
}