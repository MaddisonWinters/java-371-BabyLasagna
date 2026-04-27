package cs.BabyLasagna;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import cs.BabyLasagna.GameObj.PlayerProgress;
import cs.BabyLasagna.Worlds.WorldDefinition;
import cs.BabyLasagna.Worlds.WorldLevel;
import cs.BabyLasagna.Worlds.WorldLoader;

public class Main extends ApplicationAdapter {
    public static SpriteBatch batch;
    private static Game game = null;
    private Menu menu;
    private PausedMenu pausedMenu;
    private boolean paused = false;
    private String currentLevelId;

    private PlayerProgress progress;
    private WorldDefinition world;

    private int winWidth = 1, winHeight = 1;

    @Override
    public void create() {
        batch      = new SpriteBatch();
        progress   = new PlayerProgress();
        world      = WorldLoader.load("worlds/world.json");
        menu       = new Menu(progress, world);
        pausedMenu = new PausedMenu();
    }

    @Override
    public void render() {
        float deltaTime = Math.min(Gdx.graphics.getDeltaTime(), 0.1f);
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);

        if (game == null) {
            paused = false;
            menu.render();
            if (menu.startGame()) {
                WorldLevel chosen = world.levels.get(menu.getLevelChoice());
                currentLevelId = chosen.id;
                game = new Game(chosen.mapFile, winWidth, winHeight);
            }
            return;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            paused = !paused;
        }

        if (paused) {
            game.render(deltaTime, batch);
            pausedMenu.render();
            if (pausedMenu.resume())    paused = false;
            if (pausedMenu.restart()) { game.gameInterface.restart(); paused = false; }
            if (pausedMenu.mainMenu()) { game.gameInterface.end(false); paused = false; return; }
            return;
        }

        if (!game.isRunning()) {
            Game.Result res = game.getResult();
            if (res == Game.Result.Win) {
                progress.onLevelComplete(currentLevelId);
                System.out.println("GAME WON");
            } else if (res == Game.Result.Loss) {
                System.out.println("GAME LOST");
            } else {
                System.err.println("ERROR: Game not running but has no result");
            }
            game.dispose();
            game = null;
            return;
        }

        if (game.shouldRestart()) {
            System.out.println("Restarted level");
            String mapFile = game.getLevelFile();
            game.dispose();
            game = new Game(mapFile, winWidth, winHeight);
            game.render(deltaTime, batch);
        } else {
            game.update(deltaTime);
            game.render(deltaTime, batch);
        }
    }

    @Override
    public void dispose() {
        if (batch      != null) batch.dispose();
        if (game       != null) game.dispose();
        if (menu       != null) menu.dispose();
        if (pausedMenu != null) pausedMenu.dispose();
    }

    @Override
    public void resize(int width, int height) {
        if (width <= 0 || height <= 0) return;
        winWidth  = width;
        winHeight = height;
        if (game != null) game.updateViewport(winWidth, winHeight);
    }
}
