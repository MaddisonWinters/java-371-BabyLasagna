package com.Test;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

public class JumpState implements PlayerState {

    @Override
    public void update(Player player, float delta) {

        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            player.x -= player.speed * delta;
            player.facingRight = false;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            player.x += player.speed * delta;
            player.facingRight = true;
        }

        if (player.onGround) {
            player.setState(new IdleState());
        }

        if (Gdx.input.isKeyPressed(Input.Keys.SPACE) && player.isTouchingWall()) {
            player.setState(new ClimbState());
        }
    }
}
