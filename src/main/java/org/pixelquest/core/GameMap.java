package org.pixelquest.core;

import org.rpgl.json.JsonArray;

import java.awt.image.BufferedImage;

public class GameMap {

    private final BufferedImage background;
    private final BufferedImage foreground;
    private final JsonArray collisions;

    public GameMap(BufferedImage background, BufferedImage foreground, JsonArray collisions) {
        this.background = background;
        this.foreground = foreground;
        this.collisions = collisions;
    }

    public BufferedImage getBackground() {
        return this.background;
    }

    public BufferedImage getForeground() {
        return this.foreground;
    }

    public boolean canMoveToTile(int x, int y) {
        return this.collisions.getJsonArray(y).getInteger(x) == 0;
    }

}
