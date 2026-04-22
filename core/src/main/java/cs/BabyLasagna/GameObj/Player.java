package cs.BabyLasagna.GameObj;

import cs.BabyLasagna.Game.GameInterface;
import cs.BabyLasagna.GameObj.Components.CoyoteTimeComponent;
import cs.BabyLasagna.GameObj.Components.FastFallingComponent;
import cs.BabyLasagna.GameObj.Components.JumpBufferComponent;
import cs.BabyLasagna.GameObj.Components.StateControllerComponent;
import cs.BabyLasagna.GameObj.States.Player.*;
import cs.BabyLasagna.TextureManager.Lasagna.*;

import com.badlogic.gdx.math.Vector2;

import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import cs.BabyLasagna.SoundManager.GameSnd.PlayerSnd;
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
        boolean reset = Gdx.input.isKeyPressed(Keys.R);
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
