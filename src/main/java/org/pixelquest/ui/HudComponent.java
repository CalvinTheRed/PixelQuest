package org.pixelquest.ui;

import javax.swing.*;
import java.awt.*;

public abstract class HudComponent extends JPanel {

    final double scale;
    final int margin;

    public HudComponent(double scale, int margin) {
        super();
        this.scale = scale;
        this.margin = margin;
    }

    public abstract void paintMe(Graphics2D g2, JPanel canvas);

}
