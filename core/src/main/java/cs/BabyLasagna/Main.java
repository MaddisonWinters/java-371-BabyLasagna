package cs.BabyLasagna;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {
    private static SpriteBatch batch;
    private static Game game = null;

    private boolean menu = true;
    private Texture button;
    private Texture name;

    //button pos (Sorry Michael ik how u feel ab floats)
    float buttonXpos = 192;
    float buttonYpos = 150;
    float buttonWidth = 220;
    float buttonHeight = 120;

    @Override
    public void create() {
        batch = new SpriteBatch();

        name = new Texture ("game_name.png");
        button = new Texture ("play_button.png");

    }

    @Override
    public void render() {
        float deltaTime = Gdx.graphics.getDeltaTime();
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
        //if esc is pressed need to paused game & bring up map
        //add exit button
        //add level options
        //add mini pop up for user tutorial
        //reset button on death
        if(menu){
            batch.begin();

            batch.draw(name, 130, 250, 350, 250);
            batch.draw(button,buttonXpos,buttonYpos,buttonWidth,buttonHeight);

            //if clicked on
            if(Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)){
                //get mouse pos
                float mouseX = Gdx.input.getX();
                float mouseY = Gdx.graphics.getHeight() - Gdx.input.getY();
                //does click happen on button
                if(mouseX > buttonXpos && mouseX < buttonXpos + buttonWidth && mouseY > buttonYpos && mouseY < buttonYpos + buttonHeight){
                    game = new Game("test");
                    menu = false;
                }
            }

            batch.end();
            return;
        }

        game.update(deltaTime);
        game.render(deltaTime, batch);
    }

    @Override
    public void dispose() {
        batch.dispose();
        game.dispose();
        button.dispose();
        name.dispose();

    }

    @Override
    public void resize(int width, int height) {
        final int MAX_VIEWPORT_SIZE = 20;

        // If the window is minimized on a desktop (LWJGL3) platform, width and height are 0
        if(width <= 0 || height <= 0) return;
        if(game !=null) {
            game.updateViewport(width, height);
        }
    }
}
