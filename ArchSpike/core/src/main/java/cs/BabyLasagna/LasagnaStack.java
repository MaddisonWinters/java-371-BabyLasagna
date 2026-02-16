package cs.BabyLasagna;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.lang.Math.min;
import static java.lang.Math.max;

public class LasagnaStack extends Entity {
    public enum LType {
        Cheese,
        Meat
    };


    // Textures etc.
    static Texture TEXTURE;
    static TextureRegion[] REGIONS;
    static TextureRegion[] HEAD_REGIONS;
    static TextureRegion[] LEGS_REGIONS;
    static TextureRegion[] LAYER_REGIONS;
    static Animation<TextureRegion> ANI_HEAD;
    static Animation<TextureRegion> ANI_LEGS;
    static Animation<TextureRegion> ANI_LAYERS;


    static final float SPLIT_VELOCITY = 1.5f;
    static final float SPLIT_FORCE = 0.8f;

    static final int WIDTH_P = 16;
    static final int HEIGHT_P = 14;
    static final int LAYER_HEIGHT_P = 2;
    static final int HEAD_HEIGHT_P  = 8;
    static final int LEG_HEIGHT_P   = 2;
    static final float WIDTH = WIDTH_P/16f;
    static final float HEIGHT = HEIGHT_P/16f;
    static final float LAYER_HEIGHT = LAYER_HEIGHT_P/16f;
    static final float HEAD_HEIGHT  = HEAD_HEIGHT_P/16f;
    static final float LEG_HEIGHT   = LEG_HEIGHT_P/16f;

    protected ArrayList<LType> layers = new ArrayList<>();
    protected boolean hasLegs = false;
    protected boolean hasHead = false;
    protected float age = 0f;

    public void render(float deltaTime, OrthogonalTiledMapRenderer renderer) {
        Batch batch = renderer.getBatch();
        batch.begin();
        TextureRegion frame;
        float yoff = 0f;

        if (hasLegs) {
            frame = ANI_LEGS.getKeyFrame(0);
            batch.draw(frame, hitbox.x, hitbox.y, WIDTH, LEG_HEIGHT);
            yoff = LEG_HEIGHT;
        }

        for (LType l : layers) {
            frame = ANI_LAYERS.getKeyFrame(0);
            batch.draw(frame, hitbox.x, hitbox.y+yoff, WIDTH, LAYER_HEIGHT);
            yoff += LAYER_HEIGHT;
        }

        if (hasHead) {
            frame = ANI_HEAD.getKeyFrame(0);
            batch.draw(frame, hitbox.x, hitbox.y+yoff, WIDTH, HEAD_HEIGHT);
        }

        batch.end();
    }

    @Override
    public void update(float deltaTime, TiledMap map, ArrayList<Entity> entities) {
        age += deltaTime;
        super.update(deltaTime, map, entities);
    }

    @Override
    public void move_with_collisions(float deltaTime, TiledMap map, ArrayList<Entity> entities) {
        Vector2 vel_scl = velocity.cpy();
        vel_scl.scl(deltaTime);

        // Just variables
        Array<Rectangle> tiles = new Array<>();
        int startX, startY, endX, endY;
        ArrayList<Boolean> xcollide_overlap = new ArrayList<>(
            Collections.nCopies(
                2 + layers.size(),
                false
            )
        );
        boolean hasXCollide = false;
        boolean canSplit = (size() > 2) && (Math.abs(velocity.x) > SPLIT_VELOCITY && age > 0.5f);

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
                    hitbox.x = max(hitbox.x, tile.x + tile.width);
                }
                else {
                    hitbox.x = min(hitbox.x, tile.x - hitbox.width);
                }

                hasXCollide = true;
                if (!canSplit) continue;

                float collide_top = getSplitLocation(tile.y + tile.height);
                float collide_bot = getSplitLocation(tile.y);

                int start_index = Math.min(
                    xcollide_overlap.size()-1,
                    (int)Math.ceil(collide_top+1)
                );
                int stop_index = Math.max(
                    0,
                    (int)Math.ceil(collide_bot+1)
                );

