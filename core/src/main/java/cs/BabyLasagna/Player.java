package cs.BabyLasagna;

import com.badlogic.gdx.graphics.g2d.Batch;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

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
    static float MAX_VELOCITY = 12f;
    static float ACCELERATION = 0.08f; // Range [0,1]
    static float JUMP_VELOCITY = 16f;
    static float EXTRA_JUMP_FORCE = 10.0f;

    // Member values
    Facing facing = Facing.Right;
    boolean is_walking = false;

    //Brian L: Added Physics Parameters
    static final float COYOTE_TIME = 0.075f; // 100 ms feels good
    float coyoteTimer = 0f;


    // Handle movement and collisions
    @Override
    public void update(float deltaTime, TiledMap map, ArrayList<Entity> entities) {
        age += deltaTime;   // I don't know why, but if you try to split multiple times in succession,
                            // it crashes 80% of the time, so age to keep track

        apply_gravity(deltaTime);

        // Apply friction if sliding (not walking) on the ground
        if (!is_walking) {
            apply_friction(deltaTime);
        }

        move_with_collisions(deltaTime, map, entities);

        // Check entity interactions
        for (Entity e : entities) {
            if (!e.isAlive()) continue;

            // If LasagnaStack l
            if (e instanceof LasagnaStack) {
                LasagnaStack l = (LasagnaStack) e;
                if (l.size() < 1) continue;
                if (l.age < 0.5) continue;
                l.age = 1;

                if (hitbox.overlaps(l.hitbox)) {
                    Rectangle max_hitbox = new Rectangle(hitbox.x, hitbox.y, hitbox.width, hitbox.height + l.size()*LAYER_HEIGHT);
                    int startY = (int)max_hitbox.y;
                    int endY = (int)(max_hitbox.y + max_hitbox.height);
                    Array<Rectangle> tiles = new Array<>();
                    TiledUtils.getTiles(
                        map,
                        (int)max_hitbox.x, startY,
                        (int)(max_hitbox.x+max_hitbox.width), endY,
                        tiles, "walls"
                    );

                    float lowestIntersection = 1000_000f;

                    for (Rectangle tile : tiles) {
                        if (max_hitbox.overlaps(tile)) {
                            lowestIntersection = Math.min(tile.y - hitbox.y - hitbox.height, lowestIntersection);
                        }
                    }

                    if (lowestIntersection < 99_999f) {
                        int take = (int)Math.floor(lowestIntersection / LAYER_HEIGHT);
                        for (int i = 0; i < l.layers.size() && take > 0; ++i) {
                            LType tmp = l.layers.remove(l.layers.size() - 1);
                            layers.add(0,tmp);
                            --take;
                        }
                        if (take-- > 0 && (l.hasLegs || l.hasHead)) {
                            layers.add(0, LType.Cheese);
                            if (l.hasLegs) l.hasLegs = false;
                            else l.hasHead = false;
                        }
                        if (take-- > 0 && l.hasHead) {
                            layers.add(0, LType.Cheese);
                            l.hasHead = false;
                        }
                    }
                    else {
                        layers.addAll(l.layers);
                        l.layers.clear();
                        if (l.hasLegs) {
                            layers.add(0, LType.Cheese);
                            l.hasLegs = false;
                        }
                        if (l.hasHead) {
                            layers.add(0, LType.Cheese);
                            l.hasHead = false;
                        }
                    }

                    if (l.size() < 1) {
                        l.kill();
                        l.makeDespawn();
                    }

                    updateHitbox();
                }
            }
        }
    }

    @Override
    protected LasagnaStack splitAt(int li) {
        LasagnaStack bottom = super.splitAt(li);
        growLegs();
        return bottom;
    }

    public LasagnaStack extraJump() {
        if (layers.isEmpty()) return null;

        LasagnaStack bottom = null;
        if (layers.size() > 6)      bottom = splitAt(2);
        else if (layers.size() > 3) bottom = splitAt(1);
        else                        bottom = splitAt(0);

        if (bottom == null) return null;

        velocity.y = Math.max(velocity.y, 0);
        velocity.y *= 0.5f;
        velocity.y +=  EXTRA_JUMP_FORCE * ((float)(bottom.size()+2) / (float)(size()));

        return bottom;
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
