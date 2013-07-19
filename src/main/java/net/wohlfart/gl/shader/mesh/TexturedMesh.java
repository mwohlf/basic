package net.wohlfart.gl.shader.mesh;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;

import net.wohlfart.basic.elements.IsRenderable;
import net.wohlfart.gl.shader.ShaderAttributeHandle;
import net.wohlfart.gl.shader.Vertex;
import net.wohlfart.tools.PNGDecoder;
import net.wohlfart.tools.PNGDecoder.Format;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Vector3f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TexturedMesh class.
 */
public class TexturedMesh implements IsRenderable {

    private final int vaoHandle;
    private final int texHandle;
    private final int indicesCount;
    private final int indexOffset;
    private final int indexElemSize;
    private final int indicesType;


    private TexturedMesh(int vaoHandle, int indicesType, int indexElemSize, int indicesCount, int indexOffset, int texHandle) {
        this.vaoHandle = vaoHandle;
        this.texHandle = texHandle;
        this.indicesType = indicesType;
        this.indexElemSize = indexElemSize;
        this.indicesCount = indicesCount;
        this.indexOffset = indexOffset;
    }

    @Override
    public void render() {
        GL30.glBindVertexArray(vaoHandle);
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texHandle);
        GL11.glDrawElements(indicesType, indicesCount, indexElemSize, indexOffset);
        GL30.glBindVertexArray(0);
    }

    @Override
    public void destroy() {
        GL30.glBindVertexArray(0);
        GL30.glDeleteVertexArrays(vaoHandle);
    }




    public static class Builder extends AbstractMeshBuilder {
        protected static final Logger LOGGER = LoggerFactory.getLogger(Builder.class);

        private float size = 1f;                              // length of one side of the texture
        private int textureWrap = GL11.GL_REPEAT;


        @Override
        public IsRenderable build() {
            final int vaoHandle = GL30.glGenVertexArrays();
            GL30.glBindVertexArray(vaoHandle);

            int texHandle = resolveTexHandle();

            createVboHandle(createStream());

            byte[] indices = new byte[] { 0, 1, 2, 2, 3, 0 };
            createIdxBufferHandle(indices);

            final int[] offset = {0};
            final int stride = ShaderAttributeHandle.POSITION.getByteCount()
                    + ShaderAttributeHandle.COLOR.getByteCount()
                    + ShaderAttributeHandle.TEXTURE_COORD.getByteCount()
                    ;
            ShaderAttributeHandle.POSITION.enable(stride, offset);
            ShaderAttributeHandle.COLOR.enable(stride, offset);
            ShaderAttributeHandle.TEXTURE_COORD.enable(stride, offset);
            ShaderAttributeHandle.NORMAL.disable();

            // done with the VAO
            GL30.glBindVertexArray(0);

            return new TexturedMesh(vaoHandle, GL11.GL_TRIANGLES, GL11.GL_UNSIGNED_BYTE, indices.length, 0, texHandle);
        }

        protected float[] createStream() {

            final Vector3f[] vectors = new Vector3f[] { // @formatter:off
                    new Vector3f(-(size/2f), +(size/2f), 0),
                    new Vector3f(-(size/2f), -(size/2f), 0),
                    new Vector3f(+(size/2f), -(size/2f), 0),
                    new Vector3f(+(size/2f), +(size/2f), 0),
            };

            applyRotationAndTranslation(Arrays.asList(vectors));

            final Vertex[] vertices = new Vertex[] {
                    new Vertex() {{
                        setXYZ(vectors[0].x, vectors[0].y, vectors[0].z);
                        setRGB(1, 0, 0);
                        setST(0, 0);
                    }},
                    new Vertex() {{
                        setXYZ(vectors[1].x, vectors[1].y, vectors[1].z);
                        setRGB(0, 1, 0);
                        setST(0, 1);
                    }},
                    new Vertex() {{
                        setXYZ(vectors[2].x, vectors[2].y, vectors[2].z);
                        setRGB(0, 0, 1);
                        setST(1, 1);
                    }},
                    new Vertex() {{
                        setXYZ(vectors[3].x, vectors[3].y, vectors[3].z);
                        setRGB(1, 1, 1);
                        setST(1, 0);
                    }}
            }; // @formatter:on


            // Put each 'Vertex' in one FloatBuffer the order depends on the shaders positions!
            float[] stream = new float[vertices.length * (3 + 4 + 2)];
            int i = 0;
            for (Vertex vertex : vertices) {
                stream[i++] = vertex.getXYZ()[0];
                stream[i++] = vertex.getXYZ()[1];
                stream[i++] = vertex.getXYZ()[2];
                stream[i++] = vertex.getRGBA()[0];
                stream[i++] = vertex.getRGBA()[1];
                stream[i++] = vertex.getRGBA()[2];
                stream[i++] = vertex.getRGBA()[3];
                stream[i++] = vertex.getST()[0];
                stream[i++] = vertex.getST()[1];
            }
            return stream;
        }

        private int loadPNGTexture(String filename, int textureUnit) {
            int texHandle = -1;

            // InputStream inputStream = new FileInputStream(filename);
            try (InputStream inputStream = ClassLoader.class.getResourceAsStream(filename)) {

                // Link the PNG decoder to this stream
                final PNGDecoder decoder = new PNGDecoder(inputStream);
                // Get the width and height of the texture
                final int tWidth = decoder.getWidth();
                final int tHeight = decoder.getHeight();
                // Decode the PNG file in a ByteBuffer
                final ByteBuffer buffer = ByteBuffer.allocateDirect(4 * decoder.getWidth() * decoder.getHeight());
                decoder.decode(buffer, decoder.getWidth() * 4, Format.RGBA);
                buffer.flip();

                // Create a new texture object in memory and bind it
                texHandle = GL11.glGenTextures();
                GL13.glActiveTexture(textureUnit);
                GL11.glBindTexture(GL11.GL_TEXTURE_2D, texHandle);
                // All RGB bytes are aligned to each other and each component is 1 byte
                GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
                // Upload the texture data and generate mip maps (for scaling)
                GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, tWidth, tHeight, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
                GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
                // Setup the ST coordinate system
                GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, textureWrap);
                GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, textureWrap);
                // Setup what to do when the texture has to be scaled
                GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
                GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR);

            } catch (final FileNotFoundException ex) {
                LOGGER.error("can't load texture image", ex);
            } catch (final IOException ex) {
                LOGGER.error("can't load texture image", ex);
            }
            return texHandle;
        }

        public void setSize(float size) {
            this.size = size;
        }

        public void setTextureWrap(int textureWrap) {
            this.textureWrap = textureWrap;
        }

    }

}
