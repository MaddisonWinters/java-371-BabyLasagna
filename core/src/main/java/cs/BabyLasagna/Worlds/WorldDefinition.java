package cs.BabyLasagna.Worlds;

import java.util.List;

public class WorldDefinition {
    public final String name;
    public final List<WorldLevel> levels;

    public WorldDefinition(String name, List<WorldLevel> levels) {
        this.name   = name;
        this.levels = levels;
    }
}
