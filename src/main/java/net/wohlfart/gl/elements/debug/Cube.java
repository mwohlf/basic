package net.wohlfart.gl.elements.debug;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.wohlfart.basic.elements.IsRenderable;
import net.wohlfart.gl.elements.AbstractRenderable;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

/**
 * @formatter:off
 * 
 *                5---------4 /| /| / | / | 1 ------- 0 | | | | | | 6 - - -|- 7 | / | / |/ |/ 2-------- 3
 * 
 * @formatter:on <p>
 *               A simple Cube class.
 *               </p>
 */
public class Cube extends AbstractRenderable {

    private float length = 1;

    /**
     * <p>
     * Constructor for Cube.
     * </p>
     */
    public Cube() {
        // nothing to do
    }

    /**
     * <p>
     * Constructor for Cube.
     * </p>
     * 
     * @param length
     *            a float.
     */
    public Cube(float length) {
        this.length = length;
    }

    /** {@inheritDoc} */
    @Override
    protected IsRenderable setupMesh() {
        final WireframeMeshBuilder builder = new WireframeMeshBuilder();
        builder.setVertices(createVertices());
        builder.setIndices(createIndices());
        builder.setLinePrimitive(GL11.GL_LINES);
        builder.setColor(color);
        builder.setRotation(initialRotation);
        builder.setTranslation(initialTranslation);
        return builder.build();
    }

    /**
     * <p>
     * createVertices.
     * </p>
     * 
     * @return a {@link java.util.List} object.
     */
    protected List<Vector3f> createVertices() {
        final float l = length / 2f;
        final List<Vector3f> result = new ArrayList<Vector3f>(8);
        result.add(new Vector3f(+l, +l, +l));
        result.add(new Vector3f(-l, +l, +l));
        result.add(new Vector3f(-l, -l, +l));
        result.add(new Vector3f(+l, -l, +l));
        result.add(new Vector3f(+l, +l, -l));
        result.add(new Vector3f(-l, +l, -l));
        result.add(new Vector3f(-l, -l, -l));
        result.add(new Vector3f(+l, -l, -l));
        return result;
    }

    /**
     * <p>
     * createIndices.
     * </p>
     * 
     * @return an array of {@link java.lang.Integer} objects.
     */
    protected List<Integer> createIndices() {
        final List<Integer> result = new ArrayList<Integer>(6 * 2 * 3);
        result.addAll(createIndices(0, 1, 2, 3));
        result.addAll(createIndices(4, 0, 3, 7));
        result.addAll(createIndices(5, 4, 7, 6));
        result.addAll(createIndices(1, 5, 6, 2));
        result.addAll(createIndices(0, 4, 5, 1));
        result.addAll(createIndices(3, 2, 6, 7));
        return result;
    }

    /**
     * <p>
     * createIndices.
     * </p>
     * 
     * @param i1
     *            a int.
     * @param i2
     *            a int.
     * @param i3
     *            a int.
     * @param i4
     *            a int.
     * @return a {@link java.util.Collection} object.
     */
    protected Collection<Integer> createIndices(int i1, int i2, int i3, int i4) {
        final List<Integer> result = new ArrayList<Integer>();
        result.add(i1);
        result.add(i2);

        result.add(i2);
        result.add(i3);

        result.add(i3);
        result.add(i4);

        result.add(i4);
        result.add(i1);
        return result;
    }

}
