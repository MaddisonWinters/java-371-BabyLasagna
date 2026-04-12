package cs.BabyLasagna.GameObj;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import cs.BabyLasagna.TextureManager;
import cs.BabyLasagna.Game.GameInterface;

public class CheeseBall extends GameObj {

    public static final float INITIAL_VX = 8.0f;
    public static final float INITIAL_VY = 3.0f;
    
    private boolean facing_right = false;
    private boolean splatted = false;

    public CheeseBall(GameInterface g, float x, float y, float vx, float vy, boolean facing_right) {
        super(
            g, x, y, 0.5f, 0.5f,
            (facing_right ? vx + INITIAL_VX : vx - INITIAL_VX ),
            (vy + INITIAL_VY)
        );

        this.facing_right = facing_right;
    }

    @Override
    public void update(float deltaTime) {
        if (splatted == true) {
            // TODO: Make it follow solid objects that can move and check
            // if the tile is still there. 
            return;
        }

        velocity.y += GRAVITY*deltaTime;

        Collision collision = moveWithCollisions(deltaTime);

        if (collision.hasCollision()) {
            splatted = true;
            // For now: Align to tile, since we'll only handle collision with tiles. 

            // Center on nearest tile position
            float cx = hitbox.x + hitbox.width*0.5f;
            float cy = hitbox.y + hitbox.height*0.5f;
            hitbox.x = (float)Math.floor(cx) + 0.5f - hitbox.width*0.5f;
            hitbox.y = (float)Math.floor(cy) + 0.5f - hitbox.height*0.5f;
        }
    }

    @Override
    public void render(float deltaTime, SpriteBatch batch) {
        TextureManager.draw(
            batch,
            TextureManager.Lasagna.LasagnaFlavor.Cheese.getIngredientTex(),
            hitbox.x,
            hitbox.y,
            hitbox.width,
            hitbox.height,
            false,
            false
        );
    }
}