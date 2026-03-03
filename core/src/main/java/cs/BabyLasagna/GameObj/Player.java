package cs.BabyLasagna.GameObj;

import com.badlogic.gdx.maps.tiled.TiledMap;
import cs.BabyLasagna.TextureManager.Lasagna.*;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;


public class Player extends LasagnaStack {

    private CoyoteTimeComponent coyoteTime;

    private static final float JUMP_FORCE = 12f;

    private static final UIHandler uidata = UIHandler.getUI();

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

        super.update(deltaTime, map);
    }

    public Player(float x, float y) {
        super(x, y, true, true);
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
