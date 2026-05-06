package cs.BabyLasagna.GameObj;

import java.util.ArrayDeque;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

import cs.BabyLasagna.Game;
import cs.BabyLasagna.TextureManager;
import cs.BabyLasagna.Game.GameInterface;
import cs.BabyLasagna.TextureManager.Lasagna.LasagnaFlavor;
import cs.BabyLasagna.TextureManager.Lasagna.LasagnaRegion;
import cs.BabyLasagna.Levels.Util;
import cs.BabyLasagna.TextureManager.LegAnim;

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
    protected boolean hasHead = false;
    protected boolean hasLegs = false;
    protected boolean facingRight = false;

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
        float bob = 0f;

        // Draw legs
        if (hasLegs) {
            TextureRegion frame = LegAnim.walk.getFrame();
            if (frame == LegAnim.walk.legFrames[0] || frame == LegAnim.walk.legFrames[2])
                bob = 1f / Game.PIXELS_PER_TILE;

            // Animated legs when moving
            if (velocity.x != 0 && isGrounded()) LegAnim.walk.update(deltaTime);
            TextureManager.draw(
                batch,
                LegAnim.walk.getFrame(),
                hitbox.x,
                hitbox.y + bob,
                LasagnaRegion.Legs.reg.gw,
                LasagnaRegion.Legs.reg.gh,
                !facingRight,
                false
            );
            yoff += LasagnaRegion.Legs.reg.gh;
            // Bottom-most layer
            TextureManager.draw(
                batch,
                // Take the flavor of the bottom layer
                bot_flavor.getStackTex(LasagnaRegion.Bottom),
                hitbox.x,
                hitbox.y + yoff + bob,
                LasagnaRegion.Bottom.reg.gw,
                LasagnaRegion.Bottom.reg.gh,
                !facingRight,
                false
            );
            // Update y offset
            yoff += LasagnaRegion.Bottom.reg.gh;
        }

        // Draw each layer
        int i = 0;
        for (final Layer layer : stack) {
            if (++i == 1 && hasLegs) continue;
            if (hasHead && i == stack.size()) break;

            TextureManager.draw(
                batch,
                layer.flavor.getStackTex(layer.region),
                hitbox.x,
                hitbox.y + yoff + bob,
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
                top_flavor.getStackTex(LasagnaRegion.Head),
                hitbox.x,
                hitbox.y + yoff + bob,
                LasagnaRegion.Head.reg.gw,
                LasagnaRegion.Head.reg.gh,
                !facingRight,
                false
            );
        }
    }

    @Override
    public void update(float deltaTime) {
        if (stack.isEmpty()) return;
        velocity.y += GRAVITY * deltaTime;
        moveWithCollisions(deltaTime);
    }

    // Perform Y collisions to see if the lasagna stack has room to grow
    protected boolean checkFit(TiledMap map, boolean up) {
        Rectangle hb = new Rectangle(
            hitbox.x,
            hitbox.y - LasagnaRegion.Layer1.reg.gh,
            hitbox.width,
            hitbox.height + 2*LasagnaRegion.Layer1.reg.gh
        );

        Array<Rectangle> tile_rects = new Array<>();
        Util.getRect(
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
        if (!checkFit(gameInt.getMap(), false)) return false;
        stack.push(layer);
        setHitboxHeight();
        return true;
    }
    public boolean addBottom(LasagnaFlavor flavor) {
        return addBottom(Layer.make(flavor, LasagnaRegion.randLayer()));
    }

    public boolean addTop(Layer layer) {
        if (!checkFit(gameInt.getMap(), true)) return false;
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
        return stack.peekLast().flavor;
    }

    public LasagnaFlavor peekBottom() {
        if (stack.isEmpty()) return null;
        return stack.peekFirst().flavor;
    }

    protected void setHitboxHeight() {
        hitbox.height = 0;

        if (hasLegs) {
            hitbox.height += LasagnaRegion.Bottom.reg.gh + LasagnaRegion.Legs.reg.gh;
            hitbox.y += LasagnaRegion.Bottom.reg.gh;
        }
        else
            hitbox.height += LasagnaRegion.Layer1.reg.gh;

        if (hasHead)
            hitbox.height += LasagnaRegion.Head.reg.gh - HEAD_DECORATIVE_SIZE;
        else
            hitbox.height += LasagnaRegion.Layer1.reg.gh;

        hitbox.height += (stack.size()-2) * LasagnaRegion.Layer1.reg.gh;
    }

    public LasagnaStack(GameInterface g, float x, float y, boolean head, boolean legs) {
        super(g, x,y,1f,1f);
        hasHead = head;
        hasLegs = legs;
        setHitboxHeight();
    }
}
