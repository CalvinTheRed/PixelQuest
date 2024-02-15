package org.pixelquest.rpgl.subevent;

import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLObject;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;
import org.rpgl.subevent.Subevent;

import java.util.ArrayList;

public class GetDialog extends Subevent {

    public GetDialog() {
        super("get_dialog");
    }

    @Override
    public Subevent clone() {
        Subevent clone = new GetDialog();
        clone.joinSubeventData(this.json);
        clone.appliedEffects.addAll(this.appliedEffects);
        return clone;
    }

    @Override
    public Subevent clone(JsonObject jsonData) {
        Subevent clone = new GetDialog();
        clone.joinSubeventData(jsonData);
        clone.appliedEffects.addAll(this.appliedEffects);
        return clone;
    }

    @Override
    public GetDialog invoke(RPGLContext context, JsonArray originPoint) throws Exception {
        return (GetDialog) super.invoke(context, originPoint);
    }

    @Override
    public GetDialog joinSubeventData(JsonObject other) {
        return (GetDialog) super.joinSubeventData(other);
    }

    @Override
    public GetDialog prepare(RPGLContext context, JsonArray originPoint) throws Exception {
        this.json.asMap().putIfAbsent("dialog", new ArrayList<>());
        return (GetDialog) super.prepare(context, originPoint);
    }

    @Override
    public GetDialog run(RPGLContext context, JsonArray originPoint) {
        if (this.getDialog().asList().isEmpty()) {
            addDialog("...");
            addDialog("Can I help you?");
        }
        return this;
    }

    @Override
    public GetDialog setOriginItem(String originItem) {
        return (GetDialog) super.setOriginItem(originItem);
    }

    @Override
    public GetDialog setSource(RPGLObject source) {
        return (GetDialog) super.setSource(source);
    }

    @Override
    public GetDialog setTarget(RPGLObject target) {
        return (GetDialog) super.setTarget(target);
    }

    public void addDialog(String dialog) {
        this.json.getJsonArray("dialog").addString(dialog);
    }

    public JsonArray getDialog() {
        return this.json.getJsonArray("dialog");
    }

}
