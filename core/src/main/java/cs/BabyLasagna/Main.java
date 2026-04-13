package cs.BabyLasagna;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {
    private static SpriteBatch batch;
    private static Game game = null;

    private int winWidth=1, winHeight=1;

    @Override
    public void create() {
        batch = new SpriteBatch();
        game = new Game("levels/level2.tmx");
    }

    @Override
    public void render() {
        float deltaTime = Gdx.graphics.getDeltaTime();
        deltaTime = Math.min(deltaTime, 0.1f);
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);

        if (game != null) {
            // End
            if (!game.isRunning()) {
                game.dispose();
                game = null;
                return;
            }
            // Restart
            else if (game.shouldRestart()) {
                String level = game.getLevelFile();
                game.dispose();
                game = new Game(level);
            }
            // Continue normally
            else {
                game.update(deltaTime);
                game.render(deltaTime, batch);
            }
        }
        else {
            game = new Game("levels/level1.tmx");
            game.updateViewport(winWidth, winHeight);
        }
    }

    @Override
    public void dispose() {
        batch.dispose();
        game.dispose();
    }

    @Override
    public void resize(int width, int height) {
        // If the window is minimized on a desktop (LWJGL3) platform, width and height are 0
        if(width <= 0 || height <= 0) return;

        winWidth = width;
        winHeight = height;
        game.updateViewport(winWidth, winHeight);
    }
}
