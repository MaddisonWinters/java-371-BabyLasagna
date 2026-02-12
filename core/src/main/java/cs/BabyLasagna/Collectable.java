package cs.BabyLasagna;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.Collections;

public class Collectable extends Entity {
    public enum Type {
        Cheese,
        Sauce
    }

    // Textures etc.
    static Texture TEXTURE;
    static TextureRegion[] REGIONS;
    static Animation<TextureRegion> ANI_DEFAULT;
    final float SIZE = 1f;

    // Member values
    public Type type;

    public void render(float deltaTime, OrthogonalTiledMapRenderer renderer) {
        TextureRegion frame = ANI_DEFAULT.getKeyFrame(0);
        Batch batch = renderer.getBatch();
        batch.begin();
        batch.draw(frame, hitbox.x, hitbox.y, hitbox.width, hitbox.height);
        batch.end();
    }

    @Override
    public void kill() {
        super.kill();
        shouldDespawn = true;
    }

    // Initializes textures and related constants
    public static void init() {
        TEXTURE = new Texture("koalio.png");
        REGIONS = TextureRegion.split(TEXTURE, 16, 16)[0];
        ANI_DEFAULT = new Animation<>(0, REGIONS[0]);
    }

    public Collectable(Type col_type, float x, float y) {
        velocity.set(0,0);
        type = col_type;
        hitbox.set(x, y, SIZE, SIZE);
    }
}
