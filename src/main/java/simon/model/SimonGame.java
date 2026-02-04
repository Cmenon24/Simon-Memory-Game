package simon.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * SimonGame contains the main rules and state of the Simon game.
 *
 * IMPORTANT:
 * This class does NOT deal with JavaFX buttons or animations.
 * It only handles the game logic, which makes it easier to test.
 */
public class SimonGame {

    // Generates the next color for each round
    private final PatternGenerator generator;

    // Stores the current player
    private Player player;

    // Stores the Simon pattern (dynamic list that grows each round)
    private final List<SimonColor> pattern = new ArrayList<>();

    // Tracks how many correct inputs the player has entered for the current round
    private int attemptIndex = 0;

    // Score = how many rounds the player has completed successfully
    private int score = 0;

    // If true, the game is finished and should not accept more input
    private boolean gameOver = false;

    /**
     * Constructor requires a PatternGenerator.
     * This makes the game easier to test because we can control the pattern.
     */
    public SimonGame(PatternGenerator generator) {
        this.generator = generator;
    }

    /**
     * Starts a brand new game for a player.
     * Resets pattern, score, attempt progress, and gameOver flag.
     */
    public void startNewGame(Player player) {
        this.player = player;

        // Clear old pattern and reset game state
        pattern.clear();
        attemptIndex = 0;
        score = 0;
        gameOver = false;
    }

    /**
     * Moves the game to the next round by adding one new random color to the pattern.
     * Resets attemptIndex so the player must enter the pattern from the beginning.
     */
    public void nextRound() {
        // If the game is already over, do nothing
        if (gameOver) return;

        // Add one new color to make the pattern longer
        pattern.add(generator.nextColor());

        // Reset attempt progress for the new round
        attemptIndex = 0;
    }

    /**
     * Accepts one player button press (one SimonColor).
     *
     * Returns:
     * - CORRECT_SO_FAR if correct but not finished pattern
     * - ROUND_COMPLETE if correct and finished full pattern
     * - WRONG if incorrect input (game over)
     */
    public InputResult acceptInput(SimonColor input) {
        // If game is over, treat any input as wrong (no more playing)
        if (gameOver) return InputResult.WRONG;

        // If pattern is empty, UI forgot to call nextRound() first
        if (pattern.isEmpty()) {
            throw new IllegalStateException("Call nextRound() before accepting input.");
        }

        // The correct expected color at this position in the pattern
        SimonColor expected = pattern.get(attemptIndex);

        // If input does not match expected -> game over
        if (input != expected) {
            gameOver = true;
            return InputResult.WRONG;
        }

        // Input was correct, move to next position
        attemptIndex++;

        // If we reached the end of the pattern, round is complete
        if (attemptIndex == pattern.size()) {
            score++;           // player completed the round successfully
            attemptIndex = 0;  // reset for next round
            return InputResult.ROUND_COMPLETE;
        }

        // Player is still entering the pattern
        return InputResult.CORRECT_SO_FAR;
    }

    /**
     * Returns the current pattern.
     * We return an unmodifiable list so outside code cannot cheat/change it.
     */
    public List<SimonColor> getPattern() {
        return Collections.unmodifiableList(pattern);
    }

    /**
     * Returns the current score.
     */
    public int getScore() {
        return score;
    }

    /**
     * Returns the current player.
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Returns true if the game is over.
     */
    public boolean isGameOver() {
        return gameOver;
    }
}

