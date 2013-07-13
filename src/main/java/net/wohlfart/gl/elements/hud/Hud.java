package net.wohlfart.gl.elements.hud;

import net.wohlfart.basic.elements.IsRenderable;
import net.wohlfart.basic.elements.IsUpdatable;
import net.wohlfart.gl.elements.hud.Layer.Widget;

public interface Hud extends IsRenderable, IsUpdatable {

    void setup();

    @Override
    void destroy();

    void add(Widget label);

    @Override
    void update(float tpf);

}
