package simon;

import org.junit.jupiter.api.Test;
import simon.score.Leaderboard;
import simon.score.ScoreEntry;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class LeaderboardTest {

    @Test
    void leaderboardKeepsOnlyTop10() {
        // Create leaderboard
        Leaderboard lb = new Leaderboard();

        // Add 12 scores (more than 10)
        for (int i = 1; i <= 12; i++) {
            lb.addScore(new ScoreEntry("P" + i, i, LocalDateTime.now().minusSeconds(i)));
        }

        // Only top 10 should remain
        assertEquals(10, lb.getTopScores().size());
    }

    @Test
    void leaderboardSortsHighestScoreFirst() {
        // Create leaderboard
        Leaderboard lb = new Leaderboard();

        // Add scores in random order
        lb.addScore(new ScoreEntry("A", 3, LocalDateTime.now()));
        lb.addScore(new ScoreEntry("B", 10, LocalDateTime.now()));
        lb.addScore(new ScoreEntry("C", 5, LocalDateTime.now()));

        // Highest score should appear first
        assertEquals(10, lb.getTopScores().get(0).score());
        assertEquals(5, lb.getTopScores().get(1).score());
        assertEquals(3, lb.getTopScores().get(2).score());
    }
}
