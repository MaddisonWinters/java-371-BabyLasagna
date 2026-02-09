package com.Test;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

public class IdleState implements PlayerState {

    @Override
    public void update(Player player, float delta) {

        if (player.isMoving()) {
            player.setState(new WalkState());
            return;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.W) && player.onGround) {
            player.velocityY = player.jumpStrength;
            player.onGround = false;
            player.setState(new JumpState());
        }

        if (Gdx.input.isKeyPressed(Input.Keys.SPACE) && player.isTouchingWall()) {
            player.setState(new ClimbState());
        }
    }
}
