package cs.BabyLasagna.GameObj.States.Player;

import cs.BabyLasagna.GameObj.Player;
import cs.BabyLasagna.GameObj.State;
import cs.BabyLasagna.TextureManager.LegAnim;

public class FallState implements State<Player> {

    @Override
    public void enter(Player player) {
        LegAnim.walk.start();
    }

    @Override
    public void update(Player player, float deltaTime) {

        // Apply gravity
        player.getVelocity().y -= player.getGravity() * deltaTime;

        // Air movement
        float move = player.getUIData().getMoveXDir();
        player.getVelocity().x = move * player.getMoveSpeed();

        // Landing check
        if (player.isGrounded()) {

            if (move == 0) {
                player.getStateController().changeState(player.idleState);
            } else {
                player.getStateController().changeState(player.runState);
            }

        }
    }

    @Override
    public void exit(Player player) {
    }
}
