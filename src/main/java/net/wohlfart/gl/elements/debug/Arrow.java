package net.wohlfart.gl.elements.debug;

import java.util.Arrays;
import java.util.List;

import net.wohlfart.gl.elements.AbstractRenderable;
import net.wohlfart.gl.renderer.IsRenderable;
import net.wohlfart.tools.SimpleMath;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

/**
 * <p>
 * A simple Arrow that points in a direction.
 * </p>
 */
public class Arrow extends AbstractRenderable {

    // vertices[0] is the direction of the arrow
    private final Vector3f[] vertices = new Vector3f[] {// @formatter:off
            new Vector3f(+0.00f, +0.00f, +1.00f), // tip is in z direction <-- end
            new Vector3f(+0.00f, +0.00f, +0.00f), // base <-- start
            new Vector3f(+0.02f, +0.02f, +0.90f), // tip right
            new Vector3f(-0.02f, +0.02f, +0.90f), // tip left
            new Vector3f(-0.02f, -0.02f, +0.90f), // tip top
            new Vector3f(+0.02f, -0.02f, +0.90f), // tip bottom  @formatter:on
    };

    private final List<Integer> indices = Arrays.asList(new Integer[] { 1, 0, // shaft
            2, 0, // tip1
            3, 0, // tip2
            4, 0, // tip3
            5, 0, // tip4
    });

    /**
     * <p>
     * This creates an arrow that points from start to end.
     * </p>
     * 
     * @param start
     *            a {@link org.lwjgl.util.vector.Vector3f} object.
     * @param end
     *            a {@link org.lwjgl.util.vector.Vector3f} object.
     * @return a {@link net.wohlfart.gl.elements.debug.Arrow} object.
     */
    public static Arrow createLink(Vector3f start, Vector3f end) {
        final Arrow result = new Arrow(new Vector3f(end.x - start.x, end.y - start.y, end.z - start.z));
        result.withTranslation(start);
        return result;
    }

    /**
     * <p>
     * Default Constructor for Arrow, points from origin to (0,0,1).
     * </p>
     */
    public Arrow() {
        // nothing to do
    }

    /**
     * <p>
     * Constructor for Arrow, points from origin to tip.
     * </p>
     * 
     * @param tip
     *            a {@link org.lwjgl.util.vector.Vector3f} object which defines the tip of the arrow.
     */
    public Arrow(Vector3f tip) {
        final float length = tip.length();
        for (final Vector3f vec : vertices) {
            vec.z *= length;
        }
        SimpleMath.createQuaternion(vertices[0], tip, rotation);
    }

    /** {@inheritDoc} */
    @Override
    protected IsRenderable setupMesh() {
        final WireframeMeshBuilder builder = new WireframeMeshBuilder();
        builder.setVertices(Arrays.<Vector3f> asList(vertices));
        builder.setIndices(indices);
        builder.setLinePrimitive(GL11.GL_LINES);
        builder.setColor(color);
        builder.setRotation(rotation);
        builder.setTranslation(translation);
        return builder.build();
    }

}
