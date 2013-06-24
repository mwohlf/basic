package net.wohlfart.gl.shader.mesh;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import net.wohlfart.gl.renderer.IsRenderable;
import net.wohlfart.gl.shader.ShaderAttributeHandle;
import net.wohlfart.gl.shader.Vertex;
import net.wohlfart.tools.PNGDecoder;
import net.wohlfart.tools.PNGDecoder.Format;
import net.wohlfart.tools.SimpleMath;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * TexturedFragmentMesh class.
 * </p>
 *
 *
 *
 */
public class TexturedMesh implements IsRenderable {

    private final int vaoHandle;
    private final int indicesCount;
    private final int indexOffset;
    private final int indexElemSize;
    private final int indicesType;
    private final int textureId;


    protected TexturedMesh(int vaoHandle, int indicesType, int indexElemSize, int indicesCount, int indexOffset, int textureId) {
        this.vaoHandle = vaoHandle;
        this.indicesType = indicesType;
        this.indexElemSize = indexElemSize;
        this.indicesCount = indicesCount;
        this.indexOffset = indexOffset;
        this.textureId = textureId;
    }

    /** {@inheritDoc} */
    @Override
    public void render() {
        GL30.glBindVertexArray(vaoHandle);
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId);
        GL11.glDrawElements(indicesType, indicesCount, indexElemSize, indexOffset);
        GL30.glBindVertexArray(0);
    }

    /** {@inheritDoc} */
    @Override
    public void destroy() {
        GL30.glBindVertexArray(0);
        GL30.glDeleteVertexArrays(vaoHandle);
    }


    public static class Builder {
        
        protected static final Logger LOGGER = LoggerFactory.getLogger(Builder.class);

        private String textureFilename;
        private Integer texId;

        private Quaternion rotation = new Quaternion();
        private Vector3f translation = new Vector3f();

        /**
         * <p>
         * build.
         * </p>
         *
         * @return a {@link net.wohlfart.gl.shader.mesh.IRenderable} object.
         */
        public IsRenderable build() {

            // load the texture if needed
            if (textureFilename != null) {
                texId  = loadPNGTexture(textureFilename, GL13.GL_TEXTURE0);
            }

            final Vector3f[] vectors = new Vector3f[] { // @formatter:off
                    new Vector3f(-0.5f, +0.5f, 0),
                    new Vector3f(-0.5f, -0.5f, 0),
                    new Vector3f(+0.5f, -0.5f, 0),
                    new Vector3f(+0.5f, +0.5f, 0) };  // @formatter:on

            if (rotation != null) {
                for (final Vector3f vec : vectors) {
                    SimpleMath.mul(rotation, vec, vec);
                }
            }
            if (translation != null) {
                for (final Vector3f vec : vectors) {
                    SimpleMath.add(translation, vec, vec);
                }
            }


            final Vertex[] vertices = new Vertex[] { new Vertex() {
                {
                    setXYZ(vectors[0].x, vectors[0].y, vectors[0].z);
                    setRGB(1, 0, 0);
                    setST(0, 0);
                }
            }, new Vertex() {
                {
                    setXYZ(vectors[1].x, vectors[1].y, vectors[1].z);
                    setRGB(0, 1, 0);
                    setST(0, 1);
                }
            }, new Vertex() {
                {
                    setXYZ(vectors[2].x, vectors[2].y, vectors[2].z);
                    setRGB(0, 0, 1);
                    setST(1, 1);
                }
            }, new Vertex() {
                {
                    setXYZ(vectors[3].x, vectors[3].y, vectors[3].z);
                    setRGB(1, 1, 1);
                    setST(1, 0);
                }
            } };


            // Put each 'Vertex' in one FloatBuffer the order depends on the shaders positions!
            final FloatBuffer verticesBuffer = BufferUtils.createFloatBuffer(vertices.length * Vertex.elementCount);
            for (int i = 0; i < vertices.length; i++) {
                verticesBuffer.put(vertices[i].getXYZW());
                verticesBuffer.put(vertices[i].getRGBA());
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

            // Create a new Vertex Buffer Object in memory and select it (bind)
            final int vboVerticesHandle = GL15.glGenBuffers();
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboVerticesHandle);
            GL15.glBufferData(GL15.GL_ARRAY_BUFFER, verticesBuffer, GL15.GL_STATIC_DRAW);

            final int vboIndicesHandle = GL15.glGenBuffers();
            GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboIndicesHandle);
            GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL15.GL_STATIC_DRAW);

            ShaderAttributeHandle.POSITION.enable();
            GL20.glVertexAttribPointer(ShaderAttributeHandle.POSITION.getLocation(),
                    Vertex.POSITION_ELEM_COUNT, GL11.GL_FLOAT, false, Vertex.stride, Vertex.positionByteOffset);

            ShaderAttributeHandle.COLOR.enable();
            GL20.glVertexAttribPointer(ShaderAttributeHandle.COLOR.getLocation(),
                    Vertex.COLOR_ELEM_COUNT, GL11.GL_FLOAT, false, Vertex.stride, Vertex.colorByteOffset);

            ShaderAttributeHandle.TEXTURE_COORD.enable();
            GL20.glVertexAttribPointer(ShaderAttributeHandle.TEXTURE_COORD.getLocation(),
                    Vertex.TEXTURE_ELEM_COUNT, GL11.GL_FLOAT, false, Vertex.stride, Vertex.textureByteOffset);

            ShaderAttributeHandle.NORMAL.disable();

            // done with the VAO
            GL30.glBindVertexArray(0);

            return new TexturedMesh(vaoHandle, GL11.GL_TRIANGLES, GL11.GL_UNSIGNED_BYTE, indicesCount, 0, texId);
        }

        private int loadPNGTexture(String filename, int textureUnit) {
            int texId = 0;

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
                texId = GL11.glGenTextures();
                GL13.glActiveTexture(textureUnit);
                GL11.glBindTexture(GL11.GL_TEXTURE_2D, texId);
                // All RGB bytes are aligned to each other and each component is 1 byte
                GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
                // Upload the texture data and generate mip maps (for scaling)
                GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, tWidth, tHeight, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
                GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
                // Setup the ST coordinate system
                GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
                GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
                // Setup what to do when the texture has to be scaled
                GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
                GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR);

            } catch (final FileNotFoundException ex) {
                LOGGER.error("can't load texture image", ex);
            } catch (final IOException ex) {
                LOGGER.error("can't load texture image", ex);
            }
            return texId;
        }

        /**
         * <p>
         * Setter for the field <code>textureFilename</code>.
         * </p>
         *
         * @param textureFilename
         *            a {@link java.lang.String} object.
         */
        public void setTextureFilename(final String textureFilename) {
            this.textureFilename = textureFilename;
        }

        public void setTextureId(int texId) {
            this.texId = texId;
        }


        /**
         * <p>
         * Setter for the field <code>rotation</code>.
         * </p>
         *
         * @param rotation
         *            a {@link org.lwjgl.util.vector.Quaternion} object.
         */
        public void setRotation(Quaternion rotation) {
            this.rotation = rotation;
        }

        /**
         * <p>
         * Setter for the field <code>translation</code>.
         * </p>
         *
         * @param translation
         *            a {@link org.lwjgl.util.vector.Vector3f} object.
         */
        public void setTranslation(Vector3f translation) {
            this.translation = translation;
        }

    }
}
