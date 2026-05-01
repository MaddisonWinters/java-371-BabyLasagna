package cs.BabyLasagna;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import cs.BabyLasagna.GameObj.PlayerProgress;

public class Menu {
    private static final int NUM_LEVELS = 5;

    // Virtual (reference) resolution all layout constants are defined against
    private static final float VWIDTH  = 640f;
    private static final float VHEIGHT = 480f;

    private static final float PEG_SIZE    = 80f;
    private static final float PEG_SPACING = 16f;
    private static final float PEG_Y       = 140f;

    private static final float LOGO_W = 528f;
    private static final float LOGO_H = 96f;
    private static final float LOGO_X = (VWIDTH - LOGO_W) / 2f;
    private static final float LOGO_Y = 250f;

    private static final float EXIT_SIZE = 64f;
    private static final float EXIT_X = VWIDTH  - 80f;
    private static final float EXIT_Y = VHEIGHT - 80f;

    private OrthographicCamera camera;
    private float scale = 1f, offsetX = 0f, offsetY = 0f;

    private Texture logo;
    private Texture exit;
    private final Texture[] levelPegs          = new Texture[NUM_LEVELS];
    private final Texture[] levelPegsCompleted = new Texture[NUM_LEVELS];
    private Texture levelPegLocked;
    private Texture levelPegPlaceholder;

    private boolean startGame = false;
    private int levelChoice = 0;

    private final PlayerProgress progress;
    private final boolean[] levelExists = new boolean[NUM_LEVELS];

    public Menu(PlayerProgress progress) {
        this.progress = progress;

        logo             = new Texture("menu/logo.png");
        exit             = new Texture("menu/exit.png");
        levelPegLocked      = new Texture("menu/levelpeg-locked.png");
        levelPegPlaceholder = new Texture("menu/levelpeg-placeholder.png");

        for (int i = 0; i < NUM_LEVELS; i++) {
            levelPegs[i]          = new Texture("menu/levelpeg-"           + (i + 1) + ".png");
            levelPegsCompleted[i] = new Texture("menu/levelpeg-completed-" + (i + 1) + ".png");
            levelExists[i] = Gdx.files.local("Levels/level" + (i + 1) + ".tmx").exists();
        }

        camera = new OrthographicCamera();
        updateViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    // Returns the virtual X of peg slot i
    private float pegVX(int i) {
        float totalWidth = NUM_LEVELS * PEG_SIZE + (NUM_LEVELS - 1) * PEG_SPACING;
        return (VWIDTH - totalWidth) / 2f + i * (PEG_SIZE + PEG_SPACING);
    }

    // Draws a texture at virtual coordinates, scaled and offset to screen space
    private void drawScaled(Texture tex, float vx, float vy, float vw, float vh) {
        Main.batch.draw(tex, offsetX + vx * scale, offsetY + vy * scale, vw * scale, vh * scale);
    }

    public void render() {
        camera.update();
        Main.batch.setProjectionMatrix(camera.combined);
        Main.batch.begin();

        drawScaled(logo, LOGO_X, LOGO_Y, LOGO_W, LOGO_H);

        for (int i = 0; i < NUM_LEVELS; i++) {
            Texture tex;
            if (!levelExists[i]) {
                tex = progress.canAccess(i) ? levelPegPlaceholder : levelPegLocked;
            } else if (progress.isCompleted(i)) {
                tex = levelPegsCompleted[i];
            } else if (progress.canAccess(i)) {
                tex = levelPegs[i];
            } else {
                tex = levelPegLocked;
            }
            drawScaled(tex, pegVX(i), PEG_Y, PEG_SIZE, PEG_SIZE);
        }

        drawScaled(exit, EXIT_X, EXIT_Y, EXIT_SIZE, EXIT_SIZE);

        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            // Map screen mouse position into virtual coordinate space
            float mx = (Gdx.input.getX() - offsetX) / scale;
            float my = ((Gdx.graphics.getHeight() - Gdx.input.getY()) - offsetY) / scale;

            for (int i = 0; i < NUM_LEVELS; i++) {
                if (!levelExists[i]) continue;
                float px = pegVX(i);
                if (mx > px && mx < px + PEG_SIZE && my > PEG_Y && my < PEG_Y + PEG_SIZE) {
                    if (progress.canAccess(i)) {
                        levelChoice = i + 1;
                        startGame = true;
                    }
                }
            }

            if (mx > EXIT_X && mx < EXIT_X + EXIT_SIZE && my > EXIT_Y && my < EXIT_Y + EXIT_SIZE) {
                Gdx.app.exit();
            }
        }

        Main.batch.end();
    }

    public void updateViewport(int width, int height) {
        camera.setToOrtho(false, width, height);
        scale   = Math.min(width / VWIDTH, height / VHEIGHT);
        offsetX = (width  - VWIDTH  * scale) / 2f;
        offsetY = (height - VHEIGHT * scale) / 2f;
    }

    public boolean startGame() {
        if (startGame) {
            startGame = false;
            return true;
        }
        return false;
    }

    public int getLevel() { return levelChoice; }
    public void reset()   { startGame = false; }

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