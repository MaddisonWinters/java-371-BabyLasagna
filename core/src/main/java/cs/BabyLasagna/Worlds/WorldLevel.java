package cs.BabyLasagna.Worlds;

import java.util.Set;

public class WorldLevel {
    public final String id;
    public final String displayName;
    public final String mapFile;       // path relative to assets root, without .tmx extension
    public final String buttonTexture; // path relative to assets root, nullable
    private final UnlockCondition unlockCondition;

    public WorldLevel(String id, String displayName, String mapFile, String buttonTexture, UnlockCondition unlockCondition) {
        this.id               = id;
        this.displayName      = displayName;
        this.mapFile          = mapFile;
        this.buttonTexture    = buttonTexture;
        this.unlockCondition  = unlockCondition;
    }

    public boolean isUnlocked(Set<String> completedLevels) {
        return unlockCondition.isMet(completedLevels);
    }
}
