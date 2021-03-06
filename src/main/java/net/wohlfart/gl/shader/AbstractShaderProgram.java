package net.wohlfart.gl.shader;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Scanner;

import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.util.glu.GLU;

/**
 * the class for dealing with the basic shader stuff and keeping the shader program id
 */
class AbstractShaderProgram implements IShaderProgram {

    private int programId = -1;

    /**
     * <p>
     * loadShader.
     * </p>
     *
     * @param filename
     *            a {@link java.lang.String} object.
     * @param shaderType
     *            a int.
     * @return a int.
     */
    protected int loadShader(final String filename, int shaderType) {
        int shader = 0;
        Scanner scanner = null;
        try (InputStream inputStream = ClassLoader.class.getResourceAsStream(filename);) {

            scanner = new Scanner(inputStream);
            scanner.useDelimiter("\\A");
            if (!scanner.hasNext()) {
                throw new ShaderException("empty shader file");
            }
            shader = ARBShaderObjects.glCreateShaderObjectARB(shaderType);
            if (shader == 0) {
                throw new ShaderException("glCreateShaderObjectARB returned 0");
            }
            ARBShaderObjects.glShaderSourceARB(shader, scanner.next());
            ARBShaderObjects.glCompileShaderARB(shader);

            final int compileStatus = ARBShaderObjects.glGetObjectParameteriARB(shader, ARBShaderObjects.GL_OBJECT_COMPILE_STATUS_ARB);
            if (compileStatus == GL11.GL_FALSE) {
                throw new ShaderException("Error creating shader, couldn't compile, reason: " + getLogInfo(shader) + " the shader file is '" + filename
                        + "', the shaderType is '" + shaderType + "'");
            }
            return shader;
        } catch (final FileNotFoundException ex) {
            throw new ShaderException("file not found: '" + filename + "'", ex);
        } catch (final NullPointerException ex) {
            throw new ShaderException("null pointer, file not found: '" + filename + "'", ex);
        } catch (final IOException ex) {
            throw new ShaderException("stream problems", ex);
        } finally {
            if (scanner != null) {
                scanner.close();
            }
        }
    }

    /**
     * attach, link and validate the shaders into a shader program
     *
     * @param handles
     *            the shaders
     */
    protected void linkAndValidate(int... handles) {
        programId = GL20.glCreateProgram();
        for (final int handle : handles) {
            GL20.glAttachShader(programId, handle);
        }
        GL20.glLinkProgram(programId);

        GL20.glValidateProgram(programId);
        final int error = GL11.glGetError();
        if (error != GL11.GL_NO_ERROR) {// @formatter:off
            throw new ShaderException(""
                    + "error validating shader, error string is '" + GLU.gluErrorString(error)
                    + "' \n" + "programmId is '" + programId
                    + "' \n" + "handles are: " + Arrays.toString(handles));  // @formatter:on
        }
    }

    /**
     * <p>
     * unlink.
     * </p>
     *
     * @param handles
     *            a int.
     */
    protected void unlink(int... handles) {
        GL20.glUseProgram(0);
        for (final int handle : handles) {
            GL20.glDetachShader(programId, handle);
        }
        for (final int handle : handles) {
            GL20.glDeleteShader(handle);
        }
    }

    /**
     * <p>
     * getLogInfo.
     * </p>
     *
     * @param obj
     *            a int.
     * @return a {@link java.lang.String} object.
     */
    protected String getLogInfo(int obj) {
        return ARBShaderObjects.glGetInfoLogARB(obj, ARBShaderObjects.glGetObjectParameteriARB(obj, ARBShaderObjects.GL_OBJECT_INFO_LOG_LENGTH_ARB));
    }

    /** {@inheritDoc} */
    @Override
    public int getProgramId() {
        return programId;
    }

    /** {@inheritDoc} */
    @Override
    public void setup() {
        //
    }

    /** {@inheritDoc} */
    @Override
    public void bind() {
        GL20.glUseProgram(programId);
    }

    /** {@inheritDoc} */
    @Override
    public void unbind() {
        GL20.glUseProgram(0);
    }

    /** {@inheritDoc} */
    @Override
    public void dispose() {
        GL20.glDeleteProgram(programId);
    }

}
