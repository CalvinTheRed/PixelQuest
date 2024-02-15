package org.pixelquest.ui;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class HealthBar extends JPanel {

    private final BufferedImage healthBarLeft;
    private final BufferedImage healthBlockEmpty;
    private final BufferedImage healthBlockFilled;
    private final BufferedImage healthBarRight;
    private final int margin = 10;
    private final int cells = 10;
    private final int scale;

    private int current;
    private int max;

    public HealthBar(int current, int max, int scale) throws IOException {
        this.current = current;
        this.max = max;
        this.scale = scale;

        healthBarLeft = ImageIO.read(new File("resources/textures/hud/healthbar-left.png"));
        healthBlockEmpty = ImageIO.read(new File("resources/textures/hud/healthbar-block-empty.png"));
        healthBlockFilled = ImageIO.read(new File("resources/textures/hud/healthbar-block-filled.png"));
        healthBarRight = ImageIO.read(new File("resources/textures/hud/healthbar-right.png"));
    }

    public void setCurrentHealth(int current) {
        this.current = current;
    }

    public void setMaximumHealth(int max) {
        this.max = max;
    }

    public void paintMe(Graphics2D g2) {
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
                this.healthBarLeft.getWidth() * this.scale,
                this.healthBarLeft.getHeight() * this.scale,
                null
        );
    }

    private void paintHealthBarCell(Graphics2D g2, int index, boolean filled) {
        if (filled) {
            if (index == 0) {
                // first
                g2.drawImage(this.healthBlockFilled,
                        this.margin + (this.scale * this.healthBarLeft.getWidth()),
                        this.margin,
                        this.margin + (this.scale * (this.healthBarLeft.getWidth() + this.healthBlockEmpty.getWidth())),
                        this.margin + (this.scale * this.healthBlockFilled.getHeight()),
                        0,
                        0,
                        this.healthBlockFilled.getWidth() / 3,
                        this.healthBlockFilled.getHeight(),
                        null
                );
            } else if (index == this.cells - 1) {
                // last
                g2.drawImage(this.healthBlockFilled,
                        this.margin + (this.scale * (this.healthBarLeft.getWidth() + (index * this.healthBlockFilled.getWidth() / 3))),
                        this.margin,
                        this.margin + (this.scale * (this.healthBarLeft.getWidth() + ((index + 1) * this.healthBlockFilled.getWidth() / 3))),
                        this.margin + (this.scale * this.healthBlockFilled.getHeight()),
                        this.healthBlockFilled.getWidth() * 2 / 3,
                        0,
                        this.healthBlockFilled.getWidth(),
                        this.healthBlockFilled.getHeight(),
                        null
                );
            } else {
                // middle
                g2.drawImage(this.healthBlockFilled,
                        this.margin + (this.scale * (this.healthBarLeft.getWidth() + (index * this.healthBlockFilled.getWidth() / 3))),
                        this.margin,
                        this.margin + (this.scale * (this.healthBarLeft.getWidth() + ((index + 1) * this.healthBlockFilled.getWidth() / 3))),
                        this.margin + (this.scale * this.healthBlockFilled.getHeight()),
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
                    this.margin + this.scale * (this.healthBarLeft.getWidth() + (index * this.healthBlockEmpty.getWidth())),
                    this.margin,
                    this.scale * this.healthBlockEmpty.getWidth(),
                    this.scale * this.healthBlockEmpty.getHeight(),
                    null
            );
        }
    }

    private void paintHealthBarRight(Graphics2D g2) {
        g2.drawImage(this.healthBarRight,
                this.margin + this.scale * (this.healthBarLeft.getWidth() + this.cells * this.healthBlockEmpty.getWidth()),
                this.margin,
                this.scale * this.healthBarRight.getWidth(),
                this.scale * this.healthBarLeft.getHeight(),
                null
        );
    }

}
