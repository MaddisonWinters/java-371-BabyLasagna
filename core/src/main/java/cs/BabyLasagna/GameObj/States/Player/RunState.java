package cs.BabyLasagna.GameObj.States.Player;

import cs.BabyLasagna.GameObj.Player;
import cs.BabyLasagna.GameObj.State;

public class RunState implements State<Player> {
    @Override
    public void enter(Player player) {
        // Optional: play run animation or sound
    }

    @Override
    public void update(Player player, float deltaTime) {

        float move = player.getUIData().getMoveXDir();

        // Horizontal movement
        player.getVelocity().x = move * player.getMoveSpeed();

        // Stop running
        if (move == 0) {
            player.getStateController().changeState(player.idleState);
            return;
        }

        // Walk off ledge
        if (!player.isGrounded()) {
            player.getStateController().changeState(player.fallState);
        }
    }

    @Override
    public void exit(Player player) {
        // Optional: stop run animation
    }
}
