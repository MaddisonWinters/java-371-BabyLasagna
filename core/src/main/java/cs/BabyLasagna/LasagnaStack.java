package cs.BabyLasagna;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;

public class LasagnaStack extends Entity {
    enum LType {
        Cheese,
        Meat
    }

    // Textures etc.
    static Texture TEXTURE;
    static TextureRegion[] REGIONS;
    static TextureRegion[] HEAD_REGIONS;
    static TextureRegion[] LEGS_REGIONS;
    static TextureRegion[] LAYER_REGIONS;
    static Animation<TextureRegion> ANI_HEAD;
    static Animation<TextureRegion> ANI_LEGS;
    static Animation<TextureRegion> ANI_LAYERS;

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

    ArrayList<LType> layers = new ArrayList<>();
    boolean hasLegs = false;
    boolean hasHead = false;

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
        updateHitbox();
        super.update(deltaTime, map, entities);
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

    public LasagnaStack(float x, float y) {
        velocity.set(0,0);
        hitbox.set(x, y, WIDTH, 0);
    }

    public LasagnaStack(float x, float y, boolean head, boolean legs, int lcnt) {
        this(x,y);

        hasHead = head;
        hasLegs = legs;

        for (int i = 0; i < lcnt; ++i) {
            layers.add(LType.Cheese);
        }
    }
}
