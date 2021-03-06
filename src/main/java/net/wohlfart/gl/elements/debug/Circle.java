package net.wohlfart.gl.elements.debug;

import java.util.ArrayList;
import java.util.List;

import net.wohlfart.basic.elements.IsRenderable;
import net.wohlfart.gl.elements.AbstractRenderable;
import net.wohlfart.tools.SimpleMath;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

/**
 * A simple Circle class.
 */
public class Circle extends AbstractRenderable { // REVIEWED

    private int pieces = 15; // LOD
    private float radius = 1;


    public Circle() {
    }

    public Circle(float radius) {
        this.radius = radius;
    }

    public Circle(float radius, int pieces) {
        this.radius = radius;
        this.pieces = pieces;
    }

    @Override
    protected IsRenderable setupMesh() {
        final WireframeMeshBuilder builder = new WireframeMeshBuilder();
        builder.setVertices(createVertices());
        builder.setIndices(createIndices());
        builder.setLinePrimitive(GL11.GL_LINES);
        builder.setColor(color);
        builder.setInitRotation(initialRotation);
        builder.setInitTranslation(initialTranslation);
        return builder.build();
    }

    protected List<Vector3f> createVertices() {
        final List<Vector3f> result = new ArrayList<Vector3f>(pieces);
        for (int i = 0; i < pieces; i++) {
            final float rad = SimpleMath.TWO_PI * i / pieces;
            final float x = SimpleMath.sin(rad) * radius;
            final float y = SimpleMath.cos(rad) * radius;
            result.add(i, new Vector3f(x, y, 0));
        }
        return result;
    }


    protected List<Integer> createIndices() {
        final List<Integer> result = new ArrayList<Integer>(pieces * 2);
        for (int i = 0; i < pieces; i++) {
            result.add(i);
            result.add((i + 1) % pieces);
        }
        return result;
    }

}
