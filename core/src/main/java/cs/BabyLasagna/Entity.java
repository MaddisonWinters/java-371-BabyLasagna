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

    static float GRAVITY = 48;

    // What fraction of speed is kept per-second in air
    static float NORMAL_DAMPING = 0.7f;
    // For types of ground that the player is standing on, use .get() to get the friction of the surface
    enum Ground {
        Air(NORMAL_DAMPING),
        Slick(NORMAL_DAMPING*0.15f),
        Normal(NORMAL_DAMPING*0.00003f),
        Rough(0f),
        // Special
        Sheer(1.0f),
        Boost(1.2f);

        final float speed;
        Ground(float speed) { this.speed = speed; }
        public float get() { return speed; }
    }

    Rectangle hitbox = new Rectangle();
    final Vector2 velocity = new Vector2();
    Ground standing_on = Ground.Normal;

    // Render the entity
    public abstract void render(float deltaTime, OrthogonalTiledMapRenderer renderer);
    // Handle movement and collisions
    public void update(float deltaTime, TiledMap map, ArrayList<Entity> entities) {
        velocity.scl(deltaTime);// if the player is moving upwards, check the tiles to the top of its

        // Just variables
        Array<Rectangle> tiles = new Array<>();
        int startX, startY, endX, endY;

        // Find relevant area
        if (velocity.y > 0) {
            startY = endY = (int)(hitbox.y + Lasagna.LAYER_HEIGHT + velocity.y);
        } else {
            startY = endY = (int)(hitbox.y + velocity.y);
        }
        startX = (int)(hitbox.x);
        endX = (int)(hitbox.x + Lasagna.LAYER_WIDTH);
        hitbox.y += velocity.y;

        // Get tiles for relevant area
        tiles.clear();
        TiledUtils.getTiles(map, startX, startY, endX, endY, tiles, "walls");

        // Check for vertical collisions
        boolean never_hit_ground = true;
        for (Rectangle tile : tiles) {
            if (hitbox.overlaps(tile)) {
                // Align with tile edge
                if (velocity.y < 0) {
                    never_hit_ground = false;
                    standing_on = Ground.Normal; // TODO?: Some way to check what ground we're on and assign value accordingly
                    hitbox.y = tile.y + tile.height;
                }
                else {
                    hitbox.y = tile.y - hitbox.height - 0.001f;
                }
                velocity.y = 0;
                break;
            }
        }
        if (never_hit_ground) standing_on = Ground.Air;

        // Update relevant area
        if (velocity.x > 0) {
            startX = endX = (int)(hitbox.x + Lasagna.LAYER_WIDTH + velocity.x);
        } else {
            startX = endX = (int)(hitbox.x + velocity.x);
        }
        startY = (int)(hitbox.y);
        endY = (int)(hitbox.y + Lasagna.LAYER_HEIGHT);
        hitbox.x += velocity.x;

        // Get tiles for relevant area
        tiles.clear();
        TiledUtils.getTiles(map, startX, startY, endX, endY, tiles, "walls");

        // Check for horizontal collisions
        for (Rectangle tile : tiles) {
            if (hitbox.overlaps(tile)) {
                // Align with tile edge
                if (velocity.x < 0) {
                    hitbox.x = tile.x + tile.width;
                }
                else {
                    hitbox.x = tile.x - hitbox.width;
                    // Known issue: Sometimes jam into the tile when traveling this direction and
                    // end up getting shot into the floor
                }
                velocity.x = 0;
                break;
            }
        }

        // Update position based on corrected velocity
        hitbox.x += velocity.x;
        hitbox.y += velocity.y;
        // Un-scale velocity based on time interval
        velocity.scl(1 / deltaTime);
    }
}
