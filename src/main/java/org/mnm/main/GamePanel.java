package org.mnm.main;

import org.mnm.core.Map;
import org.mnm.core.MapLoader;
import org.mnm.input.KeyHandler;
import org.mnm.util.SpriteUtils;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class GamePanel extends JPanel implements Runnable {

    final int FPS = 60;

    final int TILE_SIZE = 12;
    final int SCALE = 4;

    final int COLS = 19;
    final int ROWS = 13;

    private final int SPRITE_OFFSET_Y = -TILE_SIZE * SCALE / 4;
    private final int TEXTURE_OFFSET_X = TILE_SIZE * SCALE * (COLS / 2);
    private final int TEXTURE_OFFSET_Y = TILE_SIZE * SCALE * (ROWS / 2);

    private final KeyHandler keyHandler = new KeyHandler();

    private Thread gameThread;
    private final int centerX, centerY;
    private int cameraX, cameraY;
    private int stride = 0;

    private final List<Sprite> sprites;

    private Map map;

    Sprite focusSprite = new Sprite(
            "resources/textures/sprites/player.png",
            25, 18, SpriteUtils.FACING_DOWN, 1
    );

    public GamePanel(String initialMapDir) throws IOException {
        this.setPreferredSize(new Dimension(
                TILE_SIZE * SCALE * COLS,
                TILE_SIZE * SCALE * ROWS
        ));
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyHandler);
        this.setFocusable(true);

        sprites = new ArrayList<>();
        sprites.add(focusSprite);
        sprites.add(new Sprite(
                "resources/textures/sprites/player.png",
                43, 26, SpriteUtils.FACING_DOWN, 1
        ));

        map = MapLoader.getMap(initialMapDir);

        centerX = COLS / 2;
        centerY = ROWS / 2;

        cameraX = focusSprite.getX() - centerX;
        cameraY = focusSprite.getY() - centerY;
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
            focusSprite.setFacing(SpriteUtils.FACING_UP);
            keyHandler.wDown = false;
            if (canEnterSpace(focusSprite.getX(), focusSprite.getY() - 1)) {
                focusSprite.setY(focusSprite.getY() - 1);
                cameraY--;
            }
        }
        if (keyHandler.aDown) {
            focusSprite.setFacing(SpriteUtils.FACING_LEFT);
            keyHandler.aDown = false;
            if (canEnterSpace(focusSprite.getX() - 1, focusSprite.getY())) {
                focusSprite.setX(focusSprite.getX() - 1);
                cameraX--;
            }
        }
        if (keyHandler.sDown) {
            focusSprite.setFacing(SpriteUtils.FACING_DOWN);
            keyHandler.sDown = false;
            if (canEnterSpace(focusSprite.getX(), focusSprite.getY() + 1)) {
                focusSprite.setY(focusSprite.getY() + 1);
                cameraY++;
            }
        }
        if (keyHandler.dDown) {
            focusSprite.setFacing(SpriteUtils.FACING_RIGHT);
            keyHandler.dDown = false;
            if (canEnterSpace(focusSprite.getX() + 1, focusSprite.getY())) {
                focusSprite.setX(focusSprite.getX() + 1);
                cameraX++;
            }
        }
//        if (keyHandler.equalsDown) {
//            keyHandler.equalsDown = false;
//            focusSprite.setSize(Math.min(focusSprite.getSize() + 1, 4));
//        }
//        if (keyHandler.minusDown) {
//            keyHandler.minusDown = false;
//            focusSprite.setSize(Math.max(focusSprite.getSize() - 1, 1));
//        } // TODO size-ups don't quite work right for multiple sprites. Issue for later.
    }

    public boolean canEnterSpace(int x, int y) {
        for (Sprite sprite : sprites) {
            if (sprite.getX() == x && sprite.getY() == y) {
                return false;
            }
        }
        return map.canMoveToTile(x, y);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        try {
            renderBackgroundLayer(g2);
            renderDetailLayer(g2);
            renderSpriteLayer(g2);
            renderForegroundLayer(g2);
        } catch (IOException e) {
            g2.dispose();
            throw new RuntimeException(e);
        }

        g2.dispose();
    }

    private void renderBackgroundLayer(Graphics2D g2) throws IOException {
        BufferedImage texture = map.getBackground();
        g2.drawImage(texture,
                TILE_SIZE * SCALE * (centerX - cameraX) - TEXTURE_OFFSET_X,
                TILE_SIZE * SCALE * (centerY - cameraY) - TEXTURE_OFFSET_Y,
                texture.getWidth() * SCALE,
                texture.getHeight() * SCALE,
                null
        );
    }

    private void renderDetailLayer(Graphics2D g2) {

    }

    private void renderSpriteLayer(Graphics2D g2) {
        sprites.stream().sorted(Comparator.comparing(Sprite::getY)).forEach(sprite -> {
            try {
                this.renderSprite(g2, sprite);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void renderForegroundLayer(Graphics2D g2) {
        BufferedImage texture = map.getForeground();
        g2.drawImage(texture,
                TILE_SIZE * SCALE * (centerX - cameraX) - TEXTURE_OFFSET_X,
                TILE_SIZE * SCALE * (centerY - cameraY) - TEXTURE_OFFSET_Y,
                texture.getWidth() * SCALE,
                texture.getHeight() * SCALE,
                null
        );
    }

    private void renderSprite(Graphics2D g2, Sprite sprite) throws IOException {
        BufferedImage texture = ImageIO.read(new File(sprite.getTexture()));

        g2.drawImage(texture,
                (sprite.getX() - cameraX) * TILE_SIZE * SCALE,
                ((sprite.getY() - cameraY) * TILE_SIZE - (texture.getHeight() / 4 - TILE_SIZE)) * SCALE
                        + SPRITE_OFFSET_Y,
                (sprite.getX() - cameraX + 1) * TILE_SIZE * SCALE,
                (sprite.getY() - cameraY + 1) * TILE_SIZE * SCALE
                        + SPRITE_OFFSET_Y,
                (texture.getWidth() / 4) * stride,
                (texture.getHeight() / 4) * sprite.getFacing(),
                (texture.getWidth() / 4) * (stride + 1),
                (texture.getHeight() / 4) * (sprite.getFacing() + 1),
                null
        );
    }

}
