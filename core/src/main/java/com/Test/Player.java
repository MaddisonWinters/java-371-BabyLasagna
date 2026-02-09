package com.Test;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Player {

    public float x, y;
    public float velocityY;

    public final float speed = 200f;
    public final float gravity = -800f;
    public final float jumpStrength = 350f;
    public final float groundY = 100f;

    public boolean onGround = true;
    public boolean facingRight = true;

    private Texture texture;
    private PlayerState state;

    public Player() {
        texture = new Texture("libgdx16.png");
        x = 200;
        y = groundY;
        setState(new IdleState());
    }

    public void update(float delta) {

        // Apply gravity unless climbing
        if (!(state instanceof ClimbState)) {
            velocityY += gravity * delta;
            y += velocityY * delta;
        }

        // Ground collision
        if (y <= groundY) {
            y = groundY;
            velocityY = 0;
            onGround = true;
        } else {
            onGround = false;
        }

        // Screen bounds
        float screenWidth = Gdx.graphics.getWidth();

        if (x < 0) x = 0;
        if (x + texture.getWidth() > screenWidth)
            x = screenWidth - texture.getWidth();

        state.update(this, delta);
    }

    public void draw(SpriteBatch batch) {
        batch.draw(texture, x, y);
    }

    public void setState(PlayerState newState) {
        this.state = newState;
    }

    public boolean isMoving() {
        return Gdx.input.isKeyPressed(Input.Keys.A)
            || Gdx.input.isKeyPressed(Input.Keys.D);
    }

    public boolean isTouchingWall() {
        float screenWidth = Gdx.graphics.getWidth();
        return x <= 0 || x + texture.getWidth() >= screenWidth;
    }

    public float getWidth() {
        return texture.getWidth();
    }

    public float getHeight() {
        return texture.getHeight();
    }

    public void dispose() {
        texture.dispose();
    }
}
