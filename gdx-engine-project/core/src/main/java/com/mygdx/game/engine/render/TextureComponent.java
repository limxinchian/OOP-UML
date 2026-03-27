package com.mygdx.game.engine.render;

import com.badlogic.gdx.graphics.Texture;
import com.mygdx.game.engine.entity.Component;

public class TextureComponent extends Component {

    private Texture texture;
    private boolean flipX = false;

    public TextureComponent(String assetPath) {
        this.texture = new Texture(assetPath);
    }

    /**
     * Construct from an already-created Texture (e.g. procedurally generated
     * via Pixmap).  The component takes ownership and will dispose it.
     */
    public TextureComponent(Texture texture) {
        if (texture == null) throw new IllegalArgumentException("texture cannot be null");
        this.texture = texture;
    }

    public Texture getTexture() {
        return texture;
    }

    public boolean isFlipX() {
        return flipX;
    }

    public void setFlipX(boolean flipX) {
        this.flipX = flipX;
    }

    public void setTexture(String assetPath) {
        if (texture != null) {
            texture.dispose();
        }
        texture = new Texture(assetPath);
    }

    /**
     * Releases the GPU texture.  Called automatically by Entity.clearComponents()
     * or Entity.removeComponent() through the Component.dispose() lifecycle hook.
     *
     * Addresses: OOP resource lifecycle, prevents GPU texture leaks.
     */
    public void dispose() {
        if (texture != null) {
            texture.dispose();
            texture = null;
        }
    }

    @Override
    public void update(float deltaTime) {
    }
}