package cs.BabyLasagna;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {
    private static SpriteBatch batch;
    private static Game game = null;

    @Override
    public void create() {
        batch = new SpriteBatch();
        game = new Game("levels/level1.tmx");
    }

    @Override
    public void render() {
        float deltaTime = Gdx.graphics.getDeltaTime();
        deltaTime = Math.min(deltaTime, 0.1f);
        ScreenUtils.clear(157/255f, 133/255f, 151/255f, 1f);

        game.update(deltaTime);
        game.render(deltaTime, batch);
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

        game.updateViewport(width, height);
    }
}
