package org.pixelquest.input;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;

public class ContinuousKeyHandler implements KeyListener {

    public boolean wDown, aDown, sDown, dDown;
    public final List<Integer> pressOrder;
    public final List<Integer> whitelist;

    public ContinuousKeyHandler(List<Integer> whitelist) {
        this.pressOrder = new ArrayList<>();
        this.whitelist = whitelist;
    }

    @Override
    public void keyTyped(KeyEvent e) { }

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        switch (code) {
            case KeyEvent.VK_W -> wDown = true;
            case KeyEvent.VK_A -> aDown = true;
            case KeyEvent.VK_S -> sDown = true;
            case KeyEvent.VK_D -> dDown = true;
        }
        if (this.whitelist.contains(code) && !this.pressOrder.contains(code)) {
            pressOrder.add(0, code);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();
        switch (code) {
            case KeyEvent.VK_W -> wDown = false;
            case KeyEvent.VK_A -> aDown = false;
            case KeyEvent.VK_S -> sDown = false;
            case KeyEvent.VK_D -> dDown = false;
        }
        pressOrder.remove((Integer) code);
    }

    public Integer getKey() {
        if (pressOrder.isEmpty()) {
            return null;
        }
        return pressOrder.get(0);
    }

}
