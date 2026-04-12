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
import cs.BabyLasagna.GameObj.CheeseBall;


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

    @Override
    public void update(float deltaTime) {
        boolean cheeseStick = false;

        // Game object interactions
        Iterator<GameObj> oi = gameInt.getObjects().iterator();
        while(oi.hasNext()) {
            GameObj obj = oi.next();

            // Collectables
            if (obj instanceof Collectable) {
                Collectable col = (Collectable) obj;

                if (hitbox.overlaps(col.hitbox)) {
                    col.collect(this);
                    oi.remove();
                }
            }
            // Cheese
            else if (obj instanceof CheeseBall) {
                CheeseBall chb = (CheeseBall) obj;
                if (!chb.isSplatted()) continue; // Ignore projectile cheese balls

                if (hitbox.overlaps(chb.hitbox)) {
                    this.grounded = true;
                    cheeseStick = true;
                }
            }
        }

        uidata.update();

        // Use ability
        if (uidata.useAbility.press) {
            this.useAbilityTop();
        }

        // Makes sure the player doesn't stay in Idle
        stateController.update(deltaTime);

        // Set horizontal velocity
        velocity.x = uidata.getMoveXDir() * MOVE_SPEED;

        // Handle facing direction
        if (uidata.move_x == UIHandler.Ternary.Neg)
            facingRight = false;
        else if (uidata.move_x == UIHandler.Ternary.Pos)
            facingRight = true;

        
        // Update coyote timer
        coyoteTime.update(deltaTime, grounded);

        // Buffer jump button
        if (uidata.jump.press) {
            jumpBuffer.recordJumpPress();
        }
        jumpBuffer.update(deltaTime);

        // Jump (only if grounded/on ground)        
        if (jumpBuffer.hasBufferedJump() && (grounded || coyoteTime.canJump())) {
            velocity.y = JUMP_FORCE;
            coyoteTime.consume(); // prevent double jump
            jumpBuffer.consume();
            PlayerSnd.jump();
        }

        // Apply fast fall
        velocity.y = fastFall.apply(
            velocity.y,
            GRAVITY,
            deltaTime,
            grounded,
            uidata.move_y == UIHandler.Ternary.Neg
        );

        if (cheeseStick && velocity.y < 0)
            velocity.y = Math.max(-CheeseBall.STICKY_VEL, velocity.y);

        // Movement with collisions
        super.update(deltaTime);
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

    // Uses an ability using the top layer of the lasagna stack
    public void useAbilityTop() {
        LasagnaFlavor f = this.popTop();
        switch (f) {
            case Pasta:
                break;
            case Cheese:
                throwCheese();
                break;
            case Meat:
                break;
            case Pepper:
                break;
            default:
                System.err.print("Error: Unknown LasagnaFlavor: ");
                System.err.println(f);
                break;
        }
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

    private void throwCheese() {
        gameInt.getObjects().add(
            new CheeseBall(
                gameInt,
                this.getCenterX(),
                this.getCenterY(),
                this.velocity.x,
                this.velocity.y,
                this.facingRight
            )
        );
        this.addTop(LasagnaFlavor.Cheese);
    }
}
