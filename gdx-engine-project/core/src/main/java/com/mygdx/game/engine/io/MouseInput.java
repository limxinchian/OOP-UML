package com.mygdx.game.engine.io;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

/**
 * Engine-level mouse input utility.
 *
 * Provides a clean API for querying mouse state — position, button presses,
 * and rectangle hit-testing — through the engine's IO layer.  Scenes use
 * this instead of scattering raw {@code Gdx.input} calls into rendering code.
 *
 * Architecture note:
 * MouseInput lives in the engine's io package and is exposed through
 * IOManager.  It has no knowledge of menus, buttons, or any game-specific
 * concept — it only reports raw pointer state and geometric queries.
 *
 * LibGDX reports mouse Y with 0 at the <b>top</b> of the screen, while the
 * game world uses 0 at the <b>bottom</b>.  This class automatically flips
 * the Y axis so callers can use world coordinates directly.
 */
public class MouseInput {

    /**
     * @return the current mouse X position in world coordinates.
     */
    public float getX() {
        return Gdx.input.getX();
    }

    /**
     * @return the current mouse Y position in world coordinates
     *         (0 = bottom of screen, flipped from LibGDX's raw value).
     */
    public float getY() {
        // LibGDX reports Y from the top; game world has Y from the bottom.
        return Gdx.graphics.getHeight() - Gdx.input.getY();
    }

    /**
     * @return true if the left mouse button was just pressed this frame.
     */
    public boolean isLeftJustPressed() {
        return Gdx.input.isButtonJustPressed(Input.Buttons.LEFT);
    }

    /**
     * @return true if the left mouse button is currently held down.
     */
    public boolean isLeftPressed() {
        return Gdx.input.isButtonPressed(Input.Buttons.LEFT);
    }

    /**
     * Tests whether the current mouse position is inside a rectangle.
     * Useful for menu button hover detection.
     *
     * @param x      left edge of the rectangle (world coords)
     * @param y      bottom edge of the rectangle (world coords)
     * @param width  rectangle width
     * @param height rectangle height
     * @return true if the mouse pointer is inside the rectangle
     */
    public boolean isOver(float x, float y, float width, float height) {
        float mx = getX();
        float my = getY();
        return mx >= x && mx <= x + width && my >= y && my <= y + height;
    }

    /**
     * Convenience: returns true if the mouse was just clicked inside a rectangle.
     * Combines {@link #isOver(float, float, float, float)} with {@link #isLeftJustPressed()}.
     *
     * @param x      left edge of the rectangle
     * @param y      bottom edge of the rectangle
     * @param width  rectangle width
     * @param height rectangle height
     * @return true if left button just pressed AND pointer is inside the rect
     */
    public boolean isClickedInside(float x, float y, float width, float height) {
        return isLeftJustPressed() && isOver(x, y, width, height);
    }
}
