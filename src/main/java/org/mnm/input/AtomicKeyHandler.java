package org.mnm.input;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class AtomicKeyHandler implements KeyListener {

    private Integer lastPressed;
    private boolean locked;

    public AtomicKeyHandler() {
        locked = false;
    }

    @Override
    public void keyTyped(KeyEvent e) { }

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        if (!locked) {
            switch(code) {
                case KeyEvent.VK_W, KeyEvent.VK_A, KeyEvent.VK_S, KeyEvent.VK_D -> {
                    lastPressed = code;
                    lock();
                }
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    public void lock() {
        this.locked = true;
    }

    public void unlock() {
        this.locked = false;
        this.lastPressed = null;
    }

    public boolean isLocked() {
        return this.locked;
    }

    public Integer getLastPressed() {
        return this.lastPressed;
    }

}
