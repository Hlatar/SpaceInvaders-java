package io.github.some_example_name;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import io.github.some_example_name.screen.GameScreen;

import java.util.Random;

public class Enemy {
    private TextureRegion regionEnemy;
    private long lastEnemyDropTime = 0;
    private int screenWidth, screenHeight;
    private Random rand = new Random();
    private Rectangle rectangle;
    private Texture image;
    private Texture explSheet;
    Animation<TextureRegion> explAnimation;
    private float explTimer = 0f;
    public static final int STATE_ALIVE = 0;
    public static final int STATE_DEAD = 1;
    private TextureRegion stayRegion;
    public int state = STATE_ALIVE;


    public Enemy(int screenWidth, int screenHeight){
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        image = new Texture("kenney_space-shooter-redux/Spritesheet/sheet.png");
        regionEnemy = new TextureRegion(image, 225 , 0 , 100, 83);
        rectangle = new Rectangle(rand.nextInt(screenWidth - 36 + 1), screenHeight, 36, 50);
        explSheet = new Texture("1_yIEkKeFkho8P4-Xmr0KI9g.png");
        TextureRegion[][] tmpFrames = TextureRegion.split(
            explSheet,
            explSheet.getWidth() / 8,
            explSheet.getHeight() / 4
        );

// Собираем все кадры в один массив
        TextureRegion[] explFrames = new TextureRegion[8 * 4];
        int index = 0;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 8; j++) {
                explFrames[index++] = tmpFrames[i][j];
            }
        }
        explAnimation = new Animation<TextureRegion>(0.05f, explFrames);
        stayRegion = new TextureRegion(random_enem());
    }


    public void draw(SpriteBatch spriteBatch){
        TextureRegion region = getCurrentTextureRegion();
        float drawWidth = rectangle.width;
        float drawHeight = rectangle.height;

        if (state == STATE_DEAD) {
            drawWidth *= 4f;   // увеличиваем на 50%
            drawHeight *= 4f;
        }

        spriteBatch.draw(region,
            rectangle.x - (drawWidth - rectangle.width) / 2, // центрируем
            rectangle.y - (drawHeight - rectangle.height) / 2,
            drawWidth / 2, 0,
            drawWidth, drawHeight,
            1, 1, 0
        );
    }

    private Texture random_enem(){
        int i = rand.nextInt(3);
        switch (i) {
            case (0):
                return new Texture("kenney_space-shooter-redux/PNG/Enemies/enemyGreen3.png");
            case (1):
                return new Texture("kenney_space-shooter-redux/PNG/Enemies/enemyBlue3.png");
            case (2):
                return new Texture("kenney_space-shooter-redux/PNG/Enemies/enemyBlack3.png");
            default:
                return new Texture("kenney_space-shooter-redux/PNG/Enemies/enemyBlue3.png");
        }
    }


    public void update(float delta) {
        if (state != STATE_ALIVE) {
            explTimer += delta;
        }
    }

    public void hit() {
        if (state == STATE_ALIVE) {
            state = STATE_DEAD;
            explTimer = 0f;
        }
    }

    public void reset() {
        state = STATE_ALIVE;
        rectangle.set(rand.nextInt(screenWidth - 36 + 1), screenHeight, this.rectangle.width, this.rectangle.height);
        // обнуляем анимации/таймеры, что нужно…
    }

    public Rectangle getRectangle() {
        return rectangle;
    }

    public boolean isDead() {
        return state == STATE_DEAD && explAnimation.isAnimationFinished(explTimer);
    }


    public TextureRegion getCurrentTextureRegion() {
        TextureRegion currentExplFrame = explAnimation.getKeyFrame(this.explTimer, false);
        if(state == STATE_DEAD){
            return currentExplFrame;
        }
        return stayRegion;
    }

}
