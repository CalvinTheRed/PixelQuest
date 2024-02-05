package org.mnm.core;

import org.rpgl.json.JsonArray;

import java.awt.image.BufferedImage;

public class GameMap {

    private final BufferedImage background;
    private final BufferedImage foreground;
    private final JsonArray collisions;
    private final int tileSize;

    public GameMap(BufferedImage background, BufferedImage foreground, JsonArray collisions, int tileSize) {
        this.background = background;
        this.foreground = foreground;
        this.collisions = collisions;
        this.tileSize = tileSize;
    }

    public BufferedImage getBackground() {
        return this.background;
    }

    public BufferedImage getForeground() {
        return this.foreground;
    }

    public int getTileSize() {
        return this.tileSize;
    }

    public boolean canMoveToTile(int x, int y) {
        return this.collisions.getJsonArray(y).getInteger(x) == 0;
    }

}
