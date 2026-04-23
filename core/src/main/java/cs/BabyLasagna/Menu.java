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
    private Texture level1;
    private Texture level2;
    private Texture exit;

    private boolean startGame = false;
    private int levelChoice = 0;

    float levelButtonWidth = 112*2;
    float levelButtonHeight = 32*2;

    //button pos (Sorry Michael ik how u feel ab floats)
    float buttonXpos1 = (Gdx.graphics.getWidth() / 2f) - (176*3 / 2f);
    float buttonXpos2 = (Gdx.graphics.getWidth() / 2f) - (176*3 / 2f);
    float buttonYpos1 = 175;
    float buttonYpos2 = 115;

    float exitXpos = Gdx.graphics.getWidth() - 80;
    float exitYpos = Gdx.graphics.getHeight() - 80;

    private final PlayerProgress progress;

    public Menu(PlayerProgress progress){
        this.progress = progress;

        name = new Texture("menu/logo.png");
        //button = new Texture("menu/play.png");
        level1 = new Texture ("menu/level1.png");
        level2 = new Texture ("menu/level2.png");
        exit = new Texture ("menu/exit.png");
        camera = new OrthographicCamera();
        updateViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    public void render(){
        camera.update();
        Main.batch.setProjectionMatrix(camera.combined);
        Main.batch.begin();
        drawLevelButton(level1, buttonXpos1, buttonYpos1, 0);
        drawLevelButton(level2, buttonXpos2, buttonYpos2, 1);
        Main.batch.draw(name,(Gdx.graphics.getWidth() / 2f) - (176*3 / 2f),250,176*3,32*3);

        Main.batch.draw(exit,exitXpos,exitYpos,96,96);

        if(Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)){

            float mouseX = Gdx.input.getX() * (camera.viewportWidth / Gdx.graphics.getWidth());;
            float mouseY = (Gdx.graphics.getHeight() - Gdx.input.getY()) * (camera.viewportHeight / Gdx.graphics.getHeight());

            //level1 button
            if(mouseX > buttonXpos1 && mouseX < buttonXpos1 + levelButtonWidth
                && mouseY > buttonYpos1 && mouseY < buttonYpos1 + levelButtonHeight){
                if (progress.canAccess(0)) {
                    levelChoice = 1;
                    startGame = true;
                }
            }
            //level2 button
            if(mouseX > buttonXpos2 && mouseX < buttonXpos2 + levelButtonWidth
                && mouseY > buttonYpos2 && mouseY < buttonYpos2 + levelButtonHeight){
                if (progress.canAccess(1)) {
                    levelChoice = 2;
                    startGame = true;
                }
            }
            //exit button
            if(mouseX > exitXpos && mouseX < exitXpos + levelButtonWidth
                && mouseY > exitYpos && mouseY < exitYpos + levelButtonHeight){
                Gdx.app.exit();
            }
        }

        Main.batch.end();
    }

    public void updateViewport(int width, int height) {camera.setToOrtho(false, width, height);}
    public boolean startGame(){
        if(startGame){
            startGame = false; //needs to be here this is what caused main menu not to pop up when the button is pressed from pause menu
            return true;
        }
        return false;
    }

    public int getLevel(){
        return levelChoice;
    }
    public void reset(){startGame = false;}

    public void dispose(){
        name.dispose();
        level1.dispose();
        //level2.dispose();
        exit.dispose();
    }

    // Greys out locked levels
    private void drawLevelButton(Texture texture, float x, float y, int levelIndex) {
        float tint = progress.canAccess(levelIndex) ? 1f : 0.4f;
        Main.batch.setColor(tint, tint, tint, 1f);
        Main.batch.draw(texture, x, y, levelButtonWidth, levelButtonHeight);
        Main.batch.setColor(1f, 1f, 1f, 1f);
    }
}
