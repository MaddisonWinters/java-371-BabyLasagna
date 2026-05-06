package cs.BabyLasagna;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.Input;
import cs.BabyLasagna.GameObj.PlayerProgress;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {
    public static SpriteBatch batch;
    private static Game game = null;
    private Menu menu;
    private PausedMenu pausedMenu;
    private boolean paused = false;
    private String currentLevel;
    private int currentLevelIndex = 0; // track index for progress reporting

    private PlayerProgress progress;

    private int winWidth=1, winHeight=1;

    @Override
    public void create() {
        batch = new SpriteBatch();
        initLevelsFolder();
        progress = new PlayerProgress();
        menu = new Menu(progress);
        pausedMenu = new PausedMenu();
    }

    private void initLevelsFolder() {
        Gdx.files.local("levels").mkdirs();
        String[] bundled = {"level1.tmx", "level2.tmx", "tileSet.png"};
        for (String name : bundled) {
            FileHandle dest = Gdx.files.local("levels/" + name);
            if (!dest.exists()) {
                Gdx.files.internal("levels/" + name).copyTo(dest);
            }
        }
    }

    @Override
    public void render() {
        float deltaTime = Gdx.graphics.getDeltaTime();
        deltaTime = Math.min(deltaTime, 0.1f);
        ScreenUtils.clear(157/255f, 133/255f, 151/255f, 1f);

        if(game == null){
            paused = false;
            menu.render();
            if(menu.startGame()){
                int level = menu.getLevel();
                if (level >= 1 && level <= 5) {
                    currentLevel = "level" + level;
                    currentLevelIndex = level - 1;
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
            if (res == Game.Result.Win) {
                progress.onLevelComplete(currentLevelIndex);
                System.out.println("GAME WON");
            }
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
        if (menu != null) menu.updateViewport(winWidth, winHeight);
        if (game != null) game.updateViewport(winWidth, winHeight);
    }
}
