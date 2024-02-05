package org.mnm.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.mnm.util.SpriteUtils;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public final class MapLoader {

    public static GameMap getMap(String mapFile) throws IOException {
        @SuppressWarnings("unchecked")
        JsonObject mapData = new JsonObject(new ObjectMapper().readValue(new File(mapFile), HashMap.class));

        BufferedImage background = ImageIO.read(new File(mapData.getString("background")));
        BufferedImage foreground = ImageIO.read(new File(mapData.getString("foreground")));

        JsonArray objects = mapData.getJsonArray("objects");
        for (int i = 0; i < objects.size(); i++) {
            JsonObject objectData = objects.getJsonObject(i);
            JsonArray pos = objectData.getJsonArray("pos");
            SpriteUtils.newObject(objectData.getString("id"), pos.getInteger(0), pos.getInteger(1), objectData.getInteger("rot"))
                    .setName(objectData.getString("name"));
        }

        return new GameMap(background, foreground, mapData.getJsonArray("collisions"));
    }

}
