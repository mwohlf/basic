package net.wohlfart.gl.shader.mesh;

import net.wohlfart.gl.renderer.IsRenderable;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

/**
 * <p>
 * CharacterMesh class.
 * </p>
 */
public class CharacterMesh implements IsRenderable {

    private final int vaoHandle;
    private final int vboVerticesHandle;
    private final int vboIndicesHandle;
    private final int indicesCount;
    private final int indexOffset;
    private final int indexElemSize;
    private final int indicesType;

    private final int positionAttrib;
    private final int textureAttrib;

    private final int textureId;

    // package private, created by the builder
    /**
     * <p>
     * Constructor for CharacterMesh.
     * </p>
     * 
     * @param vaoHandle
     *            a int.
     * @param vboVerticesHandle
     *            a int.
     * @param vboIndicesHandle
     *            a int.
     * @param indicesType
     *            a int.
     * @param indexElemSize
     *            a int.
     * @param indicesCount
     *            a int.
     * @param indexOffset
     *            a int.
     * @param positionAttrib
     *            a int.
     * @param textureAttrib
     *            a int.
     * @param textureId
     *            a int.
     */
    public CharacterMesh(int vaoHandle, int vboVerticesHandle,
    // index
            int vboIndicesHandle, int indicesType, int indexElemSize, int indicesCount, int indexOffset,
            // attrib pos
            int positionAttrib, int textureAttrib, int textureId) {

        this.vaoHandle = vaoHandle;
        this.vboVerticesHandle = vboVerticesHandle;
        // index
        this.vboIndicesHandle = vboIndicesHandle;
        this.indicesType = indicesType;
        this.indexElemSize = indexElemSize;
        this.indicesCount = indicesCount;
        this.indexOffset = indexOffset;
        // attr pos
        this.positionAttrib = positionAttrib;
        this.textureAttrib = textureAttrib;

        this.textureId = textureId;
    }

    /** {@inheritDoc} */
    @Override
    public void render() {
        // Bind the texture
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId);

        GL30.glBindVertexArray(vaoHandle);
        GL20.glEnableVertexAttribArray(positionAttrib);
        GL20.glEnableVertexAttribArray(textureAttrib);
        // Bind to the index VBO that has all the information about the order of the vertices
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboIndicesHandle);
        // Draw the vertices
        GL11.glDrawElements(indicesType, indicesCount, indexElemSize, indexOffset);
        // Put everything back to default (deselect)
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
        GL20.glDisableVertexAttribArray(positionAttrib);
        GL20.glDisableVertexAttribArray(textureAttrib);
        GL30.glBindVertexArray(0);
    }

    /** {@inheritDoc} */
    @Override
    public void destroy() {
        // Disable the VBO index from the VAO attributes list
        GL20.glDisableVertexAttribArray(0);
        // Delete the index VBO
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        GL15.glDeleteBuffers(vboVerticesHandle);
        // Delete the vertex VBO
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
        GL15.glDeleteBuffers(vboIndicesHandle);
        // Delete the VAO
        GL30.glBindVertexArray(0);
        GL30.glDeleteVertexArrays(vaoHandle);
    }

}
