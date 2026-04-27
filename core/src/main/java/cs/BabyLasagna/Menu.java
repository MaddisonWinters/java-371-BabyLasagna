package cs.BabyLasagna;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import cs.BabyLasagna.GameObj.PlayerProgress;
import cs.BabyLasagna.Worlds.WorldDefinition;
import cs.BabyLasagna.Worlds.WorldLevel;

import java.util.ArrayList;
import java.util.List;

public class Menu {
    private final OrthographicCamera camera;
    private final Texture logo;
    private final Texture exit;
    private final List<Texture> levelTextures = new ArrayList<>();

    private boolean startGame = false;
    private int levelChoice = -1;

    private static final float BUTTON_WIDTH   = 112 * 2f;
    private static final float BUTTON_HEIGHT  = 32 * 2f;
    private static final float BUTTON_SPACING = 60f;
    private static final float BUTTON_START_Y = 175f;

    private final PlayerProgress progress;
    private final WorldDefinition world;

    public Menu(PlayerProgress progress, WorldDefinition world) {
        this.progress = progress;
        this.world    = world;

        logo = new Texture("menu/logo.png");
        exit = new Texture("menu/exit.png");

        for (WorldLevel level : world.levels) {
            levelTextures.add(level.buttonTexture != null ? new Texture(level.buttonTexture) : null);
        }

        camera = new OrthographicCamera();
        updateViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    public void render() {
        float screenW = Gdx.graphics.getWidth();
        float screenH = Gdx.graphics.getHeight();
        float buttonX = (screenW / 2f) - (176 * 3 / 2f);
        float exitX   = screenW - 80f;
        float exitY   = screenH - 80f;

        camera.update();
        Main.batch.setProjectionMatrix(camera.combined);
        Main.batch.begin();

        Main.batch.draw(logo, (screenW / 2f) - (176 * 3 / 2f), 250, 176 * 3, 32 * 3);
        Main.batch.draw(exit, exitX, exitY, 96, 96);

        for (int i = 0; i < world.levels.size(); i++) {
            float buttonY = BUTTON_START_Y - i * BUTTON_SPACING;
            drawLevelButton(i, buttonX, buttonY);
        }

        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            float mouseX = Gdx.input.getX() * (camera.viewportWidth  / screenW);
            float mouseY = (screenH - Gdx.input.getY()) * (camera.viewportHeight / screenH);

            for (int i = 0; i < world.levels.size(); i++) {
                float buttonY = BUTTON_START_Y - i * BUTTON_SPACING;
                if (mouseX > buttonX && mouseX < buttonX + BUTTON_WIDTH
                        && mouseY > buttonY && mouseY < buttonY + BUTTON_HEIGHT) {
                    if (world.levels.get(i).isUnlocked(progress.getCompletedLevels())) {
                        levelChoice = i;
                        startGame  = true;
                    }
                    break;
                }
            }

            if (mouseX > exitX && mouseX < exitX + 96 && mouseY > exitY && mouseY < exitY + 96) {
                Gdx.app.exit();
            }
        }

        Main.batch.end();
    }

    private void drawLevelButton(int index, float x, float y) {
        boolean unlocked = world.levels.get(index).isUnlocked(progress.getCompletedLevels());
        float tint = unlocked ? 1f : 0.4f;
        Main.batch.setColor(tint, tint, tint, 1f);
        Texture tex = levelTextures.get(index);
        if (tex != null) Main.batch.draw(tex, x, y, BUTTON_WIDTH, BUTTON_HEIGHT);
        Main.batch.setColor(1f, 1f, 1f, 1f);
    }

    public void updateViewport(int width, int height) { camera.setToOrtho(false, width, height); }

    public boolean startGame() {
        if (startGame) {
            startGame = false;
            return true;
        }
        return false;
    }

    public int getLevelChoice() { return levelChoice; }

    public void dispose() {
        logo.dispose();
        exit.dispose();
        for (Texture tex : levelTextures) {
            if (tex != null) tex.dispose();
        }
    }
}
