package io.github.some_example_name;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;



public class Player {
    Animation<TextureRegion> tiltAnimation;
    private int bg_x = 0, bg_y = 0;
    private Texture tiltSheet;
    private Texture stay;
    private Rectangle rectangle;
    private TextureRegion stayRegion;
    public int state = STATE_STAY;
    public static final int STATE_STAY = 1;
    public static final int STATE_TILT = 2;
    public static final int STATE_ALIVE = 3;
    private boolean directionRight = true;
    private float tiltTimer = 0f;
    private boolean isTilting = false;
    private Texture explSheet;



    public Player() {
        tiltSheet = new Texture("shipanimation.png");
        stay = new Texture("kenney_space-shooter-redux/PNG/playerShip2_red.png");
        stayRegion = new TextureRegion(stay);
        TextureRegion[] tiltFrames = TextureRegion.split(tiltSheet,
            tiltSheet.getWidth() / 4, tiltSheet.getHeight())[0];
        tiltAnimation = new Animation<TextureRegion>(0.1f, tiltFrames);
        rectangle = new Rectangle(bg_x, bg_y, 70, 40);
        explSheet = new Texture("1_yIEkKeFkho8P4-Xmr0KI9g.png");
        TextureRegion[][] tmpFrames = TextureRegion.split(
            explSheet,
            explSheet.getWidth() / 8,
            explSheet.getHeight() / 4
        );
    }

    public Rectangle getRectangle() {
        return rectangle;
    }

    public int getWidth() {
        return (int) this.rectangle.getWidth();
    }

    public int getHeight() {
        return (int) this.rectangle.getHeight();
    }

    public int getX() {
        return (int) this.rectangle.getX();
    }

    public int getY() {
        return (int) this.rectangle.getY();
    }

    public void setX(int x) {
        this.rectangle.x = x;
    }

    public void setY(int y) {
        this.rectangle.y = y;
    }

    public void left() {
        rectangle.x -= 5;
        if (!isTilting) {
            tiltTimer = 0f;
            isTilting = true;
            state = STATE_TILT;
        }
        directionRight = false;
    }

    public void right() {
        rectangle.x += 5;
        if (!isTilting) {
            tiltTimer = 0f;
            isTilting = true;
            state = STATE_TILT;
        }
        directionRight = true;
    }

    public void setBg_x(int Bg_x) {
        this.bg_x = Bg_x;
    }

    public void setBg_y(int Bg_y) {
        this.bg_y = Bg_y;
    }

    public void setState(int state) {
        this.state = state;
    }

    public void setTiltTimer(float tiltTimer) {
        this.tiltTimer = tiltTimer;
    }

    public void draw(SpriteBatch spriteBatch) {
        spriteBatch.draw(getCurrentTextureRegion(), rectangle.x, rectangle.y,
            rectangle.width / 2, 0,
            rectangle.width, rectangle.height,
            directionRight ? 1 : -1, 1, 0);

    }

    public void update(float delta) {
        if (state == STATE_TILT) {
            if (tiltTimer < tiltAnimation.getAnimationDuration()) {
                tiltTimer += delta;
            } else {
                // остаёмся на последнем кадре
                tiltTimer = tiltAnimation.getAnimationDuration();
            }
        }
    }

    public void stopTilting() {
        isTilting = false;
        state = STATE_STAY;
        tiltTimer = 0f;
    }




    public TextureRegion getCurrentTextureRegion() {
        TextureRegion currentTiltFrame = tiltAnimation.getKeyFrame(this.tiltTimer, false);
        if (state == STATE_TILT)
            return currentTiltFrame;
        return stayRegion;
    }
}
