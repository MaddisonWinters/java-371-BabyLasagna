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
    private FastFallingComponent fastFall;

        ///  Constants
    private static final float  DRAW_WIDTH =16/(float)(Game.PIXELS_PER_TILE),
                                DRAW_HEIGHT=17/(float)(Game.PIXELS_PER_TILE);
    private static final float  DRAW_X     =0f,
                                DRAW_Y     =0f;
    private static final float  HIT_WIDTH  =16/(float)(Game.PIXELS_PER_TILE),
                                HIT_HEIGHT =14/(float)(Game.PIXELS_PER_TILE);

    private static final float GRAVITY = -30f;
    private static final float JUMP_FORCE = 12f;

    private static final Texture texture;
    private static final UIHandler uidata = UIHandler.getUI();

    static {
        texture = new Texture("lasagna_single.png");
    }

    private boolean facingRight = true;

    @Override
    public void render(float deltaTime, SpriteBatch batch) {
        if(facingRight) {
            batch.draw(texture,
                hitbox.x + DRAW_X,
                hitbox.y + DRAW_Y,
                DRAW_WIDTH,
                DRAW_HEIGHT);
        }
        else {
            batch.draw(texture,
                hitbox.x + DRAW_X + hitbox.width,
                hitbox.y + DRAW_Y,
                -DRAW_WIDTH,
                DRAW_HEIGHT
            );
        }
    }

    @Override
    public void update(float deltaTime, TiledMap map) {
        uidata.update();

        // Update coyote timer
        coyoteTime.update(deltaTime, grounded);

        velocity.x = uidata.getMoveXDir() * 6f;

        if (uidata.move_x == UIHandler.Ternary.Neg) {
            facingRight = false;
        }
        else if (uidata.move_x == UIHandler.Ternary.Pos) {
            facingRight = true;
        }

        // Jump (only if grounded/on ground)
        if (uidata.jump_pressed && (grounded || coyoteTime.canJump())) {
            velocity.y = JUMP_FORCE;
            coyoteTime.consume(); // prevent double jump
        }

        velocity.y = fastFall.apply(
            velocity.y,
            GRAVITY,
            deltaTime,
            grounded,
            uidata.move_y == UIHandler.Ternary.Neg
        );

        // Move and collide with tilemap
        moveWithCollisions(deltaTime, map);
    }

    public Player(float x, float y) {
        super(x, y, HIT_WIDTH, HIT_HEIGHT);

        coyoteTime = new CoyoteTimeComponent(0.12f);
        fastFall = new FastFallingComponent(2.0f);
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
