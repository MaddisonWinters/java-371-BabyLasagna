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
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;


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
        uidata.update();

        // === Manual reset for testing ===
        boolean reset = Gdx.input.isKeyPressed(Keys.R);
        if (reset) {
            kill();  // triggers DeathState
        }

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

        // Movement with collisions
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

    // Uses an ability using the top layer of the lasagna stack
    public void useAbilityTop() {
        LasagnaFlavor f = this.peekTop();
        switch (f) {
            case Pasta:
                this.popTop();
                break;
            case Cheese:
                this.popTop();
                break;
            case Meat:
                if (throwMeat()) {
                    this.popTop();
                }
                break;
            case Pepper:
                this.popTop();
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
    private boolean throwMeat() {

        int tileX;
        if (facingRight) {
            tileX = (int)Math.floor(hitbox.x + hitbox.width + 1);
        } else {
            tileX = (int)Math.floor(hitbox.x - 1);
        }
        int tileY = (int)Math.floor(hitbox.y);

        // get tile layer
        var layer = gameInt.getMap().getLayers().get("Wall");
        if (layer == null) return false;

        com.badlogic.gdx.maps.tiled.TiledMapTileLayer tileLayer = (com.badlogic.gdx.maps.tiled.TiledMapTileLayer) layer;

        // if the tile map is there meat block does not get thrown or popped
        if (tileLayer.getCell(tileX, tileY) != null) {
            return false;
        }

        float spawnX = tileX;
        float spawnY = tileY;

        Rectangle box = new Rectangle(spawnX, spawnY, 1, 1);

        for (GameObj obj : gameInt.getObjects()) {
            if (obj.isSolid() && obj.getHitbox().overlaps(box)) {
                return false;
            }
        }

        gameInt.addObject(new Meat(gameInt, spawnX, spawnY));
        return true;
    }
}
