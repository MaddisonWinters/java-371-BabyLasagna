package cs.BabyLasagna.GameObj;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import cs.BabyLasagna.Game.GameInterface;
import cs.BabyLasagna.TextureManager;

public class Meat extends GameObj {

    public Meat(GameInterface g, float x, float y) {
        super(g, x, y, 1, 1);
        this.isSolid = true;
    }

    @Override
    public void update(float deltaTime) {
        //no gravity
        moveWithCollisions(deltaTime);
    }

    @Override
    public void render(float deltaTime, SpriteBatch batch) {
        TextureManager.draw(
            batch,
            TextureManager.Lasagna.LasagnaFlavor.Meat.getIngredientTex(),
            hitbox.x,
            hitbox.y,
            hitbox.width,
            hitbox.height,
            false,
            false
        );
    }
}
