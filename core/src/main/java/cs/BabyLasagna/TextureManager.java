package cs.BabyLasagna;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.Random;

public class TextureManager {

    public static class Region {
        public final int tx,ty,tw,th; // Texture region definition (i.e. which pixels on the texture)
        public final float gx,gy,gw,gh; // Game region definition (i.e. which coordinates in the world, mostly for height/width)
        Region(int px, int py, int pw, int ph) {
            tx=px; ty=py; tw=pw; th=ph;
            float scl = 1f / Game.PIXELS_PER_TILE;
            gx=scl*px; gy=scl*py; gw=scl*pw; gh=scl*ph;
        }
    }

    // Draws a texture, supports flipping the texture for convenience
    public static void draw(SpriteBatch batch, TextureRegion tex, float x, float y, float w, float h, boolean flipX, boolean flipY) {
        batch.draw(
            tex,
            (flipX
                ? x + w
                : x
            ),
            (flipY
                ? y + h
                : y
            ),
            (flipX
                ? -w
                : w
            ),
            (flipY
                ? -h
                : h
            )
        );
    }


    public static class Lasagna {
        public enum LasagnaRegion {
            Head  (0, 0,16, 8),
            Layer1(0, 8,16, 2), // Different layers for variety
            Layer2(0, 10,16, 2),
            Legs  (0,12,16, 5),
            FULL  (0, 0,16,17);

            private static final Random rand = new Random();
            public final Region reg;
            LasagnaRegion(int x, int y, int w, int h) { reg = new Region(x,y,w,h); }

            public boolean isLayer() { return this == Layer1 || this == Layer2; }
            public static LasagnaRegion randLayer() {
                // nextInt(2) returns either 0 or 1
                return rand.nextInt(2) == 0 ? Layer1 : Layer2;
            }
        }

        // Each LasagnaFlavor stores a list of texture regions corresponding to its flavor
        public enum LasagnaFlavor {
            Pasta("BabyLasagna/Pasta.png"),
            Cheese("BabyLasagna/Cheese.png"),
            Meat("BabyLasagna/Meat.png"),
            Pepper("BabyLasagna/Pepper.png");

            public final String file;
            private final TextureRegion[] textures;

            // Loads texture and creates all TextureRegions
            private LasagnaFlavor(String filepath) {
                file = filepath;

                Texture sheet = new Texture(this.file);
                textures = new TextureRegion[LasagnaRegion.values().length];
                for (final LasagnaRegion reg : LasagnaRegion.values()) {
                    textures[reg.ordinal()] = new TextureRegion(
                        sheet,
                        reg.reg.tx,
                        reg.reg.ty,
                        reg.reg.tw,
                        reg.reg.th
                    );
                }
            }

            // Returns the texture for the specified region
            public final TextureRegion getTex(LasagnaRegion reg) { return this.textures[reg.ordinal()]; }
        }
    }
}
