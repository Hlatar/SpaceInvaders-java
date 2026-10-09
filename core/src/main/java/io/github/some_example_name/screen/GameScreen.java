package io.github.some_example_name.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import io.github.some_example_name.*;

import java.util.Random;
import java.util.concurrent.RecursiveAction;


public class GameScreen implements Screen{



    private static final float VIRTUAL_WIDTH = 800f;
    private static final float VIRTUAL_HEIGHT = 480f;
    private float scaleX = VIRTUAL_WIDTH / 637f;
    private float scaleY = VIRTUAL_HEIGHT / 477f;
    private float scoreTimer = 0f;
    private int score = 0;
    private int return_height = (int)(120 * scaleY);
    private int return_width =(int)(230 * scaleX);
    private int menu_height =(int)(120 * scaleY);
    private int menu_width =(int)(200 * scaleX);
    private int bg_x = 0, bg_y = 0;
    private int ufo_y = 0;
    private int highScore = 0;
    private long lastEnemyDropTime = 0;
    private boolean gameOver = false;
    private Player Ship;
    private BitmapFont font;
    private Texture restart;
    private Texture menu;
    private Texture btnFireRegular;
    private Texture btnFireClicked;
    private Texture btnLeftRegular;
    private Texture btnLeftClicked;
    private Texture btnRightRegular;
    private Texture btnRightClicked;
    private Texture image;
    private Texture ufo;
    private Texture background;
    private TextureRegion regionFire;
    private MyButton btnFire;
    private MyButton btnLeft;
    private MyButton btnRight;
    private Array<Enemy> EnemyList = new Array<>();
    private Array<Rectangle> FireList = new Array<>();
    private Music musicBackgroud;
    private Sound hitSound;
    private Sound explSound;
    private Sound reloadSound;
    private OrthographicCamera camera;
    private Viewport viewport;
    private final Vector3 touchPos = new Vector3();
    private final Rectangle restartRect =
        new Rectangle(VIRTUAL_WIDTH/2f - return_width/2f,
            VIRTUAL_HEIGHT/2f - return_height/2f,
            return_width, return_height);

    private final Rectangle menuRect =
        new Rectangle(VIRTUAL_WIDTH/2f - menu_width/2f,
            VIRTUAL_HEIGHT/2f - menu_height/2f - return_height,
            menu_width, menu_height);

    // --- параметры оружия ---
    private static final int   MAG_SIZE     = 10;   // патронов в магазине
    private static final float RELOAD_TIME  = 2f;   // секунд на перезарядку // 2f
    private static final float FIRE_RATE    = 0.3f;// задержка между выстрелами //0.15f

    // --- состояние ---
    private int   ammoInMag   = MAG_SIZE;
    private float reloadTimer = 0f;     // >0 → идёт перезарядка
    private float fireCooldown = 0f;    // >0 → ждём следующего выстрела

    private float spawnInterval    = 1.0f;      // секунд между спавном
    private float spawnAccel       = 0.005f;    // каждую секунду уменьшаем интервал
    private float minSpawnInterval = 0.3f;
    float difficulty = 1f;
    float baseSpeed = MathUtils.random(60f, 120f);
    private static final int MAX_ENEMIES = 25;

    private static final float BULLET_SPEED = 600f; // px/sec
    private float spawnTimer       = 0f;
    private StringBuilder sb ;



    Pool<Enemy> enemyPool = new Pool<Enemy>() {
        @Override protected Enemy newObject() {
            return new Enemy((int)VIRTUAL_WIDTH, (int)VIRTUAL_HEIGHT);
        }
    };

    private final Pool<Rectangle> bulletPool = new Pool<Rectangle>() {
        @Override protected Rectangle newObject() { return new Rectangle(); }
    };




    private int sx(float oldX){
        return (int)(VIRTUAL_WIDTH  * oldX / 637f);
    }   // scale X
    private int sy(float oldY){
        return (int)(VIRTUAL_HEIGHT * oldY / 477f); }
    // scale Y

