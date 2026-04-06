package cs.BabyLasagna.GameObj.Collectables;
import cs.BabyLasagna.Game.GameInterface;
import cs.BabyLasagna.GameObj.GameObj;
import cs.BabyLasagna.GameObj.Player;

public abstract class Collectable extends GameObj {

    public void update(float deltaTime) {
        velocity.y += GRAVITY * deltaTime;
        moveWithCollisions(deltaTime);
    }

    public abstract void collect(Player player);

    public Collectable(GameInterface g, float x, float y, float width, float height) {
        super(g,x,y,width,height,0,0);
    }
}
