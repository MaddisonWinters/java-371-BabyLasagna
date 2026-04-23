package cs.BabyLasagna.GameObj;

import cs.BabyLasagna.Game.GameInterface;
import cs.BabyLasagna.GameObj.MyComponents.CoyoteTimeComponent;
import cs.BabyLasagna.GameObj.MyComponents.FastFallingComponent;
import cs.BabyLasagna.GameObj.MyComponents.JumpBufferComponent;
import cs.BabyLasagna.GameObj.MyComponents.StateControllerComponent;
import cs.BabyLasagna.GameObj.States.Player.*;
import cs.BabyLasagna.TextureManager.Lasagna.*;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.maps.MapProperties;

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
    public final WinState winState = new WinState();

    private static final float JUMP_FORCE = 12f;
    private static final float MOVE_SPEED = 6f;

    private static final float DAMAGE_COOLDOWN = 0.8f;
    private float damageTimer = 0.0f;

    private UIHandler uidata;

    private static boolean debug = true;

    @Override
    public void update(float deltaTime) {

        uidata.update();

        if (damageTimer > 0) {
            damageTimer -= deltaTime;
            damageTimer = Math.max(damageTimer, 0.0f);
        }

        if (stack.isEmpty()) {
            kill();
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

        // Win condition
        Array<MapProperties> tags = new Array<>();
        Array<Rectangle> tiles = new Array<>();
        getNearbyTags(tags, tiles, new Vector2(0,0));

        for (int i = 0; i < tags.size; ++i) {
            MapProperties props = tags.get(i);
            Rectangle tile = tiles.get(i);

            if (!tile.overlaps(hitbox)) continue;

            if (props.containsKey("goal")) {
                win();
            }

            if (props.containsKey("hurt") && damageTimer <= 0) {
                popBottom();
                damageTimer = DAMAGE_COOLDOWN;
            }
        }
    }

    public Player(GameInterface g, float x, float y) {
        super(g, x, y, true, true);

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

    public void win() {
        if (this.getStateController().isInState(WinState.class))
            return; // already won

        stateController.changeState(winState);
        gameInt.end(true);
    }

    // Ending the player ends the game. Distinct from kill() because there can be a post-death animation.
    public void end(boolean success) { gameInt.end(success); }
    public void restart() { gameInt.restart(); }
}
