package cs.BabyLasagna.GameObj;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import cs.BabyLasagna.Game;
import cs.BabyLasagna.TextureManager.Lasagna.*;

import java.util.ArrayDeque;

public class LasagnaStack extends GameObj {

    public static class Layer {
        public final LasagnaFlavor flavor;
        public final LasagnaRegion region;

        private Layer(LasagnaFlavor flav, LasagnaRegion reg) { flavor = flav; region = reg; }

        // Public "constructor" that returns null if invalid parameters are given
        public static Layer make(LasagnaFlavor flav, LasagnaRegion reg) {
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
                peekBottom().flavor.getTex(LasagnaRegion.Legs),
                hitbox.x,
                hitbox.y,
                LasagnaRegion.Legs.reg.gw,
                LasagnaRegion.Legs.reg.gh
            );

            yoff += LasagnaRegion.Legs.reg.gh;
        }

        for (final Layer layer : stack) {
            batch.draw(
                layer.flavor.getTex(layer.region),
                hitbox.x,
                hitbox.y + yoff,
                layer.region.reg.gw,
                layer.region.reg.gh
            );

            yoff += layer.region.reg.gh;
        }

        if (hasHead) {
            batch.draw(
                peekBottom().flavor.getTex(LasagnaRegion.Head),
                hitbox.x,
                hitbox.y + yoff,
                LasagnaRegion.Head.reg.gw,
                LasagnaRegion.Head.reg.gh
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
