package org.pixelquest.rpgl.core;

import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLObject;
import org.rpgl.subevent.Subevent;

public class CustomContext extends RPGLContext {

    @Override
    public boolean isObjectsTurn(RPGLObject object) {
        return false;
    }

    @Override
    public void viewCompletedSubevent(Subevent subevent) {
        super.viewCompletedSubevent(subevent);
    }

}
