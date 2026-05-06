package cs.BabyLasagna.GameObj;

import cs.BabyLasagna.Game.GameInterface;
import cs.BabyLasagna.GameObj.Components.CoyoteTimeComponent;
import cs.BabyLasagna.GameObj.Components.FastFallingComponent;
import cs.BabyLasagna.GameObj.Components.JumpBufferComponent;
import cs.BabyLasagna.GameObj.Components.StateControllerComponent;
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
import cs.BabyLasagna.GameObj.Collectables.Collectable;
import cs.BabyLasagna.GameObj.CheeseBall;
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
    public final WinState winState = new WinState();

    private static final float JUMP_FORCE = 12f;
    private static final float MOVE_SPEED = 6f;

    private static final float DAMAGE_COOLDOWN = 0.8f;
    private float damageTimer = 0.0f;

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

        if (stack.isEmpty()) {
            kill();
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

        // Pasta
        // Check for pasta bounce AFTER collisions
        if (grounded && velocity.y <= 0) {
            Rectangle feet = new Rectangle(
                hitbox.x,
                hitbox.y - 0.05f,   // tiny area below player
                hitbox.width,
                0.1f
            );

            for (GameObj obj : gameInt.getObjects()) {
                if (obj instanceof Pasta) {
                    if (feet.overlaps(obj.getHitbox())) {

                        velocity.y = 14f;  // bounce
                        grounded = false;

                        break;
                    }
                }
            }
        }

        // Update damage timer
        if (damageTimer > 0) {
            damageTimer -= deltaTime;
            damageTimer = Math.max(damageTimer, 0.0f);
        }

        // Damage and win condition
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

    // Uses an ability using the top layer of the lasagna stack
    public void useAbilityTop() {
        LasagnaFlavor f = this.peekTop();
        switch (f) {
            case Pasta:
                if (throwPasta()) {
                    this.popTop();
                }
                break;
            case Cheese:
                throwCheese();
                this.popTop();
                break;
            case Meat:
                if (throwMeat()) {
                    this.popTop();
                }
                break;
            case Pepper:
                this.popTop();
                throwPepper();
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

    public void win() {
        if (this.getStateController().isInState(WinState.class))
            return; // already won

        stateController.changeState(winState);
        gameInt.end(true);
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

    private boolean throwPasta() {

        int tileX;
        if (facingRight) {
            tileX = (int)Math.floor(hitbox.x + hitbox.width);
        } else {
            tileX = (int)Math.floor(hitbox.x - 1);
        }

        int tileY = (int)Math.floor(hitbox.y);

        var layer = gameInt.getMap().getLayers().get("Wall");
        if (layer == null) return false;

        com.badlogic.gdx.maps.tiled.TiledMapTileLayer tileLayer =
            (com.badlogic.gdx.maps.tiled.TiledMapTileLayer) layer;

        // Blocked by tile
        if (tileLayer.getCell(tileX, tileY) != null) {
            return false;
        }

        float spawnX = tileX + 1f;
        float spawnY = tileY + 11/16f;

        // NOTE: thinner hitbox
        Rectangle box = new Rectangle(spawnX, spawnY, 1f, 0.3f);

        for (GameObj obj : gameInt.getObjects()) {
            if (obj.isSolid() && obj.getHitbox().overlaps(box)) {
                return false;
            }
        }

        gameInt.addObject(new Pasta(gameInt, spawnX, spawnY));
        return true;
    }

    private void throwPepper() {
        gameInt.getObjects().add(
            new Pepper(
                gameInt,
                this.getCenterX(),
                this.getCenterY(),
                this.velocity.x,
                this.velocity.y,
                this.facingRight
            )
        );
    }

    // Ending the player ends the game. Distinct from kill() because there can be a post-death animation. 
    public void end(boolean success) { gameInt.end(success); }
    public void restart() { gameInt.restart(); }
}
