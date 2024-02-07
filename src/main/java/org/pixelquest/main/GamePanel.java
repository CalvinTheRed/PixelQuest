package org.pixelquest.main;

import org.pixelquest.core.GameMap;
import org.pixelquest.core.MapLoader;
import org.pixelquest.input.ContinuousKeyHandler;
import org.pixelquest.util.SpriteUtils;
import org.rpgl.core.RPGLObject;
import org.rpgl.uuidtable.UUIDTable;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;

public class GamePanel extends JPanel implements Runnable {

    final int FPS = 60;
    final int SCALE = 4;
    final int COLS = 19;
    final int ROWS = 13;

    private GameMap gameMap;
    private Thread gameThread;
    private final ContinuousKeyHandler keyHandler = new ContinuousKeyHandler(List.of(
            KeyEvent.VK_W,
            KeyEvent.VK_A,
            KeyEvent.VK_S,
            KeyEvent.VK_D
    ));

    private final int tileSize = 12;
    private final int spriteOffsetY;
    private double cameraX, cameraY;

    private double speed = 0.14d;
    private Integer handlingKeyPress;

    RPGLObject focusObject = SpriteUtils.newObject("std:humanoid/commoner", 25, 18, SpriteUtils.FACING_DOWN);

    public GamePanel(String initialMap) throws IOException {
        gameMap = MapLoader.getMap(initialMap);
        spriteOffsetY = -tileSize * SCALE / 4;

        this.setPreferredSize(new Dimension(
                tileSize * SCALE * COLS,
                tileSize * SCALE * ROWS
        ));
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyHandler);
        this.setFocusable(true);

        cameraX = SpriteUtils.getX(focusObject) - COLS / 2;
        cameraY = SpriteUtils.getY(focusObject) - ROWS / 2;
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

    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    public void update() {
        Integer code = keyHandler.getKey();

        // look to start processing a key press
        if (handlingKeyPress == null) {
            handlingKeyPress = code;
        }

        // process current key
        if (handlingKeyPress != null) {
            switch (handlingKeyPress) {
                case KeyEvent.VK_W -> {
                    SpriteUtils.setRotation(focusObject, SpriteUtils.FACING_UP);
                    SpriteUtils.setStride(focusObject, (SpriteUtils.getStride(focusObject) + 1) % 4);
                    int targetX = (int) SpriteUtils.getX(focusObject);
                    int targetY = (int) Math.ceil(SpriteUtils.getY(focusObject)) - 1;
                    if (this.isSpaceEmpty(targetX, targetY) && gameMap.canMoveToTile(targetX, targetY)) {
                        cameraY -= speed;
                        SpriteUtils.setY(focusObject, SpriteUtils.getY(focusObject) - speed);
                        if (cameraY % 1 < speed / 2d || cameraY % 1 > 1 - speed / 2d) {
                            cameraY = Math.round(cameraY);
                            SpriteUtils.setY(focusObject, Math.round(SpriteUtils.getY(focusObject)));
                            this.handlingKeyPress = null;
                        }
                    } else {
                        this.handlingKeyPress = null;
                    }
                }
                case KeyEvent.VK_A -> {
                    SpriteUtils.setRotation(focusObject, SpriteUtils.FACING_LEFT);
                    SpriteUtils.setStride(focusObject, (SpriteUtils.getStride(focusObject) + 1) % 4);
                    int targetX = (int) Math.ceil(SpriteUtils.getX(focusObject)) - 1;
                    int targetY = (int) SpriteUtils.getY(focusObject);
                    if (this.isSpaceEmpty(targetX, targetY) && gameMap.canMoveToTile(targetX, targetY)) {
                        cameraX -= speed;
                        SpriteUtils.setX(focusObject, SpriteUtils.getX(focusObject) - speed);
                        if (cameraX % 1 < speed / 2d || cameraX % 1 > 1 - speed / 2d) {
                            cameraX = Math.round(cameraX);
                            SpriteUtils.setX(focusObject, Math.round(SpriteUtils.getX(focusObject)));
                            this.handlingKeyPress = null;
                        }
                    } else {
                        this.handlingKeyPress = null;
                    }
                }
                case KeyEvent.VK_S -> {
                    SpriteUtils.setRotation(focusObject, SpriteUtils.FACING_DOWN);
                    SpriteUtils.setStride(focusObject, (SpriteUtils.getStride(focusObject) + 1) % 4);
                    int targetX = (int) SpriteUtils.getX(focusObject);
                    int targetY = (int) Math.floor(SpriteUtils.getY(focusObject)) + 1;
                    if (this.isSpaceEmpty(targetX, targetY) && gameMap.canMoveToTile(targetX, targetY)) {
                        cameraY += speed;
                        SpriteUtils.setY(focusObject, SpriteUtils.getY(focusObject) + speed);
                        if (cameraY % 1 < speed / 2d || cameraY % 1 > 1 - speed / 2d) {
                            cameraY = Math.round(cameraY);
                            SpriteUtils.setY(focusObject, Math.round(SpriteUtils.getY(focusObject)));
                            this.handlingKeyPress = null;
                        }
                    } else {
                        this.handlingKeyPress = null;
                    }
                }
                case KeyEvent.VK_D -> {
                    SpriteUtils.setRotation(focusObject, SpriteUtils.FACING_RIGHT);
                    SpriteUtils.setStride(focusObject, (SpriteUtils.getStride(focusObject) + 1) % 4);
                    int targetX = (int) Math.floor(SpriteUtils.getX(focusObject)) + 1;
                    int targetY = (int) SpriteUtils.getY(focusObject);
                    if (this.isSpaceEmpty(targetX, targetY) && gameMap.canMoveToTile(targetX, targetY)) {
                        cameraX += speed;
                        SpriteUtils.setX(focusObject, SpriteUtils.getX(focusObject) + speed);
                        if (cameraX % 1 < speed / 2d || cameraX % 1 > 1 - speed / 2d) {
                            cameraX = Math.round(cameraX);
                            SpriteUtils.setX(focusObject, Math.round(SpriteUtils.getX(focusObject)));
                            this.handlingKeyPress = null;
                        }
                    } else {
                        this.handlingKeyPress = null;
                    }
                }
            }
        }
    }

