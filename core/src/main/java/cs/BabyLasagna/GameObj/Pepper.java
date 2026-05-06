package cs.BabyLasagna.GameObj;

import java.util.Iterator;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;

import cs.BabyLasagna.Game;
import cs.BabyLasagna.TextureManager;
import cs.BabyLasagna.Game.GameInterface;
import cs.BabyLasagna.Levels.Util;
import cs.BabyLasagna.TextureManager.Abilities.Cheese;
import cs.BabyLasagna.TextureManager.Abilities.PepperWheel;

import com.badlogic.gdx.utils.Array;

public class Pepper extends Projectile {
    public static final float PEPPER_SIZE = (float)PepperWheel.SIZE / Game.PIXELS_PER_TILE;
    public static final float EXPLOSION_SIZE = (float)PepperWheel.EXPLOSION_SIZE / Game.PIXELS_PER_TILE;

    public static final float INITIAL_X_OFF = 0.6f;
    public static final float INITIAL_VX = 8.0f;
    public static final float INITIAL_VY = 0.1f;
    public static final float SOURCE_VEL = 0.3f;

    public static final float EXPLOSION_RADIUS = 3.0f;

    private boolean hit = false;
    private float lifetime = 0.0f;

    public Pepper(GameInterface g, float x, float y, float vx, float vy, boolean facing_right) {
        super(g,x,y,vx,vy,PEPPER_SIZE,SOURCE_VEL,facing_right);

        velocity.x += (this.facing_right ? INITIAL_VX : -INITIAL_VX);
        velocity.y += INITIAL_VY;
        hitbox.x += (this.facing_right ? INITIAL_X_OFF : -INITIAL_X_OFF);
        hitbox.y += 0.1f;
    }

    @Override
    public void update(float deltaTime) {
        lifetime += deltaTime;

        if (hit) {
            if (lifetime < PepperWheel.EXPLOSION_DURATION) return;

            Iterator<GameObj> oi = gameInt.getObjects().iterator();
            while(oi.hasNext()) {
                if (oi.next() == this) {
                    setShouldRemoveSelf();
                    break;
                }
            }

            return;
        }

        super.update(deltaTime);
    }

    @Override
    public void hit(Rectangle alignTile) {
        hit = true;
        lifetime = 0.0f;

        float cx = hitbox.x + 0.5f*hitbox.width;
        float cy = hitbox.y + 0.5f*hitbox.height;

        float x0 = cx - EXPLOSION_RADIUS,
              x1 = cx + EXPLOSION_RADIUS,
              y0 = cy - EXPLOSION_RADIUS,
              y1 = cy + EXPLOSION_RADIUS;

        Array<Rectangle> rects = new Array<>();
        Array<MapProperties> props = new Array<>();
        TiledMap map = gameInt.getMap();

        Util.getTags(
            map,
            "Wall",
            props,
            rects,
            (int)Math.floor(x0), 
            (int)Math.floor(y0), 
            (int)Math.ceil(x1), 
            (int)Math.ceil(y1)
        );

        TiledMapTileLayer wallLayer = (TiledMapTileLayer)map.getLayers().get("Wall");
        for (int i = 0; i < rects.size; ++i) {
            final MapProperties prop = props.get(i);
            if (!prop.containsKey("explodable")) continue;

            final Rectangle rect = rects.get(i);
            float rcx = rect.x + 0.5f*rect.width;
            float rcy = rect.y + 0.5f*rect.height;

            float dist = (rcx-cx)*(rcx-cx) + (rcy-cy)*(rcy-cy);
            if (dist > EXPLOSION_RADIUS*EXPLOSION_RADIUS) continue;

            wallLayer.setCell((int)rect.x, (int)rect.y, null);
        }

        // Set explosion sprite hitbox
        hitbox.x = cx - (0.5f*EXPLOSION_SIZE);
        hitbox.y = cy - (0.5f*EXPLOSION_SIZE);
        hitbox.width = hitbox.height = EXPLOSION_SIZE;
    }

    @Override
    protected float bounciness() { return 0.3f; }

    @Override
    public void render(float deltaTime, SpriteBatch batch) {
        TextureRegion tex = null;
        if (hit) {
            tex = PepperWheel.getExplosionTex(lifetime);
        }
        else {
            tex = PepperWheel.getWheelTex(lifetime);
        }

        TextureManager.draw(
            batch,
            tex,
            hitbox.x,
            hitbox.y,
            hitbox.width,
            hitbox.height,
            facing_right,
            false
        );
    }
}
