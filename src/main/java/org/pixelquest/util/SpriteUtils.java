package org.pixelquest.util;

import org.rpgl.core.RPGLFactory;
import org.rpgl.core.RPGLObject;
import org.rpgl.json.JsonArray;

public final class SpriteUtils {

    public static final int FACING_DOWN = 0;
    public static final int FACING_LEFT = 1;
    public static final int FACING_UP = 2;
    public static final int FACING_RIGHT = 3;

    public static RPGLObject newObject(String objectId, int x, int y, int rotation) {
        return SpriteUtils.setStride(SpriteUtils.setRotation(SpriteUtils.setY(SpriteUtils.setX(RPGLFactory.newObject(objectId, "user"), x), y), rotation), 0);
    }

    public static double getX(RPGLObject object) {
        return object.getPosition().getDouble(0);
    }

    public static RPGLObject setX(RPGLObject object, double x) {
        return object.setPosition(new JsonArray() {{
            this.addDouble(x);
            this.addDouble(object.getPosition().getDouble(1));
        }});
    }

    public static double getY(RPGLObject object) {
        return object.getPosition().getDouble(1);
    }

    public static RPGLObject setY(RPGLObject object, double y) {
        return object.setPosition(new JsonArray() {{
            this.addDouble(object.getPosition().getDouble(0));
            this.addDouble(y);
        }});
    }

    public static int getRotation(RPGLObject object) {
        return (int) Math.round(object.getRotation().getDouble(0));
    }

    public static RPGLObject setRotation(RPGLObject object, int rotation) {
        return object.setRotation(new JsonArray() {{
            this.addDouble((double) rotation);
            this.addDouble((double) SpriteUtils.getStride(object));
        }});
    }

    public static int getStride(RPGLObject object) {
        return (int) Math.round(object.getRotation().getDouble(1));
    }

    public static RPGLObject setStride(RPGLObject object, int stride) {
        return object.setRotation(new JsonArray() {{
            this.addDouble((double) SpriteUtils.getRotation(object));
            this.addDouble((double) stride);
        }});
    }

}
