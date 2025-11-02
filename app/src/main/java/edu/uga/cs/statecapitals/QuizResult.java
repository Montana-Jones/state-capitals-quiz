package edu.uga.cs.statecapitals;

/** Model representing a stored quiz result row. */
public class QuizResult {
    public final long id;
    public final long takenAtMillis;
    public final int score;
    public final int total;

    public QuizResult(long id, long takenAtMillis, int score, int total) {
        this.id = id; this.takenAtMillis = takenAtMillis;
        this.score = score; this.total = total;
    }
}
