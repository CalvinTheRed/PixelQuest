package org.pixelquest.ui;

import org.pixelquest.core.GameMap;
import org.pixelquest.core.MapLoader;
import org.pixelquest.input.ContinuousKeyHandler;
import org.pixelquest.input.DiscreteKeyHandler;
import org.pixelquest.rpgl.subevent.GetDialog;
import org.pixelquest.util.SpriteUtils;
import org.rpgl.core.RPGLObject;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;
import org.rpgl.uuidtable.UUIDTable;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class GamePanel extends JPanel implements Runnable {

    final int FPS = 60;
    final int SCALE = 4;
    final int COLS = 19;
    final int ROWS = 13;
    private final int TILE_SIZE = 12;

    private GameMap gameMap;
    private Thread gameThread;
    private final ContinuousKeyHandler continuousKeyHandler = new ContinuousKeyHandler();
    private final DiscreteKeyHandler discreteKeyHandler = new DiscreteKeyHandler();

    private final int spriteOffsetY;
    private double cameraX, cameraY;

    private double speed = 0.07d;
    private Integer keyBeingHandled;
    private JsonArray currentDialog = null;

    private RPGLObject focusObject = SpriteUtils.newObject("std:humanoid/commoner", 25, 18, SpriteUtils.FACING_DOWN);
    private final HealthBar healthBar;

    public GamePanel(String initialMap) throws Exception {
        gameMap = MapLoader.getMap(initialMap);
        spriteOffsetY = -TILE_SIZE * SCALE / 4;

        this.setPreferredSize(new Dimension(
                TILE_SIZE * SCALE * COLS,
                TILE_SIZE * SCALE * ROWS
        ));
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true);
        this.addKeyListener(continuousKeyHandler);
        this.addKeyListener(discreteKeyHandler);
        this.setFocusable(true);

        focusCamera(focusObject);
        gameMap.getContext().add(focusObject);

        healthBar = new HealthBar(
                focusObject.getHealthData().getInteger("current"),
                focusObject.getMaximumHitPoints(gameMap.getContext())
        );
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

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        try {
            renderBackgroundLayer(g2);
            renderDetailLayer(g2);
            renderObjectLayer(g2);
            renderForegroundLayer(g2);
            renderPanels(g2);
            renderDialog(g2);
        } catch (IOException e) {
            g2.dispose();
            throw new RuntimeException(e);
        }

        g2.dispose();
    }

    private void renderBackgroundLayer(Graphics2D g2) throws IOException {
        BufferedImage texture = gameMap.getBackground();
        g2.drawImage(texture,
                (int) (TILE_SIZE * SCALE * -cameraX),
                (int) (TILE_SIZE * SCALE * -cameraY),
                texture.getWidth() * SCALE,
                texture.getHeight() * SCALE,
                null
        );
    }

    private void renderDetailLayer(Graphics2D g2) {

    }

    private void renderObjectLayer(Graphics2D g2) {
        gameMap.getContext().getContextObjects().stream()
                .sorted(Comparator.comparingDouble(SpriteUtils::getY))
                .forEach(object -> {
                    try {
                        this.renderObject(g2, object);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    private void renderObject(Graphics2D g2, RPGLObject object) throws IOException {
        BufferedImage texture = ImageIO.read(new File(object.getTexture()));

        g2.drawImage(texture,
                (int) ((SpriteUtils.getX(object) - cameraX) * TILE_SIZE * SCALE),
                (int) (((SpriteUtils.getY(object) - cameraY) * TILE_SIZE - (texture.getHeight() / 4 - TILE_SIZE)) * SCALE
                        + spriteOffsetY),
                (int) ((SpriteUtils.getX(object) - cameraX + 1) * TILE_SIZE * SCALE),
                (int) ((SpriteUtils.getY(object) - cameraY + 1) * TILE_SIZE * SCALE
                        + spriteOffsetY),
                (texture.getWidth() / 4) * SpriteUtils.getStride(object),
                (texture.getHeight() / 4) * SpriteUtils.getRotation(object),
                (texture.getWidth() / 4) * (SpriteUtils.getStride(object) + 1),
                (texture.getHeight() / 4) * (SpriteUtils.getRotation(object) + 1),
                null
        );
    }

    private void renderForegroundLayer(Graphics2D g2) {
        BufferedImage texture = gameMap.getForeground();
        if (texture != null) {
            g2.drawImage(texture,
                    (int) (TILE_SIZE * SCALE * -cameraX),
                    (int) (TILE_SIZE * SCALE * -cameraY),
                    texture.getWidth() * SCALE,
                    texture.getHeight() * SCALE,
                    null
            );
        }
    }

    private void renderPanels(Graphics2D g2) throws IOException {
        this.healthBar.paintMe(g2);
    }

    private void renderDialog(Graphics2D g2) {
        if (this.currentDialog != null && this.currentDialog.size() > 0) {
            int borderThickness = 5;
            g2.setColor(Color.WHITE);
            g2.fillRoundRect(
                    borderThickness,
                    borderThickness,
                    getWidth() - (borderThickness * 2),
                    (TILE_SIZE * SCALE - borderThickness) * 2,
                    20,
                    20
            );
            g2.setColor(Color.DARK_GRAY);
            g2.setStroke(new BasicStroke(borderThickness));
            g2.drawRoundRect(
                    borderThickness,
                    borderThickness,
                    getWidth() - (borderThickness * 2),
                    (TILE_SIZE * SCALE - borderThickness) * 2,
                    20,
                    20
            );
            g2.setFont(new Font("EXEPixelPerfect", Font.PLAIN, 20));
            g2.drawString(this.currentDialog.getString(0), borderThickness * 3, borderThickness * 6);
        }
    }

    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    public void update() {
        updateMotion();
        try {
            updateInteraction();
            updateDialog();
        } catch (Exception e) {
            System.out.println("Failed to update dialog!");
            System.out.println(e.getMessage());
        }
    }

    public void updateMotion() {
        Integer code = this.continuousKeyHandler.getPriorityKey(List.of(KeyEvent.VK_W, KeyEvent.VK_A, KeyEvent.VK_S, KeyEvent.VK_D));

        // look to start processing a key press if not in dialog
        if (this.keyBeingHandled == null && this.currentDialog == null) {
            this.keyBeingHandled = code;
        }

        // process current key
        if (this.keyBeingHandled != null) {
            switch (this.keyBeingHandled) {
                case KeyEvent.VK_W -> {
                    SpriteUtils.setRotation(this.focusObject, SpriteUtils.FACING_UP);
                    int targetX = (int) SpriteUtils.getX(this.focusObject);
                    int targetY = (int) Math.ceil(SpriteUtils.getY(this.focusObject)) - 1;
                    if (this.canMoveToTile(targetX, targetY)) {
                        this.cameraY -= this.speed;
                        SpriteUtils.setY(this.focusObject, SpriteUtils.getY(this.focusObject) - this.speed);
                        if (SpriteUtils.getStride(this.focusObject) % 2 == 0 && SpriteUtils.getY(this.focusObject) % 1 < 0.5d) {
                            SpriteUtils.setStride(this.focusObject, (SpriteUtils.getStride(this.focusObject) + 1) % 4);
                        }
                        if (this.cameraY % 1 < this.speed / 2d || this.cameraY % 1 > 1 - this.speed / 2d) {
                            this.cameraY = Math.round(this.cameraY);
                            SpriteUtils.setY(this.focusObject, Math.round(SpriteUtils.getY(this.focusObject)));
                            SpriteUtils.setStride(this.focusObject, (SpriteUtils.getStride(this.focusObject) + 1) % 4);
                            this.keyBeingHandled = null;
                            this.checkForWarp();
                        }
                    } else {
                        this.keyBeingHandled = null;
                    }
                }
                case KeyEvent.VK_A -> {
                    SpriteUtils.setRotation(this.focusObject, SpriteUtils.FACING_LEFT);
                    int targetX = (int) Math.ceil(SpriteUtils.getX(this.focusObject)) - 1;
                    int targetY = (int) SpriteUtils.getY(this.focusObject);
                    if (this.canMoveToTile(targetX, targetY)) {
                        this.cameraX -= this.speed;
                        SpriteUtils.setX(this.focusObject, SpriteUtils.getX(this.focusObject) - this.speed);
                        if (SpriteUtils.getStride(this.focusObject) % 2 == 0 && SpriteUtils.getX(this.focusObject) % 1 < 0.5d) {
                            SpriteUtils.setStride(this.focusObject, (SpriteUtils.getStride(this.focusObject) + 1) % 4);
                        }
                        if (this.cameraX % 1 < this.speed / 2d || this.cameraX % 1 > 1 - this.speed / 2d) {
                            this.cameraX = Math.round(this.cameraX);
                            SpriteUtils.setX(this.focusObject, Math.round(SpriteUtils.getX(this.focusObject)));
                            SpriteUtils.setStride(this.focusObject, (SpriteUtils.getStride(this.focusObject) + 1) % 4);
                            this.keyBeingHandled = null;
                            this.checkForWarp();
                        }
                    } else {
                        this.keyBeingHandled = null;
                    }
                }
                case KeyEvent.VK_S -> {
                    SpriteUtils.setRotation(this.focusObject, SpriteUtils.FACING_DOWN);
                    int targetX = (int) SpriteUtils.getX(this.focusObject);
                    int targetY = (int) Math.floor(SpriteUtils.getY(this.focusObject)) + 1;
                    if (this.canMoveToTile(targetX, targetY)) { // short-circuit by checking if coordinate is decimal?
                        this.cameraY += this.speed;
                        SpriteUtils.setY(this.focusObject, SpriteUtils.getY(this.focusObject) + this.speed);
                        if (SpriteUtils.getStride(this.focusObject) % 2 == 0 && SpriteUtils.getY(this.focusObject) % 1 > 0.5d) {
                            SpriteUtils.setStride(this.focusObject, (SpriteUtils.getStride(this.focusObject) + 1) % 4);
                        }
                        if (this.cameraY % 1 < this.speed / 2d || this.cameraY % 1 > 1 - this.speed / 2d) {
                            this.cameraY = Math.round(this.cameraY);
                            SpriteUtils.setY(this.focusObject, Math.round(SpriteUtils.getY(this.focusObject)));
                            SpriteUtils.setStride(this.focusObject, (SpriteUtils.getStride(this.focusObject) + 1) % 4);
                            this.keyBeingHandled = null;
                            this.checkForWarp();
                        }
                    } else {
                        this.keyBeingHandled = null;
                    }
                }
                case KeyEvent.VK_D -> {
                    SpriteUtils.setRotation(this.focusObject, SpriteUtils.FACING_RIGHT);
                    int targetX = (int) Math.floor(SpriteUtils.getX(this.focusObject)) + 1;
                    int targetY = (int) SpriteUtils.getY(this.focusObject);
                    if (this.canMoveToTile(targetX, targetY)) {
                        this.cameraX += this.speed;
                        SpriteUtils.setX(this.focusObject, SpriteUtils.getX(this.focusObject) + this.speed);
                        if (SpriteUtils.getStride(this.focusObject) % 2 == 0 && SpriteUtils.getX(this.focusObject) % 1 > 0.5d) {
                            SpriteUtils.setStride(this.focusObject, (SpriteUtils.getStride(this.focusObject) + 1) % 4);
                        }
                        if (this.cameraX % 1 < this.speed / 2d || this.cameraX % 1 > 1 - this.speed / 2d) {
                            this.cameraX = Math.round(this.cameraX);
                            SpriteUtils.setX(this.focusObject, Math.round(SpriteUtils.getX(this.focusObject)));
                            SpriteUtils.setStride(this.focusObject, (SpriteUtils.getStride(this.focusObject) + 1) % 4);
                            this.keyBeingHandled = null;
                            this.checkForWarp();
                        }
                    } else {
                        this.keyBeingHandled = null;
                    }
                }
            }
        }
    }

    private void updateInteraction() {

    }

    public void updateDialog() throws Exception {
        if (this.discreteKeyHandler.isKeyDown(KeyEvent.VK_SPACE)) {
            this.discreteKeyHandler.acknowledgeKeyPressed(KeyEvent.VK_SPACE);
            final RPGLObject target;
            switch(SpriteUtils.getRotation(this.focusObject)) {
                case SpriteUtils.FACING_DOWN -> {
                    List<RPGLObject> objectsAt = this.objectsAt(
                            SpriteUtils.getX(this.focusObject),
                            SpriteUtils.getY(this.focusObject) + 1
                    );
                    if (objectsAt.isEmpty()) {
                        target = null;
                    } else {
                        target = objectsAt.get(0);
                    }
                }
                case SpriteUtils.FACING_LEFT -> {
                    List<RPGLObject> objectsAt = this.objectsAt(
                            SpriteUtils.getX(this.focusObject) - 1,
                            SpriteUtils.getY(this.focusObject)
                    );
                    if (objectsAt.isEmpty()) {
                        target = null;
                    } else {
                        target = objectsAt.get(0);
                    }
                }
                case SpriteUtils.FACING_UP -> {
                    List<RPGLObject> objectsAt = this.objectsAt(
                            SpriteUtils.getX(this.focusObject),
                            SpriteUtils.getY(this.focusObject) - 1
                    );
                    if (objectsAt.isEmpty()) {
                        target = null;
                    } else {
                        target = objectsAt.get(0);
                    }
                }
                case SpriteUtils.FACING_RIGHT -> {
                    List<RPGLObject> objectsAt = this.objectsAt(
                            SpriteUtils.getX(this.focusObject) + 1,
                            SpriteUtils.getY(this.focusObject)
                    );
                    if (objectsAt.isEmpty()) {
                        target = null;
                    } else {
                        target = objectsAt.get(0);
                    }
                }
                default -> target = null;
            }
            if (target != null) {
                if (this.currentDialog == null) {
                    // start dialog
                    this.currentDialog = new GetDialog()
                            .joinSubeventData(new JsonObject())
                            .setSource(focusObject)
                            .prepare(gameMap.getContext(), focusObject.getPosition())
                            .setTarget(target)
                            .invoke(gameMap.getContext(), focusObject.getPosition())
                            .getDialog();
                } else if (!this.currentDialog.asList().isEmpty()) {
                    // continue dialog
                    this.currentDialog.asList().remove(0);
                }
                if (this.currentDialog.asList().isEmpty()) {
                    // end dialog
                    this.currentDialog = null;
                }
            }
        }
    }

    public boolean canMoveToTile(double x, double y) {
        if (this.objectsAt(x, y).size() == 0) {
            return gameMap.canMoveToTile((int) Math.round(x), (int) Math.round(y));
        }
        return false;
    }

    public List<RPGLObject> objectsAt(double x, double y) {
        List<RPGLObject> objectsAtLocation = new ArrayList<>();
        double epsilon = 0.000001d;
        for (RPGLObject object : UUIDTable.getObjects()) {
            if (Math.abs(Double.compare(object.getPosition().getDouble(0), x)) < epsilon
                    && Math.abs(Double.compare(object.getPosition().getDouble(1), y)) < epsilon) {
                objectsAtLocation.add(object);
            }
        }
        return objectsAtLocation;
    }

    private void checkForWarp() {
        GameMap.WarpZoneDetails details = gameMap.isWarpZone(
                (int) SpriteUtils.getX(focusObject), (int) SpriteUtils.getY(focusObject)
        );
        if (details != null) {
            try {
                gameMap.getContext().remove(focusObject);
                gameMap = MapLoader.getMap(details.map);
                gameMap.getContext().add(focusObject);
                SpriteUtils.setX(focusObject, details.coordinate.getInteger(0));
                SpriteUtils.setY(focusObject, details.coordinate.getInteger(1));
                focusCamera(focusObject);
            } catch (IOException e) {
                System.out.println(e.getMessage());
                throw new RuntimeException(e);
            }
        }
    }

    public void focusCamera(RPGLObject object) {
        this.cameraX = SpriteUtils.getX(object) - COLS / 2;
        this.cameraY = SpriteUtils.getY(object) - ROWS / 2;
    }

}
