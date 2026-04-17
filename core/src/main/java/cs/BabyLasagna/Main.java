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
        game = new Game("level2");
    }

    @Override
    public void render() {
        float deltaTime = Gdx.graphics.getDeltaTime();
        deltaTime = Math.min(deltaTime, 0.1f);
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);

        if (game != null) {
            // End
            if (!game.isRunning()) {
                Game.Result res = game.getResult();
                if (res == Game.Result.Win)
                    System.out.println("GAME WON");
                else if (res == Game.Result.Loss)
                    System.out.println("GAME LOST");
                else
                    System.err.println("ERROR: Game not running but has no result");
                
                game.dispose();
                game = null;
                return;
            }
            // Restart
            else if (game.shouldRestart()) {
                System.out.println("Restarted level");
                String level = game.getLevelFile();
                game.dispose();
                game = new Game(level);
                game.updateViewport(winWidth, winHeight);
            }
            // Continue normally
            else {
                game.update(deltaTime);
                game.render(deltaTime, batch);
            }
        }
        else {
            game = new Game("level1");
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
