package org.pixelquest.input;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;

public class ContinuousKeyHandler implements KeyListener {

    public final List<Integer> pressOrder;

    public ContinuousKeyHandler() {
        this.pressOrder = new ArrayList<>();
    }

    @Override
    public void keyTyped(KeyEvent e) { }

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        if (!pressOrder.contains(code)) {
            pressOrder.add(0, code);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        pressOrder.remove((Integer) e.getKeyCode());
    }

    public Integer getPriorityKey(List<Integer> codes) {
        for (Integer code : this.pressOrder) {
            if (codes.contains(code)) {
                return code;
            }
        }
        return null;
    }

}
