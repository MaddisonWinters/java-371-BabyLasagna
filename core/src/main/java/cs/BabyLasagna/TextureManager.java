package cs.BabyLasagna;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class TextureManager {

    public static class RectInt {
        public final int x,y,w,h;
        RectInt(int x_, int y_, int w_, int h_) {
            x=x_; y=y_; w=w_; h=h_;
        }
    }

    public static class PlayerTex {
        public enum Region {
            Head  (0, 0,16, 7),
            Layer1(0, 7,16, 2), // Three different layers for variety
            Layer2(0, 9,16, 2),
            Layer3(0,11,16, 2),
            Legs  (0,13,16, 4),
            FULL  (0, 0,16,17);

            public final RectInt rect;
            Region(int x, int y, int w, int h) { rect = new RectInt(x,y,w,h); }
        }

        public enum Flavor {
            Plain("BabyLasagna/Plain.png");

            public final String file;
            Flavor(String filepath) { file = filepath; }
        }


        public static final TextureRegion[][] textures;

        private static void loadTex(Flavor flavor, TextureRegion[] regions) {
            Texture sheet = new Texture(flavor.file);

            for (final Region reg : Region.values()) {
                regions[reg.ordinal()] = new TextureRegion(
                    sheet,
                    reg.rect.x,
                    reg.rect.y,
                    reg.rect.w,
                    reg.rect.h
                );
            }
        }

        static {
            textures = new TextureRegion[Flavor.values().length][Region.values().length];

            for (final Flavor flavor : Flavor.values()) {
                loadTex(flavor, textures[flavor.ordinal()]);
            }
        }
    }
}
