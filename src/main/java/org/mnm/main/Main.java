package org.mnm.main;

import org.rpgl.core.RPGLCore;

import javax.swing.*;
import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        setupRPGL();
        setupGraphics();
    }

    private static void setupRPGL() {
        RPGLCore.initialize();
    }

    private static void setupGraphics() {
        JFrame window = new JFrame();
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);
        window.setTitle("Might & Magic");

        GamePanel gamePanel;
        try {
            gamePanel = new GamePanel("resources/maps/test-island");
        } catch (IOException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
        window.add(gamePanel);
        window.pack();

        window.setLocationRelativeTo(null);
        window.setVisible(true);

        gamePanel.startGameThread();
    }

}