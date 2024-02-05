package org.mnm.main;

import org.mnm.core.GameMap;
import org.mnm.core.MapLoader;
import org.mnm.input.DiscreteKeyHandler;
import org.mnm.util.SpriteUtils;
import org.rpgl.core.RPGLFactory;
import org.rpgl.core.RPGLObject;
import org.rpgl.uuidtable.UUIDTable;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Comparator;

public class GamePanel extends JPanel implements Runnable {

    final int FPS = 60;
    final int SCALE = 4;
    final int COLS = 19;
    final int ROWS = 13;

    private GameMap gameMap;
    private Thread gameThread;
    private final DiscreteKeyHandler keyHandler = new DiscreteKeyHandler();

    private int tileSize;
    private int spriteOffsetY;
    private int textureOffsetX;
    private int textureOffsetY;
    private final int centerX, centerY;
    private int cameraX, cameraY;

    private int stride = 0;

    RPGLObject focusObject = SpriteUtils.newObject("std:humanoid/commoner", 25, 18, SpriteUtils.FACING_DOWN);

    public GamePanel(String initialMap) throws IOException {
        gameMap = MapLoader.getMap(initialMap);
        this.tileSize = gameMap.getTileSize();
        spriteOffsetY = -tileSize * SCALE / 4;
        textureOffsetX = tileSize * SCALE * (COLS / 2);
        textureOffsetY = tileSize * SCALE * (ROWS / 2);

        this.setPreferredSize(new Dimension(
                tileSize * SCALE * COLS,
                tileSize * SCALE * ROWS
        ));
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyHandler);
        this.setFocusable(true);

//        newObject("std:humanoid/commoner", 43, 26);

        centerX = COLS / 2;
        centerY = ROWS / 2;

        cameraX = SpriteUtils.getX(focusObject) - centerX;
        cameraY = SpriteUtils.getY(focusObject) - centerY;
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
            SpriteUtils.setRotation(focusObject, SpriteUtils.FACING_UP);
            keyHandler.wDown = false;
            if (canEnterSpace(SpriteUtils.getX(focusObject), SpriteUtils.getY(focusObject) - 1)) {
                SpriteUtils.setY(focusObject, SpriteUtils.getY(focusObject) - 1);
                cameraY--;
            }
        }
        if (keyHandler.aDown) {
            SpriteUtils.setRotation(focusObject, SpriteUtils.FACING_LEFT);
            keyHandler.aDown = false;
            if (canEnterSpace(SpriteUtils.getX(focusObject) - 1, SpriteUtils.getY(focusObject))) {
                SpriteUtils.setX(focusObject, SpriteUtils.getX(focusObject) - 1);
                cameraX--;
            }
        }
        if (keyHandler.sDown) {
            SpriteUtils.setRotation(focusObject, SpriteUtils.FACING_DOWN);
            keyHandler.sDown = false;
            if (canEnterSpace(SpriteUtils.getX(focusObject), SpriteUtils.getY(focusObject) + 1)) {
                SpriteUtils.setY(focusObject, SpriteUtils.getY(focusObject) + 1);
                cameraY++;
            }
        }
        if (keyHandler.dDown) {
            SpriteUtils.setRotation(focusObject, SpriteUtils.FACING_RIGHT);
            keyHandler.dDown = false;
            if (canEnterSpace(SpriteUtils.getX(focusObject) + 1, SpriteUtils.getY(focusObject))) {
                SpriteUtils.setX(focusObject, SpriteUtils.getX(focusObject) + 1);
                cameraX++;
            }
        }
    }

    public boolean canEnterSpace(double x, double y) {
        double epsilon = 0.000001d;
        for (RPGLObject object : UUIDTable.getObjects()) {
            if (Math.abs(Double.compare(object.getPosition().getDouble(0), x)) < epsilon
                    && Math.abs(Double.compare(object.getPosition().getDouble(1), y)) < epsilon) {
                return false;
            }
        }
        return gameMap.canMoveToTile((int) Math.round(x), (int) Math.round(y));
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        try {
            renderBackgroundLayer(g2);
            renderDetailLayer(g2);
            renderObjectLayer(g2);
            renderForegroundLayer(g2);
        } catch (IOException e) {
            g2.dispose();
            throw new RuntimeException(e);
        }

        g2.dispose();
    }

    private void renderBackgroundLayer(Graphics2D g2) throws IOException {
        BufferedImage texture = gameMap.getBackground();
        g2.drawImage(texture,
                tileSize * SCALE * (centerX - cameraX) - textureOffsetX,
                tileSize * SCALE * (centerY - cameraY) - textureOffsetY,
                texture.getWidth() * SCALE,
                texture.getHeight() * SCALE,
                null
        );
    }

    private void renderDetailLayer(Graphics2D g2) {

    }

    private void renderObjectLayer(Graphics2D g2) {
        UUIDTable.getObjects().stream().sorted(Comparator.comparingInt(SpriteUtils::getY)).forEach(object -> {
            try {
                this.renderObject(g2, object);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void renderForegroundLayer(Graphics2D g2) {
        BufferedImage texture = gameMap.getForeground();
        g2.drawImage(texture,
                tileSize * SCALE * (centerX - cameraX) - textureOffsetX,
                tileSize * SCALE * (centerY - cameraY) - textureOffsetY,
                texture.getWidth() * SCALE,
                texture.getHeight() * SCALE,
                null
        );
    }

    private void renderObject(Graphics2D g2, RPGLObject object) throws IOException {
        BufferedImage texture = ImageIO.read(new File(object.getTexture()));

        g2.drawImage(texture,
                (SpriteUtils.getX(object) - cameraX) * tileSize * SCALE,
                ((SpriteUtils.getY(object) - cameraY) * tileSize - (texture.getHeight() / 4 - tileSize)) * SCALE
                        + spriteOffsetY,
                (SpriteUtils.getX(object) - cameraX + 1) * tileSize * SCALE,
                (SpriteUtils.getY(object) - cameraY + 1) * tileSize * SCALE
                        + spriteOffsetY,
                (texture.getWidth() / 4) * stride,
                (texture.getHeight() / 4) * SpriteUtils.getRotation(object),
                (texture.getWidth() / 4) * (stride + 1),
                (texture.getHeight() / 4) * (SpriteUtils.getRotation(object) + 1),
                null
        );
    }



}
