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

import java.util.ArrayList;

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

    private ArrayList<Entity> entities = new ArrayList<>();

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
        camera.position.x = lasagna.hitbox.x;
        camera.update();

        // set the TiledMapRenderer view based on what the
        // camera sees, and render the map
        renderer.setView(camera);
        renderer.render();

        // render the player
        lasagna.render(deltaTime, renderer);
    }

    private void updatePlayer (float deltaTime) {
        if (deltaTime == 0) return;

        if (deltaTime > 0.1f)
            deltaTime = 0.1f;

        // --- COYOTE TIMER UPDATE ---
        if (lasagna.standing_on != Entity.Ground.Air) {
            // Player is grounded → reset timer
            lasagna.coyoteTimer = lasagna.COYOTE_TIME;
        } else {
            // Player is airborne → count down
            lasagna.coyoteTimer -= deltaTime;
        }

        lasagna.is_walking = false;

        if (Gdx.input.isKeyPressed(Keys.LEFT) || Gdx.input.isKeyPressed(Keys.A)) {
            lasagna.velocity.x *= 1.0f - Lasagna.ACCELERATION;
            lasagna.velocity.x -= Lasagna.ACCELERATION * Lasagna.MAX_VELOCITY;
            lasagna.is_walking = true;
        }

        if (Gdx.input.isKeyPressed(Keys.RIGHT) || Gdx.input.isKeyPressed(Keys.D)) {
            lasagna.velocity.x *= 1.0f - Lasagna.ACCELERATION;
            lasagna.velocity.x += Lasagna.ACCELERATION * Lasagna.MAX_VELOCITY;
            lasagna.is_walking = true;
        }

        // --- COYOTE JUMP ---
        boolean jumpPressed =
            Gdx.input.isKeyPressed(Keys.UP) ||
                Gdx.input.isKeyPressed(Keys.W) ||
                Gdx.input.isKeyPressed(Keys.SPACE);

        if (jumpPressed && lasagna.coyoteTimer > 0f) {
            lasagna.velocity.y = Lasagna.JUMP_VELOCITY;
            lasagna.coyoteTimer = 0f; // prevent double jumps during coyote window
        }

        lasagna.update(deltaTime, map, entities);
    }


//    private void updatePlayer (float deltaTime) {
//        if (deltaTime == 0) return;
//
//        if (deltaTime > 0.1f)
//            deltaTime = 0.1f;
//
//        lasagna.is_walking = false;
//        if (Gdx.input.isKeyPressed(Keys.LEFT) || Gdx.input.isKeyPressed(Keys.A)) {
//            lasagna.velocity.x *= 1.0f - Lasagna.ACCELERATION;
//            lasagna.velocity.x -= Lasagna.ACCELERATION*Lasagna.MAX_VELOCITY;
//            lasagna.is_walking = true;
//        }
//
//        if (Gdx.input.isKeyPressed(Keys.RIGHT) || Gdx.input.isKeyPressed(Keys.D)) {
//            lasagna.velocity.x *= 1.0f - Lasagna.ACCELERATION;
//            lasagna.velocity.x += Lasagna.ACCELERATION*Lasagna.MAX_VELOCITY;
//            lasagna.is_walking = true;
//        }
//
//        if (lasagna.standing_on != Entity.Ground.Air) {
//            if (Gdx.input.isKeyPressed(Keys.UP) || Gdx.input.isKeyPressed(Keys.W) || Gdx.input.isKeyPressed(Keys.SPACE)) {
//                lasagna.velocity.y += Lasagna.JUMP_VELOCITY;
//            }
//        }
//
//        lasagna.update(deltaTime, map, entities);
//    }

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
