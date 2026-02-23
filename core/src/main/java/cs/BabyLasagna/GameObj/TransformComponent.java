package cs.BabyLasagna.GameObj;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import cs.BabyLasagna.Levels.TileData;

public class TransformComponent {

    // World position (bottom-left corner by convention)
    public final Vector2 position = new Vector2();

    // Dimensions (used for rendering + collision)
    public final Vector2 size = new Vector2(1f, 1f);

    // Rotation in degrees
    public float rotation = 0f;

    // Scale
    public final Vector2 scale = new Vector2(1f, 1f);

    // Origin for rotation/scaling (relative to position)
    public final Vector2 origin = new Vector2();

    // Reusable bounds object (avoids allocations)
    private final Rectangle bounds = new Rectangle();

    public TransformComponent(float x, float y, float width, float height) {
        position.set(x, y);
        size.set(width, height);
        origin.set(width / 2f, height / 2f);
    }

    // -------------------------
    // Bounds (for collisions)
    // -------------------------
    public Rectangle getBounds() {
        bounds.set(
            position.x,
            position.y,
            size.x * scale.x,
            size.y * scale.y
        );
        return bounds;
    }

    // -------------------------
    // Convenience Methods
    // -------------------------

    public float getCenterX() {
        return position.x + (size.x * scale.x) / 2f;
    }

    public float getCenterY() {
        return position.y + (size.y * scale.y) / 2f;
    }

    public void setCenter(float x, float y) {
        position.set(
            x - (size.x * scale.x) / 2f,
            y - (size.y * scale.y) / 2f
        );
    }

    public void translate(float dx, float dy) {
        position.add(dx, dy);
    }

}
