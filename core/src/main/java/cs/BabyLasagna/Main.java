package cs.BabyLasagna;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.ScreenUtils;

import cs.BabyLasagna.Lasagna;

/** Super Mario Brothers-like very basic platformer, using a tile map built using <a href="https://www.mapeditor.org/">Tiled</a> and a
 * tileset and sprites by <a href="http://www.vickiwenderlich.com/">Vicky Wenderlich</a></p>
 *
 * Shows simple platformer collision detection as well as on-the-fly map modifications through destructible blocks!
 * @author mzechner */
public class Main extends InputAdapter implements ApplicationListener {
    private TiledMap map;
    private Lasagna lasagna;
    private OrthogonalTiledMapRenderer renderer;
    private OrthographicCamera camera;
    private final Pool<Rectangle> rectPool = new Pool<Rectangle>() {
        @Override
        protected Rectangle newObject () {
            return new Rectangle();
        }
    };
    private final Array<Rectangle> tiles = new Array<Rectangle>();

    @Override
    public void create () {
        // load the map, set the unit scale to 1/16 (1 unit == 16 pixels)
        map = new TmxMapLoader().load("level1.tmx");
        renderer = new OrthogonalTiledMapRenderer(map, 1 / 16f);

        // create an orthographic camera, shows us 30x20 units of the world
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 30, 20);
        camera.update();

        // Initialize the player
        Lasagna.init();
        lasagna = new Lasagna(new Vector2(20,3));
    }

    @Override
    public void render () {
        // clear the screen
        ScreenUtils.clear(0.7f, 0.7f, 1.0f, 1);

        // get the delta time
        float deltaTime = Gdx.graphics.getDeltaTime();

        // update the player (process input, collision detection, position update)
        updatePlayer(deltaTime);

        // let the camera follow the player, x-axis only
        camera.position.x = lasagna.position.x;
        camera.update();

        // set the TiledMapRenderer view based on what the
        // camera sees, and render the map
        renderer.setView(camera);
        renderer.render();

        // render the player
        renderPlayer(deltaTime);
    }

    private void updatePlayer (float deltaTime) {
        if (deltaTime == 0) return;

        if (deltaTime > 0.1f)
            deltaTime = 0.1f;

        if (Gdx.input.isKeyPressed(Keys.LEFT) || Gdx.input.isKeyPressed(Keys.A)) {
            lasagna.velocity.x = -Lasagna.VELOCITY;
        }

        if (Gdx.input.isKeyPressed(Keys.RIGHT) || Gdx.input.isKeyPressed(Keys.D)) {
            lasagna.velocity.x = Lasagna.VELOCITY;
        }

        if (Gdx.input.isKeyPressed(Keys.DOWN) || Gdx.input.isKeyPressed(Keys.S)) {
            lasagna.velocity.y = -Lasagna.VELOCITY;
        }

        if (Gdx.input.isKeyPressed(Keys.UP) || Gdx.input.isKeyPressed(Keys.W)) {
            lasagna.velocity.y = Lasagna.VELOCITY;
        }

        // perform collision detection & response, on each axis, separately
        // if the player is moving right, check the tiles to the right of it's
        // right bounding box edge, otherwise check the ones to the left
        Rectangle playerRect = rectPool.obtain();
        playerRect.set(lasagna.position.x, lasagna.position.y, Lasagna.LAYER_WIDTH, Lasagna.LAYER_HEIGHT);
        int startX, startY, endX, endY;
        if (lasagna.velocity.x > 0) {
            startX = endX = (int)(lasagna.position.x + Lasagna.LAYER_WIDTH + lasagna.velocity.x);
        } else {
            startX = endX = (int)(lasagna.position.x + lasagna.velocity.x);
        }
        startY = (int)(lasagna.position.y);
        endY = (int)(lasagna.position.y + Lasagna.LAYER_HEIGHT);
        getTiles(startX, startY, endX, endY, tiles);
        playerRect.x += lasagna.velocity.x;
        for (Rectangle tile : tiles) {
            if (playerRect.overlaps(tile)) {
                lasagna.velocity.x = 0;
                break;
            }
        }
        playerRect.x = lasagna.position.x;

        // if the player is moving upwards, check the tiles to the top of its
        // top bounding box edge, otherwise check the ones to the bottom
        if (lasagna.velocity.y > 0) {
            startY = endY = (int)(lasagna.position.y + Lasagna.LAYER_HEIGHT + lasagna.velocity.y);
        } else {
            startY = endY = (int)(lasagna.position.y + lasagna.velocity.y);
        }
        startX = (int)(lasagna.position.x);
        endX = (int)(lasagna.position.x + Lasagna.LAYER_WIDTH);
        getTiles(startX, startY, endX, endY, tiles);
        playerRect.y += lasagna.velocity.y;
        for (Rectangle tile : tiles) {
            if (playerRect.overlaps(tile)) {
                lasagna.velocity.y = 0;
                break;
            }
        }
        rectPool.free(playerRect);

        // unscale the velocity by the inverse delta time and set
        // the latest position
        lasagna.position.add(lasagna.velocity);
        lasagna.velocity.scl(1 / deltaTime);


        lasagna.velocity.x *= 0;
        lasagna.velocity.y *= 0;
    }

    private void getTiles (int startX, int startY, int endX, int endY, Array<Rectangle> tiles) {
        TiledMapTileLayer layer = (TiledMapTileLayer)map.getLayers().get("walls");
        rectPool.freeAll(tiles);
        tiles.clear();
        for (int y = startY; y <= endY; y++) {
            for (int x = startX; x <= endX; x++) {
                Cell cell = layer.getCell(x, y);
                if (cell != null) {
                    Rectangle rect = rectPool.obtain();
                    rect.set(x, y, 1, 1);
                    tiles.add(rect);
                }
            }
        }
    }

    private void renderPlayer (float deltaTime) {
        // TODO: based on the player state, get the animation frame
        // Currently just use the original
        TextureRegion frame = Lasagna.ANI_DEFAULT.getKeyFrame(0);

        Batch batch = renderer.getBatch();
        batch.begin();
        batch.draw(frame, lasagna.position.x, lasagna.position.y, Lasagna.LAYER_WIDTH, Lasagna.LAYER_HEIGHT);
        batch.end();
    }

    @Override
    public void dispose () {
    }

    @Override
    public void resume () {
    }

    @Override
    public void resize(int width, int height) {
        // If the window is minimized on a desktop (LWJGL3) platform, width and height are 0, which causes problems.
        // In that case, we don't resize anything, and wait for the window to be a normal size before updating.
        if(width <= 0 || height <= 0) return;

        // Resize your screen here. The parameters represent the new window size.
    }

    @Override
    public void pause() {
    }
}
