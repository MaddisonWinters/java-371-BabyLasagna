package cs.BabyLasagna.GameObj;

public class PlayerProgress {
    private int unlockedUpTo = 0;

    public boolean canAccess(int levelIndex) {
        return levelIndex <= unlockedUpTo;
    }

    public void onLevelComplete(int levelIndex) {
        unlockedUpTo = Math.max(unlockedUpTo, levelIndex + 1);
    }
}
