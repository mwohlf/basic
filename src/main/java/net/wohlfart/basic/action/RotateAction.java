package net.wohlfart.basic.action;

import net.wohlfart.tools.SimpleMath;

import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;

public final class RotateAction implements SpatialActor.Action {

    private float rotTime; // the time for one single rotation
    private Vector3f axis;

    private RotateAction() {
        // use the factory method
    }

    public static RotateAction create() {
        final RotateAction result = new RotateAction();
        result.rotTime = 10f;
        result.axis = new Vector3f(0, 1, 0);
        return result;
    }

    public static RotateAction create(float rotTime, Vector3f axis) {
        final RotateAction result = new RotateAction();
        result.rotTime = rotTime;
        result.axis = axis;
        return result;
    }

    @Override
    public void perform(SpatialActor spatialActor, float time) {
        final Quaternion q = spatialActor.getRotation();
        SimpleMath.rotate(q, time * SimpleMath.TWO_PI / rotTime, axis);
        spatialActor.setRotation(q);
    }

}
