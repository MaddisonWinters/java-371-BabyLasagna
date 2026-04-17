package cs.BabyLasagna;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.Input;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {
    public static SpriteBatch batch;
    private static Game game = null;
    private Menu menu;
    private PausedMenu pausedMenu;
    private boolean paused = false;
    private String currentLevel;
    private int win_width=1, win_height=1;

    @Override
    public void create() {
        batch = new SpriteBatch();
        menu = new Menu();
        pausedMenu = new PausedMenu();
    }

    @Override
    public void render() {
        float deltaTime = Gdx.graphics.getDeltaTime();
        deltaTime = Math.min(deltaTime, 0.1f);
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);

        if(game == null){
            paused = false;
            menu.render();
            if(menu.startGame()){
                int level = menu.getLevel();
                if(level ==1 ){
                    currentLevel = "level1";
                    game = new Game(currentLevel);
                    game.updateViewport(win_width, win_height);
                    paused = false;
                }
                if(level == 2) {
                    currentLevel = "level2";
                    game = new Game(currentLevel);
                    game.updateViewport(win_width, win_height);
                    paused = false;
                }

            }
            return;
        }
        // if esc is pressed need to paused game & bring up menu
        if (game != null && Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            if (paused) {
                paused = false;
            } else {
                paused = true;
            }
        }
        game.render(deltaTime, batch);
        if(paused){
            //game.render(deltaTime, batch);
            pausedMenu.render();

            if(pausedMenu.resume()){
                paused = false;
            }

            if(pausedMenu.restart()){
                game = new Game(currentLevel);
                game.updateViewport(win_width, win_height);
                paused = false;
            }

            if(pausedMenu.mainMenu()){
                game.dispose();
                game = null;
                paused = false;
                return;
            }
            return;
        }

        game.update(deltaTime);
    }

    @Override
    public void dispose() {
        if (batch != null) batch.dispose();
        if (game != null) game.dispose();
        if (menu != null) menu.dispose();
        if (pausedMenu != null) pausedMenu.dispose();
    }

    @Override
    public void resize(int width, int height) {
        // If the window is minimized on a desktop (LWJGL3) platform, width and height are 0
        if(width <= 0 || height <= 0) return;

        win_width = width;
        win_height = height;

        if(game !=null) {
            game.updateViewport(width, height);
        }
    }
}
