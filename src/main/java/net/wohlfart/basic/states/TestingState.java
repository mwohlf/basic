package net.wohlfart.basic.states;

import net.wohlfart.basic.container.DefaultRenderSet;
import net.wohlfart.gl.elements.hud.Hud;
import net.wohlfart.gl.elements.hud.HudImpl;
import net.wohlfart.gl.elements.hud.widgets.Label;
import net.wohlfart.gl.shader.DefaultGraphicContext;
import net.wohlfart.gl.shader.ShaderRegistry;
import net.wohlfart.gl.shader.VertexLight;
import net.wohlfart.gl.spatial.ParticleEmitter;

import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

public class TestingState extends AbstractGraphicState implements InitializingBean {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestingState.class);

    private final Hud hud = new HudImpl();

    private final DefaultRenderSet<ParticleEmitter> emitterSet = new DefaultRenderSet<>();


    @Override
    public void afterPropertiesSet() throws Exception {
        LOGGER.debug("<afterPropertiesSet>");
        Assert.notNull(hud);
    }

    @Override
    public void setup() {
        super.setup();
        hud.setup();
        hud.add(new Label(0, 0, "hello world at (0,0)"));

        ParticleEmitter particleEmitter = new ParticleEmitter();
        particleEmitter.setPosition(new Vector3f(20,0,0));
        emitterSet.setGraphicContext(new DefaultGraphicContext(ShaderRegistry.AMBIENT_SHADER));
        emitterSet.add(particleEmitter);

        VertexLight light3 = new VertexLight(0.001f, new Vector4f(0.9f, 0.9f, 0.9f, 1.0f), new Vector3f( 7, 0, -10));
        VertexLight light4 = new VertexLight(0.001f, new Vector4f(0.9f, 0.9f, 0.9f, 1.0f), new Vector3f( 7, 0, 10));
        emitterSet.add(light3);
        emitterSet.add(light4);
        emitterSet.setup();
    }

    @Override
    public void update(float tpf) {
        hud.update(tpf);
        emitterSet.update(tpf);
    }

    @Override
    public void render() {
        hud.render();
        emitterSet.render();
    }

    @Override
    public void destroy() {
        hud.destroy();
        emitterSet.destroy();
        super.destroy();
    }

}
