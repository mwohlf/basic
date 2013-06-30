package net.wohlfart.gl.elements.hud.widgets;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import net.wohlfart.basic.elements.IsRenderable;
import net.wohlfart.gl.shader.GraphicContextManager;
import net.wohlfart.gl.shader.ShaderAttributeHandle;
import net.wohlfart.gl.shader.Vertex;
import net.wohlfart.gl.shader.mesh.CharacterMesh;
import net.wohlfart.tools.SimpleMath;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * this creates a mesh for a single character
 */
class CharMeshBuilder {

    protected static final Logger LOGGER = LoggerFactory.getLogger(CharMeshBuilder.class);

    private final GraphicContextManager cxtManager = GraphicContextManager.INSTANCE;

    private CharAtlas atlas;
    private CharInfo info;

    private float screenX;
    private float screenY;

    public IsRenderable build() {
        final float atlasWidth = atlas.getImage().getWidth();
        final float atlasHeight = atlas.getImage().getHeight();
        final float width = cxtManager.getScreenWidth();
        final float height = cxtManager.getScreenHeight();

        // this is the z range we need to have a 1:1 dot match from the mesh to the screen
        final float z = -0.5f * SimpleMath.coTan(SimpleMath.deg2rad(cxtManager.getFieldOfView() / 2f));

        final float x1 = screenX / atlasWidth - 0.5f * (width / height);
        final float y1 = screenY / atlasHeight + 0.5f;
        final float x2 = x1 + info.getWidth() / atlasWidth;
        final float y2 = y1 - info.getHeight() / atlasHeight;

        final float s1 = info.getX() / atlasWidth;
        final float t1 = info.getY() / atlasHeight;
        final float s2 = (info.getX() + info.getWidth()) / atlasWidth;
        final float t2 = (info.getY() + info.getHeight()) / atlasHeight;

        // We'll define our quad using 4 vertices of the custom 'Vertex' class
        final Vertex v0 = new Vertex();
        v0.setXYZ(x1, y1, z);
        v0.setST(s1, t1);

        final Vertex v1 = new Vertex();
        v1.setXYZ(x1, y2, z);
        v1.setST(s1, t2);

        final Vertex v2 = new Vertex();
        v2.setXYZ(x2, y2, z);
        v2.setST(s2, t2);

        final Vertex v3 = new Vertex();
        v3.setXYZ(x2, y1, z);
        v3.setST(s2, t1);

        final Vertex[] vertices = new Vertex[] { v0, v1, v2, v3 };
        final FloatBuffer verticesBuffer = BufferUtils.createFloatBuffer(vertices.length * (3 + 4 + 2));
        for (int i = 0; i < vertices.length; i++) {
            verticesBuffer.put(vertices[i].getXYZ());
            //verticesBuffer.put(vertices[i].getRGBA());
            verticesBuffer.put(vertices[i].getST());
        }
        verticesBuffer.flip();

        // OpenGL expects to draw vertices in counter clockwise order by default
        final byte[] indices = { 0, 1, 2, 2, 3, 0 };
        final int indicesCount = indices.length;
        final ByteBuffer indicesBuffer = BufferUtils.createByteBuffer(indicesCount);
        indicesBuffer.put(indices);
        indicesBuffer.flip();

        // Create a new Vertex Array Object in memory and select it (bind)
        final int vaoHandle = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(vaoHandle);

        final int vboVerticesHandle = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboVerticesHandle);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, verticesBuffer, GL15.GL_STATIC_DRAW);

        final int vboIndicesHandle = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboIndicesHandle);
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL15.GL_STATIC_DRAW);

        int offset;
        final int stride = ShaderAttributeHandle.POSITION.getByteCount()
              //  + ShaderAttributeHandle.COLOR.getByteCount()
                + ShaderAttributeHandle.TEXTURE_COORD.getByteCount();

        offset = 0;

        ShaderAttributeHandle.POSITION.enable();
        GL20.glVertexAttribPointer(ShaderAttributeHandle.POSITION.getLocation(),
                Vertex.POSITION_ELEM_COUNT, GL11.GL_FLOAT, false, stride, offset);
        offset += ShaderAttributeHandle.POSITION.getByteCount();

        ShaderAttributeHandle.TEXTURE_COORD.enable();
        GL20.glVertexAttribPointer(ShaderAttributeHandle.TEXTURE_COORD.getLocation(),
                Vertex.TEXTURE_ELEM_COUNT, GL11.GL_FLOAT, false, stride, offset);
        offset += ShaderAttributeHandle.TEXTURE_COORD.getByteCount();

        ShaderAttributeHandle.NORMAL.disable();

        ShaderAttributeHandle.COLOR.disable();

        // done with the VAO
        GL30.glBindVertexArray(0);

        final int texId = atlas.getTextureId();
        return new CharacterMesh(vaoHandle, GL11.GL_TRIANGLES, GL11.GL_UNSIGNED_BYTE, indicesCount, 0, texId);
    }

    /**
     * <p>
     * setCharInfo.
     * </p>
     *
     * @param info
     *            a {@link net.wohlfart.gl.elements.hud.widgets.CharInfo} object.
     */
    public void setCharInfo(CharInfo info) {
        this.info = info;
    }

    /**
     * <p>
     * setCharAtlas.
     * </p>
     *
     * @param atlas
     *            a {@link net.wohlfart.gl.elements.hud.widgets.CharAtlas} object.
     */
    public void setCharAtlas(CharAtlas atlas) {
        this.atlas = atlas;
    }

    /**
     * <p>
     * Setter for the field <code>screenX</code>.
     * </p>
     *
     * @param x
     *            a float.
     */
    public void setScreenX(float x) {
        this.screenX = x;
    }

    /**
     * <p>
     * Setter for the field <code>screenY</code>.
     * </p>
     *
     * @param y
     *            a float.
     */
    public void setScreenY(float y) {
        this.screenY = y;
    }

}
