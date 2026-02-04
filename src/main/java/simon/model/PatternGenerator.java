package simon.model;

import java.util.Random;

/**
 * PatternGenerator is responsible for generating the next random SimonColor.
 * We keep this separate from SimonGame so the game logic stays clean and testable.
 */
public class PatternGenerator {

    // Random is injected so tests can use a fixed seed (predictable patterns)
    private final Random random;

    /**
     * Constructor takes a Random object.
     * In real gameplay you can use: new Random()
     * In tests you can use: new Random(123)
     */
    public PatternGenerator(Random random) {
        this.random = random;
    }

    /**
     * Returns a random SimonColor.
     */
    public SimonColor nextColor() {
        // Get all possible enum values
        SimonColor[] values = SimonColor.values();

        // Pick one randomly
        return values[random.nextInt(values.length)];
    }
}
