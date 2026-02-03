package cs.BabyLasagna;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.Array;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import cs.BabyLasagna.TiledUtils;

public class Lasagna extends Entity {

    // Textures etc.
    static Texture TEXTURE;
    static TextureRegion[] REGIONS;
    static Animation<TextureRegion> ANI_DEFAULT;

    // Constants
    static float LAYER_WIDTH, LAYER_HEIGHT;
    static float MAX_VELOCITY = 15f;
    static float ACCELERATION = 0.08f; // Range [0,1]
    static float JUMP_VELOCITY = 30f;

    // Member values
    final Vector2 velocity = new Vector2();
    Facing facing = Facing.Right;
    Ground standing_on = Ground.Normal;
    boolean is_walking = false;

    // Render the entity
    @Override
    public void render(float deltaTime, OrthogonalTiledMapRenderer renderer) {
        // TODO: based on the player state, get the animation frame
        // Currently just use the original
        TextureRegion frame = ANI_DEFAULT.getKeyFrame(0);

        Batch batch = renderer.getBatch();
        batch.begin();
        batch.draw(frame, position.x, position.y, LAYER_WIDTH, LAYER_HEIGHT);
        batch.end();
    }

    @Override
    // Handle movement and collisions
    public void update(float deltaTime, TiledMap map, ArrayList<Entity> entities) {
        // Check if jumping (or otherwise going into the air)
        if (velocity.y > 0) { standing_on = Ground.Air; }

        velocity.y -= GRAVITY;
        velocity.scl(deltaTime);// if the player is moving upwards, check the tiles to the top of its

        // Just variables
        Array<Rectangle> tiles = new Array<>();
        int startX, startY, endX, endY;

        // Ensure correct hitbox position is used
        hitbox.setPosition(position.x, position.y);
        // Find relevant area
        if (velocity.y > 0) {
            startY = endY = (int)(position.y + Lasagna.LAYER_HEIGHT + velocity.y);
        } else {
            startY = endY = (int)(position.y + velocity.y);
        }
        startX = (int)(position.x);
        endX = (int)(position.x + Lasagna.LAYER_WIDTH);
        hitbox.y += velocity.y;

        // Get tiles for relevant area
        tiles.clear();
        TiledUtils.getTiles(map, startX, startY, endX, endY, tiles, "walls");

        // Check for vertical collisions
        for (Rectangle tile : tiles) {
            if (hitbox.overlaps(tile)) {
                // Align with tile edge
                if (velocity.y < 0) {
                    standing_on = Ground.Normal;
                    position.y = tile.y + tile.height;
                }
                else {
                    position.y = tile.y - hitbox.height - 0.001f;
                }
                velocity.y = 0;
                break;
            }
        }

        // Apply friction if sliding (not walking) on the ground
        if (!is_walking) {
            velocity.x *= (float) Math.pow(standing_on.get(), deltaTime);
        }

        // Update relevant area
        if (velocity.x > 0) {
            startX = endX = (int)(position.x + Lasagna.LAYER_WIDTH + velocity.x);
        } else {
            startX = endX = (int)(position.x + velocity.x);
        }
        startY = (int)(position.y);
        endY = (int)(position.y + Lasagna.LAYER_HEIGHT);
        hitbox.x += velocity.x;

        // Get tiles for relevant area
        tiles.clear();
        TiledUtils.getTiles(map, startX, startY, endX, endY, tiles, "walls");

        // Check for horizontal collisions
        for (Rectangle tile : tiles) {
            if (hitbox.overlaps(tile)) {
                // Align with tile edge
                if (velocity.x < 0) {
                    position.x = tile.x + tile.width;
                }
                else {
                    position.x = tile.x - tile.width;
                }
                velocity.x = 0;
                break;
            }
        }

        // Update position based on corrected velocity
        position.add(velocity);
        // Un-scale velocity based on time interval
        velocity.scl(1 / deltaTime);
        // Update hitbox
        hitbox.setPosition(position);
    }

    // Initializes textures and related constants
    public static void init() {
        TEXTURE = new Texture("koalio.png");
        REGIONS = TextureRegion.split(TEXTURE, 18, 26)[0];
        LAYER_WIDTH = 1/16f * REGIONS[0].getRegionWidth();
        LAYER_HEIGHT = 1/16f * REGIONS[0].getRegionHeight();
        ANI_DEFAULT = new Animation<>(0, REGIONS[0]);
    }

    public Lasagna(Vector2 start_pos) {
        position.set(start_pos);
        velocity.set(0,0);
        hitbox.set(position.x, position.y, LAYER_WIDTH, LAYER_HEIGHT);
    }
}
