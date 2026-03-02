package cs.BabyLasagna;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

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

    public static class Lasagna {
        public enum LasagnaRegion {
            Head  (0, 0,16, 7),
            Layer1(0, 7,16, 2), // Three different layers for variety
            Layer2(0, 9,16, 2),
            Layer3(0,11,16, 2),
            Legs  (0,13,16, 4),
            FULL  (0, 0,16,17);

            public final Region reg;
            LasagnaRegion(int x, int y, int w, int h) { reg = new Region(x,y,w,h); }

            public boolean isLayer() { return this == Layer1 || this == Layer2 || this == Layer3; }
        }

        public enum LasagnaFlavor {
            Plain("BabyLasagna/Plain.png");

            public final String file;
            private final TextureRegion[] textures;
            LasagnaFlavor(String filepath) {
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

            public final TextureRegion getTex(LasagnaRegion reg) { return this.textures[reg.ordinal()]; }
        }
    }
}
