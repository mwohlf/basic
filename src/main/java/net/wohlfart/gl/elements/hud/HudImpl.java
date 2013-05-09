package net.wohlfart.gl.elements.hud;

import net.wohlfart.gl.elements.hud.widgets.AbstractTextComponent;
import net.wohlfart.gl.shader.GraphicContextManager;
import net.wohlfart.gl.shader.GraphicContextManager.IGraphicContext;
import net.wohlfart.gl.shader.ShaderUniformHandle;
import net.wohlfart.tools.SimpleMath;

import org.lwjgl.opengl.GL11;


/**
 * this class is responsible for switching context and rendering the hud components
 *
 *
 *
 */
public class HudImpl implements Hud {

    private final GraphicContextManager cxtManagert = GraphicContextManager.INSTANCE;

    private IGraphicContext hudContext;

    private LayerImpl layer;


    @Override
    public void setGraphicContext(IGraphicContext hudContext) {
        this.hudContext = hudContext;
        this.layer = new LayerImpl();  // FIXME: this looks strange
    }

    /**
     * <p>add.</p>
     *
     * @param label a {@link net.wohlfart.gl.elements.hud.widgets.AbstractTextComponent} object.
     */
    @Override
    public void add(AbstractTextComponent label) {
        layer.add(label);
    }

    /** {@inheritDoc} */
    @Override
    public void render() {
        cxtManagert.setCurrentGraphicContext(hudContext);
        ShaderUniformHandle.MODEL_TO_WORLD.set(SimpleMath.UNION_MATRIX);
        ShaderUniformHandle.WORLD_TO_CAM.set(SimpleMath.UNION_MATRIX);
        ShaderUniformHandle.CAM_TO_CLIP.set(cxtManagert.getPerspectiveProjMatrix());
        //ShaderUniformHandle.CAM_TO_CLIP.set(contextManagert.getOrthographicProjMatrix());
        //ShaderUniformHandle.CAM_TO_CLIP.set(SimpleMath.UNION_MATRIX);
        GL11.glEnable(GL11.GL_BLEND);
        layer.render();
    }


    /** {@inheritDoc} */
    @Override
    public void destroy() {
        layer.destroy();
    }

}