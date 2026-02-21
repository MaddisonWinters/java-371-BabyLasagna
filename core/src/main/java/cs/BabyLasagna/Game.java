package cs.BabyLasagna;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import cs.BabyLasagna.GameObj.Player;
import com.badlogic.gdx.math.Rectangle;
import java.util.ArrayList;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import java.rmi.server.UID;

public class Game {
    private static final float MAX_VIEWPORT_SIZE=12;
    public static final int PIXELS_PER_TILE=16;

    private final OrthographicCamera camera;
    // private OrthogonalTiledMapRenderer renderer;

    private final Player player;

    //new 2/21-----------------------------
    private final ArrayList<Rectangle> testTiles = new ArrayList<>();
    private final ShapeRenderer shapeRenderer = new ShapeRenderer();
    //---------------
    public void update(float deltaTime) {
        player.update(deltaTime);
        for (Rectangle tile : testTiles) {
            player.resolveCollision(tile);
        }

        //camera.position.set(player.getX(), player.getY(), 0);
        //new----------------------
        camera.position.set(
            player.getX() + player.getHitbox().width / 2f,
            player.getY() + player.getHitbox().height / 2f,
            0
        );
        //----------------------------
    }

    // Renders map and all objects to `batch`
    public void render(float deltaTime, SpriteBatch batch) {
        // camera.position.set(...)
        camera.update();
        batch.setProjectionMatrix(camera.combined);

        //new-------------------
        // --- Draw rectangles (tiles) ---
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(1, 1, 1, 1); // white

        for (Rectangle tile : testTiles) {
            shapeRenderer.rect(tile.x, tile.y, tile.width, tile.height);
        }

        shapeRenderer.end();
        //-----------------------

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

        //new 2/21----------------------
        testTiles.add(new Rectangle(0,0,10,1));   // ground
        testTiles.add(new Rectangle(4,1,1,2));    // wall
        testTiles.add(new Rectangle(2,2,3,0.5f)); // platform
        //--------------------
    }

    public void dispose() {}
}
