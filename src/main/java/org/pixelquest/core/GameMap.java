package org.pixelquest.core;

import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;

import java.awt.image.BufferedImage;

public class GameMap {

    private final BufferedImage background;
    private final BufferedImage foreground;
    private final JsonArray collisions;
    private final JsonArray warpZones;

    public class WarpZoneDetails {
        public String map;
        public JsonArray coordinate;

        public WarpZoneDetails(String map, JsonArray coordinate) {
            this.map = map;
            this.coordinate = coordinate;
        }
    }

    public GameMap(BufferedImage background, BufferedImage foreground, JsonArray collisions, JsonArray warpZones) {
        this.background = background;
        this.foreground = foreground;
        this.collisions = collisions;
        this.warpZones = warpZones;
    }

    public BufferedImage getBackground() {
        return this.background;
    }

    public BufferedImage getForeground() {
        return this.foreground;
    }

    public boolean canMoveToTile(int x, int y) {
        return this.collisions.getJsonArray(y).getInteger(x) == 0;
    }

    public WarpZoneDetails isWarpZone(int x, int y) {
        for (int i = 0; i < this.warpZones.size(); i++) {
            JsonObject warpZone = warpZones.getJsonObject(i);
            JsonArray tileCoordinate = warpZone.getJsonArray("pos");
            if (x == tileCoordinate.getInteger(0) && y == tileCoordinate.getInteger(1)) {
                return new WarpZoneDetails(warpZone.getString("map"), warpZone.getJsonArray("coordinate"));
            }
        }
        return null;
    }

}
