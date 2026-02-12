package cs.BabyLasagna;

import com.badlogic.gdx.graphics.g2d.Batch;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;

public class Player extends Entity {

    // Textures etc.
    static Texture TEXTURE;
    static TextureRegion[] REGIONS;
    static Animation<TextureRegion> ANI_DEFAULT;

    // Constants
    static float LAYER_WIDTH, LAYER_HEIGHT;
    static float MAX_VELOCITY = 6f;
    static float ACCELERATION = 0.08f; // Range [0,1]
    static float JUMP_VELOCITY = 8.5f;

    // Member values
    Facing facing = Facing.Right;
    boolean is_walking = false;

    //Brian L: Added Physics Parameters
    static final float COYOTE_TIME = 0.075f; // 100 ms feels good
    float coyoteTimer = 0f;

    // Render the entity
    @Override
    public void render(float deltaTime, OrthogonalTiledMapRenderer renderer) {
        // TODO: based on the player state, get the animation frame
        // Currently just use the original
        TextureRegion frame = ANI_DEFAULT.getKeyFrame(0);

        Batch batch = renderer.getBatch();
        batch.begin();
        batch.draw(frame, hitbox.x, hitbox.y, LAYER_WIDTH, LAYER_HEIGHT);
        batch.end();
    }

    // Handle movement and collisions
    @Override

    public void update(float deltaTime, TiledMap map, ArrayList<Entity> entities) {
        apply_gravity(deltaTime);

        // Apply friction if sliding (not walking) on the ground
        if (!is_walking) {
            apply_friction(deltaTime);
        }

        move_with_collisions(deltaTime, map, entities);

        // Check entity interactions
        for (Entity e : entities) {
            if (!e.isAlive()) continue;

            // If Collectable c
            if (e instanceof Collectable) {
                Collectable c = (Collectable) e;
                if (hitbox.overlaps(c.hitbox)) {
                    // Collect item
                    if (c.type == Collectable.Type.Cheese)
                        System.out.println("Cheese");
                    else
                        System.out.println("Sauce");
                    e.kill();
                }
            }
        }
    }

    // Initializes textures and related constants
    public static void init() {
        TEXTURE = new Texture("lasagna_single.png");
        REGIONS = TextureRegion.split(TEXTURE, 16, 16)[0];
        LAYER_WIDTH = 1/16f * REGIONS[0].getRegionWidth();
        LAYER_HEIGHT = 1/16f * REGIONS[0].getRegionHeight();
        ANI_DEFAULT = new Animation<>(0, REGIONS[0]);
    }

    public Player(Vector2 start_pos) {
        velocity.set(0,0);
        hitbox.set(start_pos.x, start_pos.y, LAYER_WIDTH, LAYER_HEIGHT);
    }

    public Player(float start_x, float start_y) {
        velocity.set(0,0);
        hitbox.set(start_x, start_y, LAYER_WIDTH, LAYER_HEIGHT);
    }
}
