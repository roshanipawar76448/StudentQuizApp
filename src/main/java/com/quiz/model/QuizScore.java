package com.quiz.model;

import java.time.LocalDateTime;

/**
 * Stores one player's quiz result for the leaderboard.
 *
 * implements Comparable<QuizScore> lets Java sort a list
 * of QuizScore objects automatically using Collections.sort()
 */
public class QuizScore implements Comparable<QuizScore> {

    private String playerName;
    private int score;
    private int totalQuestions;
    private long timeTakenSeconds;
    private LocalDateTime playedAt;

    // Default constructor — needed when loading from XML
    public QuizScore() {
        this.playedAt = LocalDateTime.now();
    }

    // Full constructor — used when saving a new result
    public QuizScore(String playerName, int score,
                     int totalQuestions, long timeTakenSeconds) {
        this.playerName       = playerName;
        this.score            = score;
        this.totalQuestions   = totalQuestions;
        this.timeTakenSeconds = timeTakenSeconds;
        this.playedAt         = LocalDateTime.now();
    }

    // ── Getters ───────────────────────────────────────────
    public String getPlayerName()      { return playerName; }
    public int getScore()              { return score; }
    public int getTotalQuestions()     { return totalQuestions; }
    public long getTimeTakenSeconds()  { return timeTakenSeconds; }
    public LocalDateTime getPlayedAt() { return playedAt; }

    // ── Setters (needed when loading from XML) ────────────
    public void setPlayerName(String playerName)         { this.playerName = playerName; }
    public void setScore(int score)                      { this.score = score; }
    public void setTotalQuestions(int totalQuestions)    { this.totalQuestions = totalQuestions; }
    public void setTimeTakenSeconds(long timeTakenSeconds){ this.timeTakenSeconds = timeTakenSeconds; }
    public void setPlayedAt(LocalDateTime playedAt)      { this.playedAt = playedAt; }

    // ── Business method ───────────────────────────────────

    /** Returns score as a percentage e.g. 80.0 */
    public double getPercentage() {
        if (totalQuestions == 0) return 0;
        return (score * 100.0) / totalQuestions;
    }

    /**
     * Defines how two QuizScore objects are compared.
     * Used automatically by Collections.sort() on the leaderboard.
     *
     * Rule: Higher score wins. If scores are equal, faster time wins.
     */
    @Override
    public int compareTo(QuizScore other) {
        // Compare scores descending (other first = higher score ranks first)
        if (other.score != this.score) {
            return Integer.compare(other.score, this.score);
        }
        // Tiebreaker: lower time is better
        return Long.compare(this.timeTakenSeconds, other.timeTakenSeconds);
    }

    @Override
    public String toString() {
        return String.format("%s: %d/%d (%.0f%%) in %ds",
                playerName, score, totalQuestions,
                getPercentage(), timeTakenSeconds);
    }
}