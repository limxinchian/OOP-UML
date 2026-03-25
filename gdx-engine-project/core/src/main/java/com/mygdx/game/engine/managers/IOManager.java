package com.mygdx.game.engine.managers;

import com.mygdx.game.engine.audio.AudioManager;
import com.mygdx.game.engine.io.InputManager;
import com.mygdx.game.engine.io.MouseInput;
import com.mygdx.game.engine.render.FontManager;
import com.mygdx.game.engine.render.OutputManager;

/**
 * Engine-level IO facade:
 * - InputManager  : dispatches keyboard commands to InputComponents
 * - MouseInput    : provides mouse position, click, and hit-test queries
 * - OutputManager : renders entities (shapes + textures) and text overlays
 * - FontManager   : loads, caches, and disposes BitmapFonts by name + size
 * - AudioManager  : loads, caches, and controls Sound and Music playback
 *
 * No context-specific game logic belongs here.
 *
 * Phase 6: Added AudioManager for engine-level audio support.
 */
public class IOManager implements IManager {

    private final InputManager inputManager;
    private final MouseInput mouseInput;
    private final OutputManager outputManager;
    private final FontManager fontManager;
    private final AudioManager audioManager;

    public IOManager(EntityManager entityManager) {
        this.inputManager = new InputManager(entityManager);
        this.mouseInput = new MouseInput();
        this.outputManager = new OutputManager();
        this.fontManager = new FontManager();
        this.audioManager = new AudioManager();
    }

    public InputManager getInput() { return inputManager; }
    public MouseInput getMouse() { return mouseInput; }
    public OutputManager getOutput() { return outputManager; }
    public FontManager getFontManager() { return fontManager; }
    public AudioManager getAudio() { return audioManager; }

    @Override
    public void initialize() {
        inputManager.initialize();
        outputManager.initialize();
    }

    @Override
    public void update(float deltaTime) {
        inputManager.update(deltaTime);
    }

    @Override
    public void shutdown() {
        inputManager.shutdown();
        outputManager.dispose();
        fontManager.dispose();
        audioManager.dispose();
    }
}
