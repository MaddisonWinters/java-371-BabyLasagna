package cs.BabyLasagna.GameObj;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import cs.BabyLasagna.Game;
import cs.BabyLasagna.Game.GameInterface;
import cs.BabyLasagna.Levels.Util;

// A generic object that has a hitbox, velocity, and movement/collision functions
public abstract class GameObj {
        ///  Constants
    protected static final float GRAVITY = -30f;
    public static final float MAX_TILES_PER_FRAME = 0.48f;
    public static final float MAX_VELOCITY = MAX_TILES_PER_FRAME / Game.MAX_DELTA_TIME;

        /// Positional members
    protected final Rectangle hitbox = new Rectangle();
    protected final Vector2 velocity = new Vector2();
    protected boolean grounded = false;

        /// Environment
    protected final GameInterface gameInt;

        /// Getters
    public final float getX() { return hitbox.x; }
    public final float getY() { return hitbox.y; }
    public final Vector2 getPosition() { return new Vector2(hitbox.x, hitbox.y); }
    public final float getCenterX() { return hitbox.x + hitbox.width*0.5f; }
    public final float getCenterY() { return hitbox.y + hitbox.height*0.5f; }
    public final Vector2 getCenterPosition() { return new Vector2(getCenterX(), getCenterY()); }
    public final Rectangle getHitbox() { return new Rectangle(hitbox); }
    public final Vector2 getVelocity() { return new Vector2(velocity); }
    public boolean isGrounded() { return grounded; }
    public float getGravity() { return GRAVITY; }

        ///  Setters
    protected final void setPosition(Vector2 v) { hitbox.x=v.x; hitbox.y=v.y; }
    protected final void setX(float x) { hitbox.x=x; }
    protected final void setY(float y) { hitbox.y=y; }
    protected final void setHitbox(Rectangle r) { hitbox.set(r); }
    protected final void setVelocity(Vector2 v) { velocity.set(v); }

        /// Abstract member functions
    // batch.begin() and batch.end() are not to be called within this function
    public abstract void render(float deltaTime, SpriteBatch batch);
    public abstract void update(float deltaTime);

        /// Collision-related functions
    // Adds hitboxes of nearby tiles to the Array `tiles`
    public void getTilesFromMap(Array<Rectangle> tiles, Vector2 movement_vec) {
        // Calculate area of relevance in tilemap
        int startX = (int)Math.floor(Math.min(hitbox.x, hitbox.x+movement_vec.x));
        int startY = (int)Math.floor(Math.min(hitbox.y, hitbox.y+movement_vec.y));
        int endX   = (int)Math.ceil(hitbox.width + Math.max(hitbox.x, hitbox.x+movement_vec.x));
        int endY   = (int)Math.ceil(hitbox.height+ Math.max(hitbox.y, hitbox.y+movement_vec.y));

        // Get relevant tile rectangles/hitboxes
        Util.getTiles(
            gameInt.getMap(),
            "Wall",
            tiles,
            startX,
            startY,
            endX,
            endY
        );
    }

    // Move and collide with general list of hitboxes | Primary collision function
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

            float dx = (hitbox.x + hitbox.width/2f) - (tile.x + tile.width/2f);

            if (dx > 0)
                hitbox.x = tile.x + tile.width;
            else
                hitbox.x = tile.x - hitbox.width;
        }

        // Handle y-movement and y-collisions last
        hitbox.y += movement_vec.y;

        for (Rectangle tile : tile_rects) {
            if (!hitbox.overlaps(tile)) continue;

            float dy = (hitbox.y + hitbox.height/2f) - (tile.y + tile.height/2f);

            // Note: Why are there nested if statements here checking velocity?
            //       This doesn't happen for x, and I'm not sure what the significance is. 
            if (dy > 0) {
                if (velocity.y <= 0) {
                    hitbox.y = tile.y + tile.height;
                    grounded = true;
                    velocity.y = 0;
                }
            } else {
                if (velocity.y > 0) {
                    hitbox.y = tile.y - hitbox.height;
                    velocity.y = 0;
                }
            }
        }
    }

    // Move and collide with tilemap
    public void moveWithCollisions(float deltaTime) {
        // Calculate movement vector
        Vector2 velocity_scaled = new Vector2(velocity);
        velocity_scaled.scl(deltaTime);

        // Get nearby tiles
        Array<Rectangle> near_tiles = new Array<>();
        getTilesFromMap(near_tiles, velocity_scaled);

        moveWithCollisions(near_tiles, velocity_scaled);
    }

        /// Constructors
    public GameObj(GameInterface g, float x, float y, float width, float height, float vx, float vy) {
        hitbox.set(x, y, width, height);
        velocity.set(vx, vy);
        gameInt = g;
    }
    public GameObj(GameInterface g, float x, float y, float width, float height) {
        this(g,x,y,width,height,0,0);
    }
}
