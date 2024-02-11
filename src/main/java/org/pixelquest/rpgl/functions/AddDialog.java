package org.pixelquest.rpgl.functions;

import org.pixelquest.rpgl.subevents.GetDialog;
import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLEffect;
import org.rpgl.function.Function;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;
import org.rpgl.subevent.Subevent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AddDialog extends Function {

    private static final Logger LOGGER = LoggerFactory.getLogger(AddDialog.class);

    public AddDialog() {
        super("add_dialog");
    }

    @Override
    public void run(RPGLEffect effect, Subevent subevent, JsonObject functionJson, RPGLContext context, JsonArray originPoint) {
        if (subevent instanceof GetDialog getDialog) {
            JsonArray dialog = functionJson.getJsonArray("dialog");
            for (int i = 0; i < dialog.size(); i++) {
                getDialog.addDialog(dialog.getString(i));
            }
        } else {
            LOGGER.warn("Can not execute function on " + subevent.getClass());
        }
    }

}
