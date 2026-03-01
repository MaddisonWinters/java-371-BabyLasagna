package cs.BabyLasagna.GameObj;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.utils.Array;
import cs.BabyLasagna.Game;
import cs.BabyLasagna.Levels.Util;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.Rectangle;
import org.w3c.dom.css.Rect;


public class Player extends GameObj {

    private CoyoteTimeComponent coyoteTime;

        ///  Constants
    private static final float  WIDTH=16/(float)(Game.PIXELS_PER_TILE),
                                HEIGHT=17/(float)(Game.PIXELS_PER_TILE);
    private static final float GRAVITY = -20f;
    private static final float JUMP_FORCE = 8f;

    private static final Texture texture;
    private static final UIHandler uidata = UIHandler.getUI();

    static {
        texture = new Texture("lasagna_single.png");
    }

    private boolean grounded = false;
    private boolean facingRight = true;

    @Override
    public void render(float deltaTime, SpriteBatch batch) {
        if(facingRight) {
            batch.draw(texture, hitbox.x, hitbox.y, hitbox.width, hitbox.height);
        }
        else {
            batch.draw(texture, hitbox.x + hitbox.width, hitbox.y, -hitbox.width, hitbox.height);
        }
    }

    @Override
    public void update(float deltaTime, TiledMap map) {
        uidata.update();

       velocity.x = uidata.getMoveXDir() * 6f;

        if (uidata.move_x == UIHandler.Ternary.Neg) {
            facingRight = false;
        }
        else if (uidata.move_x == UIHandler.Ternary.Pos) {
            facingRight = true;
        }

        // Jump (only if grounded/on ground)
        if (uidata.jump_pressed && grounded) {
            velocity.y = JUMP_FORCE;
        }
        velocity.y += GRAVITY * deltaTime;

        hitbox.x += velocity.x * deltaTime;
        hitbox.y += velocity.y * deltaTime;

        grounded = false;

        Array<Rectangle> tile_rects = new Array<>();
        Util.getTiles(
            map,
            "Wall",
            tile_rects,
            (int)Math.floor(hitbox.x),
            (int)Math.floor(hitbox.y),
            (int)Math.ceil(hitbox.x),
            (int)Math.ceil(hitbox.y)
        );

        for (Rectangle rect : tile_rects) {
            resolveCollision(rect);
        }
    }

    public void resolveCollision(Rectangle tile) {
        if (!hitbox.overlaps(tile)) return;

        float dx = (hitbox.x + hitbox.width/2f) - (tile.x + tile.width/2f);
        float dy = (hitbox.y + hitbox.height/2f) - (tile.y + tile.height/2f);

        float combinedHalfWidths = (hitbox.width + tile.width) / 2f;
        float combinedHalfHeights = (hitbox.height + tile.height) / 2f;

        float overlapX = combinedHalfWidths - Math.abs(dx);
        float overlapY = combinedHalfHeights - Math.abs(dy);

        // Resolve the smaller overlap first
        if (overlapX < overlapY) {

            // Horizontal collision
            if (dx > 0)
                hitbox.x = tile.x + tile.width;
            else
                hitbox.x = tile.x - hitbox.width;

            velocity.x = 0;

        } else {

            // Vertical collision
            if (dy > 0) {

                if (velocity.y <= 0) {
                    hitbox.y = tile.y + tile.height;
                    grounded = true;
                    velocity.y = 0;
                }

            } else {
                if (velocity.y > 0) {
                    hitbox.y = tile.y - hitbox.height;
                    velocity.y = 0;
                }
            }

            velocity.y = 0;
        }
    }

    public Player(float x, float y) {
        super(x, y, WIDTH, HEIGHT);
        coyoteTime = new CoyoteTimeComponent(2f);
    }
}

// Singleton class for handling player-UI
class UIHandler {
    //  Singleton class shenanigans
    private static UIHandler INSTANCE = null;
    private UIHandler() {}
    public static UIHandler getUI() {
        if (INSTANCE == null) INSTANCE = new UIHandler();
        return INSTANCE;
    }

    // Store -1,0,1 with an enum, convert to float with .toFloat()
    public enum Ternary {
        Neg(-1f),
        Zero(0f),
        Pos(1f);

        final float i;
        Ternary(float i) { this.i = i; }
        public float toFloat() { return this.i; }
    }

    // Not bothering with getters/setters since this class has such a limited scope of use anyways
    public Ternary move_x = Ternary.Zero;
    public Ternary move_y = Ternary.Zero;
    public boolean jump_held = false;
    public boolean jump_pressed = false;

    public float getMoveXDir() { return move_x.toFloat(); }
    public float getMoveYDir() { return move_y.toFloat(); }
    public Vector2 getMoveVector() {
        Vector2 v = new Vector2(move_x.toFloat(), move_y.toFloat());
        v.nor();
        return v;
    }

    // Gets all relevant UI and condenses it
    public void update() {
        boolean right, left, up, down;

        right = Gdx.input.isKeyPressed(Keys.RIGHT) || Gdx.input.isKeyPressed(Keys.D);
        left = Gdx.input.isKeyPressed(Keys.LEFT) || Gdx.input.isKeyPressed(Keys.A);
        up = Gdx.input.isKeyPressed(Keys.UP) || Gdx.input.isKeyPressed(Keys.W) || Gdx.input.isKeyPressed(Keys.SPACE);
        down = Gdx.input.isKeyPressed(Keys.DOWN) || Gdx.input.isKeyPressed(Keys.S);

        // Set `move_x`
        if (right == left)
            move_x = Ternary.Zero;
        else if (right)
            move_x = Ternary.Pos;
        else
            move_x = Ternary.Neg;

        // Set `move_y`
        if (up == down)
            move_y = Ternary.Zero;
        else if (up)
            move_y = Ternary.Pos;
        else
            move_y = Ternary.Neg;

        // Set jump state
        jump_pressed = up && (!jump_held);
        jump_held = up;
    }
}
