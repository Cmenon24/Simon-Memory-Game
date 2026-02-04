package simon.score;

import java.time.LocalDateTime;

/**
 * ScoreEntry stores one leaderboard entry.
 * It includes the player name, score, and time the score was achieved.
 *
 * Using a record keeps it simple and clean.
 */
public record ScoreEntry(String playerName, int score, LocalDateTime when) { }
