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

    private int winWidth=1, winHeight=1;

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
                    game = new Game(currentLevel, winWidth, winHeight);
                    paused = false;
                }
                if(level == 2) {
                    currentLevel = "level2";
                    game = new Game(currentLevel, winWidth, winHeight);
                    paused = false;
                }

            }
            return;
        }

        // if esc is pressed need to paused game & bring up menu
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            if (paused) {
                paused = false;
            } else {
                paused = true;
            }
        }
        
        // If paused
        if (paused) {
            // Render game then pause menu over top
            game.render(deltaTime, batch);
            pausedMenu.render();

            // Resume
            if(pausedMenu.resume()){
                paused = false;
            }

            // Set game to restart, then resume
            if(pausedMenu.restart()){
                game.gameInterface.restart();
                paused = false;
            }

            // Set game to end, then resume
            if(pausedMenu.mainMenu()){
                game.gameInterface.end(false);
                paused = false;
                return;
            }

            return;
        }

        // If game is over
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

        // If game needs restart
        else if (game.shouldRestart()) {
            System.out.println("Restarted level");
            String level = game.getLevelFile();
            game.dispose();
            game = new Game(level, winWidth, winHeight);
            game.render(deltaTime, batch);
        }
        // Continue normally
        else {
            game.update(deltaTime);
            game.render(deltaTime, batch);
        }
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

        winWidth = width;
        winHeight = height;
        if (game != null) game.updateViewport(winWidth, winHeight);
    }
}
