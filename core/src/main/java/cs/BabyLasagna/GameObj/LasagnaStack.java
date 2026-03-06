package cs.BabyLasagna.GameObj;

import java.util.ArrayDeque;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

import cs.BabyLasagna.Game;
import cs.BabyLasagna.TextureManager;
import cs.BabyLasagna.TextureManager.Lasagna.LasagnaFlavor;
import cs.BabyLasagna.TextureManager.Lasagna.LasagnaRegion;
import cs.BabyLasagna.Levels.Util;

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
            if (++i == 1 && hasLegs) continue;
            if (hasHead && i == stack.size()) break;

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

    protected boolean checkFit(TiledMap map, boolean up) {
        Rectangle hb = new Rectangle(
            hitbox.x,
            hitbox.y - LasagnaRegion.Layer1.reg.gh,
            hitbox.width,
            hitbox.height + 2*LasagnaRegion.Layer1.reg.gh
        );

        Array<Rectangle> tile_rects = new Array<>();
        Util.getTiles(
            map, 
            "Wall",
            tile_rects,
            (int)Math.floor(hb.x),
            (int)Math.floor(hb.y),
            (int)Math.ceil(hb.x + hb.width),
            (int)Math.ceil(hb.y + hb.height)
        );

        for (final Rectangle tile : tile_rects) {
            if (!tile.overlaps(hb)) continue;

            float dy = (hb.y + hb.height*0.5f) - (tile.y + tile.height*0.5f);

            if (dy < 0) {
                hb.height = tile.y - hb.y;
            }
            else {
                float tileTop = tile.y + tile.height;
                hb.height = (hb.y + hb.height) - tileTop;
                hb.y = tileTop;
            }
        }
        // If can't fit, then return false
        hb.height -= hitbox.height + LasagnaRegion.Layer1.reg.gh;
        if (hb.height < 0) return false;
        
        // Can fit, so get correct position for new hitbox
        if (up) {
            hitbox.y = Math.min(hitbox.y, hb.y+hb.height);
        }
        else {
            hitbox.y -= LasagnaRegion.Layer1.reg.gh;
            hitbox.y = Math.max(hitbox.y, hb.y+hb.height);
        }

        return true;
    }

        /// Add/Remove layers
    public boolean addBottom(Layer layer) {
        if (!checkFit(map, false)) return false;
        stack.push(layer);
        setHitboxHeight();
        return true;
    }
    public boolean addBottom(LasagnaFlavor flavor) {
        return addBottom(Layer.make(flavor, LasagnaRegion.randLayer()));
    }
    public boolean addTop(Layer layer) {
        if (!checkFit(map, true)) return false;
        stack.addLast(layer);
        setHitboxHeight();
        return true;
    }
    public boolean addTop(LasagnaFlavor flavor) {
        return addTop(Layer.make(flavor, LasagnaRegion.randLayer()));
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

    public LasagnaStack(TiledMap map_, float x, float y, boolean head, boolean legs) {
        super(map_, x,y,1f,1f);
        hasHead = head;
        hasLegs = legs;
        setHitboxHeight();
    }
}
