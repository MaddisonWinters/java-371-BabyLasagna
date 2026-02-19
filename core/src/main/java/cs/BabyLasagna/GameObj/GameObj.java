package cs.BabyLasagna.GameObj;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import cs.BabyLasagna.Levels.TileData;

// A generic object that has a hitbox, velocity, and movement/collision functions
public abstract class GameObj {
        /// Positional members
    protected final Rectangle hitbox = new Rectangle();
    protected final Vector2 velocity = new Vector2();

        /// Getters
    public final Vector2 getPosition() { return new Vector2(hitbox.x, hitbox.y); }
    public final float getX() { return hitbox.x; }
    public final float getY() { return hitbox.y; }
    public final Rectangle getHitbox() { return new Rectangle(hitbox); }
    public final Vector2 getVelocity() { return new Vector2(velocity); }

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
    public void collideWithTile(Rectangle rect, TileData.TType ttype) {
        // TODO
    }
    public void collideWithOther(GameObj otherObj) {} // Does nothing by default; no entity-entity interactions

        /// Constructors
    GameObj(float x, float y, float width, float height, float vx, float vy) {
        hitbox.set(x, y, width, height);
        velocity.set(vx, vy);
    }
    GameObj(float x, float y, float width, float height) {
        this(x,y,width,height,0,0);
    }
}
