package cs.BabyLasagna.GameObj;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class PlayerProgress {
    private final Set<String> completedLevels = new HashSet<>();
    private static final String SAVE_PATH = "save.json";

    public PlayerProgress() {
        load();
    }

    public Set<String> getCompletedLevels() {
        return Collections.unmodifiableSet(completedLevels);
    }

    public void onLevelComplete(String levelId) {
        completedLevels.add(levelId);
        save();
    }

    private void save() {
        StringBuilder sb = new StringBuilder("{\n  \"completedLevels\": [");
        String[] ids = completedLevels.toArray(new String[0]);
        for (int i = 0; i < ids.length; i++) {
            sb.append('"').append(ids[i]).append('"');
            if (i < ids.length - 1) sb.append(", ");
        }
        sb.append("]\n}");
        try {
            Gdx.files.local(SAVE_PATH).writeString(sb.toString(), false);
        } catch (Exception e) {
            Gdx.app.error("PlayerProgress", "Failed to save progress", e);
        }
    }

    private void load() {
        FileHandle file = Gdx.files.local(SAVE_PATH);
        if (!file.exists()) return;
        try {
            JsonValue root = new JsonReader().parse(file);
            JsonValue arr = root.get("completedLevels");
            if (arr != null) {
                for (JsonValue item = arr.child; item != null; item = item.next) {
                    completedLevels.add(item.asString());
                }
            }
        } catch (Exception e) {
            Gdx.app.error("PlayerProgress", "Corrupted save file, starting fresh", e);
        }
    }
}
