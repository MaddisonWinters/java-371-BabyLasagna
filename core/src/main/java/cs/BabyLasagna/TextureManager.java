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
            Pasta("Pasta.png"),
            Cheese("Cheese.png"),
            Meat("Meat.png"),
            Pepper("Pepper.png");

            public final String stackFile;
            public final String ingredientFile;
            private final TextureRegion[] stackTextures;
            private final TextureRegion[] ingredientTextures;

            // Loads texture and creates all TextureRegions
            private LasagnaFlavor(String filename) {
                stackFile = "BabyLasagna/" + filename;
                ingredientFile = "Collectable/" + filename;

                Texture stackSheet = new Texture(this.stackFile);
                stackTextures = new TextureRegion[LasagnaRegion.values().length];
                for (final LasagnaRegion reg : LasagnaRegion.values()) {
                    stackTextures[reg.ordinal()] = new TextureRegion(
                        stackSheet,
                        reg.reg.tx,
                        reg.reg.ty,
                        reg.reg.tw,
                        reg.reg.th
                    );
                }

                Texture ingredientSheet = new Texture(this.ingredientFile);
                ingredientTextures = new TextureRegion[1];
                ingredientTextures[0] = new TextureRegion(ingredientSheet); // Later: animated spritesheet
            }

            // Returns the texture for the specified region of a lasagna stack
            public final TextureRegion getStackTex(LasagnaRegion reg) { return this.stackTextures[reg.ordinal()]; }

            public final TextureRegion getIngredientTex() { return ingredientTextures[0]; }
        }
    }

    public static class Abilities {
        public static class Cheese {
            private static final TextureRegion[] globTextures;
            private static final TextureRegion[] splatTextures; 

            private static final int GLOB_TEX_CNT = 1;
            private static final int SPLAT_TEX_CNT = 1;

            static {
                String globFile = "Collectable/Cheese.png";
                String splatFile = "Abilities/CheeseSplat.png";

                Texture globSheet = new Texture(globFile);
                Texture splatSheet = new Texture(splatFile);

                globTextures = new TextureRegion[GLOB_TEX_CNT];
                splatTextures = new TextureRegion[SPLAT_TEX_CNT];

                for (int i = 0; i < GLOB_TEX_CNT; ++i) {
                    globTextures[i] = new TextureRegion(
                        globSheet,
                        0, // Hardcoded for now
                        0, // ^
                        6,
                        16
                    );
                }

                for (int i = 0; i < SPLAT_TEX_CNT; ++i) {
                    splatTextures[i] = new TextureRegion(
                        splatSheet,
                        0, // Hardcoded for now
                        0, // ^
                        6,
                        16
                    );
                }
            }

            public static final TextureRegion getGlobTex() { return globTextures[0]; }
            public static final TextureRegion getSplatTex() { return splatTextures[0]; }
        }
    }
}
