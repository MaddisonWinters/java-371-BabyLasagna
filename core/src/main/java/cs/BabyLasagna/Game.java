package cs.BabyLasagna;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import cs.BabyLasagna.GameObj.Meat;
import cs.BabyLasagna.GameObj.Player;
import cs.BabyLasagna.GameObj.Collectables.Ingredient;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import cs.BabyLasagna.TextureManager.Lasagna.*;
import cs.BabyLasagna.SoundManager.BGMusic.GameMsc;
import cs.BabyLasagna.GameObj.GameObj;


public class Game {
    private static final float MAX_VIEWPORT_SIZE=12;
    public static final int PIXELS_PER_TILE=16;

    private static final int MINIMUM_FPS=24;
    public static final float MAX_DELTA_TIME = 1.0f / (float)MINIMUM_FPS;

    private final OrthographicCamera camera;
    private final OrthogonalTiledMapRenderer renderer;

    private final TiledMap map;
    private static final int[] backgroundLayers = new int[] {0, 1};
    private static final int[] foregroundLayers = new int[] {2};

    private final GameInterface gameInterface;

    private final Player player;
    private final ArrayList<GameObj> objects;


    // For functionality that entities within the game need
    public class GameInterface {
        private final Game game;
        public GameInterface(Game g) { game = g; }

        public final TiledMap getMap() { return game.map; }
        public final Player getPlayer() { return game.player; }
        public final ArrayList<GameObj> getObjects() { return game.objects; }
        public final Meat addObject(GameObj obj) { game.objects.add(obj);
            return null;
        }

        public void resetLevel() {
            for (GameObj obj : game.objects) {
                obj.reset();
            }
        }
    }

    public void update(float deltaTime) {
        deltaTime = Math.min(deltaTime, MAX_DELTA_TIME);

        for (GameObj obj : objects) {
            if (obj.isActive()) {
                obj.update(deltaTime);
            }
        }

        player.update(deltaTime);

        camera.position.set(
            player.getX() + player.getHitbox().width / 2f,
            player.getY() + player.getHitbox().height / 2f,
            0
        );
    }

    // Renders map and all objects to `batch`
    public void render(float deltaTime, SpriteBatch batch) {
        deltaTime = Math.min(deltaTime, MAX_DELTA_TIME);

        // camera.position.set(...)
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        renderer.setView(camera);

        renderer.render(backgroundLayers);

        // Object rendering
        batch.begin();

        for (GameObj obj : objects) {
            if (obj.isActive()) {
                obj.render(deltaTime, batch);
            }
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

    public Game(String level) {
        gameInterface = new GameInterface(this);

        camera = new OrthographicCamera();
        updateViewport(1,1);

        map = new TmxMapLoader().load(level);
        renderer = new OrthogonalTiledMapRenderer(map, 1/16f);

        player = new Player(gameInterface, 3,3);
        player.addTop(LasagnaFlavor.Cheese);
        player.addTop(LasagnaFlavor.Pasta);

        // INGREDIENT TEST CODE
        Ingredient p = new Ingredient(gameInterface, LasagnaFlavor.Pasta, 5, 12);
        Ingredient c = new Ingredient(gameInterface, LasagnaFlavor.Cheese, 6, 12);
        Ingredient m = new Ingredient(gameInterface, LasagnaFlavor.Meat, 7, 12);
        Ingredient r = new Ingredient(gameInterface, LasagnaFlavor.Pepper, 8, 12);
        objects = new ArrayList<>();
        objects.add(p);
        objects.add(c);
        objects.add(m);
        objects.add(r);

        // END INGREDIENT TEST CODE

        GameMsc.playMain();
    }

    public void dispose() {}
}
