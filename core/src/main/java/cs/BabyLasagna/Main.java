package cs.BabyLasagna;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {
    public static SpriteBatch batch;
    private static Game game = null;
    private Menu menu;

    private boolean inMenu = true;

    @Override
    public void create() {
        batch = new SpriteBatch();

        menu = new Menu();

    }

    @Override
    public void render() {
        float deltaTime = Gdx.graphics.getDeltaTime();
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
//        if esc is pressed need to paused game & bring up map
//        add exit button
//        add level options
//        add mini pop up for user tutorial
//        reset button on death
        if(inMenu){
            menu.render();
            if(menu.startGame()){
                int level = menu.getLevel();
                if(level ==1 ){
                    game = new Game("level1");
                    inMenu = false;
                }
                if(level == 2) {
                    game = new Game("level2");
                    inMenu = false;
                }

            }
            return;
        }

        game.update(deltaTime);
        game.render(deltaTime, batch);
    }

    @Override
    public void dispose() {
        batch.dispose();
        game.dispose();
        menu.dispose();

    }

    @Override
    public void resize(int width, int height) {
        final int MAX_VIEWPORT_SIZE = 20;

        // If the window is minimized on a desktop (LWJGL3) platform, width and height are 0
        if(width <= 0 || height <= 0) return;
        if(game !=null) {
            game.updateViewport(width, height);
        }
    }
}
