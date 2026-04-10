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

    // Button sizes
    float width = 64*3;
    float height = 16*3;

    // Button X/Y Positions
    float pausedX = (Gdx.graphics.getWidth() / 2f) - (96*3 / 2f);
    float resumeX = (Gdx.graphics.getWidth() / 2f) - (width / 2f);
    float restartX = (Gdx.graphics.getWidth() / 2f) - (width / 2f);
    float menuX = (Gdx.graphics.getWidth() / 2f) - (width / 2f);

    float resumeY = 225;
    float restartY = 175;
    float menuY = 125;

    public PausedMenu(){
        paused = new Texture("menu/paused.png");
        resume = new Texture("menu/resume.png");
        restart = new Texture("menu/restart.png");
        mainMenu = new Texture("menu/menu.png");
        camera = new OrthographicCamera();
        updateViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    public void render(){
        camera.update();
        Main.batch.setProjectionMatrix(camera.combined);
        Main.batch.begin();

        Main.batch.draw(paused, pausedX,70, 112*3, 112*3);
        Main.batch.draw(resume, resumeX, resumeY, width, height);
        Main.batch.draw(restart, restartX, restartY, width, height);
        Main.batch.draw(mainMenu, menuX, menuY, width, height);

        if(Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)){

            float mouseX = Gdx.input.getX() * (camera.viewportWidth / Gdx.graphics.getWidth());;
            float mouseY = (Gdx.graphics.getHeight() - Gdx.input.getY()) * (camera.viewportHeight / Gdx.graphics.getHeight());

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
