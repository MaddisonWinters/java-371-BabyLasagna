package cs.BabyLasagna.GameObj;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import cs.BabyLasagna.Game;
import cs.BabyLasagna.TextureManager;
import cs.BabyLasagna.Game.GameInterface;
import com.badlogic.gdx.utils.Array;
import cs.BabyLasagna.TextureManager.Abilities.Cheese;

public class CheeseBall extends Projectile {

    public static final float BALL_SIZE    = 0.5f;
    public static final float SPLAT_WIDTH  = 6.0f / Game.PIXELS_PER_TILE;
    public static final float SPLAT_HEIGHT = 16.0f / Game.PIXELS_PER_TILE;

    public static final float INITIAL_VX = 12.0f;
    public static final float INITIAL_VY = 7.0f;
    public static final float SOURCE_VEL = 0.5f;

    public static final float STICKY_VEL = 1.6f;

    public CheeseBall(GameInterface g, float x, float y, float vx, float vy, boolean facing_right) {
        super(g,x,y,vx,vy,SOURCE_VEL,facing_right);

        velocity.x += (this.facing_right ? INITIAL_VX : -INITIAL_VX);
        velocity.y += INITIAL_VY;
    }

    @Override
    public void hit(Rectangle alignTile) {
        stuck = true;

        hitbox.height = SPLAT_HEIGHT;
        hitbox.y = alignTile.y + 0.5f*(1.0f - hitbox.height);

        hitbox.width = SPLAT_WIDTH;
        hitbox.x = alignTile.x - 0.5f*hitbox.width;
        if (facing_right) hitbox.x += 1.0f;
    }

    public boolean isSplatted() { return stuck; }

    @Override
    public void render(float deltaTime, SpriteBatch batch) {
        TextureManager.draw(
            batch,
            (stuck ? Cheese.getSplatTex() : Cheese.getGlobTex()),
            hitbox.x,
            hitbox.y,
            hitbox.width,
            hitbox.height,
            facing_right,
            false
        );
    }

    
}