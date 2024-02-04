package org.mnm.core;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public final class MapLoader {

    private static final String BACKGROUND_FILE_APPEND = "/background.png";
    private static final String FOREGROUND_FILE_APPEND = "/foreground.png";
    private static final String COLLISION_FILE_APPEND = "/collisions.csv";
    private static final int TILE_SIZE = 12;

    public static Map getMap(String mapDir) throws IOException {
        BufferedImage background = ImageIO.read(new File(mapDir + BACKGROUND_FILE_APPEND));
        BufferedImage foreground = ImageIO.read(new File(mapDir + FOREGROUND_FILE_APPEND));
        int mapWidth = background.getWidth() / TILE_SIZE;
        int mapHeight = background.getHeight() / TILE_SIZE;

        boolean[][] collisions = new boolean[mapHeight][mapWidth];
        try (BufferedReader br = new BufferedReader(new FileReader(mapDir + COLLISION_FILE_APPEND))) {
            String line;
            int x = 0; int y = 0;
            while ((line = br.readLine()) != null) {
                for (String value : line.replaceAll("\\s", "").split(",")) {
                    collisions[y][x++] = !"0".equals(value);
                    if (x == mapWidth) {
                        x = 0;
                        y++;
                    }
                }
            }
        }

        return new Map(background, foreground, collisions);
    }

}
