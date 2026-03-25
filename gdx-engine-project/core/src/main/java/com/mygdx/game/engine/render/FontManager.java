package com.mygdx.game.engine.render;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

/**
 * Engine-level font manager that loads, caches, and disposes BitmapFonts.
 *
 * Scenes request fonts through this manager instead of creating their own
 * {@code new BitmapFont()} instances. This centralises resource management,
 * prevents duplicate allocations, and keeps font configuration (filtering,
 * scaling) consistent across the entire application.
 *
 * Architecture note:
 * FontManager lives in the engine's render package and has no knowledge of
 * any specific game.  Game code obtains a reference through IOManager and
 * calls {@link #getFont(String, int)} with a logical font name and size.
 *
 * If a custom .fnt / .ttf asset is placed in the assets folder, it can be
 * loaded by name.  When no matching asset file exists, the manager falls
 * back to libGDX's built-in default font with linear texture filtering for
 * smooth scaling — dramatically cleaner than raw {@code setScale()} on the
 * unfiltered default bitmap.
 */
public class FontManager {

    /**
     * Cache key: "fontName:size" → ready-to-use BitmapFont.
     */
    private final Map<String, BitmapFont> cache = new HashMap<>();

    /**
     * Returns a BitmapFont matching the requested logical name and point size.
     *
     * The first call for a given name+size pair creates the font and caches it.
     * Subsequent calls return the cached instance (cheap).
     *
     * @param name logical font name — if a file {@code <name>.fnt} exists in
     *             the assets folder it will be loaded; otherwise the libGDX
     *             default font is used with smooth-scaling applied.
     * @param size desired display size in approximate screen-pixels.  The
     *             default BitmapFont baseline is 15 px, so a size of 30 applies
     *             a scale factor of 2.0.
     * @return a ready-to-draw BitmapFont (owned by this manager — callers
     *         must <b>not</b> dispose it)
     */
    public BitmapFont getFont(String name, int size) {
        if (name == null) throw new IllegalArgumentException("font name cannot be null");
        if (size <= 0) throw new IllegalArgumentException("font size must be > 0");

        String cacheKey = name + ":" + size;

        BitmapFont cached = cache.get(cacheKey);
        if (cached != null) return cached;

        BitmapFont font = createFont(name, size);
        cache.put(cacheKey, font);
        return font;
    }

    /**
     * Disposes every cached font.  Called by IOManager / EngineCore on shutdown.
     */
    public void dispose() {
        for (BitmapFont font : cache.values()) {
            if (font != null) font.dispose();
        }
        cache.clear();
    }

    // -----------------------------------------------------------------------

    private BitmapFont createFont(String name, int size) {
        BitmapFont font;

        // Try to load a custom .fnt file from assets
        String fntPath = name + ".fnt";
        if (Gdx.files.internal(fntPath).exists()) {
            font = new BitmapFont(Gdx.files.internal(fntPath));
        } else {
            // Fall back to the built-in default font
            font = new BitmapFont();
        }

        // Apply linear filtering so scaled text looks clean instead of pixelated.
        // The default BitmapFont uses Nearest filtering which produces jagged
        // edges at any scale other than 1.0.
        font.getRegion().getTexture().setFilter(
                Texture.TextureFilter.Linear,
                Texture.TextureFilter.Linear);

        // Scale to the requested display size (default baseline is ~15 px)
        float baselineSize = font.getCapHeight();
        if (baselineSize > 0f) {
            font.getData().setScale(size / baselineSize);
        }

        font.setUseIntegerPositions(false);

        return font;
    }
}
