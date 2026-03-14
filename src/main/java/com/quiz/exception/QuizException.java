package com.quiz.exception;

/**
 * Base exception for all quiz-related errors.
 *
 * WHY: Professional apps never crash with "NullPointerException".
 * We throw meaningful errors: "No questions found for category: Java"
 * Extends RuntimeException = no need to declare 'throws' everywhere.
 */
public class QuizException extends RuntimeException {

    private final String errorCode;

    public QuizException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public QuizException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public String getErrorCode() { return errorCode; }
}