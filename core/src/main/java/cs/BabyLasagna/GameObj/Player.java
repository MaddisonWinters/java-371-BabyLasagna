package cs.BabyLasagna.GameObj;

import cs.BabyLasagna.Game.GameInterface;
import cs.BabyLasagna.GameObj.MyComponents.CoyoteTimeComponent;
import cs.BabyLasagna.GameObj.MyComponents.FastFallingComponent;
import cs.BabyLasagna.GameObj.MyComponents.JumpBufferComponent;
import cs.BabyLasagna.GameObj.MyComponents.StateControllerComponent;
import cs.BabyLasagna.GameObj.States.Player.*;
import cs.BabyLasagna.TextureManager.Lasagna.*;

import com.badlogic.gdx.math.Vector2;

import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import cs.BabyLasagna.SoundManager.GameSnd.PlayerSnd;
import cs.BabyLasagna.GameObj.UIHandler;
import cs.BabyLasagna.GameObj.Collectables.Collectable;


public class Player extends LasagnaStack {

    private CoyoteTimeComponent coyoteTime;
    private FastFallingComponent fastFall;
    private JumpBufferComponent jumpBuffer;
    private StateControllerComponent<Player> stateController;

    public final IdleState idleState = new IdleState();
    public final RunState runState = new RunState();
    public final FallState fallState = new FallState();
    public final DeathState deathState = new DeathState();

    private static final float JUMP_FORCE = 12f;
    private static final float MOVE_SPEED = 6f;
    private final Vector2 spawnPosition;

    private UIHandler uidata;

    private static boolean debug = true;

    @Override
    public void update(float deltaTime) {

        uidata.update();

        // === Manual reset for testing ===
        boolean reset = Gdx.input.isKeyPressed(Keys.X);
        if (reset) {
            kill();  // triggers DeathState
        }

        // Makes sure the player doesn't stay in Idle
        stateController.update(deltaTime);

        // Update coyote timer
        coyoteTime.update(deltaTime, grounded);

        velocity.x = uidata.getMoveXDir() * MOVE_SPEED;

        if (uidata.jump.press) {
            jumpBuffer.recordJumpPress();
        }

        jumpBuffer.update(deltaTime);

        if (debug) {
            if (uidata.addTop.press) { addTop(LasagnaFlavor.Pasta); PlayerSnd.grow(); }
            if (uidata.addBot.press) { addBottom(LasagnaFlavor.Pasta); PlayerSnd.grow(); }
            if (uidata.popTop.press) { popTop(); PlayerSnd.shrink(); }
            if (uidata.popBot.press) { popBottom(); PlayerSnd.shrink(); }
        }

        if (uidata.move_x == UIHandler.Ternary.Neg) {
            facingRight = false;
        }
        else if (uidata.move_x == UIHandler.Ternary.Pos) {
            facingRight = true;
        }

        // Jump (only if grounded/on ground)
        if (jumpBuffer.hasBufferedJump() && (grounded || coyoteTime.canJump())) {
            velocity.y = JUMP_FORCE;
            coyoteTime.consume(); // prevent double jump
            jumpBuffer.consume();
            PlayerSnd.jump();
        }

        velocity.y = fastFall.apply(
            velocity.y,
            GRAVITY,
            deltaTime,
            grounded,
            uidata.move_y == UIHandler.Ternary.Neg
        );

        super.update(deltaTime);

        // Collectables
        Iterator<GameObj> oi = gameInt.getObjects().iterator();
        while(oi.hasNext()) {
            GameObj obj = oi.next();

            if (!(obj instanceof Collectable)) continue;
            Collectable col = (Collectable) obj;
            
            if (hitbox.overlaps(obj.hitbox)) {
                col.collect(this);
                oi.remove();
            }
        }
    }
    
    public Player(GameInterface g, float x, float y) {
        super(g, x, y, true, true);

        // Save spawn point for respawn
        spawnPosition = new Vector2(x, y);

        uidata = UIHandler.getUI();
        stateController = new StateControllerComponent<>(this, idleState);
        coyoteTime = new CoyoteTimeComponent(0.12f);
        fastFall = new FastFallingComponent(2.0f);
        jumpBuffer = new JumpBufferComponent(0.12f);
    }

