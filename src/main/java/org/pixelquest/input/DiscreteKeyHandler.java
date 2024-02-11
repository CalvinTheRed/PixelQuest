package org.pixelquest.input;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;

public class DiscreteKeyHandler implements KeyListener {

    private final List<Integer> keysDown;
    private final List<Integer> lockedKeys;

    public DiscreteKeyHandler() {
        this.keysDown = new ArrayList<>();
        this.lockedKeys = new ArrayList<>();
    }

    @Override
    public void keyTyped(KeyEvent e) { }

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        if (!lockedKeys.contains(code)) {
            keyDown(code);
            lockKey(code);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();
        keyUp(code);
        unlockKey(code);
    }
    
    public void keyDown(int code) {
        if (!this.keysDown.contains(code)) {
            this.keysDown.add(code);
        }
    }

    public void keyUp(int code) {
        this.keysDown.remove((Integer) code);
    }

    public void lockKey(int code) {
        if (!this.lockedKeys.contains(code)) {
            this.lockedKeys.add(code);
        }
    }

    public void unlockKey(int code) {
        this.lockedKeys.remove((Integer) code);
    }

    public boolean isKeyDown(int code) {
        return this.keysDown.contains(code);
    }

    public void acknowledgeKeyPressed(int code) {
        this.keysDown.remove((Integer) code);
    }

}
