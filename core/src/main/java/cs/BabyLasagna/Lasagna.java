package cs.BabyLasagna;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class Lasagna {
    enum Facing {
        Left, Right
    }

    // Textures etc.
    static Texture TEXTURE;
    static TextureRegion[] REGIONS;
    static Animation<TextureRegion> ANI_DEFAULT;

    // Constants
    static float LAYER_WIDTH, LAYER_HEIGHT;
    static float VELOCITY = 0.2f;
    static float DAMPING = 0.87f;

    final Vector2 position = new Vector2();
    final Vector2 velocity = new Vector2();
    Facing facing = Facing.Right;

    public static void init() {
        TEXTURE = new Texture("koalio.png");
        REGIONS = TextureRegion.split(TEXTURE, 18, 26)[0];
        LAYER_WIDTH = 1/16f * REGIONS[0].getRegionWidth();
        LAYER_HEIGHT = 1/16f * REGIONS[0].getRegionHeight();
        VELOCITY = 0.1f;
        ANI_DEFAULT = new Animation<>(0, REGIONS[0]);
    }

    public Lasagna(Vector2 start_pos) {
        position.set(start_pos);
        velocity.set(0,0);
    }
}
