package cs.BabyLasagna;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import cs.BabyLasagna.GameObj.PlayerProgress;

public class Menu {
    private OrthographicCamera camera;
    private Texture name;
    private Texture exit;
    private Texture levelLabel;
    private BitmapFont levelFont;
    private GlyphLayout glyphLayout;

    private static final int   LEVEL_COUNT        = 2;
    private static final float LEVEL_LABEL_WIDTH   = 128f;
    private static final float LEVEL_LABEL_HEIGHT  = 64f;
    private static final float LEVEL_GAP           = 8f;
    private static final float[] LEVEL_BUTTON_YPOS = { 175, 115 };

    private boolean startGame = false;
    private int     levelChoice = 0;

    private final float buttonXpos;
    private final float exitXpos;
    private final float exitYpos;

    private final PlayerProgress progress;

    public Menu(PlayerProgress progress) {
        this.progress = progress;

        name       = new Texture("menu/logo.png");
        exit       = new Texture("menu/exit.png");
        levelLabel = new Texture("menu/level_label.png");

        levelFont   = new BitmapFont();
        levelFont.getData().setScale(2f);
        glyphLayout = new GlyphLayout();

        buttonXpos = (Gdx.graphics.getWidth() / 2f) - (LEVEL_LABEL_WIDTH / 2f);
        exitXpos   = Gdx.graphics.getWidth()  - 80;
        exitYpos   = Gdx.graphics.getHeight() - 80;

        camera = new OrthographicCamera();
        updateViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    public void render() {
        camera.update();
        Main.batch.setProjectionMatrix(camera.combined);
        Main.batch.begin();

        Main.batch.draw(name, (Gdx.graphics.getWidth() / 2f) - (176 * 3 / 2f), 250, 176 * 3, 32 * 3);
        Main.batch.draw(exit, exitXpos, exitYpos, 96, 96);

        for (int i = 0; i < LEVEL_COUNT; i++) {
            drawLevelButton(buttonXpos, LEVEL_BUTTON_YPOS[i], i);
        }

        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            float mouseX = Gdx.input.getX() * (camera.viewportWidth  / Gdx.graphics.getWidth());
            float mouseY = (Gdx.graphics.getHeight() - Gdx.input.getY()) * (camera.viewportHeight / Gdx.graphics.getHeight());

            for (int i = 0; i < LEVEL_COUNT; i++) {
                float totalWidth = LEVEL_LABEL_WIDTH + LEVEL_GAP + glyphNumberWidth(i + 1);
                if (mouseX > buttonXpos && mouseX < buttonXpos + totalWidth
                    && mouseY > LEVEL_BUTTON_YPOS[i] && mouseY < LEVEL_BUTTON_YPOS[i] + LEVEL_LABEL_HEIGHT) {
                    if (progress.canAccess(i)) {
                        levelChoice = i + 1;
                        startGame   = true;
                    }
                }
            }

            if (mouseX > exitXpos && mouseX < exitXpos + 96
                && mouseY > exitYpos && mouseY < exitYpos + 96) {
                Gdx.app.exit();
            }
        }

        Main.batch.end();
    }

    private void drawLevelButton(float x, float y, int levelIndex) {
        float tint = progress.canAccess(levelIndex) ? 1f : 0.4f;

        // "Level" label sprite
        Main.batch.setColor(tint, tint, tint, 1f);
        Main.batch.draw(levelLabel, x, y, LEVEL_LABEL_WIDTH, LEVEL_LABEL_HEIGHT);
        Main.batch.setColor(1f, 1f, 1f, 1f);

        // Number drawn flush to the right of the label, vertically centred
        String number = String.valueOf(levelIndex + 1);
        glyphLayout.setText(levelFont, number);

        float numX = x + LEVEL_LABEL_WIDTH + LEVEL_GAP;
        float numY = y + (LEVEL_LABEL_HEIGHT / 2f) + (glyphLayout.height / 2f);

        levelFont.setColor(tint, tint, tint, 1f);
        levelFont.draw(Main.batch, glyphLayout, numX, numY);
        levelFont.setColor(1f, 1f, 1f, 1f);
    }

    /** Returns the rendered pixel width of a level number — used for hit-box math. */
    private float glyphNumberWidth(int number) {
        glyphLayout.setText(levelFont, String.valueOf(number));
        return glyphLayout.width;
    }

    public void updateViewport(int width, int height) { camera.setToOrtho(false, width, height); }

    public boolean startGame() {
        if (startGame) { startGame = false; return true; }
        return false;
    }

    public int getLevel() { return levelChoice; }
    public void reset()   { startGame = false; }

    public void dispose() {
        name.dispose();
        exit.dispose();
        levelLabel.dispose();
        levelFont.dispose();
    }
}
