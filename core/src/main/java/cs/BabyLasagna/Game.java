package cs.BabyLasagna;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import cs.BabyLasagna.GameObj.Player;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.rmi.server.UID;

public class Game {
    private static final float MAX_VIEWPORT_SIZE=12;
    public static final int PIXELS_PER_TILE=16;

    private final OrthographicCamera camera;
    // private OrthogonalTiledMapRenderer renderer;

    private final Player player;

    public void update(float deltaTime) {
        player.update(deltaTime);
    }

    // Renders map and all objects to `batch`
    public void render(float deltaTime, SpriteBatch batch) {
        // camera.position.set(...)
        camera.update();
        batch.setProjectionMatrix(camera.combined);

        batch.begin();
        player.render(deltaTime, batch);
        batch.end();
    }

    // Updates the viewport of the camera
    public void updateViewport(int width, int height) {
        float vp_width=MAX_VIEWPORT_SIZE, vp_height=MAX_VIEWPORT_SIZE;

        if (width < height) {
            vp_width *= ((float)width / (float)height);
        }
        else {
            vp_height *= ((float)height / (float)width);
        }

        camera.setToOrtho(false, vp_width, vp_height);
    }

    public Game(String level) {
        camera = new OrthographicCamera();
        updateViewport(1,1);
        player = new Player(1,1);
    }

    public void dispose() {}
}
