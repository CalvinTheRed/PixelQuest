package org.pixelquest.main;

import org.pixelquest.rpgl.functions.AddDialog;
import org.pixelquest.rpgl.subevents.GetDialog;
import org.rpgl.core.RPGLCore;
import org.rpgl.datapack.DatapackLoader;
import org.rpgl.function.Function;
import org.rpgl.subevent.Subevent;

import javax.swing.*;
import java.io.File;
import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        setupRPGL();
        setupGraphics();
    }

    private static void setupRPGL() {
        RPGLCore.initialize();
        Subevent.SUBEVENTS.put("get_dialog", new GetDialog());
        Function.FUNCTIONS.put("add_dialog", new AddDialog());
        DatapackLoader.loadDatapacks(new File("datapacks"));
    }

    private static void setupGraphics() {
        JFrame window = new JFrame();
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);
        window.setTitle("Pixel Quest");

        GamePanel gamePanel;
        try {
            gamePanel = new GamePanel("resources/maps/test-island.json");
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