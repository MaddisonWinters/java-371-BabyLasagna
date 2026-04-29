package cs.BabyLasagna;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import cs.BabyLasagna.GameObj.Player;
import cs.BabyLasagna.GameObj.PlayerProgress;

public class Menu {
    private OrthographicCamera camera;
    private Texture name;
    private Texture exit;

    private static final String[] LEVEL_TEXTURE_PATHS = {
        "menu/level1.png",
        "menu/level2.png"
    };
    private static final float[] LEVEL_BUTTON_YPOS = { 175, 115 };

    private Texture[] levelTextures;

    private boolean startGame = false;
    private int levelChoice = 0;

    float levelButtonWidth  = 112 * 2;
    float levelButtonHeight = 32  * 2;
    float buttonXpos = (Gdx.graphics.getWidth() / 2f) - (176 * 3 / 2f);

    float exitXpos = Gdx.graphics.getWidth()  - 80;
    float exitYpos = Gdx.graphics.getHeight() - 80;

    private final PlayerProgress progress;

    public Menu(PlayerProgress progress) {
        this.progress = progress;

        name = new Texture("menu/logo.png");
        exit = new Texture("menu/exit.png");

        levelTextures = new Texture[LEVEL_TEXTURE_PATHS.length];
        for (int i = 0; i < LEVEL_TEXTURE_PATHS.length; i++) {
            levelTextures[i] = new Texture(LEVEL_TEXTURE_PATHS[i]);
        }

        camera = new OrthographicCamera();
        updateViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    public void render() {
        camera.update();
        Main.batch.setProjectionMatrix(camera.combined);
        Main.batch.begin();

        Main.batch.draw(name, (Gdx.graphics.getWidth() / 2f) - (176 * 3 / 2f), 250, 176 * 3, 32 * 3);
        Main.batch.draw(exit, exitXpos, exitYpos, 96, 96);

        for (int i = 0; i < levelTextures.length; i++) {
            drawLevelButton(levelTextures[i], buttonXpos, LEVEL_BUTTON_YPOS[i], i);
        }

        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            float mouseX = Gdx.input.getX() * (camera.viewportWidth  / Gdx.graphics.getWidth());
            float mouseY = (Gdx.graphics.getHeight() - Gdx.input.getY()) * (camera.viewportHeight / Gdx.graphics.getHeight());

            for (int i = 0; i < levelTextures.length; i++) {
                if (mouseX > buttonXpos && mouseX < buttonXpos + levelButtonWidth
                    && mouseY > LEVEL_BUTTON_YPOS[i] && mouseY < LEVEL_BUTTON_YPOS[i] + levelButtonHeight) {
                    if (progress.canAccess(i)) {
                        levelChoice = i + 1;
                        startGame  = true;
                    }
                }
            }

            if (mouseX > exitXpos && mouseX < exitXpos + levelButtonWidth
                && mouseY > exitYpos && mouseY < exitYpos + levelButtonHeight) {
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

    public int getLevel()  { return levelChoice; }
    public void reset()    { startGame = false; }

    public void dispose() {
        name.dispose();
        exit.dispose();
        for (Texture t : levelTextures) {
            if (t != null) t.dispose();
        }
    }

    private void drawLevelButton(Texture texture, float x, float y, int levelIndex) {
        float tint = progress.canAccess(levelIndex) ? 1f : 0.4f;
        Main.batch.setColor(tint, tint, tint, 1f);
        Main.batch.draw(texture, x, y, levelButtonWidth, levelButtonHeight);
        Main.batch.setColor(1f, 1f, 1f, 1f);
    }
}
