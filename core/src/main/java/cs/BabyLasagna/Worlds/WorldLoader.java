package cs.BabyLasagna.Worlds;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

import java.util.ArrayList;
import java.util.List;

public class WorldLoader {

    public static WorldDefinition load(String assetPath) {
        FileHandle file = Gdx.files.internal(assetPath);
        JsonValue root = new JsonReader().parse(file);

        String name = root.getString("name");
        List<WorldLevel> levels = new ArrayList<>();

        for (JsonValue entry = root.get("levels").child; entry != null; entry = entry.next) {
            String id            = entry.getString("id");
            String displayName   = entry.getString("displayName");
            String mapFile       = entry.getString("mapFile");
            String buttonTexture = entry.getString("buttonTexture", null);
            UnlockCondition cond = UnlockCondition.fromJson(entry.get("unlockCondition"));
            levels.add(new WorldLevel(id, displayName, mapFile, buttonTexture, cond));
        }

        return new WorldDefinition(name, levels);
    }
}
