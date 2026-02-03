package cs.BabyLasagna;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;

public abstract class Entity {
    enum Facing {
        Left, Right, // Up, Down
    }

    static float GRAVITY = 1.4f;

    // What fraction of speed is kept per-second in air
    static float NORMAL_DAMPING = 0.7f;
    // For types of ground that the player is standing on, use .get() to get the friction of the surface
    enum Ground {
        Air(NORMAL_DAMPING),
        Slick(NORMAL_DAMPING*0.15f),
        Normal(NORMAL_DAMPING*0.00004f),
        Rough(0f),
        // Special
        Sheer(1.0f),
        Boost(1.2f);

        final float speed;
        Ground(float speed) { this.speed = speed; }
        public float get() { return speed; }
    }

    // Render the entity
    public abstract void render(float deltaTime, OrthogonalTiledMapRenderer renderer);
    // Handle movement and collisions
    public abstract void update(float deltaTime, TiledMap map, ArrayList<Entity> entities);

    // These should probably be combined into one variable for consitency
    Vector2 position = new Vector2();
    Rectangle hitbox = new Rectangle();
}
