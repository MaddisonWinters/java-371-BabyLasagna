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

public class Menu {
    private OrthographicCamera camera;
    private Texture name;
    private Texture level1;
    private Texture level2;
    private Texture exit;

    private boolean startGame = false;
    private int levelChoice = 0;

    //button pos (Sorry Michael ik how u feel ab floats)
    float buttonXpos1 = 50;
    float buttonYpos1 = 150;
    float buttonXpos2 = 200;
    float buttonYpos2 = 145;
    float levelButtonWidth = 150;
    float levelButtonHeight = 100;

    float exitXpos = 470;
    float exitYpos = 375;
    float howXPos = 195;
    float howYPos = 300;

    public Menu(){

        name = new Texture("game_name.png");
        //button = new Texture("play_button.png");
        level1 = new Texture ("level1.png");
        level2 = new Texture ("level2.png");
        exit = new Texture ("exit.png");
        camera = new OrthographicCamera();
        updateViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    public void render(){
        camera.update();
        Main.batch.setProjectionMatrix(camera.combined);
        Main.batch.begin();

        Main.batch.draw(name,130,250,350,250);
        Main.batch.draw(level1,buttonXpos1,buttonYpos1,levelButtonWidth,levelButtonHeight);
        Main.batch.draw(level2,buttonXpos2,buttonYpos2,levelButtonWidth,levelButtonHeight);
        Main.batch.draw(exit,exitXpos,exitYpos,levelButtonWidth,levelButtonHeight);

        if(Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)){

            float mouseX = Gdx.input.getX() * (camera.viewportWidth / Gdx.graphics.getWidth());;
            float mouseY = (Gdx.graphics.getHeight() - Gdx.input.getY()) * (camera.viewportHeight / Gdx.graphics.getHeight());
            //level1
            if(mouseX > buttonXpos1 && mouseX < buttonXpos1 + levelButtonWidth
                && mouseY > buttonYpos1 && mouseY < buttonYpos1 + levelButtonHeight){
                levelChoice = 1;
                startGame = true;
            }
            //level2
            if(mouseX > buttonXpos2 && mouseX < buttonXpos2 + levelButtonWidth
                && mouseY > buttonYpos2 && mouseY < buttonYpos2 + levelButtonHeight){
                levelChoice = 2;
                startGame = true;
            }
            //exit
            if(mouseX > exitXpos && mouseX < exitXpos + levelButtonWidth
                && mouseY > exitYpos && mouseY < exitYpos + levelButtonHeight){
                Gdx.app.exit();
            }
        }

        Main.batch.end();
    }

    public void updateViewport(int width, int height) {camera.setToOrtho(false, width, height);}
    public boolean startGame(){
        return startGame;
    }

    public int getLevel(){
        return levelChoice;
    }

    public void dispose(){
        name.dispose();
        level1.dispose();
        //level2.dispose();
        exit.dispose();
    }
}
