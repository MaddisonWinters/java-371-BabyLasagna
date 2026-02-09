package com.Test;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

import java.util.ArrayList;

public class Main extends ApplicationAdapter {

    private SpriteBatch batch;
    private Player player;
    private ArrayList<Projectile> projectiles;

    @Override
    public void create() {
        batch = new SpriteBatch();
        player = new Player();
        projectiles = new ArrayList<>();
    }

    @Override
    public void render() {

        float delta = Gdx.graphics.getDeltaTime();

        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);

        // Spawn projectile
        if (Gdx.input.isKeyJustPressed(Input.Keys.X)) {

            float spawnX = player.facingRight
                ? player.x + player.getWidth()
                : player.x - player.getWidth();

            projectiles.add(
                new Projectile(spawnX, player.y, player.facingRight)
            );
        }

        player.update(delta);

        // Update projectiles
        for (Projectile p : projectiles) {
            p.update(delta);
        }

        batch.begin();

        player.draw(batch);

        for (Projectile p : projectiles) {
            p.draw(batch);
        }

        batch.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
        player.dispose();

        for (Projectile p : projectiles) {
            p.dispose();
        }
    }
}
