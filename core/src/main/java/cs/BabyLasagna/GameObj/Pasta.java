package cs.BabyLasagna.GameObj;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import cs.BabyLasagna.Game.GameInterface;
import cs.BabyLasagna.TextureManager;

public class Pasta extends GameObj {

    private static final float WIDTH = 1f;
    private static final float HEIGHT = 0.3f; // thinner than meat
    private static final float BOUNCE_FORCE = 14f;

    public Pasta(GameInterface g, float x, float y) {
        super(g, x, y, WIDTH, HEIGHT);
        this.isSolid = true;
    }

    @Override
    public void update(float deltaTime) {
        // stays in place, just handles collisions
        moveWithCollisions(deltaTime);
    }

    @Override
    public void render(float deltaTime, SpriteBatch batch) {
        TextureManager.draw(
            batch,
            TextureManager.Lasagna.LasagnaFlavor.Pasta.getIngredientTex(),
            hitbox.x,
            hitbox.y,
            hitbox.width,
            hitbox.height,
            false,
            false
        );
    }

    // Called when something lands on it
    public void bounce(Player player) {
        player.getVelocity().y = BOUNCE_FORCE;
    }
}
