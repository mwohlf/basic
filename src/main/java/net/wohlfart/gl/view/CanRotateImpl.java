package net.wohlfart.gl.view;

import net.wohlfart.tools.SimpleMath;

import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;

/**
 * <p>CanRotateImpl class.</p>
 */
@SuppressWarnings("serial")
public class CanRotateImpl extends Quaternion implements CanRotate {
    final Quaternion rot = new Quaternion();

    /** {@inheritDoc} */
    @Override
    public Quaternion getRotation() {
        return this;
    }


    @Override
    public void setRotation(Quaternion quaternion) {
        set(quaternion);
    }


    /** {@inheritDoc} */
    @Override
    public void rotate(float deltaAngle, Vector3f axis) {
        axis.normalise();
        final double n = Math.sqrt(axis.x * axis.x + axis.y * axis.y + axis.z * axis.z);
        final float sin = (float) (Math.sin(0.5 * deltaAngle) / n);
        rot.x = axis.x * sin;
        rot.y = axis.y * sin;
        rot.z = axis.z * sin;
        rot.w = (float) Math.cos(0.5 * deltaAngle);
        Quaternion.mul(rot, this, rot);
        SimpleMath.normalizeLocal(rot);
        set(rot);
    }

    /** the (1,0,0) vector / X axis */
    @Override
    public Vector3f getRght(final Vector3f result) {
        result.x = 1f - 2f * (y * y + z * z);
        result.y = 2f * (x * y - w * z);
        result.z = 2f * (x * z + w * y);
        return result.normalise(new Vector3f());
    }

    /** the (0,1,0) vector / Y axis */
    @Override
    public Vector3f getUp(final Vector3f result) {
        result.x = 2f * (x * y + w * z);
        result.y = 1f - 2f * (z * z + x * x);
        result.z = 2f * (y * z - w * x);
        return result.normalise(new Vector3f());
    }

    /** the (0,0,1) vector / Z axis */
    @Override
    public Vector3f getDir(final Vector3f result) {
        result.x = 2f * (x * z - w * y);
        result.y = 2f * (y * z + w * x);
        result.z = 1f - 2f * (x * x + y * y);
        return result.normalise(new Vector3f());
    }

}