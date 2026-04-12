package cs.BabyLasagna.GameObj;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import cs.BabyLasagna.TextureManager;
import cs.BabyLasagna.Game.GameInterface;
import com.badlogic.gdx.utils.Array;

public class CheeseBall extends GameObj {

    public static final float INITIAL_VX = 8.0f;
    public static final float INITIAL_VY = 3.0f;
    
    private boolean facing_right = false;
    private boolean splatted = false;

    public CheeseBall(GameInterface g, float x, float y, float vx, float vy, boolean facing_right) {
        super(
            g, x, y, 0.5f, 0.5f,
            (facing_right ? vx + INITIAL_VX : vx - INITIAL_VX ),
            (vy + INITIAL_VY)
        );

        this.facing_right = facing_right;
    }

    @Override
    public void update(float deltaTime) {
        if (splatted == true) {
            // TODO: Make it follow solid objects that can move and check
            // if the tile is still there. 
            return;
        }

        velocity.y += GRAVITY*deltaTime;

        moveWithCollisions(deltaTime);

        if (splatted) {
            // For now: Align to tile, since we'll only handle collision with tiles. 

            // Center on nearest tile position
            float cx = hitbox.x + hitbox.width*0.5f;
            float cy = hitbox.y + hitbox.height*0.5f;
            hitbox.x = (float)Math.floor(cx) + 0.5f - hitbox.width*0.5f;
            hitbox.y = (float)Math.floor(cy) + 0.5f - hitbox.height*0.5f;
        }
    }

    @Override
    public void render(float deltaTime, SpriteBatch batch) {
        TextureManager.draw(
            batch,
            TextureManager.Lasagna.LasagnaFlavor.Cheese.getIngredientTex(),
            hitbox.x,
            hitbox.y,
            hitbox.width,
            hitbox.height,
            false,
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

        for (Rectangle tile : tile_rects) {
            if (!hitbox.overlaps(tile)) continue;
            splatted = true;

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

            velocity.y *= -0.8f;
        }
    }
}