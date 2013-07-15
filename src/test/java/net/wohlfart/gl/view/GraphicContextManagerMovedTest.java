package net.wohlfart.gl.view;

import static org.junit.Assert.assertEquals;
import net.wohlfart.basic.Settings;
import net.wohlfart.basic.container.DefaultRenderSet;
import net.wohlfart.basic.elements.IsUpdatable;
import net.wohlfart.gl.shader.GraphicContextManager;

import org.junit.Before;
import org.junit.Test;
import org.lwjgl.util.vector.Matrix4f;

public class GraphicContextManagerMovedTest {

    GraphicContextManager contxt;
    Settings settings;
    PickEvent pickEvent;

    @SuppressWarnings("serial")
    DefaultRenderSet<IsUpdatable> matrices = new DefaultRenderSet<IsUpdatable>() {

        @Override
        public Matrix4f getModelViewMatrix() {
            final Matrix4f m = new Matrix4f();
            return m;
        }

        @Override
        public Matrix4f getProjectionMatrix() {
            final Matrix4f m = contxt.getPerspectiveProjMatrix();
            return m;
        }

    };

    @Before
    public void setup() {
        settings = createSettings();
        contxt = GraphicContextManager.INSTANCE;
        contxt.setSettings(settings);
        pickEvent = new PickEvent(settings.getWidth(), settings.getHeight(), settings.getWidth() / 2f, settings.getHeight() / 2f);
    }

    Settings createSettings() {
        settings = new Settings();
        settings.setWidth(1000);
        settings.setHeight(700);
        settings.setFullscreen(false);
        settings.setFieldOfView(45);
        settings.setNearPlane(0.1f);
        settings.setFarPlane(100);
        return settings;
    }

    @Test
    public void testCenter() {
        PickingRay ray = pickEvent.createPickingRay(matrices);

        assertEquals(0.0, ray.getStart().x, 0.01);
        assertEquals(0.0, ray.getStart().y, 0.01);
        assertEquals(-0.1, ray.getStart().z, 0.01);

        assertEquals(0.0, ray.getEnd().x, 0.01);
        assertEquals(0.0, ray.getEnd().y, 0.01);
        assertEquals(-100, ray.getEnd().z, 0.01);
    }

}