package org.mnm.input;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;

public class KeyHandler implements KeyListener {

    public boolean wDown, aDown, sDown, dDown;

    private List<Integer> lockedKeys;

    public KeyHandler() {
        this.lockedKeys = new ArrayList<>();
    }

    @Override
    public void keyTyped(KeyEvent e) { }

    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        if (!lockedKeys.contains(keyCode)) {
            switch(keyCode) {
                case KeyEvent.VK_W -> {
                    wDown = true;
                    lockKey(keyCode);
                }
                case KeyEvent.VK_A -> {
                    aDown = true;
                    lockKey(keyCode);
                }
                case KeyEvent.VK_S -> {
                    sDown = true;
                    lockKey(keyCode);
                }
                case KeyEvent.VK_D -> {
                    dDown = true;
                    lockKey(keyCode);
                }
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        unlockKey(e.getKeyCode());
    }

    public void lockKey(int keyCode) {
        if (!this.lockedKeys.contains(keyCode)) {
            this.lockedKeys.add(keyCode);
        }
    }

    public void unlockKey(int keyCode) {
        this.lockedKeys.remove((Integer) keyCode);
    }

}
