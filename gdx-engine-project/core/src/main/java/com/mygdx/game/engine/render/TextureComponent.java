package com.mygdx.game.engine.render;

import com.badlogic.gdx.graphics.Texture;
import com.mygdx.game.engine.ecs.Component;

public class TextureComponent extends Component {

    private Texture texture;
    private boolean flipX = false;

    public TextureComponent(String assetPath) {
        this.texture = new Texture(assetPath);
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