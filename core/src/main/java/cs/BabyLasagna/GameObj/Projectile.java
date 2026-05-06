package cs.BabyLasagna.GameObj;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import cs.BabyLasagna.Game.GameInterface;
import com.badlogic.gdx.utils.Array;

public abstract class Projectile extends GameObj {
    protected boolean facing_right = false;
    protected boolean stuck = false; // Whether the projectile is stuck onto something

    public Projectile(GameInterface g, float x, float y, float vx, float vy, float size, float source_vel, boolean facing_right) {
        super(
            g, 
            x-(0.5f*size), 
            y-(0.5f*size), 
            size, size,
            source_vel*vx,
            source_vel*vy
        );

        this.facing_right = facing_right;
    }

    @Override
    public void update(float deltaTime) {
        if (stuck) return;

        velocity.y += GRAVITY*deltaTime;

        moveWithCollisions(deltaTime);
    }

    protected abstract void hit(Rectangle target);

    protected float bounciness() { return 0.5f; }

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
            hit(tileForAlign);
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

            velocity.y *= -bounciness(); // Bounce off of top/bottom surface
        }
    }
}
