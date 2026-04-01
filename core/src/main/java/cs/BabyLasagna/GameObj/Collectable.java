package cs.BabyLasagna.GameObj;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import cs.BabyLasagna.TextureManager;
import cs.BabyLasagna.Game.GameInterface;
import cs.BabyLasagna.TextureManager.CollectableTex.Generic;

public class Collectable extends GameObj {

    public void render(float detaTime, SpriteBatch batch) {
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

    public Collectable(GameInterface g, float x, float y, float width, float height) {
        super(g,x,y,width,height,0,0);
    }
}
