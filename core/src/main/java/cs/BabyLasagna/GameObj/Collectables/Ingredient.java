package cs.BabyLasagna.GameObj.Collectables;

import cs.BabyLasagna.Game.GameInterface;
import cs.BabyLasagna.GameObj.Player;
import cs.BabyLasagna.TextureManager.Lasagna.LasagnaFlavor;

public class Ingredient extends Collectable {
    public final LasagnaFlavor type; 

    public void collect(Player player) {
        player.addBottom(type);
    }

    public Ingredient(GameInterface g, LasagnaFlavor type, float x, float y) {
        super(g, x, y, 1, 1);
        this.type = type;
    }
}
