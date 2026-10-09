package io.github.some_example_name;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class MyButton {
    private Rectangle rectangle;
    private Texture texture, textureClicked;
    private boolean clickedState = false;
    private boolean pressed = false;


    public MyButton(Texture texture, Texture textureClicked, int x, int y, int width, int height) {
        this.texture = texture;
        this.textureClicked = textureClicked;
        rectangle = new Rectangle(x, y, width, height);
    }


    public boolean wasPressed() {
        return pressed;
    }

    public void setPressed(boolean pressed) {
        this.pressed = pressed;
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

    public void setState(boolean state) {
        this.clickedState = state;
    }

    public boolean isClickedState() {
        return clickedState;
    }

    public boolean checkClick(float x, float y) {
        return this.rectangle.contains(x, y);
    }

    public Texture getTexture() {
        return this.clickedState ? this.textureClicked : this.texture;
    }

}

