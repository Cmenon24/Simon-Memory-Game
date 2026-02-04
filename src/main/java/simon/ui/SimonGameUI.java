package simon.ui;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Duration;
import simon.model.*;
import simon.score.Leaderboard;
import simon.score.ScoreEntry;

import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.List;
import java.util.Random;

/**
 * SimonGameUI contains:
 * - All JavaFX UI components
 * - The game loop (play pattern -> enable input -> check input -> next round/game over)
 * - Restart button (only button)
 * - Clean separation from MainApp and SoundManager
 */
public class SimonGameUI extends VBox {

    // -------------------------
    // Logic layer (game rules)
    // -------------------------
    private final SimonGame game = new SimonGame(new PatternGenerator(new Random()));
    private final Leaderboard leaderboard = new Leaderboard();

    // -------------------------
    // Sound layer
    // -------------------------
    private final SoundManager sound = new SoundManager();

    // -------------------------
    // UI state + controls
    // -------------------------

    // Map each SimonColor to its Arc so we can flash it later
    private final EnumMap<SimonColor, Arc> pads = new EnumMap<>(SimonColor.class);

    // Labels
    private final Label scoreLabel = new Label("Score: 0");
    private final Label statusLabel = new Label("Starting...");

    // Only button now
    private final Button restartBtn = new Button("Restart");

    // Store player name so restart uses the same player
    private final String playerName;

    // If true: player can click pads
    private boolean inputEnabled = false;

    // If true: game has ended, must press restart
    private boolean gameOver = false;

    public SimonGameUI() {
        // Ask for a player name once when the UI is created
        String name = askPlayerName();
        if (name == null) {
            Platform.exit();
            playerName = "Player";
            return;
        }
        playerName = name;

        // Build UI layout
        setSpacing(18);
        setAlignment(Pos.TOP_CENTER);
        setPadding(new Insets(18));
        setStyle("-fx-background-color: #1e1e1e;");

        // Make labels readable on dark background
        scoreLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px;");
        statusLabel.setStyle("-fx-text-fill: white; -fx-font-size: 28px; -fx-font-weight: bold;");

        // Top bar with player + score + restart
        HBox topBar = buildTopBar();

        // Circular pad
        StackPane pad = buildSimonPad();

        /*
         * IMPORTANT:
         * The pad is drawn inside a fixed-size Pane.
         * Wrapping it in an HBox with CENTER alignment forces perfect centering.
         */
        HBox padRow = new HBox(pad);
        padRow.setAlignment(Pos.CENTER);
        padRow.setMaxWidth(Double.MAX_VALUE);

        // Let the pad row use extra vertical space so layout stays balanced
        VBox.setVgrow(padRow, Priority.ALWAYS);

        // Add to screen (NOTE: we add padRow, not pad)
        getChildren().addAll(topBar, statusLabel, padRow);

        // Restart button always resets the game
        restartBtn.setOnAction(e -> restartGame());

        // Start the game automatically on launch
        restartGame();
    }

    /**
     * Ask user for name using a popup dialog.
     */
    private String askPlayerName() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Player Name");
        dialog.setHeaderText("Enter your name to start Simon");
        dialog.setContentText("Name:");

