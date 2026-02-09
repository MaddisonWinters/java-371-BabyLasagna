package com.Test;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

public class WalkState implements PlayerState {

    @Override
    public void update(Player player, float delta) {

        boolean moving = false;

        // Enter climb
        if (Gdx.input.isKeyPressed(Input.Keys.SPACE) && player.isTouchingWall()) {
            player.setState(new ClimbState());
            return;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            player.x -= player.speed * delta;
            player.facingRight = false;
            moving = true;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            player.x += player.speed * delta;
            player.facingRight = true;
            moving = true;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.W) && player.onGround) {
            player.velocityY = player.jumpStrength;
            player.onGround = false;
            player.setState(new JumpState());
            return;
        }

        if (!moving) {
            player.setState(new IdleState());
        }
    }
}
