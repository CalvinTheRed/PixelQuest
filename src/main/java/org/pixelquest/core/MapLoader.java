package org.pixelquest.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.pixelquest.rpgl.core.CustomContext;
import org.pixelquest.util.SpriteUtils;
import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLFactory;
import org.rpgl.core.RPGLObject;
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

        String foregroundPath = mapData.getString("foreground");
        BufferedImage foreground;
        if (foregroundPath != null) {
            foreground = ImageIO.read(new File(foregroundPath));
        } else {
            foreground = null;
        }

        RPGLContext context = new CustomContext();

        JsonArray objects = mapData.getJsonArray("objects");
        for (int i = 0; i < objects.size(); i++) {
            JsonObject objectData = objects.getJsonObject(i);
            JsonArray pos = objectData.getJsonArray("pos");
            RPGLObject newObject = SpriteUtils.newObject(
                    objectData.getString("id"),
                    pos.getInteger(0),
                    pos.getInteger(1),
                    objectData.getInteger("rot")
            );
            JsonArray effects = objectData.getJsonArray("effects");
            if (effects != null) {
                for (int j = 0; j < effects.size(); j++) {
                    newObject.addEffect(RPGLFactory.newEffect(effects.getString(j))
                            .setSource(newObject)
                            .setTarget(newObject));
                }
            }
            newObject.setName(objectData.getString("name"));
            context.add(newObject);
        }

        return new GameMap(background, foreground, mapData.getJsonArray("collisions"), mapData.getJsonArray("warp_zones"), context);
    }

}
