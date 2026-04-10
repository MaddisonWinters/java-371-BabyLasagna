package cs.BabyLasagna.GameObj.States.Player;

import cs.BabyLasagna.GameObj.Player;
import cs.BabyLasagna.GameObj.State;

public class DeathState implements State<Player> {

    private float respawnTimer;
    private static final float RESPAWN_DELAY = 1.0f;

    @Override
    public void enter(Player player) {

        respawnTimer = RESPAWN_DELAY;

        // Stop all movement
        player.getVelocity().x = 0;
        player.getVelocity().y = 0;

        // Optional sound
        // PlayerSnd.die();
    }

    @Override
    public void update(Player player, float deltaTime) {

        respawnTimer -= deltaTime;

        if (respawnTimer <= 0) {

            player.respawn();
            player.getStateController().changeState(player.idleState);

        }
    }

    @Override
    public void exit(Player player) {
        // Reset any death effects if needed
    }
}
