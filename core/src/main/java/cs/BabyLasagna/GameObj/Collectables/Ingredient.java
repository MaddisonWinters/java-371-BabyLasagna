package cs.BabyLasagna.GameObj.Collectables;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import cs.BabyLasagna.Game.GameInterface;
import cs.BabyLasagna.TextureManager;
import cs.BabyLasagna.GameObj.Player;
import cs.BabyLasagna.TextureManager.Lasagna.LasagnaFlavor;

public class Ingredient extends Collectable {
    public final LasagnaFlavor type; 

    @Override
    public final void render(float detaTime, SpriteBatch batch) {
        TextureManager.draw(
            batch,
            type.getIngredientTex(),
            hitbox.x,
            hitbox.y,
            hitbox.width,
            hitbox.height,
            false,
            false
        );
    }

    @Override
    public void collect(Player player) {
        player.addBottom(type);
    }

    public Ingredient(GameInterface g, LasagnaFlavor type, float x, float y) {
        super(g, x, y, 1, 1);
        this.type = type;
    }
}
