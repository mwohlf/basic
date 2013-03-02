package net.wohlfart.gl;

import net.wohlfart.gl.elements.debug.Arrow;
import net.wohlfart.gl.input.CommandEvent;
import net.wohlfart.gl.renderer.RenderBucket;
import net.wohlfart.gl.shader.GraphicContextManager;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import com.google.common.eventbus.Subscribe;

public class MousePicker {


    private Matrix4f transformMatrix = new Matrix4f();

    private final RenderBucket elemBucket;

    private final float width;
    private final float height;


    public MousePicker(RenderBucket elemBucket, float width, float height) {
        this.elemBucket = elemBucket;
        this.width = width;
        this.height = height;
    }

    @Subscribe
    public void onMouseClick(CommandEvent.LeftClick clickEvent) {
        float x = clickEvent.getX();
        float y = clickEvent.getY();
        GraphicContextManager ctxManager = GraphicContextManager.INSTANCE;

        PickingRay ray = createPickingRay(x, y,
                ctxManager.projectionMatrix,
                ctxManager.modelViewMatrix
                );
        elemBucket.add(Arrow.createLink(ray.getStart(), ray.getEnd()).lineWidth(1f));
    }



    // see: http://gamedev.stackexchange.com/questions/8974/converting-a-mouse-click-to-a-ray
    public PickingRay createPickingRay(float x, float y, Matrix4f projectionMatrix, Matrix4f modelViewMatrix){

        Matrix4f.mul(projectionMatrix, modelViewMatrix, transformMatrix);
        transformMatrix = Matrix4f.invert(transformMatrix, transformMatrix);


        Vector4f cameraSpaceNear = new Vector4f(x / width * 2f - 1f, y / height * 2f - 1f, -1.0f, 1.0f);
        Vector4f cameraSpaceFar = new Vector4f(x / width * 2f - 1f, y / height * 2f - 1f,  1.0f, 1.0f);

        Vector4f worldSpaceNear = new Vector4f();
        Matrix4f.transform(transformMatrix, cameraSpaceNear, worldSpaceNear);

        Vector4f worldSpaceFar = new Vector4f();
        Matrix4f.transform(transformMatrix, cameraSpaceFar, worldSpaceFar);

        Vector3f start = new Vector3f(worldSpaceNear.x / worldSpaceNear.w,
                                      worldSpaceNear.y / worldSpaceNear.w,
                                      worldSpaceNear.z / worldSpaceNear.w);
        Vector3f end = new Vector3f(worldSpaceFar.x / worldSpaceFar.w,
                                    worldSpaceFar.y / worldSpaceFar.w,
                                    worldSpaceFar.z / worldSpaceFar.w);

        return new PickingRay(start, end);
    }

}
