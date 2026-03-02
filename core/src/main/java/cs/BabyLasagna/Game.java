package cs.BabyLasagna;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import cs.BabyLasagna.GameObj.LasagnaStack;
import cs.BabyLasagna.GameObj.Player;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import cs.BabyLasagna.TextureManager.Lasagna.*;


public class Game {
    private static final float MAX_VIEWPORT_SIZE=12;
    public static final int PIXELS_PER_TILE=16;

    private final OrthographicCamera camera;
    private final OrthogonalTiledMapRenderer renderer;
    private final Player player;
    private final LasagnaStack lasagna;

    private final TiledMap map;

    public void update(float deltaTime) {
        player.update(deltaTime, map);
        lasagna.update(deltaTime, map);

        camera.position.set(
            player.getX() + player.getHitbox().width / 2f,
            player.getY() + player.getHitbox().height / 2f,
            0
        );
    }

    // Renders map and all objects to `batch`
    public void render(float deltaTime, SpriteBatch batch) {
        // camera.position.set(...)
        camera.update();
        batch.setProjectionMatrix(camera.combined);

        batch.begin();
        player.render(deltaTime, batch);
        lasagna.render(deltaTime, batch);
        batch.end();

        renderer.setView(camera);
        renderer.render();
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
        player = new Player(3,3);

        lasagna = new LasagnaStack(4,6, true, true);
        lasagna.addTop(LasagnaStack.Layer.make(LasagnaFlavor.Plain, LasagnaRegion.Layer1));
        lasagna.addTop(LasagnaStack.Layer.make(LasagnaFlavor.Plain, LasagnaRegion.Layer2));
        lasagna.addTop(LasagnaStack.Layer.make(LasagnaFlavor.Plain, LasagnaRegion.Layer3));
        lasagna.addTop(LasagnaStack.Layer.make(LasagnaFlavor.Plain, LasagnaRegion.Layer2));
        lasagna.addTop(LasagnaStack.Layer.make(LasagnaFlavor.Plain, LasagnaRegion.Layer3));
        lasagna.addTop(LasagnaStack.Layer.make(LasagnaFlavor.Plain, LasagnaRegion.Layer2));
        lasagna.addTop(LasagnaStack.Layer.make(LasagnaFlavor.Plain, LasagnaRegion.Layer3));

        map = new TmxMapLoader().load("levels/level1.tmx");
        renderer = new OrthogonalTiledMapRenderer(map, 1/16f);
    }

    public void dispose() {}
}