                for (int i = start_index; i >= stop_index; --i) {
                    xcollide_overlap.set(i, true);
                }
            }
        }

        if (hasXCollide) {
            if (!canSplit) {
                velocity.x = 0;
                return;
            }
            int splitAt = 0;
            int xcolLast = xcollide_overlap.size() - 1;
            boolean stopUpper = xcollide_overlap.get(xcolLast);

            int i;
            for (i = xcolLast-1; i > 0; --i) {
                if (xcollide_overlap.get(i) != stopUpper) {
                    System.out.println(i);
                    splitAt = i;
                    i=xcolLast;
                    break;
                }
            }

            // If never found a split location, don't split
            if (i != xcolLast) {
                velocity.x = 0;
                return;
            }

            LasagnaStack lower = null;
            if (stopUpper) {
                lower = splitAt(splitAt-1);
                velocity.x = 0;
            }
            else {
                velocity.x = 0;
                lower = splitAt(splitAt-1);
            }

            if (lower != null) {
                velocity.y += SPLIT_FORCE;
                lower.velocity.y -= SPLIT_FORCE;
                entities.add(lower);
                age = 0;
            }
        }
    }

    // Updates the hitbox height based on hasHead/hasLegs/layers
    protected void updateHitbox() {
        hitbox.height = (0f
            + LAYER_HEIGHT * layers.size()
            + (hasHead ? HEAD_HEIGHT : 0f)
            + (hasLegs ? LEG_HEIGHT : 0f)
        );
    }

    // Initializes textures and related constants
    public static void init() {
        TEXTURE = new Texture("lasagna_single.png");
        REGIONS = TextureRegion.split(TEXTURE, 16, 16)[0];

        HEAD_REGIONS = new TextureRegion[REGIONS.length];
        LEGS_REGIONS = new TextureRegion[REGIONS.length];
        LAYER_REGIONS = new TextureRegion[REGIONS.length];

        for (int i = 0; i < REGIONS.length; ++i) {
            HEAD_REGIONS[i] = new TextureRegion(REGIONS[i],
                0, 2,
                WIDTH_P, HEAD_HEIGHT_P);
            LEGS_REGIONS[i] = new TextureRegion(REGIONS[i],
                0, 2+HEIGHT_P-LEG_HEIGHT_P,
                WIDTH_P, LEG_HEIGHT_P);
            LAYER_REGIONS[i] = new TextureRegion(REGIONS[i],
                0, 2+HEIGHT_P-LEG_HEIGHT_P-2,
                WIDTH_P, LAYER_HEIGHT_P);
        }

        ANI_HEAD = new Animation<>(0, HEAD_REGIONS);
        ANI_LEGS = new Animation<>(0, LEGS_REGIONS);
        ANI_LAYERS = new Animation<>(0, LAYER_REGIONS);
    }

    public void addLayer(LasagnaStack.LType l) {
        layers.add(0, l);
        updateHitbox();
    }

    // Returns the Lasagna stack containing legs
    // li is the index of the lowest layer kept by the upper section
    protected LasagnaStack splitAt(int li) {
        if (size() < 3) return null;
        LasagnaStack bottom;

        int old_size = size();

        // Miss (below legs)
        if (li < -1) {
            return null;
        }
        // Lose legs-only
        else if (li < 0) {
            if (!hasLegs) return null; // No legs to lose

            bottom = new LasagnaStack(
                hitbox.x, hitbox.y,
                false, true
            );
        }
        // Lose some layers
        else if (li < layers.size()) {
            bottom = new LasagnaStack(
                hitbox.x, hitbox.y,
                false, hasLegs,
                layers.subList(0,li)
            );
            layers.subList(0,li).clear();
        }
        // Lose all layers (keep only head)
        else if (li == layers.size()) {
            if (!hasHead) return null; // No head to keep

            bottom = new LasagnaStack(
                hitbox.x, hitbox.y,
                false, hasLegs,
                layers
            );
            layers.clear();
        }
        // Miss (above head)
        else {
            return null;
        }

        hasLegs = false;
        hitbox.y += bottom.hitbox.height;
        updateHitbox();
        bottom.velocity.set(velocity);

        return bottom;
    }

    // Maps height to an "index" for the layer list:
        // <-1: Miss (below)
        // [-1,0): Hits legs or below
        // [x,y): Hits layer x
        // [layers.size(), layers.size()+1): Hits head or above
        // >layers.size()+1: Miss (above)
    public float getSplitLocation(float height) {
        height -= hitbox.y;

        if (hasLegs) {
            height -= LEG_HEIGHT;
        }

        // If hit legs
        if (height < 0) {
            if (!hasLegs) return -2f; // No legs to hit
            height /= LEG_HEIGHT;
            return height;
        }

        // Scale to layers: [0,1):L0, [1,2):L1...
        height /= LAYER_HEIGHT;

        // If head or above
        if (height > layers.size()) {
            if (!hasHead) return layers.size()+2; // No head to hit
            height = (
                (height - layers.size()) * (HEAD_HEIGHT / LAYER_HEIGHT)
                + layers.size()
            );
        }

        // If missed head
        if (height >= layers.size() + 1) {
            return layers.size()+2;
        }
        return height;
    }

    protected void growLegs() {
        if (layers.size() <= 0 || hasLegs) return;
        layers.remove(0);
        hasLegs = true;
        updateHitbox();
    }

    protected void growHead() {
        if (layers.size() <= 0 || hasHead) return;
        layers.remove(layers.size()-1);
        hasHead = true;
        updateHitbox();
    }

    public int size() { return layers.size() + (hasLegs?1:0) + (hasHead?1:0); }

    public LasagnaStack(float x, float y) {
        velocity.set(0,0);
        hitbox.set(x, y, WIDTH, 0);
    }

    public LasagnaStack(float x, float y, boolean head, boolean legs) {
        this(x,y);

        hasHead = head;
        hasLegs = legs;
    }

    public LasagnaStack(float x, float y, boolean head, boolean legs, ArrayList<LType> lyrs) {
        this(x,y,head,legs);

        layers = new ArrayList<>(lyrs);
        updateHitbox();
    }

    public LasagnaStack(float x, float y, boolean head, boolean legs, List<LType> lyrs) {
        this(x,y,head,legs);

        layers = new ArrayList<>(lyrs);
        updateHitbox();
    }
}
