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

import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import cs.BabyLasagna.Player;
import jdk.internal.foreign.ArenaAllocator;

import java.util.ArrayList;
import java.util.Iterator;


public class Main extends InputAdapter implements ApplicationListener {
    private TiledMap map;
    private Player player;
    private OrthogonalTiledMapRenderer renderer;
    private OrthographicCamera camera;
    private final Vector2 viewport_size = new Vector2(20,15);

    private ArrayList<Entity> entities = new ArrayList<>();

    @Override
    public void create () {
        // load the map, set the unit scale to 1/16 (1 unit == 16 pixels)
        map = new TmxMapLoader().load("level1.tmx");
        renderer = new OrthogonalTiledMapRenderer(map, 1 / 16f);

        // create an orthographic camera, shows us 30x20 units of the world
        camera = new OrthographicCamera();
        camera.setToOrtho(false, viewport_size.x, viewport_size.y);
        camera.update();


        // Initialize textures for various entities
        Player.init();
        Collectable.init();
        LasagnaStack.init();

        // Add a collectable cheese for testing
        entities.add(new Collectable(Collectable.Type.Cheese, 24, 3f));
        entities.add(new LasagnaStack(26, 3f, true, true));

        // Create player
        player = new Player(20,3);
    }

    @Override
    public void render () {
        // clear the screen
        ScreenUtils.clear(0.7f, 0.7f, 1.0f, 1);

        // get the delta time
        float deltaTime = Gdx.graphics.getDeltaTime();

        // Create copy of entities array
        ArrayList<Entity> entities_cpy = new ArrayList<>(entities);

        Iterator<Entity> it = entities.iterator();
        while (it.hasNext()) {
            Entity e = it.next();
            if (e.shouldDespawn()) {
                it.remove();
            }
            else {
                e.update(deltaTime, map, entities_cpy);
            }
        }

        entities = entities_cpy;

        // update the player (process input, collision detection, position update)
        updatePlayer(deltaTime);

        // let the camera follow the player, x-axis only
        // TODO: Make camera follow x and y, follow more smoothly, bound location to avoid seeing out of the world
        camera.position.x = player.hitbox.x;
        camera.update();

        // set the TiledMapRenderer view based on what the
        // camera sees, and render the map
        renderer.setView(camera);
        renderer.render();

        // render all non-player entities
        for (Entity e : entities) {
            e.render(deltaTime, renderer);
        }

        // render the player
        player.render(deltaTime, renderer);
    }

    private void updatePlayer (float deltaTime) {
        if (deltaTime == 0) return;

        if (deltaTime > 0.1f)
            deltaTime = 0.1f;

        // --- COYOTE TIMER UPDATE ---
        if (player.standing_on != Entity.Ground.Air) {
            // Player is grounded → reset timer
            player.coyoteTimer = Player.COYOTE_TIME;
        } else {
            // Player is airborne → count down
            player.coyoteTimer -= deltaTime;
        }

        player.is_walking = false;

        if (Gdx.input.isKeyPressed(Keys.LEFT) || Gdx.input.isKeyPressed(Keys.A)) {
            player.velocity.x *= 1.0f - Player.ACCELERATION;
            player.velocity.x -= Player.ACCELERATION * Player.MAX_VELOCITY;
            player.is_walking = true;
        }

        if (Gdx.input.isKeyPressed(Keys.RIGHT) || Gdx.input.isKeyPressed(Keys.D)) {
            player.velocity.x *= 1.0f - Player.ACCELERATION;
            player.velocity.x += Player.ACCELERATION * Player.MAX_VELOCITY;
            player.is_walking = true;
        }

        // --- COYOTE JUMP ---
        boolean jumpPressed =
            Gdx.input.isKeyPressed(Keys.UP) ||
                Gdx.input.isKeyPressed(Keys.W) ||
                Gdx.input.isKeyPressed(Keys.SPACE);

        if (jumpPressed && player.coyoteTimer > 0f) {
            player.velocity.y = Player.JUMP_VELOCITY;
            player.coyoteTimer = 0f; // prevent double jumps during coyote window
        }

        player.update(deltaTime, map, entities);
    }

    @Override
    public void dispose () {
    }

    @Override
    public void resume () {
    }

    @Override
    public void resize(int width, int height) {
        final int MAX_VIEWPORT_SIZE = 20;

        // If the window is minimized on a desktop (LWJGL3) platform, width and height are 0
        if(width <= 0 || height <= 0) return;

        if (width < height) {
            viewport_size.y = MAX_VIEWPORT_SIZE;
            viewport_size.x = MAX_VIEWPORT_SIZE * ((float)width / (float)height);
        }
        else {
            viewport_size.x = MAX_VIEWPORT_SIZE;
            viewport_size.y = MAX_VIEWPORT_SIZE * ((float)height / (float)width);
        }

        camera.setToOrtho(false, viewport_size.x, viewport_size.y);
    }

    @Override
    public void pause() {
    }
}
