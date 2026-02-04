package simon.score;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Leaderboard stores the Top 10 scores.
 *
 * Uses a List (dynamic array) with generics: List<ScoreEntry>
 * This meets your "collections and generics" requirement.
 */
public class Leaderboard {

    // Stores all scores (we will keep it trimmed to top 10)
    private final List<ScoreEntry> scores = new ArrayList<>();

    /**
     * Adds a new score to the leaderboard.
     * Automatically sorts and keeps only the top 10.
     */
    public void addScore(ScoreEntry entry) {
        // Add new entry
        scores.add(entry);

        // Sort by score descending (highest first)
        // If scores tie, sort by time (newest first)
        scores.sort(Comparator
                .comparingInt(ScoreEntry::score).reversed()
                .thenComparing(ScoreEntry::when, Comparator.reverseOrder()));

        // Keep only the top 10 scores
        if (scores.size() > 10) {
            scores.subList(10, scores.size()).clear();
        }
    }

    /**
     * Returns the top scores as an unmodifiable list so it cannot be edited outside.
     */
    public List<ScoreEntry> getTopScores() {
        return Collections.unmodifiableList(scores);
    }
}
