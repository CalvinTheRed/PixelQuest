package org.mnm.core;

import java.awt.image.BufferedImage;

public class Map {

    private final BufferedImage background;
    private final BufferedImage foreground;
    private final boolean[][] collisions;

    public Map(BufferedImage background, BufferedImage foreground, boolean[][] collisions) {
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
        System.out.println("Checking tile at (" + x + "," + y + ")");
        return !collisions[y][x];
    }

}