    Main game;
    public GameScreen(Main game){
        this.game = game;
    }




    @Override
    public void show() {
        font = new BitmapFont(Gdx.files.internal("fonts/fonts.fnt"));
        sb = new StringBuilder();
        ufo = new Texture("kenney_space-shooter-redux/PNG/ufoGreen.png");
        background = new Texture("kenney_space-shooter-redux/Backgrounds/darkPurple.png");
        image = new Texture("kenney_space-shooter-redux/Spritesheet/sheet.png");
        btnFireRegular = new Texture("PlayStation Series/Default/playstation_button_color_cross_outline.png");
        btnFireClicked = new Texture("PlayStation Series/Default/playstation_button_color_cross.png");
        btnLeftRegular = new Texture("PlayStation Series/Default/playstation_trigger_l1_alternative_outline.png");
        btnLeftClicked = new Texture("PlayStation Series/Default/playstation_trigger_l1_alternative.png");
        btnRightRegular = new Texture("PlayStation Series/Default/playstation_trigger_r1_alternative_outline.png");
        btnRightClicked = new Texture("PlayStation Series/Default/playstation_trigger_r1_alternative.png");
        restart = new Texture("Restart.png");
        menu = new Texture("Menu.png");

        regionFire = new TextureRegion(image, 842 , 793 , 13, 56);

        Ship = new Player();

        Ship.setX((int)VIRTUAL_WIDTH/2 - Ship.getWidth()/2);
        Ship.setY( Ship.getHeight()/2);

        btnFire  = new MyButton(btnFireRegular, btnFireClicked, sx(487), sy(100), sx(70), sy(70));
        btnLeft  = new MyButton(btnLeftRegular, btnLeftClicked, sx( 80), sy( 80), sx(70), sy(70));
        btnRight = new MyButton(btnRightRegular, btnRightClicked, sx(140), sy( 80), sx(70), sy(70));

        camera = new OrthographicCamera();
        viewport = new FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera);
        viewport.apply();
        camera.position.set(VIRTUAL_WIDTH / 2f, VIRTUAL_HEIGHT / 2f, 0);

