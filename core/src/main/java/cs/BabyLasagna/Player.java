package cs.BabyLasagna;

import com.badlogic.gdx.graphics.g2d.Batch;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Player extends LasagnaStack {

    // Textures etc.
    static Texture TEXTURE;
    static TextureRegion[] REGIONS;
    static Animation<TextureRegion> ANI_DEFAULT;

    // Constants
    static float LAYER_WIDTH, LAYER_HEIGHT;
    static float MAX_VELOCITY = 12f;
    static float ACCELERATION = 0.08f; // Range [0,1]
    static float JUMP_VELOCITY = 16f;

    // Member values
    Facing facing = Facing.Right;
    boolean is_walking = false;

    //Brian L: Added Physics Parameters
    static final float COYOTE_TIME = 0.075f; // 100 ms feels good
    float coyoteTimer = 0f;


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

    public Player(float x, float y) {
        super(x,y,true,true,
            Collections.nCopies(
                9,
                LType.Cheese
            )
        );
    }
}
