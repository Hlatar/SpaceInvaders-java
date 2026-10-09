package io.github.some_example_name.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;
import io.github.some_example_name.Main;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.viewport.FitViewport;

import io.github.some_example_name.PreferencesHelper;

public class MenuScreen implements Screen {
    // --- константы ---
    private static final int WORLD_W = 800;
    private static final int WORLD_H = 480;
    private static final int PLAY_W  = 230, PLAY_H = 120;
    private static final int EXIT_W  = 200, EXIT_H = 120;
    private static final int RESET_W = 386, RESET_H = 100;

    // --- LibGDX объекты ---
    private final OrthographicCamera camera = new OrthographicCamera();
    private final FitViewport viewport = new FitViewport(WORLD_W, WORLD_H, camera);
    private final Main game;

    // --- UI ресурсы ---
    private Texture icon;
    private Texture playOn,  playOff;
    private Texture exitOn,  exitOff;
    private BitmapFont font;
    private Music bgm;

    // --- состояние ---
    private int highScore;
    private boolean justOpened = true;

    // -------- ctor --------
    public MenuScreen(Main game) {
        this.game = game;

        playOn  = new Texture("play_button_active.png");
        playOff = new Texture("play_button_inactive.png");
        exitOn  = new Texture("exit_button_active.png");
        exitOff = new Texture("exit_button_inactive.png");
        icon = new Texture("иконка.png" );

        font  = new BitmapFont(Gdx.files.internal("fonts/fonts.fnt"));
        highScore = PreferencesHelper.getHighScore();

        bgm = Gdx.audio.newMusic(Gdx.files.internal("menumusic.mp3"));
        bgm.setLooping(true);
    }

    // -------- lifecycle --------
    @Override public void show() {
        viewport.apply();
        camera.position.set(WORLD_W/2f, WORLD_H/2f, 0);
        camera.update();
        bgm.play();
    }

    @Override public void render(float delta) {
        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);

        // --- вычисляем фиксированные позиции (в мире 800×480) ---
        float playX  = WORLD_W/2f - PLAY_W/2f;
        float playY  = WORLD_H/2f - PLAY_H/2f;

        float exitX  = WORLD_W/2f - EXIT_W/2f;
        float exitY  = playY - EXIT_H - 5; // было -20

        float resetX = WORLD_W/2f - RESET_W/2f;
        float resetY = exitY - RESET_H - 5; // было -20

        // --- переводим ввод в тот же мир ---
        Vector3 touch = null;
        if (Gdx.input.justTouched()) {
            touch = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            viewport.unproject(touch);
        }

        // --- отрисовка ---
        game.batch.begin();

        // best score
        font.draw(game.batch, "Best: " + highScore, 20, WORLD_H - 20);

        // reset
        boolean overReset = touch != null && new Rectangle(resetX, resetY, RESET_W, RESET_H)
            .contains(touch.x, touch.y);
        font.draw(game.batch,
            overReset ? "> RESET HIGH SCORE <" : "  RESET HIGH SCORE  ",
            resetX, resetY + RESET_H );

        // play
        boolean overPlay = touch != null && new Rectangle(playX, playY, PLAY_W, PLAY_H)
            .contains(touch.x, touch.y);
        game.batch.draw(overPlay ? playOn : playOff, playX, playY, PLAY_W, PLAY_H);

        // exit
        boolean overExit = touch != null && new Rectangle(exitX, exitY, EXIT_W, EXIT_H)
            .contains(touch.x, touch.y);
        game.batch.draw(overExit ? exitOn : exitOff, exitX, exitY, EXIT_W, EXIT_H);
        game.batch.draw(icon, playX + 75/2,playX + 20,150 ,150);

        game.batch.end();

        // --- логика кликов ---
        if (touch != null) {
            if (overReset) {
                PreferencesHelper.setHighScore(0);
                highScore = 0;
            } else if (overPlay) {
                bgm.stop();
                game.setScreen(new GameScreen(game));
                this.dispose();
            } else if (overExit) {
                Gdx.app.exit();
            }
        }

        if (justOpened && !Gdx.input.isTouched()) justOpened = false;
    }

    @Override public void resize(int w, int h) { viewport.update(w, h, true); }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override public void hide() {
        bgm.stop();
    }
    @Override public void dispose() {
        playOn.dispose(); playOff.dispose();
        exitOn.dispose(); exitOff.dispose();
        font.dispose();    bgm.dispose();
    }
}
