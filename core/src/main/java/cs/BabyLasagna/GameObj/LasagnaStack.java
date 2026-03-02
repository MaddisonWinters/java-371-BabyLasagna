package cs.BabyLasagna.GameObj;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import cs.BabyLasagna.Game;
import cs.BabyLasagna.TextureManager.Lasagna.*;
import cs.BabyLasagna.TextureManager;

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
    boolean facingRight = false;

    @Override
    public void render(float deltaTime, SpriteBatch batch) {
        float yoff = 0f;

        // Draw legs
        if (hasLegs) {
            TextureManager.draw(
                batch,
                // Take the flavor of the bottom layer
                peekBottom().flavor.getTex(LasagnaRegion.Legs),
                hitbox.x,
                hitbox.y,
                LasagnaRegion.Legs.reg.gw,
                LasagnaRegion.Legs.reg.gh,
                !facingRight,
                false
            );
            // Update y offset
            yoff += LasagnaRegion.Legs.reg.gh;
        }

        // Draw each layer
        for (final Layer layer : stack) {
            TextureManager.draw(
                batch,
                layer.flavor.getTex(layer.region),
                hitbox.x,
                hitbox.y + yoff,
                layer.region.reg.gw,
                layer.region.reg.gh,
                !facingRight,
                false
            );
            // Update y offset
            yoff += layer.region.reg.gh;
        }

        // Draw head
        if (hasHead) {
            TextureManager.draw(
                batch,
                // Take flavor of top layer
                peekBottom().flavor.getTex(LasagnaRegion.Head),
                hitbox.x,
                hitbox.y + yoff,
                LasagnaRegion.Head.reg.gw,
                LasagnaRegion.Head.reg.gh,
                !facingRight,
                false
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
