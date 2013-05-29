package net.wohlfart.gl.input;

import net.wohlfart.tools.ObjectPool;
import net.wohlfart.tools.ObjectPool.PoolableObject;
import net.wohlfart.tools.SimpleMath;

import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;

public class RotateEvent extends Quaternion implements PoolableObject  {

    private static final float ROTATION_SPEED = SimpleMath.TWO_PI;  // one rotation per sec


    private static ObjectPool<RotateEvent> pool = new ObjectPool<RotateEvent>(10) {
        @Override
        protected RotateEvent newObject() {
            return new RotateEvent();
        }
    };

    @Override
    public void reset() {
        // ignored the since data are overridden when borrowed
        pool.returnObject(this);
    }

    public static Object rotateLeft(float time) {
        RotateEvent result = pool.borrowObject();
        Quaternion q = new Quaternion();
        SimpleMath.rotate(q , ROTATION_SPEED/360f, new Vector3f(0,1,0));
        result.set(q);
        return result;
    }

    public static Object rotateRight(float time) {
        RotateEvent result = pool.borrowObject();
        Quaternion q = new Quaternion();
        SimpleMath.rotate(q , ROTATION_SPEED/360f, new Vector3f(0,-1,0));
        result.set(q);
        return result;
    }

    public static Object rotateUp(float time) {
        RotateEvent result = pool.borrowObject();
        Quaternion q = new Quaternion();
        SimpleMath.rotate(q , ROTATION_SPEED/360f, new Vector3f(1,0,0));
        result.set(q);
        return result;
    }

    public static Object rotateDown(float time) {
        RotateEvent result = pool.borrowObject();
        Quaternion q = new Quaternion();
        SimpleMath.rotate(q , ROTATION_SPEED/360f, new Vector3f(-1,0,0));
        result.set(q);
        return result;
    }

    public static Object rotateClockwise(float time) {
        RotateEvent result = pool.borrowObject();
        Quaternion q = new Quaternion();
        SimpleMath.rotate(q , ROTATION_SPEED/360f, new Vector3f(0,0,1));
        result.set(q);
        return result;
    }

    public static Object rotateCounterClockwise(float time) {
        RotateEvent result = pool.borrowObject();
        Quaternion q = new Quaternion();
        SimpleMath.rotate(q , ROTATION_SPEED/360f, new Vector3f(0,0,-1));
        result.set(q);
        return result;
    }

}
