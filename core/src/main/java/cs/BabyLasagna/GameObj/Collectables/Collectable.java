package cs.BabyLasagna.GameObj.Collectables;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import cs.BabyLasagna.TextureManager;
import cs.BabyLasagna.Game.GameInterface;
import cs.BabyLasagna.GameObj.GameObj;
import cs.BabyLasagna.GameObj.Player;
import cs.BabyLasagna.TextureManager.CollectableTex.Generic;

public abstract class Collectable extends GameObj {

    public final void render(float detaTime, SpriteBatch batch) {
        TextureManager.draw(
            batch,
            Generic.getTex(),
            hitbox.x,
            hitbox.y,
            hitbox.width,
            hitbox.height,
            false,
            false
        );
    }

    public void update(float deltaTime) {
        velocity.y += GRAVITY * deltaTime;
        moveWithCollisions(deltaTime);
    }

    public abstract void collect(Player player);

    public Collectable(GameInterface g, float x, float y, float width, float height) {
        super(g,x,y,width,height,0,0);
    }
}
