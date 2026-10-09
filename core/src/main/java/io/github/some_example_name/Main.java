package io.github.some_example_name;


import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.some_example_name.screen.GameScreen;
import io.github.some_example_name.screen.MenuScreen;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends Game {
    public SpriteBatch batch;

    @Override
    public void create() {
        batch = new SpriteBatch();
        setScreen(new MenuScreen(this));
    }


    @Override
    public void render() {
        super.render();

    }

    @Override
    public void dispose() {

    }
}



