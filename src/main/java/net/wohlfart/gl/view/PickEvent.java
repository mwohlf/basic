package net.wohlfart.gl.view;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

public class PickEvent {

    private final static ThreadLocal<Matrix4f> transformMatrix = new ThreadLocal<>();
    static {
        transformMatrix.set(new Matrix4f());
    }

    private final float x;
    private final float y;

    private final float width;
    private final float height;

    public PickEvent(float width, float height, float x, float y) {
        this.width = width;
        this.height = height;
        this.x = x;
        this.y = y;
    }

    // FIXME: we need to put this calculation down to the
    // render set itself where the matrixes are available
    // to do this post the complete picking ray as event and call a factory inside the render set to create the
    // actual start /endpoint...
    // this way we can get rid of the getModelViewMatrix/GetProjMatrix in the renderbatch
    public PickingRay createPickingRay(Matrix4f projectionMatrix, Matrix4f modelViewMatrix) {

        Matrix4f matrx = transformMatrix.get();

        Matrix4f.mul(projectionMatrix, modelViewMatrix, matrx);
        matrx = Matrix4f.invert(matrx, matrx);

        final Vector4f cameraSpaceNear = new Vector4f(x / width * 2f - 1f, y / height * 2f - 1f, -1.0f, 1.0f);
        final Vector4f cameraSpaceFar = new Vector4f(x / width * 2f - 1f, y / height * 2f - 1f, 1.0f, 1.0f);

        final Vector4f worldSpaceNear = new Vector4f();
        Matrix4f.transform(matrx, cameraSpaceNear, worldSpaceNear);

        final Vector4f worldSpaceFar = new Vector4f();
        Matrix4f.transform(matrx, cameraSpaceFar, worldSpaceFar);

        // @formatter:off
        final Vector3f start = new Vector3f(
                worldSpaceNear.x / worldSpaceNear.w,
                worldSpaceNear.y / worldSpaceNear.w,
                worldSpaceNear.z / worldSpaceNear.w);
        final Vector3f end = new Vector3f(
                worldSpaceFar.x / worldSpaceFar.w,
                worldSpaceFar.y / worldSpaceFar.w,
                worldSpaceFar.z / worldSpaceFar.w);
        // @formatter:on

        return new PickingRay(start, end);
    }
}
