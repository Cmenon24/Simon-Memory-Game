package integration;

import org.junit.jupiter.api.Test;
import simon.model.*;
import java.util.Random;
import static org.junit.jupiter.api.Assertions.*;

/**
Tests the integrtion between SimonGame/PatternGenerator/SimonColor
 -Checks that a new game will start correctly
 _With each round, there is one new color
 -The pattern colors grow each round
 */

public class SimonGamePattern {

    @Test
    void patternGrowsEachRound() {
        // Arrange
        PatternGenerator generator = new PatternGenerator(new Random(1));
        SimonGame game = new SimonGame(generator);
        Player player = new Player("Crystal");

        game.startNewGame(player);

        // Act + Assert round 1
        game.nextRound();
        assertEquals(1, game.getPattern().size(),
                "Pattern should have 1 color after first round");

        // Act + Assert round 2
        game.nextRound();
        assertEquals(2, game.getPattern().size(),
                "Pattern should have 2 colors after second round");

        // Act + Assert round 3
        game.nextRound();
        assertEquals(3, game.getPattern().size(),
                "Pattern should have 3 colors after third round");
    }
}

