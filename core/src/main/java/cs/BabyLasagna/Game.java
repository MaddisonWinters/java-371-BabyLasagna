package cs.BabyLasagna;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import cs.BabyLasagna.GameObj.Player;
import cs.BabyLasagna.GameObj.Collectables.Collectable;
import cs.BabyLasagna.GameObj.Collectables.Ingredient;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import cs.BabyLasagna.TextureManager.Lasagna.*;
import cs.BabyLasagna.SoundManager.BGMusic.GameMsc;
import cs.BabyLasagna.GameObj.GameObj;


public class Game {
    public enum Result { Ongoing, Win, Loss }

    private static final float MAX_VIEWPORT_SIZE=12;
    public static final int PIXELS_PER_TILE=16;

    private static final int MINIMUM_FPS=24;
    public static final float MAX_DELTA_TIME = 1.0f / (float)MINIMUM_FPS;

    private final OrthographicCamera camera;
    private final OrthogonalTiledMapRenderer renderer;

    private final String levelFile;
    private final TiledMap map;
    private static final int[] backgroundLayers = new int[] {0, 1};
    private static final int[] foregroundLayers = new int[] {2};

    public final GameInterface gameInterface;

    private final Player player;
    private final ArrayList<GameObj> objects;

    private boolean running=true, shouldRestart=false;
    private Result result = Result.Ongoing;


    // For functionality that entities within the game need
    public class GameInterface {
        private final Game game;
        public GameInterface(Game g) { game = g; }

        public final TiledMap getMap() { return game.map; }
        public final Player getPlayer() { return game.player; }
        public final ArrayList<GameObj> getObjects() { return game.objects; }
        
        public final void restart() { game.shouldRestart = true; }
        public final void end(boolean success) { 
            game.running = false; 
            if (success) {
                result = Result.Win;
            }
            else {
                result = Result.Loss;
            }
        }
    }

    protected void updateCamera(float deltaTime) {
        camera.position.set(
            player.getX() + player.getHitbox().width / 2f,
            player.getY() + player.getHitbox().height / 2f,
            0
        );
    }

    public void update(float deltaTime) {
        if (!running || shouldRestart) return;
        deltaTime = Math.min(deltaTime, MAX_DELTA_TIME);

        for (GameObj obj : objects) {
            obj.update(deltaTime);
        }

        player.update(deltaTime);

        updateCamera(deltaTime);
    }

    // Renders map and all objects to `batch`
    public void render(float deltaTime, SpriteBatch batch) {
        deltaTime = Math.min(deltaTime, MAX_DELTA_TIME);

        // camera.position.set(...)
        camera.update();

        renderer.setView(camera);
        renderer.render();

        batch.setProjectionMatrix(camera.combined);
        renderer.setView(camera);

        renderer.render(backgroundLayers);

        // Object rendering
        batch.begin();

        for (GameObj obj : objects) {
            obj.render(deltaTime, batch);
        }

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

    public boolean isRunning() { return running; }
    public boolean shouldRestart() { return shouldRestart; }
    public String getLevelFile() { return levelFile; }
    public Result getResult() { return result; }

    private void parseCollectables(TiledMapTileLayer colLayer) {
        for (int x = 0; x < colLayer.getWidth(); ++x) {
            for (int y = 0; y < colLayer.getHeight(); ++y) {
                Cell c = colLayer.getCell(x, y);
                if (c == null) continue;
                TiledMapTile t = c.getTile();
                int id = t.getId();

                Ingredient i = null;

                switch(id) {
                    case 0:
                        break;
                    case 1:
                        i = new Ingredient(gameInterface, LasagnaFlavor.Pasta, x, y);
                        break;
                    case 2:
                        i = new Ingredient(gameInterface, LasagnaFlavor.Cheese, x, y);
                        break;
                    case 3:
                        i = new Ingredient(gameInterface, LasagnaFlavor.Meat, x, y);
                        break;
                    case 4:
                        i = new Ingredient(gameInterface, LasagnaFlavor.Pepper, x, y);
                        break;
                    default:
                        break;
                }

                if (i == null) continue;

                objects.add(i);
            }
        }
    }

    public Game(String level, int winWidth, int winHeight) {
        gameInterface = new GameInterface(this);

        // Load map
        levelFile = level;
        map = new TmxMapLoader().load("levels/" + levelFile + ".tmx");
        renderer = new OrthogonalTiledMapRenderer(map, 1/16f);

        // Load objects
        objects = new ArrayList<>();
        TiledMapTileLayer colLayer = (TiledMapTileLayer)map.getLayers().get("Collectable");
        if (colLayer != null) parseCollectables(colLayer);

        // Create player
        player = new Player(gameInterface, 3,3);
        player.addTop(LasagnaFlavor.Cheese);
        player.addTop(LasagnaFlavor.Pasta);

        // Create camera
        camera = new OrthographicCamera();
        updateViewport(winWidth, winHeight);
        updateCamera(1.0f);

        GameMsc.playMain();
    }

    public void dispose() {}
}
