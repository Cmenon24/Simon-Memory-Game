package unit;

import org.junit.jupiter.api.Test;
import simon.model.*;
import java.util.Random;
import static org.junit.jupiter.api.Assertions.*;

/**
Tests for the correct input handling, round completion logic, and game over behavir when the wrong input is given
 */

class SimonGameTest {

    @Test
    void startNewGameResetsEverything() {
        // Create the game with predictable random values
        SimonGame game = new SimonGame(new PatternGenerator(new Random(1)));

        // Start a new game for  player
        game.startNewGame(new Player("Alex"));

        // Confirm the game reset correctly
        assertEquals(0, game.getScore());
        assertFalse(game.isGameOver());
        assertTrue(game.getPattern().isEmpty());
    }

    @Test
    void nextRoundAddsOneColorToPattern() {
        // Create a game with predictable random values
        SimonGame game = new SimonGame(new PatternGenerator(new Random(1)));
        game.startNewGame(new Player("Alex"));

        // Start first round and pattern should grow by 1
        game.nextRound();
        int size1 = game.getPattern().size();

        // Start second round andpattern should grow by 1 again
        game.nextRound();
        int size2 = game.getPattern().size();

        // Confirm pattern increased by exactly 1
        assertEquals(size1 + 1, size2);
    }

    @Test
    void wrongInputEndsGame() {
        // Create game with predictable random values
        SimonGame game = new SimonGame(new PatternGenerator(new Random(0)));
        game.startNewGame(new Player("Alex"));
        game.nextRound(); // creates a 1-color pattern

        // Get the expected first color
        SimonColor expected = game.getPattern().get(0);

        // Pick a different color on purpose
        SimonColor wrong = (expected == SimonColor.RED) ? SimonColor.GREEN : SimonColor.RED;

        // Enter wrong input
        InputResult result = game.acceptInput(wrong);

        // Game should end
        assertEquals(InputResult.WRONG, result);
        assertTrue(game.isGameOver());
    }
}
