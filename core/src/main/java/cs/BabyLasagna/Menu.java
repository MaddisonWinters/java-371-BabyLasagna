package cs.BabyLasagna;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.ApplicationAdapter;

public class Menu {
    //private Texture button;
    private Texture name;
    private Texture level1;
    //private Texture level2;
    private Texture exit;
    //private Texture howTo:

    private boolean startGame = false;
    private int levelChoice = 0;

    //button pos (Sorry Michael ik how u feel ab floats)
    float buttonXpos1 = 75;
    float buttonYpos1 = 150;
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
        //level2 = new Texture ("level2.png");
        exit = new Texture ("exit.png");
    }

    public void render(){

        Main.batch.begin();

        Main.batch.draw(name,130,250,350,250);
        Main.batch.draw(level1,buttonXpos1,buttonYpos1,levelButtonWidth,levelButtonHeight);
        //Main.batch.draw(level2,buttonXpos2,buttonYpos2,levelButtonWidth,levelButtonHeight);
        Main.batch.draw(exit,exitXpos,exitYpos,levelButtonWidth,levelButtonHeight);

        if(Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)){

            float mouseX = Gdx.input.getX();
            float mouseY = Gdx.graphics.getHeight() - Gdx.input.getY();
            //level1
            if(mouseX > buttonXpos1 && mouseX < buttonXpos1 + levelButtonWidth
                && mouseY > buttonYpos1 && mouseY < buttonYpos1 + levelButtonHeight){
                levelChoice = 1;
                startGame = true;
            }
            //level2
//            if(mouseX > buttonXpos2 && mouseX < buttonXpos2 + levelButtonWidth
//                && mouseY > buttonYpos2 && mouseY < buttonYpos2 + levelButtonHeight){
//                levelChoice = 2;
//                startGame = true;
//            }
            //exit
            if(mouseX > exitXpos && mouseX < exitXpos + levelButtonWidth
                && mouseY > exitYpos && mouseY < exitYpos + levelButtonHeight){
                Gdx.app.exit();
            }
        }

        Main.batch.end();
    }

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
