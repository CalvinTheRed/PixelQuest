package org.mnm.main;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class GamePanel extends JPanel implements Runnable {

    final int TILE_SIZE = 12;
    final int SCALE = 4;

    final int COLS = 19;
    final int ROWS = 13;

    private Thread gameThread;
    private String backgroundTexture;
    private int playerX, playerY;

    public GamePanel(String backgroundTexture) {
        this.setPreferredSize(new Dimension(
                TILE_SIZE * SCALE * COLS,
                TILE_SIZE * SCALE * ROWS
        ));
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true);
        this.setBackgroundTexture(backgroundTexture);

        playerX = (COLS / 2) * TILE_SIZE * SCALE;
        playerY = (ROWS / 2) * TILE_SIZE * SCALE;
    }

    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {
        while (gameThread != null) {
            update();
            repaint();
        }
    }

    public void update() {

    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        try {
            BufferedImage background = ImageIO.read(new File(this.backgroundTexture));
            g2.drawImage(background, 0, 0, null);

            BufferedImage player = ImageIO.read(new File("resources/textures/playerDown.png"));
            g2.drawImage(player,
                    playerX, playerY - player.getHeight() + (TILE_SIZE * SCALE),
                    playerX + (TILE_SIZE * SCALE), playerY + (TILE_SIZE * SCALE),
                    0, 0,
                    player.getWidth() / 4, player.getHeight(),
                    null
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void setBackgroundTexture(String backgroundTexture) {
        this.backgroundTexture = backgroundTexture;
    }
}
