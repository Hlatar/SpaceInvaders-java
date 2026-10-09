package io.github.some_example_name;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

public class PreferencesHelper {
    private static final String PREF_NAME = "space_game_prefs";
    private static final String KEY_HIGH_SCORE = "high_score";

    public static int getHighScore() {
        Preferences prefs = Gdx.app.getPreferences(PREF_NAME);
        return prefs.getInteger(KEY_HIGH_SCORE, 0); // default = 0
    }

    public static void setHighScore(int score) {
        Preferences prefs = Gdx.app.getPreferences(PREF_NAME);
        prefs.putInteger(KEY_HIGH_SCORE, score);
        prefs.flush();
    }
}
