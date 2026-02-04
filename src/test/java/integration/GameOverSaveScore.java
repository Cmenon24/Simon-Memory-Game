package integration;

import org.junit.jupiter.api.Test;
import simon.model.*;
import simon.score.*;

import java.time.LocalDateTime;
import java.util.Random;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests full gameflow-the SimonGame/Leaderboard/ScoreEntry/InputResult when the game is over
 -Checks that a wrong color ends the game
 -Checks if the score is recorded and saved in theleaderboard
 */


public class GameOverSaveScore {

    @Test
    void gameOverAddsScoreToLeaderboard() {
        // Arrange
        PatternGenerator generator = new PatternGenerator(new Random(1));
        SimonGame game = new SimonGame(generator);
        Leaderboard leaderboard = new Leaderboard();
        Player player = new Player("Crystal");

        game.startNewGame(player);
        game.nextRound();

        // Force a wrong input (almost guaranteed)
        SimonColor wrongInput = SimonColor.YELLOW;
        InputResult result = game.acceptInput(wrongInput);

        // Assert game over
        assertEquals(InputResult.WRONG, result);
        assertTrue(game.isGameOver());

        // Act: save score
        ScoreEntry entry = new ScoreEntry(
                player.name(),
                game.getScore(),
                LocalDateTime.now()
        );
        leaderboard.addScore(entry);

        // Assert leaderboard updated
        assertEquals(1, leaderboard.getTopScores().size());
        assertEquals("Crystal", leaderboard.getTopScores().get(0).playerName());
    }
}
