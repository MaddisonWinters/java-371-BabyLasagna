package cs.BabyLasagna;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import java.util.ArrayList;

public abstract class Entity {
    enum Facing {
        Left, Right, // Up, Down
    }

    static float GRAVITY = 32;

    // What fraction of speed is kept per-second in air
    static float NORMAL_DAMPING = 0.001f;
    // For types of ground that the player is standing on, use .get() to get the friction of the surface
    enum Ground {
        Air(NORMAL_DAMPING),
        Slick(NORMAL_DAMPING*0.15f),
        Normal(NORMAL_DAMPING*0.000001f),
        Rough(0f),
        // Special
        Sheer(1.0f),
        Boost(1.2f);

        final float speed;
        Ground(float speed) { this.speed = speed; }
        public float get() { return speed; }
    }

    Rectangle hitbox = new Rectangle();
    final Vector2 velocity = new Vector2(0,0);
    Ground standing_on = Ground.Normal;
    private boolean isAlive = true;
    protected boolean shouldDespawn = false;

    // Render the entity
    public abstract void render(float deltaTime, OrthogonalTiledMapRenderer renderer);
    // Handle movement and collisions
    public void update(float deltaTime, TiledMap map, ArrayList<Entity> entities) {
        apply_gravity(deltaTime);
        apply_friction(deltaTime);
        move_with_collisions(deltaTime, map, entities);
    }

    public void apply_friction(float deltaTime) {
        velocity.x *= (float) Math.pow(standing_on.get(), deltaTime);
    }

    public void apply_gravity(float deltaTime) {
        velocity.y -= GRAVITY * deltaTime;
    }

    public void move_with_collisions(float deltaTime, TiledMap map, ArrayList<Entity> entities) {
        Vector2 vel_scl = velocity.cpy();
        vel_scl.scl(deltaTime);

        // Just variables
        Array<Rectangle> tiles = new Array<>();
        int startX, startY, endX, endY;

        // Find relevant area
        if (vel_scl.y > 0) {
            startY = endY = (int)(hitbox.y + hitbox.height + vel_scl.y);
        } else {
            startY = endY = (int)(hitbox.y + vel_scl.y);
        }
        startX = (int)(hitbox.x);
        endX = (int)(hitbox.x + hitbox.width);
        hitbox.y += vel_scl.y;

        // Get tiles for relevant area
        tiles.clear();
        TiledUtils.getTiles(map, startX, startY, endX, endY, tiles, "walls");

        // Check for vertical collisions
        boolean never_hit_ground = true;
        for (Rectangle tile : tiles) {
            if (hitbox.overlaps(tile)) {
                // Align with tile edge
                if (vel_scl.y < 0) {
                    never_hit_ground = false;
                    standing_on = Ground.Normal; // TODO?: Some way to check what ground we're on and assign value accordingly
                    hitbox.y = tile.y + tile.height;
                }
                else {
                    hitbox.y = tile.y - hitbox.height - 0.001f;
                }
                vel_scl.y = 0;
                velocity.y = 0;
                break;
            }
        }
        if (never_hit_ground) standing_on = Ground.Air;

        // Update relevant area
        if (vel_scl.x > 0) {
            startX = endX = (int)(hitbox.x + hitbox.width + vel_scl.x);
        } else {
            startX = endX = (int)(hitbox.x + vel_scl.x);
        }
        startY = (int)(hitbox.y);
        endY = (int)(hitbox.y + hitbox.height);
        hitbox.x += vel_scl.x;

        // Get tiles for relevant area
        tiles.clear();
        TiledUtils.getTiles(map, startX, startY, endX, endY, tiles, "walls");

        // Check for horizontal collisions
        for (Rectangle tile : tiles) {
            if (hitbox.overlaps(tile)) {
                // Align with tile edge
                if (vel_scl.x < 0) {
                    hitbox.x = tile.x + tile.width;
                }
                else {
                    hitbox.x = tile.x - hitbox.width;
                    // Known issue: Sometimes jam into the tile when traveling this direction and
                    // end up getting shot into the floor
                }
                vel_scl.x = 0;
                velocity.x = 0;
                break;
            }
        }

        // Update position based on corrected velocity
        hitbox.x += vel_scl.x;
        hitbox.y += vel_scl.y;
    }

    public void move_no_collisions(float deltaTime) {
        Vector2 vel_scl = velocity.cpy();
        vel_scl.scl(deltaTime);

        // Update position based on corrected velocity
        hitbox.x += vel_scl.x;
        hitbox.y += vel_scl.y;
    }

    public void kill() { isAlive = false; }
    public boolean isAlive() { return isAlive; }
    public boolean shouldDespawn() { return shouldDespawn; }
}
