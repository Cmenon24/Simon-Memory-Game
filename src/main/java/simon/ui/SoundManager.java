package simon.ui;

import javafx.scene.media.AudioClip;
import simon.model.SimonColor;

import java.util.EnumMap;

/**
 * SoundManager loads and plays sound effects for Simon.
 *
 * Files must exist in:
 * src/main/resources/sounds/
 *
 * Required files:
 * - red.wav
 * - green.wav
 * - blue.wav
 * - yellow.wav
 * - wrong.wav
 */
public class SoundManager {

    // Store one AudioClip per color using an EnumMap (fast and clean)
    private final EnumMap<SimonColor, AudioClip> colorClips = new EnumMap<>(SimonColor.class);

    // Game over sound
    private final AudioClip wrongClip;

    public SoundManager() {
        // Load each color sound
        loadColor(SimonColor.RED, "sounds/red.wav");
        loadColor(SimonColor.GREEN, "sounds/green.wav");
        loadColor(SimonColor.BLUE, "sounds/blue.wav");
        loadColor(SimonColor.YELLOW, "sounds/yellow.wav");

        // Load wrong.wav
        wrongClip = loadClip("sounds/wrong.wav");
    }

    /**
     * Loads and stores a color sound.
     */
    private void loadColor(SimonColor color, String resourcePath) {
        AudioClip clip = loadClip(resourcePath);
        if (clip != null) {
            colorClips.put(color, clip);
        }
    }

    /**
     * Loads an AudioClip from resources.
     * Returns null if missing.
     */
    private AudioClip loadClip(String resourcePath) {
        var url = getClass().getClassLoader().getResource(resourcePath);

        if (url == null) {
            System.out.println("⚠️ Missing sound file: " + resourcePath);
            return null;
        }

        return new AudioClip(url.toExternalForm());
    }

    /**
     * Play the sound for a given SimonColor.
     */
    public void playColor(SimonColor color) {
        AudioClip clip = colorClips.get(color);
        if (clip != null) {
            clip.play();
        }
    }

    /**
     * Play the wrong/game over sound.
     */
    public void playWrong() {
        if (wrongClip != null) {
            wrongClip.play();
        }
    }
}
