package org.pixelquest.ui;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ToolBar extends HudComponent {

    private final BufferedImage minimap;
    private final BufferedImage blankButton;
    private final BufferedImage combatIcon;
    private final BufferedImage characterSheetIcon;
    private final BufferedImage settingsIcon;

    public ToolBar(double scale) throws IOException {
        super(scale, 10);
        this.minimap = ImageIO.read(new File("resources/textures/hud/minimap.png"));
        this.blankButton = ImageIO.read(new File("resources/textures/hud/blank-button.png"));
        this.combatIcon = ImageIO.read(new File("resources/textures/hud/combat-icon.png"));
        this.characterSheetIcon = ImageIO.read(new File("resources/textures/hud/character-sheet-icon.png"));
        this.settingsIcon = ImageIO.read(new File("resources/textures/hud/settings-icon.png"));
    }

    @Override
    public void paintMe(Graphics2D g2, JPanel canvas) {
        this.renderMinimap(g2, canvas);
        this.renderToolbarButton(g2, canvas, 0, this.combatIcon);
        this.renderToolbarButton(g2, canvas, 1, this.characterSheetIcon);
        this.renderToolbarButton(g2, canvas, 2, null);
        this.renderToolbarButton(g2, canvas, 3, this.settingsIcon);
    }

    private void renderMinimap(Graphics2D g2, JPanel canvas) {
        g2.drawImage(this.minimap,
                canvas.getWidth() - this.minimap.getWidth() - this.margin,
                this.margin,
                (int) (this.scale * this.minimap.getWidth()),
                (int) (this.scale * this.minimap.getHeight()),
                null
        );
    }

    private  void renderToolbarButton(Graphics2D g2, JPanel canvas, int index, BufferedImage icon) {
        g2.drawImage(this.blankButton,
                canvas.getWidth() - this.blankButton.getWidth() - this.margin,
                this.margin + this.minimap.getHeight() + (index * this.blankButton.getHeight()),
                (int) (this.scale * this.blankButton.getWidth()),
                (int) (this.scale * this.blankButton.getHeight()),
                null
        );
        if (icon != null) {
            int horzOffset = (int) (this.scale * (this.blankButton.getWidth() - icon.getWidth())) / 2;
            int vertOffset = (int) (this.scale * (this.blankButton.getHeight() - icon.getHeight())) / 2;
            g2.drawImage(icon,
                    canvas.getWidth() - this.blankButton.getWidth() - this.margin + horzOffset,
                    this.margin + this.minimap.getHeight() + (index * this.blankButton.getHeight()) + vertOffset,
                    (int) (this.scale * icon.getWidth()),
                    (int) (this.scale * icon.getHeight()),
                    null
            );
        }
    }

}
