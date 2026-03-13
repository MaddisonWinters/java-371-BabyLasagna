package cs.BabyLasagna.GameObj.States.Player;

import cs.BabyLasagna.GameObj.Player;
import cs.BabyLasagna.GameObj.State;

public interface PlayerState {

    void enter(Player player);

    void update(Player player, float deltaTime);

    void exit(Player player);
}
