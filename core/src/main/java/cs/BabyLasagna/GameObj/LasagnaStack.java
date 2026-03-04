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
        if (stack.isEmpty()) return;
        if (stack.size() < 2) {
            stack.clear();
            return;
        }

        LasagnaFlavor bot_flavor = peekBottom();
        LasagnaFlavor top_flavor = peekTop();

        float yoff = 0f;

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
        int i = 0;
        for (final Layer layer : stack) {
            if (++i == 1) continue;
            if (i == stack.size()) break;

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
                bot_flavor.getTex(LasagnaRegion.Head),
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
        if (stack.isEmpty()) return;
        velocity.y += GRAVITY * deltaTime;
        moveWithCollisions(deltaTime, map);
    }

        /// Add/Remove layers
    public void addBottom(Layer layer) {
        stack.push(layer);
        setHitboxHeight();
        hitbox.y -= LasagnaRegion.Layer1.reg.gh;
    }
    public void addBottom(LasagnaFlavor flavor) {
        addBottom(Layer.make(flavor, LasagnaRegion.randLayer()));
    }
    public void addTop(Layer layer) {
        stack.addLast(layer);
        setHitboxHeight();
    }
    public void addTop(LasagnaFlavor flavor) {
        addTop(Layer.make(flavor, LasagnaRegion.randLayer()));
    }

    public LasagnaFlavor popBottom() {
        if (stack.isEmpty()) return null;
        LasagnaFlavor tmp = stack.pop().flavor;
        setHitboxHeight();
        hitbox.y += LasagnaRegion.Layer1.reg.gh;
        return tmp;
    }
    public LasagnaFlavor popTop() {
        if (stack.isEmpty()) return null;
        LasagnaFlavor tmp = stack.removeLast().flavor;
        setHitboxHeight();
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
        else
            hitbox.height += LasagnaRegion.Layer1.reg.gh;

        if (hasHead)
            hitbox.height += LasagnaRegion.Head.reg.gh - HEAD_DECORATIVE_SIZE;
        else
            hitbox.height += LasagnaRegion.Layer1.reg.gh;

        hitbox.height += (stack.size()-2) * LasagnaRegion.Layer1.reg.gh;
    }

    public LasagnaStack(float x, float y, boolean head, boolean legs) {
        super(x,y,1f,1f);
        hasHead = head;
        hasLegs = legs;
        setHitboxHeight();
    }
}
