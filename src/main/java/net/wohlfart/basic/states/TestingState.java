package net.wohlfart.basic.states;

import net.wohlfart.basic.container.DefaultRenderBatch;
import net.wohlfart.basic.container.GlowRenderBatch;
import net.wohlfart.gl.elements.hud.Hud;
import net.wohlfart.gl.elements.hud.HudImpl;
import net.wohlfart.gl.elements.hud.widgets.SimpleLabel;
import net.wohlfart.gl.shader.VertexLight;
import net.wohlfart.gl.spatial.CelestialBody;
import net.wohlfart.gl.texture.CelestialType;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

public class TestingState extends AbstractGraphicState implements InitializingBean {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestingState.class);

    private final Hud hud = new HudImpl();

    private final GlowRenderBatch glowRenderBatch = new GlowRenderBatch();

    private final DefaultRenderBatch<CelestialBody> planetSet = new DefaultRenderBatch<>();



    @Override
    public void afterPropertiesSet() throws Exception {
        LOGGER.debug("<afterPropertiesSet>");
        Assert.notNull(hud);
    }

    @Override
    public void setup() {
        super.setup();


        VertexLight lightA = new VertexLight(0.001f, new Vector4f(0.9f, 0.9f, 0.9f, 1.0f), new Vector3f( 0, 0, -3));
        VertexLight lightB = new VertexLight(0.001f, new Vector4f(0.9f, 0.9f, 0.9f, 1.0f), new Vector3f( 0, 0, 3));

        CelestialBody sun1 = new CelestialBody(1L, CelestialType.SUN, 1f);
        sun1.setLod(9);
        sun1.setPosition(lightA.getPosition());
        glowRenderBatch.add(sun1);
        //CelestialBody sun2 = new CelestialBody(1L, CelestialType.SUN, 1f);
        //sun2.setPosition(lightB.getPosition());
        //renderSet.add(sun2);
        glowRenderBatch.setup();

        //planetSet.add(sun1);
        //planetSet.add(sun2);
        //CelestialBody sun3 = new CelestialBody(1L, CelestialType.SUN, 1f);
        //sun3.setPosition(new Vector3f( 0, 0, 0));
        //planetSet.add(sun3);
        planetSet.setup();

        hud.setup();
        hud.add(new SimpleLabel(200, 200, "test state"));

    }

    @Override
    public void update(float tpf) {
        planetSet.update(tpf);
        glowRenderBatch.update(tpf);
        hud.update(tpf);
    }

    @Override
    public void render() {
        GL11.glClearColor(0.7f, 0.7f, 0.9f, 0f);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_STENCIL_BUFFER_BIT);

        GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_BLEND);
        glowRenderBatch.render();


        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        planetSet.render();

        hud.render();
    }

    @Override
    public void destroy() {
        glowRenderBatch.destroy();
        planetSet.destroy();
        hud.destroy();
        super.destroy();
    }

}

