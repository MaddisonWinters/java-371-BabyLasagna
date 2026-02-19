package cs.BabyLasagna.GameObj;

import cs.BabyLasagna.Game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class Player extends GameObj {
        ///  Constants
    private static final float  WIDTH=16/(float)(Game.PIXELS_PER_TILE),
                                HEIGHT=18/(float)(Game.PIXELS_PER_TILE);

    private static final Texture texture;

    static {
        texture = new Texture("lasagna_single.png");
    }

    @Override
    public void render(float deltaTime, SpriteBatch batch) {
        batch.draw(texture, hitbox.x, hitbox.y, hitbox.width, hitbox.height);
    }

    @Override
    public void update(float deltaTime) {
        Vector2 vel_scl = new Vector2(velocity);
        vel_scl.scl(deltaTime);
        hitbox.x += vel_scl.x;
        hitbox.y += vel_scl.y;
    }

    public Player(float x, float y) {
        super(x, y, WIDTH, HEIGHT);
    }
}
