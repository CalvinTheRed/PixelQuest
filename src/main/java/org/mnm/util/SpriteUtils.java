package org.mnm.util;

import org.rpgl.core.RPGLFactory;
import org.rpgl.core.RPGLObject;
import org.rpgl.json.JsonArray;

public final class SpriteUtils {

    public static final int FACING_DOWN = 0;
    public static final int FACING_LEFT = 1;
    public static final int FACING_UP = 2;
    public static final int FACING_RIGHT = 3;

    public static RPGLObject newObject(String objectId, int x, int y, int rotation) {
        return SpriteUtils.setRotation(SpriteUtils.setY(SpriteUtils.setX(RPGLFactory.newObject(objectId, "user"), x), y), rotation);
    }

    public static RPGLObject newObject(String objectId, JsonArray pos, JsonArray rot) {
        return RPGLFactory.newObject(objectId, "user").setPosition(pos).setRotation(rot);
    }

    public static int getX(RPGLObject object) {
        return (int) Math.round(object.getPosition().getDouble(0));
    }

    public static RPGLObject setX(RPGLObject object, int x) {
        return object.setPosition(new JsonArray() {{
            this.addDouble((double) x);
            this.addDouble(object.getPosition().getDouble(1));
        }});
    }

    public static int getY(RPGLObject object) {
        return (int) Math.round(object.getPosition().getDouble(1));
    }

    public static RPGLObject setY(RPGLObject object, int y) {
        return object.setPosition(new JsonArray() {{
            this.addDouble(object.getPosition().getDouble(0));
            this.addDouble((double) y);
        }});
    }

    public static int getRotation(RPGLObject object) {
        return (int) Math.round(object.getRotation().getDouble(0));
    }

    public static RPGLObject setRotation(RPGLObject object, int rotation) {
        return object.setRotation(new JsonArray() {{
            this.addDouble((double) rotation);
        }});
    }

}
