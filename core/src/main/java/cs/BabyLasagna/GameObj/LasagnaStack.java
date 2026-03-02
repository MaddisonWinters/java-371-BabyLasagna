package cs.BabyLasagna.GameObj;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import cs.BabyLasagna.Game;
import cs.BabyLasagna.TextureManager.PlayerTex;
import cs.BabyLasagna.TextureManager.PlayerTex.Flavor;
import cs.BabyLasagna.TextureManager.PlayerTex.Region;

import java.util.ArrayDeque;

public class LasagnaStack extends GameObj {

    public static class Layer {
        public final Flavor flavor;
        public final Region region;

        private Layer(Flavor flav, Region reg) { flavor = flav; region = reg; }

        // Public "constructor" that returns null if invalid parameters are given
        public static Layer make(Flavor flav, Region reg) {
            if (reg.isLayer()) return new Layer(flav, reg);
            return null;
        }
    }

    protected ArrayDeque<Layer> stack = new ArrayDeque<>();
    boolean hasHead = false;
    boolean hasLegs = false;
    boolean facingRight = true;

    @Override
    public void render(float deltaTime, SpriteBatch batch) {
        renderFRight(deltaTime, batch);
    }

    public void renderFRight(float deltaTime, SpriteBatch batch) {
        float yoff = 0f;

        if (hasLegs) {
            batch.draw(
                PlayerTex.textures[peekBottom().flavor.ordinal()][Region.Legs.ordinal()],
                hitbox.x,
                hitbox.y,
                (float)Region.Legs.rect.w / Game.PIXELS_PER_TILE,
                (float)Region.Legs.rect.h / Game.PIXELS_PER_TILE
            );

            yoff += (float)Region.Legs.rect.h / Game.PIXELS_PER_TILE;
        }

        for (final Layer layer : stack) {
            batch.draw(
                PlayerTex.textures[layer.flavor.ordinal()][layer.region.ordinal()],
                hitbox.x,
                hitbox.y + yoff,
                (float)layer.region.rect.w / Game.PIXELS_PER_TILE,
                (float)layer.region.rect.h / Game.PIXELS_PER_TILE
            );

            yoff += (float)layer.region.rect.h / Game.PIXELS_PER_TILE;
        }

        if (hasHead) {
            batch.draw(
                PlayerTex.textures[peekBottom().flavor.ordinal()][Region.Head.ordinal()],
                hitbox.x,
                hitbox.y + yoff,
                (float)Region.Head.rect.w / Game.PIXELS_PER_TILE,
                (float)Region.Head.rect.h / Game.PIXELS_PER_TILE
            );
        }


    }

    @Override
    public void update(float deltaTime, TiledMap map) {
        velocity.y += GRAVITY * deltaTime;
        moveWithCollisions(deltaTime, map);
    }

    public    void  addTop(Layer layer)    { stack.push(layer); }
    public    void  addBottom(Layer layer) { stack.addLast(layer); }
    protected Layer popTop()     { return stack.pop(); }
    protected Layer popBottom()  { return stack.removeLast(); }
    public    Layer peekTop()    { return stack.peekFirst(); }
    public    Layer peekBottom() { return stack.peekLast(); }

    public LasagnaStack(float x, float y, boolean head, boolean legs) {
        super(x,y,1f,1f);
        hasHead = head;
        hasLegs = legs;
    }
}
