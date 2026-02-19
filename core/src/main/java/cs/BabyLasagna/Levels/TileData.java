package cs.BabyLasagna.Levels;

public class TileData {
    public enum TType {
        Air, // Probably will be left unused
        Brick, // Generic wall tile
        Kill, // Kills the player; instant lose-condition
        Win, // Instant win-condition
    }
}
