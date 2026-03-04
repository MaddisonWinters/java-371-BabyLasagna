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

    private static boolean debug = true;

    @Override
    public void update(float deltaTime, TiledMap map) {
        uidata.update();

        if (debug) {
            if (uidata.addTop.press) addTop(LasagnaFlavor.Plain);
            if (uidata.addBot.press) addBottom(LasagnaFlavor.Plain);
            if (uidata.popTop.press) popTop();
            if (uidata.popBot.press) popBottom();
        }

        velocity.x = uidata.getMoveXDir() * 6f;

        if (uidata.move_x == UIHandler.Ternary.Neg) {
            facingRight = false;
        }
        else if (uidata.move_x == UIHandler.Ternary.Pos) {
            facingRight = true;
        }

        // Jump (only if grounded/on ground)
        if (uidata.jump.keyDown && grounded) {
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

    public class KeyStatus {
        public boolean keyDown = false; // The current state of the key (down/up)
        public boolean press = false; // If the key was just pressed
        public boolean release = false; // If the key was just released

        private final int[] keys;

        public KeyStatus(int key) { keys = new int[1]; keys[0] = key; }
        public KeyStatus(int[] keys_) { keys = keys_.clone(); }
        
        public void update() {
            boolean newKeyDown = false;
            for (int k : keys) {
                newKeyDown |= Gdx.input.isKeyPressed(k);
                if (newKeyDown) break;
            }

            if (keyDown == newKeyDown) {
                press = false;
                release = false;
                return;
            }
            keyDown = newKeyDown;
            press = newKeyDown;
            release = !newKeyDown;
        }
    }

    // Not bothering with getters/setters since this class has such a limited scope of use anyways
    public Ternary move_x = Ternary.Zero;
    public Ternary move_y = Ternary.Zero;
    public KeyStatus jump = new KeyStatus(new int[]{Keys.SPACE, Keys.UP, Keys.W});
    public KeyStatus addTop = new KeyStatus(Keys.O);
    public KeyStatus addBot = new KeyStatus(Keys.P);
    public KeyStatus popTop = new KeyStatus(Keys.L);
    public KeyStatus popBot = new KeyStatus(Keys.SEMICOLON);

    public float getMoveXDir() { return move_x.toFloat(); }
    public float getMoveYDir() { return move_y.toFloat(); }
    public Vector2 getMoveVector() {
        Vector2 v = new Vector2(move_x.toFloat(), move_y.toFloat());
        v.nor();
        return v;
    }

    // Gets all relevant UI and condenses it
    public void update() {
        boolean right, left;

        right = Gdx.input.isKeyPressed(Keys.RIGHT) || Gdx.input.isKeyPressed(Keys.D);
        left = Gdx.input.isKeyPressed(Keys.LEFT) || Gdx.input.isKeyPressed(Keys.A);

        // Set `move_x`
        if (right == left)
            move_x = Ternary.Zero;
        else if (right)
            move_x = Ternary.Pos;
        else
            move_x = Ternary.Neg;

        // Set jump state
        jump.update();
        addTop.update();
        addBot.update();
        popTop.update();
        popBot.update();
    }
}
