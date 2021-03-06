package net.wohlfart.basic.hud;

import net.wohlfart.basic.elements.IsUpdatable;
import net.wohlfart.basic.hud.Layer.Widget;

/**
 * a container for renderable objects
 */
public interface Hud extends IsUpdatable {

    void setup();

    @Override
    void destroy();

    void add(Widget label);

    @Override
    void update(float tpf);

}
