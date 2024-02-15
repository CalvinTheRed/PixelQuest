package org.pixelquest.ui;

import org.pixelquest.util.ImageUtils;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class HealthBar extends JPanel {

    private int current;
    private int max;

    public HealthBar(int current, int max) {
        this.current = current;
        this.max = max;
    }

    public void setCurrentHealth(int current) {
        this.current = current;
    }

    public void setMaximumHealth(int max) {
        this.max = max;
    }

    public void paintMe(Graphics2D g2) throws IOException {
        BufferedImage healthBar = ImageIO.read(new File("resources/textures/hud/healthbar-left.png"));
        BufferedImage healthBlockGray = ImageIO.read(new File("resources/textures/hud/healthbar-block-gray.png"));
        BufferedImage healthBlockRed = ImageIO.read(new File("resources/textures/hud/healthbar-block-red.png"));
        BufferedImage heathBarRight = ImageIO.read(new File("resources/textures/hud/healthbar-right.png"));

        final int healthBarBlocks = 10;
        int healthBlocksFilled = this.current * healthBarBlocks / this.max;

        for (int i = 1; i <= healthBlocksFilled; i++) {
            if (i == 1) {
                healthBar = ImageUtils.joinImageHorz(
                        healthBar,
                        0,
                        0,
                        healthBar.getWidth(),
                        healthBar.getHeight(),
                        healthBlockRed,
                        0,
                        0,
                        healthBlockRed.getWidth() / 3,
                        healthBlockRed.getHeight()
                );
            } else if (i < healthBlocksFilled) {
                healthBar = ImageUtils.joinImageHorz(
                        healthBar,
                        0,
                        0,
                        healthBar.getWidth(),
                        healthBar.getHeight(),
                        healthBlockRed,
                        healthBlockRed.getWidth() / 3,
                        0,
                        healthBlockRed.getWidth() * 2 / 3,
                        healthBlockRed.getHeight()
                );
            } else {
                healthBar = ImageUtils.joinImageHorz(
                        healthBar,
                        0,
                        0,
                        healthBar.getWidth(),
                        healthBar.getHeight(),
                        healthBlockRed,
                        healthBlockRed.getWidth() * 2 / 3,
                        0,
                        healthBlockRed.getWidth(),
                        healthBlockRed.getHeight()
                );
            }

        }
        for (int i = healthBlocksFilled; i < healthBarBlocks; i++) {
            healthBar = ImageUtils.joinImageHorz(healthBar, healthBlockGray);
        }
        healthBar = ImageUtils.joinImageHorz(healthBar, heathBarRight);
        g2.drawImage(healthBar, 10, 10, healthBar.getWidth() * 2, healthBar.getHeight() * 2, null);
    }

}
