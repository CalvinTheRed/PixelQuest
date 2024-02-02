package org.mnm.main;

public class Sprite {

    private final String texture;
    private int x, y, facing, size;

    public Sprite(String texture, int x, int y, int facing, int size) {
        this.texture = texture;
        this.x = x;
        this.y = y;
        this.facing = facing;
        this.size = size;
    }

    public String getTexture() {
        return this.texture;
    }

    public int getX() {
        return this.x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return this.y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getFacing() {
        return this.facing;
    }

    public void setFacing(int facing) {
        this.facing = facing;
    }

    public int getSize() {
        return this.size;
    }

    public void setSize(int size) {
        this.size = size;
    }

}
