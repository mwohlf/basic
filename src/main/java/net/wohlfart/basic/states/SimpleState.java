package net.wohlfart.basic.states;

import net.wohlfart.gl.elements.hud.Hud;
import net.wohlfart.gl.elements.hud.NullHud;
import net.wohlfart.gl.elements.hud.widgets.Label;
import net.wohlfart.gl.elements.hud.widgets.MousePositionLabel;
import net.wohlfart.gl.elements.hud.widgets.Statistics;
import net.wohlfart.gl.elements.skybox.NullSkybox;
import net.wohlfart.gl.elements.skybox.Skybox;
import net.wohlfart.gl.input.InputDispatcher;
import net.wohlfart.gl.renderer.RenderableBucket;
import net.wohlfart.gl.shader.DefaultGraphicContext;
import net.wohlfart.gl.shader.GraphicContextManager;
import net.wohlfart.gl.view.MousePicker;
import net.wohlfart.tools.ControllerFrame;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;


/*
 * state implementation that consists of (in the order of rendering):
 * - skyboxImpl
 * - elementBucket
 * - hudImpl
 *
 */
final class SimpleState extends AbstractGraphicState implements InitializingBean {
    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleState.class);

    private GraphicContextManager.IGraphicContext defaultGraphicContext;
    private GraphicContextManager.IGraphicContext wireframeGraphicContext;
    private GraphicContextManager.IGraphicContext hudGraphicContext;

    private final RenderableBucket elemBucket = new RenderableBucket();

    private Statistics statistics;
    private MousePositionLabel mousePositionLabel;
    private MousePicker mousePicker;

    private final boolean elementsOn = true;
    private final boolean controlFrameOn = true;

    private Skybox skybox = new NullSkybox();
    private Hud hud = new NullHud();


    public void setSkybox(Skybox skybox) {
        this.skybox = skybox;
    }

    public void setHud(Hud hud) {
        this.hud = hud;
    }

    public void setWireframeGraphicContext(DefaultGraphicContext wireframeGraphicContext) {
        this.wireframeGraphicContext = wireframeGraphicContext;
    }

    public void setDefaultGraphicContext(DefaultGraphicContext defaultGraphicContext) {
        this.defaultGraphicContext = defaultGraphicContext;
    }

    public void setHudGraphicContext(DefaultGraphicContext hudGraphicContext) {
        this.hudGraphicContext = hudGraphicContext;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        LOGGER.debug("<afterPropertiesSet>");
        Assert.notNull(skybox, "skybox missing, you probably forgot to inject skybox in the configs");
        Assert.notNull(hud, "hud missing, you probably forgot to inject hud in the configs");
        Assert.notNull(wireframeGraphicContext);
        Assert.notNull(defaultGraphicContext);
        Assert.notNull(hudGraphicContext);
    }


    /** {@inheritDoc} */
    @Override
    public void setup() {
        super.setup();
        statistics = new Statistics(0, -40);
        mousePositionLabel = new MousePositionLabel(0, -20);
        mousePicker = new MousePicker(elemBucket, getScreenWidth(), getScreenHeight());

        defaultGraphicContext.setup();
        wireframeGraphicContext.setup();
        hudGraphicContext.setup();

        // event bus registration
        InputDispatcher inputDispatcher = getInputDispatcher();
        inputDispatcher.register(mousePositionLabel);
        inputDispatcher.register(mousePicker);

        skybox.setCamera(getCamera());
        skybox.setGraphicContext(defaultGraphicContext);


        if (elementsOn) {
            elemBucket.init(wireframeGraphicContext, getCamera());
            elemBucket.add(SceneCreator.createCircledTarget());
            elemBucket.add(SceneCreator.createRandomLocatedSpheres());
            elemBucket.add(SceneCreator.createRandomElements());
            elemBucket.add(SceneCreator.createOriginAxis());
            elemBucket.add(SceneCreator.createDebugElements());
        }



        if (controlFrameOn) {
            ControllerFrame frame = new ControllerFrame();
            frame.init();
            elemBucket.add(frame.getCube());
        }

        hud.setGraphicContext(hudGraphicContext);
        hud.add(statistics);
        hud.add(mousePositionLabel);
        hud.add(new Label(0, 0, "hello world at (0,0)"));

    }



    /** {@inheritDoc} */
    @Override
    public void update(float tpf) {
        LOGGER.debug("update called with tpf/fps {}/{}", tpf, 1f / tpf);
        statistics.update(tpf);
    }

    /** {@inheritDoc} */
    @Override
    public void render() {
        skybox.render();

        if (elementsOn) {
            elemBucket.render();
        }

        hud.render();
    }

    /** {@inheritDoc} */
    @Override
    public void destroy() {
        defaultGraphicContext.dispose();
        wireframeGraphicContext.dispose();
        hudGraphicContext.dispose();
        super.destroy();
    }

}
