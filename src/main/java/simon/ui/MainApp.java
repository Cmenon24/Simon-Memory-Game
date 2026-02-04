package simon.ui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * MainApp is ONLY responsible for launching the JavaFX window.
 * The actual game screen (UI + game flow) lives in SimonGameUI.
 */
public class MainApp extends Application {

    @Override
    public void start(Stage stage) {
        // Create the main UI for the game
        SimonGameUI root = new SimonGameUI();

        // Choose a window size that fits the 520px board + padding nicely
        Scene scene = new Scene(root, 700, 800);

        stage.setTitle("Simon Game");
        stage.setScene(scene);

        // Show first, then center (centering works best after show on macOS)
        stage.show();
        stage.centerOnScreen();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