    public boolean isSpaceEmpty(double x, double y) {
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
                (int) (tileSize * SCALE * -cameraX),
                (int) (tileSize * SCALE * -cameraY),
                texture.getWidth() * SCALE,
                texture.getHeight() * SCALE,
                null
        );
    }

    private void renderDetailLayer(Graphics2D g2) {

    }

    private void renderObjectLayer(Graphics2D g2) {
        UUIDTable.getObjects().stream().sorted(Comparator.comparingDouble(SpriteUtils::getY)).forEach(object -> {
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
                (int) (tileSize * SCALE * -cameraX),
                (int) (tileSize * SCALE * -cameraY),
                texture.getWidth() * SCALE,
                texture.getHeight() * SCALE,
                null
        );
    }

    private void renderObject(Graphics2D g2, RPGLObject object) throws IOException {
        BufferedImage texture = ImageIO.read(new File(object.getTexture()));

        g2.drawImage(texture,
                (int) ((SpriteUtils.getX(object) - cameraX) * tileSize * SCALE),
                (int) (((SpriteUtils.getY(object) - cameraY) * tileSize - (texture.getHeight() / 4 - tileSize)) * SCALE
                        + spriteOffsetY),
                (int) ((SpriteUtils.getX(object) - cameraX + 1) * tileSize * SCALE),
                (int) ((SpriteUtils.getY(object) - cameraY + 1) * tileSize * SCALE
                        + spriteOffsetY),
                (texture.getWidth() / 4) * SpriteUtils.getStride(object),
                (texture.getHeight() / 4) * SpriteUtils.getRotation(object),
                (texture.getWidth() / 4) * (SpriteUtils.getStride(object) + 1),
                (texture.getHeight() / 4) * (SpriteUtils.getRotation(object) + 1),
                null
        );
    }



}
