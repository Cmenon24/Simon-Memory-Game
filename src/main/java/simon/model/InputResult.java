package simon.model;

/**
 * InputResult represents the result after the player presses a button.
 * This helps the UI know what to do next.
 */
public enum InputResult {
    // The input was correct, but the player has not finished the full pattern yet
    CORRECT_SO_FAR,

    // The input was correct and the player completed the entire pattern for the round
    ROUND_COMPLETE,

    // The input was wrong -> game over
    WRONG
}
