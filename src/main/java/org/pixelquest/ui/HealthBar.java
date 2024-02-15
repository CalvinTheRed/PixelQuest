package org.pixelquest.ui;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class HealthBar extends HudComponent {

    private final BufferedImage healthBarLeft;
    private final BufferedImage healthBlockEmpty;
    private final BufferedImage healthBlockFilled;
    private final BufferedImage healthBarRight;
    private final int cells = 10;

    private int current;
    private int max;

    public HealthBar(int current, int max, double scale) throws IOException {
        super(scale, 10);
        this.current = current;
        this.max = max;

        this.healthBarLeft = ImageIO.read(new File("resources/textures/hud/healthbar-left.png"));
        this.healthBlockEmpty = ImageIO.read(new File("resources/textures/hud/healthbar-block-empty.png"));
        this.healthBlockFilled = ImageIO.read(new File("resources/textures/hud/healthbar-block-filled.png"));
        this.healthBarRight = ImageIO.read(new File("resources/textures/hud/healthbar-right.png"));
    }

    public void setCurrentHealth(int current) {
        this.current = current;
    }

    public void setMaximumHealth(int max) {
        this.max = max;
    }

    @Override
    public void paintMe(Graphics2D g2, JPanel canvas) {
        this.paintHealthBarLeft(g2);
        int healthBlocksFilled = this.current * this.cells / this.max;
        if (healthBlocksFilled == 0 && this.current > 0) {
            healthBlocksFilled = 1;
        }
        for (int i = 0; i < this.cells; i++) {
            this.paintHealthBarCell(g2, i, i < healthBlocksFilled);
        }
        this.paintHealthBarRight(g2);
    }

    private void paintHealthBarLeft(Graphics2D g2) {
        g2.drawImage(this.healthBarLeft,
                this.margin,
                this.margin,
                (int) (this.healthBarLeft.getWidth() * this.scale),
                (int) (this.healthBarLeft.getHeight() * this.scale),
                null
        );
    }

    private void paintHealthBarCell(Graphics2D g2, int index, boolean filled) {
        if (filled) {
            if (index == 0) {
                // first
                g2.drawImage(this.healthBlockFilled,
                        (int) (this.margin + (this.scale * this.healthBarLeft.getWidth())),
                        this.margin,
                        (int) (this.margin + (this.scale * (this.healthBarLeft.getWidth() + this.healthBlockEmpty.getWidth()))),
                        (int) (this.margin + (this.scale * this.healthBlockFilled.getHeight())),
                        0,
                        0,
                        this.healthBlockFilled.getWidth() / 3,
                        this.healthBlockFilled.getHeight(),
                        null
                );
            } else if (index == this.cells - 1) {
                // last
                g2.drawImage(this.healthBlockFilled,
                        (int) (this.margin + (this.scale * (this.healthBarLeft.getWidth() + (index * this.healthBlockFilled.getWidth() / 3)))),
                        this.margin,
                        (int) (this.margin + (this.scale * (this.healthBarLeft.getWidth() + ((index + 1) * this.healthBlockFilled.getWidth() / 3)))),
                        (int) (this.margin + (this.scale * this.healthBlockFilled.getHeight())),
                        this.healthBlockFilled.getWidth() * 2 / 3,
                        0,
                        this.healthBlockFilled.getWidth(),
                        this.healthBlockFilled.getHeight(),
                        null
                );
            } else {
                // middle
                g2.drawImage(this.healthBlockFilled,
                        (int) (this.margin + (this.scale * (this.healthBarLeft.getWidth() + (index * this.healthBlockFilled.getWidth() / 3)))),
                        this.margin,
                        (int) (this.margin + (this.scale * (this.healthBarLeft.getWidth() + ((index + 1) * this.healthBlockFilled.getWidth() / 3)))),
                        (int) (this.margin + (this.scale * this.healthBlockFilled.getHeight())),
                        this.healthBlockFilled.getWidth() / 3,
                        0,
                        this.healthBlockFilled.getWidth() * 2 / 3,
                        this.healthBlockFilled.getHeight(),
                        null
                );
            }
        } else {
            // empty
            g2.drawImage(this.healthBlockEmpty,
                    (int) (this.margin + this.scale * (this.healthBarLeft.getWidth() + (index * this.healthBlockEmpty.getWidth()))),
                    this.margin,
                    (int) (this.scale * this.healthBlockEmpty.getWidth()),
                    (int) (this.scale * this.healthBlockEmpty.getHeight()),
                    null
            );
        }
    }

    private void paintHealthBarRight(Graphics2D g2) {
        g2.drawImage(this.healthBarRight,
                (int) (this.margin + this.scale * (this.healthBarLeft.getWidth() + this.cells * this.healthBlockEmpty.getWidth())),
                this.margin,
                (int) (this.scale * this.healthBarRight.getWidth()),
                (int) (this.scale * this.healthBarLeft.getHeight()),
                null
        );
    }

}