        return dialog.showAndWait()
                .map(String::trim)
                .filter(n -> !n.isBlank())
                .orElse(null);
    }

    /**
     * Create the top bar UI.
     */
    private HBox buildTopBar() {
        Label playerLabel = new Label("Player: " + playerName);
        playerLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px;");

        restartBtn.setPrefWidth(110);

        HBox topBar = new HBox(18, playerLabel, scoreLabel, restartBtn);
        topBar.setAlignment(Pos.CENTER);
        topBar.setPadding(new Insets(12));
        topBar.setStyle("-fx-background-color: #2a2a2a; -fx-background-radius: 14;");

        return topBar;
    }

    /**
     * Create the circular Simon pad made of 4 arcs + center circle + "SIMON".
     */
    private StackPane buildSimonPad() {
        double size = 520;
        double cx = size / 2.0;
        double cy = size / 2.0;
        double radius = 240;
        double gapStroke = 24;

        // Create arcs (quadrants)
        Arc yellow = createArc(cx, cy, radius, 90, 90, Color.web("#f1c40f"), SimonColor.YELLOW);
        Arc blue   = createArc(cx, cy, radius, 0,  90, Color.web("#3498db"), SimonColor.BLUE);
        Arc red    = createArc(cx, cy, radius, 180, 90, Color.web("#e74c3c"), SimonColor.RED);
        Arc green  = createArc(cx, cy, radius, 270, 90, Color.web("#2ecc71"), SimonColor.GREEN);

        // Add black cross separation lines
        for (Arc a : List.of(yellow, blue, red, green)) {
            a.setStroke(Color.BLACK);
            a.setStrokeWidth(gapStroke);
        }

        // Base ring behind the arcs
        Circle base = new Circle(cx, cy, radius + 10);
        base.setFill(Color.web("#111111"));
        base.setStroke(Color.web("#444444"));
        base.setStrokeWidth(8);

        // Center circle
        Circle middle = new Circle(cx, cy, 85);
        middle.setFill(Color.web("#2a2a2a"));
        middle.setStroke(Color.web("#666666"));
        middle.setStrokeWidth(6);

        // Center text
        Text simonText = new Text("SIMON");
        simonText.setFill(Color.WHITE);
        simonText.setFont(Font.font("Arial", FontWeight.BOLD, 26));
        simonText.setX(cx - 45);
        simonText.setY(cy + 9);

        Pane shapePane = new Pane();
        shapePane.setPrefSize(size, size);

        // Add shapes in correct order
        shapePane.getChildren().addAll(base, yellow, blue, red, green, middle, simonText);

        // Wrap in StackPane (HBox does the real centering in the constructor)
        return new StackPane(shapePane);
    }

    /**
     * Create one arc and register click handling.
     */
    private Arc createArc(double cx, double cy, double r,
                          double startAngle, double length,
                          Color fill, SimonColor color) {

        Arc arc = new Arc(cx, cy, r, r, startAngle, length);
        arc.setType(ArcType.ROUND);
        arc.setFill(fill);
        arc.setCursor(Cursor.HAND);

        // Store in map so we can flash it later
        pads.put(color, arc);

        // Click handler
        arc.setOnMouseClicked(e -> handleClick(color));

        return arc;
    }

    /**
     * Restart button:
     * - resets the SimonGame logic completely
     * - resets UI labels
     * - starts round 1 and plays the pattern immediately
     */
    private void restartGame() {
        game.startNewGame(new Player(playerName));

        // Reset state flags
        inputEnabled = false;
        gameOver = false;

        // Reset labels
        scoreLabel.setText("Score: 0");
        statusLabel.setText("Watch the pattern...");

        // Begin round 1
        game.nextRound();
        playPattern();
    }

    /**
     * Flash the current pattern and then allow player input.
     */
    private void playPattern() {
        inputEnabled = false;
        statusLabel.setText("Watch the pattern...");

        List<SimonColor> pattern = game.getPattern();

        // Schedule flashes in sequence
        double time = 0.0;

        for (SimonColor c : pattern) {
            time += 0.65;
            scheduleFlash(c, time);
            time += 0.35;
        }

        // Enable input after last flash
        PauseTransition done = new PauseTransition(Duration.seconds(time + 0.2));
        done.setOnFinished(e -> {
            if (!gameOver) {
                inputEnabled = true;
                statusLabel.setText("Your turn!");
            }
        });
        done.play();
    }

    /**
     * Flash a color pad (opacity change) and play its sound.
     */
    private void scheduleFlash(SimonColor color, double atSeconds) {
        Arc arc = pads.get(color);

        // Flash ON
        PauseTransition on = new PauseTransition(Duration.seconds(atSeconds));
        on.setOnFinished(e -> {
            arc.setOpacity(0.35);
            sound.playColor(color);
        });
        on.play();

        // Flash OFF
        PauseTransition off = new PauseTransition(Duration.seconds(atSeconds + 0.25));
        off.setOnFinished(e -> arc.setOpacity(1.0));
        off.play();
    }

    /**
     * Handle player click.
     */
    private void handleClick(SimonColor color) {
        // Ignore clicks if pattern is playing or game ended
        if (!inputEnabled || gameOver) return;

        // Click feedback
        sound.playColor(color);

        InputResult result = game.acceptInput(color);

        if (result == InputResult.WRONG) {
            // Stop input and mark game over
            inputEnabled = false;
            gameOver = true;

            sound.playWrong();
            statusLabel.setText("Wrong! Game Over.");

            showGameOverPopup();
            return;
        }

        if (result == InputResult.ROUND_COMPLETE) {
            // Round finished successfully
            scoreLabel.setText("Score: " + game.getScore());
            statusLabel.setText("Good! Next round...");

            // Next round
            game.nextRound();
            playPattern();
        }
    }

    /**
     * Show popup with player name, score, and top 10.
     * After this, user must press Restart to play again.
     */
    private void showGameOverPopup() {
        // Save score to leaderboard
        leaderboard.addScore(new ScoreEntry(playerName, game.getScore(), LocalDateTime.now()));

        // Build text for top 10
        StringBuilder sb = new StringBuilder();
        int rank = 1;
        for (ScoreEntry entry : leaderboard.getTopScores()) {
            sb.append(rank)
                    .append(") ")
                    .append(entry.playerName())
                    .append(" - ")
                    .append(entry.score())
                    .append("\n");
            rank++;
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Game Over");
        alert.setHeaderText("Game Over!");
        alert.setContentText(
                "Player: " + playerName + "\n" +
                        "Score: " + game.getScore() + "\n\n" +
                        "Top 10:\n" + sb
        );
        alert.showAndWait();

        statusLabel.setText("Press Restart to play again!");
    }
}
