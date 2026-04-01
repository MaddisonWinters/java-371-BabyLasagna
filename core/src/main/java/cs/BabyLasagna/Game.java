package cs.BabyLasagna;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import cs.BabyLasagna.GameObj.Player;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import cs.BabyLasagna.TextureManager.Lasagna.*;
import cs.BabyLasagna.SoundManager.BGMusic.GameMsc;


public class Game {
    private static final float MAX_VIEWPORT_SIZE=12;
    public static final int PIXELS_PER_TILE=16;

    private final OrthographicCamera camera;
    private final OrthogonalTiledMapRenderer renderer;
    private final Player player;

    private final TiledMap map;
    private static final int[] backgroundLayers = new int[] {0, 1};
    private static final int[] foregroundLayers = new int[] {2};

    private final GameInterface gameInterface;


    // For functionality that entities within the game need
    public class GameInterface {
        private final Game game;
        public GameInterface(Game g) { game = g; }

        // Return the tilemap
        public final TiledMap getMap() { return game.map; }
    }

    public void update(float deltaTime) {
        player.update(deltaTime);

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
        renderer.setView(camera);

        renderer.render(backgroundLayers);

        batch.begin();
        player.render(deltaTime, batch);
        batch.end();

        renderer.render(foregroundLayers);
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
        gameInterface = new GameInterface(this);

        camera = new OrthographicCamera();
        updateViewport(1,1);

        map = new TmxMapLoader().load(level);
        renderer = new OrthogonalTiledMapRenderer(map, 1/16f);

        player = new Player(gameInterface, 3,3);
        player.addTop(LasagnaFlavor.Cheese);
        player.addTop(LasagnaFlavor.Plain);
        GameMsc.playMain();

    }

    public void dispose() {}
}
