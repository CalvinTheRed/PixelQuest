package org.mnm.main;

import org.mnm.input.KeyHandler;
import org.mnm.util.SpriteUtils;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class GamePanel extends JPanel implements Runnable {

    final int FPS = 60;

    final int TILE_SIZE = 12;
    final int SCALE = 4;

    final int COLS = 19;
    final int ROWS = 13;

    final int SPRITE_OFFSET = -TILE_SIZE * SCALE / 4;

    private final KeyHandler keyHandler = new KeyHandler();

    private Thread gameThread;
    private String backgroundTexture;
    private int playerX, playerY, facing, stride, sizeModifier, backgroundX, backgroundY;

    public GamePanel(String backgroundTexture) {
        this.setPreferredSize(new Dimension(
                TILE_SIZE * SCALE * COLS,
                TILE_SIZE * SCALE * ROWS
        ));
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true);
        this.setBackgroundTexture(backgroundTexture);
        this.addKeyListener(keyHandler);
        this.setFocusable(true);

        playerX = COLS / 2;
        playerY = ROWS / 2;
        facing = SpriteUtils.FACING_RIGHT;
        stride = 0;
        sizeModifier = 1;

        backgroundX = -15;
        backgroundY = -9;
    }

    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {
        long drawInterval = 1000000000 / FPS;
        long lastTime = System.nanoTime();
        long currentTime;
        while (gameThread != null) {
            currentTime = System.nanoTime();
            if (currentTime > lastTime + drawInterval) {
                lastTime = currentTime;
                update();
                repaint();
            }
        }
    }

    public void update() {
        if (keyHandler.wDown) {
            keyHandler.wDown = false;
            backgroundY++;
            facing = SpriteUtils.FACING_UP;
        }
        if (keyHandler.aDown) {
            keyHandler.aDown = false;
            backgroundX++;
            facing = SpriteUtils.FACING_LEFT;
        }
        if (keyHandler.sDown) {
            keyHandler.sDown = false;
            backgroundY--;
            facing = SpriteUtils.FACING_DOWN;
        }
        if (keyHandler.dDown) {
            keyHandler.dDown = false;
            backgroundX--;
            facing = SpriteUtils.FACING_RIGHT;
        }
        if (keyHandler.equalsDown) {
            keyHandler.equalsDown = false;
            sizeModifier++;
        }
        if (keyHandler.minusDown) {
            keyHandler.minusDown = false;
            sizeModifier--;
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        try {
            BufferedImage background = ImageIO.read(new File(this.backgroundTexture));
            g2.drawImage(background,
                    TILE_SIZE * SCALE * ((2 * backgroundX) - sizeModifier + 1) / 2,
                    TILE_SIZE * SCALE * ((2 * backgroundY) - sizeModifier + 1) / 2,
                    null
            );

            BufferedImage player = ImageIO.read(new File("resources/textures/player.png"));
            g2.drawImage(player,
                    playerX * TILE_SIZE * SCALE
                            - (TILE_SIZE * SCALE * (sizeModifier - 1) / 2),
                    (playerY * TILE_SIZE - (player.getHeight() / 4 - TILE_SIZE)) * SCALE
                            - (TILE_SIZE * SCALE * (sizeModifier - 1) / 2)
                            + SPRITE_OFFSET,
                    (playerX + 1) * TILE_SIZE * SCALE
                            + (TILE_SIZE * SCALE * (sizeModifier - 1) / 2),
                    (playerY + 1) * TILE_SIZE * SCALE
                            + (TILE_SIZE * SCALE * (sizeModifier - 1) / 2)
                            + SPRITE_OFFSET,
                    (player.getWidth() / 4) * stride,
                    (player.getHeight() / 4) * facing,
                    (player.getWidth() / 4) * (stride + 1),
                    (player.getHeight() / 4) * (facing + 1),
                    null
            );
        } catch (IOException e) {
            g2.dispose();
            throw new RuntimeException(e);
        }

        g2.dispose();
    }

    public void setBackgroundTexture(String backgroundTexture) {
        this.backgroundTexture = backgroundTexture;
    }

}
