package cs.BabyLasagna;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.ApplicationAdapter;

public class PausedMenu {
    private OrthographicCamera camera;
    private Texture paused;
    private Texture resume;
    private Texture restart;
    private Texture mainMenu;

    private boolean resumeLevel = false;
    private boolean restartLevel = false;
    private boolean goToMenu = false;

    float width = 150;
    float height = 100;

    float resumeX = 50;
    float resumeY = 150;

    float restartX = 220;
    float restartY = 150;

    float menuX = 390;
    float menuY = 150;

    public PausedMenu(){
        paused = new Texture("game_paused.png");
        resume = new Texture("resume.png");
        restart = new Texture("restart.png");
        mainMenu = new Texture("return.png");
        camera = new OrthographicCamera();
        updateViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    public void render(){
        camera.update();
        Main.batch.setProjectionMatrix(camera.combined);
        Main.batch.begin();

        Main.batch.draw(paused, 130,250,350,250);
        Main.batch.draw(resume, resumeX, resumeY, width, height);
        Main.batch.draw(restart, restartX, restartY, width, height);
        Main.batch.draw(mainMenu, menuX, menuY, width, height);

        if(Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)){

            float mouseX = Gdx.input.getX();
            float mouseY = Gdx.graphics.getHeight() - Gdx.input.getY();

            //resume button
            if(mouseX > resumeX && mouseX < resumeX + width
                && mouseY > resumeY && mouseY < resumeY + height){
                resumeLevel = true;
            }

            //restart button
            if(mouseX > restartX && mouseX < restartX + width
                && mouseY > restartY && mouseY < restartY + height){
                restartLevel = true;
            }

            //main menu button
            if(mouseX > menuX && mouseX < menuX + width
                && mouseY > menuY && mouseY < menuY + height){
                goToMenu = true;
            }
        }

        Main.batch.end();
    }

    public void updateViewport(int width, int height) {camera.setToOrtho(false, width, height);}

    public boolean resume(){
        if(resumeLevel){
            resumeLevel = false;
            return true;
        }
        return false;
    }

    public boolean restart(){
        if(restartLevel){
            restartLevel = false;
            return true;
        }
        return false;
    }

    public boolean mainMenu(){
        if(goToMenu){
            goToMenu = false;
            return true;
        }
        return false;
    }

    public void dispose(){
        paused.dispose();
        resume.dispose();
        restart.dispose();
        mainMenu.dispose();
    }
}
