package cs.BabyLasagna.GameObj.States.Player;

import cs.BabyLasagna.GameObj.Player;
import cs.BabyLasagna.GameObj.State;
import cs.BabyLasagna.TextureManager.LegAnim;

public class IdleState implements State<Player> {

    @Override
    public void enter(Player player) {
        player.getVelocity().x = 0;
        LegAnim.walk.reset();
    }

    @Override
    public void update(Player player, float deltaTime) {

        if (!player.isGrounded()) {
            player.getStateController().changeState(player.fallState);
            return;
        }

        if (player.getUIData().getMoveXDir() != 0) {
            player.getStateController().changeState(player.runState);
        }
    }

    @Override
    public void exit(Player player) { }
}
