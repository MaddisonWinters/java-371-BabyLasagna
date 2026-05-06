package cs.BabyLasagna.GameObj;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

public class PlayerProgress {
    private static final String SAVE_FILE = "progress.json";

    private int unlockedUpTo = 0;

    public PlayerProgress() {
        load();
    }

    private void load() {
        FileHandle file = Gdx.files.local(SAVE_FILE);
        if (!file.exists()) return;
        try {
            JsonValue root = new JsonReader().parse(file);
            unlockedUpTo = root.getInt("unlockedUpTo", 0);
        } catch (Exception e) {
            unlockedUpTo = 0;
        }
    }

    private void save() {
        Gdx.files.local(SAVE_FILE).writeString("{\"unlockedUpTo\":" + unlockedUpTo + "}", false);
    }

    public boolean canAccess(int levelIndex) {
        return levelIndex <= unlockedUpTo;
    }

    public boolean isCompleted(int levelIndex) {
        return levelIndex < unlockedUpTo;
    }

    public void onLevelComplete(int levelIndex) {
        unlockedUpTo = Math.max(unlockedUpTo, levelIndex + 1);
        save();
    }
}