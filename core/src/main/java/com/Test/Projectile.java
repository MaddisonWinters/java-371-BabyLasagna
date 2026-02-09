package com.Test;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Projectile {

    private Texture texture;
    private float x, y;
    private float speed;

    private float distanceTravelled = 0f;
    private float maxDistance;

    private boolean stopped = false;

    public Projectile(float startX, float startY, boolean goingRight) {

        texture = new Texture("libgdx16.png");

        x = startX;
        y = startY;

        speed = goingRight ? 500f : -500f;

        // Travel 2 widths of itself
        maxDistance = texture.getWidth() * 2f;
    }

    public void update(float delta) {

        if (!stopped) {

            float movement = speed * delta;
            x += movement;

            distanceTravelled += Math.abs(movement);

            if (distanceTravelled >= maxDistance) {
                stopped = true;   // stop moving
            }
        }
    }

    public void draw(SpriteBatch batch) {
        batch.draw(texture, x, y);
    }

    public void dispose() {
        texture.dispose();
    }
}
