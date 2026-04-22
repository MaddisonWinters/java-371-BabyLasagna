package cs.BabyLasagna.GameObj;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import cs.BabyLasagna.Game;
import cs.BabyLasagna.TextureManager;
import cs.BabyLasagna.Game.GameInterface;
import com.badlogic.gdx.utils.Array;
import cs.BabyLasagna.TextureManager.Abilities.Cheese;

public class CheeseBall extends GameObj {

    public static final float BALL_SIZE    = 0.5f;
    public static final float SPLAT_WIDTH  = 6.0f / Game.PIXELS_PER_TILE;
    public static final float SPLAT_HEIGHT = 16.0f / Game.PIXELS_PER_TILE;

    public static final float INITIAL_VX = 12.0f;
    public static final float INITIAL_VY = 7.0f;
    public static final float SOURCE_VEL = 0.5f;

    public static final float STICKY_VEL = 1.6f;
    
    private boolean facing_right = false;
    private boolean splatted = false;

    public CheeseBall(GameInterface g, float x, float y, float vx, float vy, boolean facing_right) {
        super(
            g, x, y, 0.5f, 0.5f,
            SOURCE_VEL*vx + (facing_right ? INITIAL_VX : -INITIAL_VX ),
            SOURCE_VEL*vy + INITIAL_VY
        );

        this.facing_right = facing_right;
    }

    public void splat(Rectangle alignTile) {
        splatted = true;

        hitbox.height = SPLAT_HEIGHT;
        hitbox.y = alignTile.y + 0.5f*(1.0f - hitbox.height);

        hitbox.width = SPLAT_WIDTH;
        hitbox.x = alignTile.x - 0.5f*hitbox.width;
        if (facing_right) hitbox.x += 1.0f;
    }

    public boolean isSplatted() { return splatted; }

    @Override
    public void update(float deltaTime) {
        if (splatted == true) {
            // TODO: Make it follow solid objects that can move and check
            // if the tile is still there. 
            return;
        }

        velocity.y += GRAVITY*deltaTime;

        moveWithCollisions(deltaTime);
    }

    @Override
    public void render(float deltaTime, SpriteBatch batch) {
        TextureManager.draw(
            batch,
            (splatted ? Cheese.getSplatTex() : Cheese.getGlobTex()),
            hitbox.x,
            hitbox.y,
            hitbox.width,
            hitbox.height,
            facing_right,
            false
        );
    }

    // Move and collide with general list of hitboxes | Primary collision function
    @Override
    public void moveWithCollisions(Array<Rectangle> tile_rects, Vector2 movement_vec) {
        grounded = false;

        // Cap speed
        if (Math.abs(movement_vec.x) > MAX_TILES_PER_FRAME) {
            movement_vec.x = MAX_TILES_PER_FRAME * Math.signum(movement_vec.x);
        }
        if (Math.abs(movement_vec.y) > MAX_TILES_PER_FRAME) {
            movement_vec.y = MAX_TILES_PER_FRAME * Math.signum(movement_vec.y);
        }

        // Handle x-movement and x-collisions first
        hitbox.x += movement_vec.x;

        Rectangle tileForAlign = null;

        for (Rectangle tile : tile_rects) {
            if (!hitbox.overlaps(tile)) continue;
            tileForAlign = new Rectangle(tile);

            float dx = (hitbox.x + hitbox.width/2f) - (tile.x + tile.width/2f);

            if (dx > 0) {
                hitbox.x = tile.x + tile.width;
                facing_right = true;
            }
            else {
                hitbox.x = tile.x - hitbox.width;
                facing_right = false;
            }
        }

        if (tileForAlign != null) {
            splat(tileForAlign);
            return;
        }

        // Handle y-movement and y-collisions last
        hitbox.y += movement_vec.y;

        for (Rectangle tile : tile_rects) {
            if (!hitbox.overlaps(tile)) continue;

            float dy = (hitbox.y + hitbox.height/2f) - (tile.y + tile.height/2f);

            if (dy > 0) {
                if (velocity.y <= 0) {
                    hitbox.y = tile.y + tile.height;
                    grounded = true;
                }
            } else {
                if (velocity.y > 0) {
                    hitbox.y = tile.y - hitbox.height;
                }
            }

            velocity.y *= -0.8f; // Bounce off of top/bottom surface
        }
    }
}