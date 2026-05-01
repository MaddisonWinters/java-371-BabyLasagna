package cs.BabyLasagna;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import cs.BabyLasagna.GameObj.PlayerProgress;

public class Menu {
    private static final int NUM_LEVELS = 5;
    private static final float PEG_SIZE = 80f;
    private static final float PEG_SPACING = 16f;

    private OrthographicCamera camera;
    private Texture logo;
    private Texture exit;
    private Texture[] levelPegs = new Texture[NUM_LEVELS];
    private Texture[] levelPegsCompleted = new Texture[NUM_LEVELS];
    private Texture levelPegLocked;
    private Texture levelPegPlaceholder;

    private boolean startGame = false;
    private int levelChoice = 0;

    private final PlayerProgress progress;
    private final boolean[] levelExists = new boolean[NUM_LEVELS];

    public Menu(PlayerProgress progress) {
        this.progress = progress;

        logo = new Texture("menu/logo.png");
        exit = new Texture("menu/exit.png");
        levelPegLocked = new Texture("menu/levelpeg-locked.png");
        levelPegPlaceholder = new Texture("menu/levelpeg-placeholder.png");

        for (int i = 0; i < NUM_LEVELS; i++) {
            levelPegs[i] = new Texture("menu/levelpeg-" + (i + 1) + ".png");
            levelPegsCompleted[i] = new Texture("menu/levelpeg-completed-" + (i + 1) + ".png");
            levelExists[i] = Gdx.files.local("Levels/level" + (i + 1) + ".tmx").exists();
        }

        camera = new OrthographicCamera();
        updateViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    private float getPegX(int index) {
        float totalWidth = NUM_LEVELS * PEG_SIZE + (NUM_LEVELS - 1) * PEG_SPACING;
        float startX = (Gdx.graphics.getWidth() - totalWidth) / 2f;
        return startX + index * (PEG_SIZE + PEG_SPACING);
    }

    private static final float PEG_Y = 140f;

    public void render() {
        camera.update();
        Main.batch.setProjectionMatrix(camera.combined);
        Main.batch.begin();

        Main.batch.draw(logo, (Gdx.graphics.getWidth() / 2f) - (176 * 3 / 2f), 250, 176 * 3, 32 * 3);

        for (int i = 0; i < NUM_LEVELS; i++) {
            Texture tex;
            if (!levelExists[i]) {
                tex = progress.canAccess(i) ? levelPegPlaceholder : levelPegLocked;
                Main.batch.draw(tex, getPegX(i), PEG_Y, PEG_SIZE, PEG_SIZE);
                continue;
            }
            if (progress.isCompleted(i)) {
                tex = levelPegsCompleted[i];
            } else if (progress.canAccess(i)) {
                tex = levelPegs[i];
            } else {
                tex = levelPegLocked;
            }
            Main.batch.draw(tex, getPegX(i), PEG_Y, PEG_SIZE, PEG_SIZE);
        }

        float exitXpos = Gdx.graphics.getWidth() - 80f;
        float exitYpos = Gdx.graphics.getHeight() - 80f;
        Main.batch.draw(exit, exitXpos, exitYpos, 64, 64);

        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            float mouseX = Gdx.input.getX() * (camera.viewportWidth / Gdx.graphics.getWidth());
            float mouseY = (Gdx.graphics.getHeight() - Gdx.input.getY()) * (camera.viewportHeight / Gdx.graphics.getHeight());

            for (int i = 0; i < NUM_LEVELS; i++) {
                if (!levelExists[i]) continue;
                float px = getPegX(i);
                if (mouseX > px && mouseX < px + PEG_SIZE && mouseY > PEG_Y && mouseY < PEG_Y + PEG_SIZE) {
                    if (progress.canAccess(i)) {
                        levelChoice = i + 1;
                        startGame = true;
                    }
                }
            }

            if (mouseX > exitXpos && mouseX < exitXpos + 64 && mouseY > exitYpos && mouseY < exitYpos + 64) {
                Gdx.app.exit();
            }
        }

        Main.batch.end();
    }

    public void updateViewport(int width, int height) { camera.setToOrtho(false, width, height); }

    public boolean startGame() {
        if (startGame) {
            startGame = false;
            return true;
        }
        return false;
    }

    public int getLevel() { return levelChoice; }
    public void reset() { startGame = false; }

    public void dispose() {
        logo.dispose();
        exit.dispose();
        levelPegLocked.dispose();
        levelPegPlaceholder.dispose();
        for (int i = 0; i < NUM_LEVELS; i++) {
            levelPegs[i].dispose();
            levelPegsCompleted[i].dispose();
        }
    }
}