    public UIHandler getUIData() { return uidata; }
    public float getMoveSpeed() { return MOVE_SPEED; }
    public StateControllerComponent<Player> getStateController() {
        return stateController;
    }

    public void kill() {

        if (this.getStateController().isInState(DeathState.class))
            return; // already dead

        stateController.changeState(deathState);

    }

    public void respawn() {
        // Reset position
        setPosition(spawnPosition);

        // Reset velocity
        getVelocity().x = 0;
        getVelocity().y = 0;

        // Reset jump state
        coyoteTime.consume();
        jumpBuffer.consume();

        // Reset facing direction if desired
        facingRight = true;
    }
}


//// Singleton class for handling player-UI
//class UIHandler {
//    //  Singleton class shenanigans
//    private static UIHandler INSTANCE = null;
//    private UIHandler() {}
//    public static UIHandler getUI() {
//        if (INSTANCE == null) INSTANCE = new UIHandler();
//        return INSTANCE;
//    }
//
//    // Store -1,0,1 with an enum, convert to float with .toFloat()
//    public enum Ternary {
//        Neg(-1f),
//        Zero(0f),
//        Pos(1f);
//
//        final float i;
//        Ternary(float i) { this.i = i; }
//        public float toFloat() { return this.i; }
//    }
//
//    public class KeyStatus {
//        public boolean keyDown = false; // The current state of the key (down/up)
//        public boolean press = false; // If the key was just pressed
//        public boolean release = false; // If the key was just released
//
//        private final int[] keys;
//
//        public KeyStatus(int key) { keys = new int[1]; keys[0] = key; }
//        public KeyStatus(int[] keys_) { keys = keys_.clone(); }
//
//        public void update() {
//            boolean newKeyDown = false;
//            for (int k : keys) {
//                newKeyDown |= Gdx.input.isKeyPressed(k);
//                if (newKeyDown) break;
//            }
//
//            if (keyDown == newKeyDown) {
//                press = false;
//                release = false;
//                return;
//            }
//            keyDown = newKeyDown;
//            press = newKeyDown;
//            release = !newKeyDown;
//        }
//    }
//
//    // Not bothering with getters/setters since this class has such a limited scope of use anyways
//    public Ternary move_x = Ternary.Zero;
//    public Ternary move_y = Ternary.Zero;
//    public KeyStatus jump = new KeyStatus(new int[]{Keys.SPACE, Keys.UP, Keys.W});
//    public KeyStatus addTop = new KeyStatus(Keys.O);
//    public KeyStatus addBot = new KeyStatus(Keys.P);
//    public KeyStatus popTop = new KeyStatus(Keys.L);
//    public KeyStatus popBot = new KeyStatus(Keys.SEMICOLON);
//
//    public float getMoveXDir() { return move_x.toFloat(); }
//    public float getMoveYDir() { return move_y.toFloat(); }
//    public Vector2 getMoveVector() {
//        Vector2 v = new Vector2(move_x.toFloat(), move_y.toFloat());
//        v.nor();
//        return v;
//    }
//
//    // Gets all relevant UI and condenses it
//    public void update() {
//        boolean right, left, up, down;
//
//        right = Gdx.input.isKeyPressed(Keys.RIGHT) || Gdx.input.isKeyPressed(Keys.D);
//        left = Gdx.input.isKeyPressed(Keys.LEFT) || Gdx.input.isKeyPressed(Keys.A);
//        up = Gdx.input.isKeyPressed(Keys.UP) || Gdx.input.isKeyPressed(Keys.W) || Gdx.input.isKeyPressed(Keys.SPACE);
//        down = Gdx.input.isKeyPressed(Keys.DOWN) || Gdx.input.isKeyPressed(Keys.S);
//
//        // Set `move_x`
//        if (right == left)
//            move_x = Ternary.Zero;
//        else if (right)
//            move_x = Ternary.Pos;
//        else
//            move_x = Ternary.Neg;
//
//        // Set `move_y`
//        if (up == down)
//            move_y = Ternary.Zero;
//        else if (up)
//            move_y = Ternary.Pos;
//        else
//            move_y = Ternary.Neg;
//
//        // Set jump state
//        jump.update();
//        addTop.update();
//        addBot.update();
//        popTop.update();
//        popBot.update();
//    }
//}
