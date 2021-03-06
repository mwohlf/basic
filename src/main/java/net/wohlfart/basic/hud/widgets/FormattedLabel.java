package net.wohlfart.basic.hud.widgets;

import java.text.MessageFormat;

import net.wohlfart.basic.elements.IsRenderable;
import net.wohlfart.basic.hud.txt.AbstractCharComponent;


public class FormattedLabel extends AbstractCharComponent {

    private final int x;
    private final int y;
    private final MessageFormat format;
    private IsRenderable characters;
    private Object[] arguments;


    public FormattedLabel(int x, int y, String pattern) {
        this.x = x;
        this.y = y;
        this.format = new MessageFormat(pattern);
    }

    public void setValue(Object[] arguments) {
        this.arguments = arguments;
        disposeCharacters();
    }

    public void setValue(float argument) {
        this.arguments = new Object[] { argument };
        disposeCharacters();
    }

    @Override
    public void render() {
        if (characters == null) {
            final String string = format.format(arguments, new StringBuffer(), null).toString();
            characters = getLayer().createCharElements(x, y, string);
        }
        characters.render();
    }

    @Override
    public void update(float timeInSec) {
    }

    private void disposeCharacters() {
        if (characters == null) {
            return;
        }
        characters.destroy();
        characters = null;
    }

    @Override
    public void destroy() {
        disposeCharacters();
    }

}
