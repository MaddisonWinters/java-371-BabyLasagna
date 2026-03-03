package cs.BabyLasagna.GameObj;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import cs.BabyLasagna.Game;
import cs.BabyLasagna.TextureManager.Lasagna.*;
import cs.BabyLasagna.TextureManager;

import java.util.ArrayDeque;

public class LasagnaStack extends GameObj {

    public final float HEAD_DECORATIVE_SIZE = 3f / Game.PIXELS_PER_TILE;

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

        LasagnaFlavor bot_flavor, top_flavor;
        if (stack.isEmpty()) {
            bot_flavor = LasagnaFlavor.Plain;
            top_flavor = LasagnaFlavor.Plain;
        }
        else {
            bot_flavor = peekBottom();
            top_flavor = peekTop();
        }

        // Draw legs
        if (hasLegs) {
            TextureManager.draw(
                batch,
                // Take the flavor of the bottom layer
                top_flavor.getTex(LasagnaRegion.Legs),
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
                top_flavor.getTex(LasagnaRegion.Head),
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

        /// Add/Remove layers
    public void addTop(Layer layer) {
        stack.push(layer);
        setHitboxHeight();
    }
    public void addBottom(Layer layer) {
        stack.addLast(layer);
        setHitboxHeight();
        hitbox.y -= LasagnaRegion.Layer1.reg.gh;
    }

    public LasagnaFlavor popTop() {
        LasagnaFlavor tmp = stack.pop().flavor;
        setHitboxHeight();
        return tmp;
    }
    public LasagnaFlavor popBottom() {
        LasagnaFlavor tmp = stack.removeLast().flavor;
        setHitboxHeight();
        hitbox.y += LasagnaRegion.Layer1.reg.gh;
        return tmp;
    }

    public LasagnaFlavor peekTop()    {
        if (stack.isEmpty()) return null;
        return stack.peekFirst().flavor;
    }
    public LasagnaFlavor peekBottom() {
        if (stack.isEmpty()) return null;
        return stack.peekLast().flavor;
    }

    protected void setHitboxHeight() {
        hitbox.height = 0;
        if (hasLegs)
            hitbox.height += LasagnaRegion.Legs.reg.gh;
        if (hasHead)
            hitbox.height += LasagnaRegion.Head.reg.gh - HEAD_DECORATIVE_SIZE;
        hitbox.height += stack.size() * LasagnaRegion.Layer1.reg.gh;
    }

    public LasagnaStack(float x, float y, boolean head, boolean legs) {
        super(x,y,1f,1f);
        hasHead = head;
        hasLegs = legs;
        setHitboxHeight();
    }
}
