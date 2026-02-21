package cs.BabyLasagna.GameObj;

import cs.BabyLasagna.Game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.Rectangle;


import javax.naming.ldap.ExtendedRequest;
import java.rmi.server.UID;

public class Player extends GameObj {
        ///  Constants
    private static final float  WIDTH=16/(float)(Game.PIXELS_PER_TILE),
                                HEIGHT=18/(float)(Game.PIXELS_PER_TILE);
    //new 2/21-----------------------------
    private static final float GRAVITY = -20f;
    private static final float JUMP_FORCE = 8f;

    private boolean grounded = false;
    //---------------------------------------
    private static final Texture texture;

    private static final UIHandler uidata = UIHandler.getUI();

    static {
        texture = new Texture("lasagna_single.png");
    }

    @Override
    public void render(float deltaTime, SpriteBatch batch) {
        batch.draw(texture, hitbox.x, hitbox.y, hitbox.width, hitbox.height);
    }

    @Override
    public void update(float deltaTime) {
        uidata.update();
        //new 2/21------------------------------

        velocity.x = uidata.getMoveXDir() * 6f;

        // Jump (only if grounded)
        if (uidata.jump_pressed && grounded) {
            velocity.y = JUMP_FORCE;
            grounded = false; // prevent double jump
        }

        velocity.y += GRAVITY * deltaTime;

        hitbox.x += velocity.x * deltaTime;

        hitbox.y += velocity.y * deltaTime;
        //------------------------------------
/*
        Vector2 vel_scl = new Vector2(velocity);
        vel_scl.scl(deltaTime);
        hitbox.x += vel_scl.x;
        hitbox.y += vel_scl.y;
 */
    }

    //new 2/21----------------------------------
    public void resolveCollision(Rectangle tile) {
        if (!hitbox.overlaps(tile)) return;

        float dx = (hitbox.x + hitbox.width/2f) - (tile.x + tile.width/2f);
        float dy = (hitbox.y + hitbox.height/2f) - (tile.y + tile.height/2f);

        float combinedHalfWidths = (hitbox.width + tile.width) / 2f;
        float combinedHalfHeights = (hitbox.height + tile.height) / 2f;

        float overlapX = combinedHalfWidths - Math.abs(dx);
        float overlapY = combinedHalfHeights - Math.abs(dy);

        if (overlapX < overlapY) {
            if (dx > 0)
                hitbox.x += overlapX;
            else
                hitbox.x -= overlapX;

            velocity.x = 0;
        } else {
            if (dy > 0) {
                hitbox.y += overlapY;
                grounded = true;
            } else {
                hitbox.y -= overlapY;
            }

            velocity.y = 0;
        }
    }
    //-----------------------------

    public Player(float x, float y) {
        super(x, y, WIDTH, HEIGHT);
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
        Pos(-1f),
        Zero(0f),
        Neg(1f);

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
            move_x = Ternary.Neg;
        else
            move_x = Ternary.Pos;

        // Set `move_y`
        if (up == down)
            move_y = Ternary.Zero;
        else if (up)
            move_y = Ternary.Neg;
        else
            move_y = Ternary.Pos;

        // Set jump state
        jump_pressed = up && (!jump_held);
        jump_held = up;
    }
}
