package com.Test;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

public class ClimbState implements PlayerState {

    private final float climbSpeed = 250f;

    @Override
    public void update(Player player, float delta) {

        player.velocityY = 0;

        boolean touchingWall = player.isTouchingWall();
        boolean holdingSpace = Gdx.input.isKeyPressed(Input.Keys.SPACE);

        if (touchingWall && holdingSpace) {

            player.y += climbSpeed * delta;

            float screenHeight = Gdx.graphics.getHeight();

            if (player.y + player.getHeight() > screenHeight) {
                player.y = screenHeight - player.getHeight();
            }

        } else {
            if (player.onGround) {
                player.setState(new IdleState());
            } else {
                player.setState(new JumpState());
            }
        }
    }
}
