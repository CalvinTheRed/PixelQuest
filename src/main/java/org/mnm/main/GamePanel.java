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
    private final int centerX, centerY;
    private int stride;

    Sprite player = new Sprite(
            "resources/textures/player.png",
            25, 18, SpriteUtils.FACING_DOWN, 1
    );

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

        centerX = COLS / 2;
        centerY = ROWS / 2;
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
            player.setY(player.getY() - 1);
            player.setFacing(SpriteUtils.FACING_UP);
        }
        if (keyHandler.aDown) {
            keyHandler.aDown = false;
            player.setX(player.getX() - 1);
            player.setFacing(SpriteUtils.FACING_LEFT);
        }
        if (keyHandler.sDown) {
            keyHandler.sDown = false;
            player.setY(player.getY() + 1);
            player.setFacing(SpriteUtils.FACING_DOWN);
        }
        if (keyHandler.dDown) {
            keyHandler.dDown = false;
            player.setX(player.getX() + 1);
            player.setFacing(SpriteUtils.FACING_RIGHT);
        }
        if (keyHandler.equalsDown) {
            keyHandler.equalsDown = false;
            player.setSize(Math.min(player.getSize() + 1, 4));
        }
        if (keyHandler.minusDown) {
            keyHandler.minusDown = false;
            player.setSize(Math.max(player.getSize() - 1, 1));
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        try {
            BufferedImage background = ImageIO.read(new File(this.backgroundTexture));
            g2.drawImage(background,
                    TILE_SIZE * SCALE * ((2 * (-player.getX() + centerX)) - player.getSize() + 1) / 2,
                    TILE_SIZE * SCALE * ((2 * (-player.getY() + centerY)) - player.getSize() + 1) / 2,
                    background.getWidth() * SCALE,
                    background.getHeight() * SCALE,
                    null
            );

            this.renderSprite(g2, player, player);

        } catch (IOException e) {
            g2.dispose();
            throw new RuntimeException(e);
        }

        g2.dispose();
    }

    public void setBackgroundTexture(String backgroundTexture) {
        this.backgroundTexture = backgroundTexture;
    }

    private void renderSprite(Graphics2D g2, Sprite sprite, Sprite focusSprite) throws IOException {
        BufferedImage playerTexture = ImageIO.read(new File(sprite.getTexture()));

        // TODO render non-focus sprites relative to the position of the focus sprite

        g2.drawImage(playerTexture,
                centerX * TILE_SIZE * SCALE
                        - (TILE_SIZE * SCALE * (sprite.getSize() - 1) / 2),
                (centerY * TILE_SIZE - (playerTexture.getHeight() / 4 - TILE_SIZE)) * SCALE
                        - (TILE_SIZE * SCALE * (sprite.getSize() - 1) / 2)
                        + SPRITE_OFFSET,
                (centerX + 1) * TILE_SIZE * SCALE
                        + (TILE_SIZE * SCALE * (sprite.getSize() - 1) / 2),
                (centerY + 1) * TILE_SIZE * SCALE
                        + (TILE_SIZE * SCALE * (sprite.getSize() - 1) / 2)
                        + SPRITE_OFFSET,
                (playerTexture.getWidth() / 4) * stride,
                (playerTexture.getHeight() / 4) * sprite.getFacing(),
                (playerTexture.getWidth() / 4) * (stride + 1),
                (playerTexture.getHeight() / 4) * (sprite.getFacing() + 1),
                null
        );
    }

}
