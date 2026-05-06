package cs.BabyLasagna.GameObj;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;

import cs.BabyLasagna.GameObj.UIHandler.KeyStatus;

// Singleton class for handling player-UI
public class UIHandler {
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

    public static class KeyStatus {
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
    public KeyStatus jump = new KeyStatus(new int[]{Input.Keys.SPACE, Input.Keys.UP, Input.Keys.W});

    // DEBUG
    public KeyStatus addTop = new KeyStatus(Input.Keys.O);
    public KeyStatus addBot = new KeyStatus(Input.Keys.P);
    public KeyStatus popTop = new KeyStatus(Input.Keys.L);
    public KeyStatus popBot = new KeyStatus(Input.Keys.SEMICOLON);
    public KeyStatus useAbility = new KeyStatus(Input.Keys.Q);

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

        right = Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D);
        left = Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A);
        up = Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.SPACE);
        down = Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S);

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
        jump.update();

        // Update keypresses
        addTop.update();
        addBot.update();
        popTop.update();
        popBot.update();
        useAbility.update();
    }
}