        hitSound = Gdx.audio.newSound(Gdx.files.internal("lazerSound.mp3"));
        explSound = Gdx.audio.newSound(Gdx.files.internal("explosion.wav"));
        musicBackgroud = Gdx.audio.newMusic(Gdx.files.internal("gamebgsound.mp3"));
        musicBackgroud.setLooping(true);
        musicBackgroud.setVolume(0.5f);
        musicBackgroud.play();
        reloadSound = Gdx.audio.newSound(Gdx.files.internal("reload.mp3"));
        highScore = PreferencesHelper.getHighScore();


    }

    @Override
    public void render(float v) {

        camera.update();
        game.batch.setProjectionMatrix(camera.combined);
        // таймеры
        if (reloadTimer > 0f)  reloadTimer -= v;
        if (fireCooldown > 0f) fireCooldown -= v;

// закончилась перезарядка?
        if (reloadTimer <= 0f && ammoInMag == 0) {
            ammoInMag = MAG_SIZE;
        }


        boolean btnFireClicked = false;
        boolean btnLeftisClicked = false;
        boolean btnRightisClicked = false;

        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
        spawnInterval = Math.max(minSpawnInterval,
            spawnInterval - spawnAccel * v);
        game.batch.begin();
        Gdx.app.log("Memory", "Java Heap: " + Gdx.app.getJavaHeap() + ", Native Heap: " + Gdx.app.getNativeHeap());


        if (Ship.getX() < 0)
            Ship.setX(0);
        if (Ship.getX() > (int)VIRTUAL_WIDTH - Ship.getWidth())
            Ship.setX((int)VIRTUAL_WIDTH - Ship.getWidth());
        if (Ship.getY() < 0)
            Ship.setY(0);
        if (Ship.getY() > (int)VIRTUAL_HEIGHT - Ship.getHeight())
            Ship.setY((int)VIRTUAL_HEIGHT - Ship.getHeight()); // here ship out of bound check

        spawnTimer += v;
        spawnInterval = Math.max(minSpawnInterval, spawnInterval - spawnAccel * v);

        while (spawnTimer >= spawnInterval) {
            spawnTimer -= spawnInterval;
            if (EnemyList.size < MAX_ENEMIES) {
                Enemy e = enemyPool.obtain();
                e.reset();
                EnemyList.add(e);
            }
        }


        // создание врага
        difficulty += 0.0000005f;
        game.batch.draw(background, bg_x , bg_y, (int)VIRTUAL_WIDTH, (int)VIRTUAL_HEIGHT );
        game.batch.draw(background, bg_x , bg_y + (int)VIRTUAL_HEIGHT, (int)VIRTUAL_WIDTH, (int)VIRTUAL_HEIGHT );
        game.batch.draw(ufo, bg_x + 128, ufo_y + (int)VIRTUAL_HEIGHT);

        Ship.draw(game.batch);

        for (Enemy enemy : EnemyList) {
            enemy.update(v);
            enemy.draw(game.batch);
        }

        for (Rectangle fire : FireList) {
            game.batch.draw(regionFire, fire.x, fire.y, fire.width, fire.height);
        } //  Прорисовка всех существ на карте

        if (!gameOver) {
            Ship.update(v);

            scoreTimer += v;
            ufo_y--;
            bg_y -= 2;
            if (scoreTimer >= 0.2f) {
                score++;
                scoreTimer = 0f;
            }
            if (bg_y < -(int)VIRTUAL_HEIGHT){
                bg_y = 0;
            }
            if (ufo_y < -91 - (int)VIRTUAL_HEIGHT){
                ufo_y = (int)VIRTUAL_HEIGHT;
            }

            game.batch.draw(btnLeft.getTexture(), btnLeft.getX(), btnLeft.getY(), btnLeft.getWidth(), btnLeft.getHeight());
            game.batch.draw(btnRight.getTexture(), btnRight.getX(), btnRight.getY(), btnRight.getWidth(), btnRight.getHeight());
            for (int i = 0; i < 10; i++) {
                if (Gdx.input.isTouched(i)) {
                    touchPos.set(Gdx.input.getX(i), Gdx.input.getY(i), 0);
                    viewport.unproject(touchPos); // Преобразование из экранных координат в мировые (виртуальные)

                    int touchX = (int) touchPos.x  ;
                    int touchY = (int) touchPos.y;

                    btnLeftisClicked = btnLeft.checkClick(touchX, touchY) || btnLeftisClicked;
                    btnRightisClicked = btnRight.checkClick(touchX, touchY) || btnRightisClicked;
                    btnFireClicked = btnFire.checkClick(touchX, touchY) || btnFireClicked;
                }
            }
            btnLeft.setState(btnLeftisClicked);
            btnRight.setState(btnRightisClicked);
            btnFire.setState(btnFireClicked);


            boolean wantFire = btnFireClicked      // палец сейчас на кнопке
                && !btnFire.wasPressed() // …а в предыдущем кадре НЕ лежал
                && fireCooldown <= 0f
                && reloadTimer <= 0f
                && ammoInMag   > 0;

            if (wantFire) {
                hitSound.play();

                Rectangle b = bulletPool.obtain();
                b.set(Ship.getX() + Ship.getWidth()/2f - 7,
                    Ship.getY() + Ship.getHeight(),
                    13, 58);
                FireList.add(b);

                ammoInMag--;
                fireCooldown = FIRE_RATE;
                if (ammoInMag == 0){
                    reloadSound.play();
                    reloadTimer = RELOAD_TIME;
                }
            }

            sb.setLength(0); // Очищаем перед использованием
            sb.append("score: ").append(score);
            font.draw(game.batch, sb, 20, (int)VIRTUAL_HEIGHT - 20);

            sb.setLength(0);
            sb.append("Ammo: ").append(ammoInMag);
            if (reloadTimer > 0) {
                sb.append("  | RELOADING...");
            }
            font.draw(game.batch, sb, 20, VIRTUAL_HEIGHT - 50);




            if (btnLeft.isClickedState() ^ btnRight.isClickedState()) {
                if (btnLeft.isClickedState()) {
                    Ship.left();
                } else {
                    Ship.right();
                }
            } else {
                Ship.stopTilting(); // Обе отпущены или обе нажаты — остаемся на месте
            }

            for (Enemy enemy : EnemyList) {
                enemy.getRectangle().y -= baseSpeed * difficulty * v; // -2
                if (enemy.getRectangle().y < -enemy.getRectangle().height)
                    EnemyList.removeValue(enemy, true);
                if (enemy.state == Enemy.STATE_ALIVE &&
                    Ship.getRectangle().overlaps(enemy.getRectangle())) { //есть попадание
                    if (score > highScore) {
                        highScore = score;
                        PreferencesHelper.setHighScore(highScore); // Сохраняем новый рекорд
                    }
                    gameOver = true;
                }
            }
            game.batch.draw(btnFire.getTexture(), btnFire.getX(), btnFire.getY(), btnFire.getWidth(), btnFire.getHeight());


            for (Rectangle fire : FireList) {
                fire.y += BULLET_SPEED * v;
                if (fire.y > (int)VIRTUAL_HEIGHT) {
                    FireList.removeValue(fire, true);
                    bulletPool.free(fire);
                }
                for (Enemy enemy : EnemyList) {
                    if (fire.overlaps(enemy.getRectangle()) && enemy.state == Enemy.STATE_ALIVE) {
                        explSound.play();
                        enemy.hit(); // запустить анимацию
                        FireList.removeValue(fire, true);
                        bulletPool.free(fire);
                        score += 10;
                    }

                }
            }
        }

        if (gameOver) {
            game.batch.setColor(0, 0, 0, 0.7f); // Полупрозрачный чёрный фон
            game.batch.draw(background, 0, 0, (int)VIRTUAL_WIDTH, (int)VIRTUAL_HEIGHT); // Просто чёрный фон (или свой)
            game.batch.setColor(1, 1, 1, 1); // Возвращаем цвет
            game.batch.draw(restart, (int)VIRTUAL_WIDTH/2 - return_width/2, (int)VIRTUAL_HEIGHT/2 - return_height/2, return_width , return_height);
            game.batch.draw(menu, (int)VIRTUAL_WIDTH/2 - menu_width/2, (int)VIRTUAL_HEIGHT/2 - menu_height/2 - return_height, menu_width, menu_height);
            if (Gdx.input.justTouched()) {          // клик в этом кадре
                touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
                viewport.unproject(touchPos);       // экран → мир

                if (restartRect.contains(touchPos.x, touchPos.y)) {
                    if (score > highScore) {
                        highScore = score;
                        PreferencesHelper.setHighScore(highScore); // Сохраняем новый рекорд
                    }
                    game.setScreen(new GameScreen(game));   // перезапуск
                    this.dispose();
                } else if (menuRect.contains(touchPos.x, touchPos.y)) {
                    game.setScreen(new MenuScreen(game));   // выход в меню
                    this.dispose();
                }
            }
        }



        for (int i=EnemyList.size-1; i>=0; i--) {
            if (EnemyList.get(i).isDead()) {
                enemyPool.free(EnemyList.removeIndex(i));
            }
        }

        game.batch.end();
    }




    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void pause() {

    }



    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        image.dispose();
        musicBackgroud.stop();
        musicBackgroud.dispose();
        hitSound.dispose();
        explSound.dispose();
        ufo.dispose();
        background.dispose();
        restart.dispose();
        menu.dispose();
        btnFireRegular.dispose();
        btnFireClicked.dispose();
        btnLeftRegular.dispose();
        btnLeftClicked.dispose();
        btnRightRegular.dispose();
        btnRightClicked.dispose();
        font.dispose();
        reloadSound.dispose();
    }
}




