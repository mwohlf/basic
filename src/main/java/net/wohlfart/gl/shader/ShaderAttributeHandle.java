package net.wohlfart.gl.shader;

import static net.wohlfart.gl.shader.GraphicContextManager.INSTANCE;

import java.util.HashMap;
import java.util.Map;

import org.lwjgl.opengl.GL20;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//
/**
 * <p>
 * All handlers for vertex attributes used in any shader.
 * This is not typesafe, the sizes must match those used in the shader
 *
 * Note that a single attribute can have different locations depending on the active shader
 * this is why the actual location is stored in the graphic context and not here in
 * a (singleton) enum
 *
 * see: https://www.opengl.org/sdk/docs/man3/
 * </p>
 */
public enum ShaderAttributeHandle {// @formatter:off
    COLOR("in_Color", 4),
    POSITION("in_Position", 3),
    TEXTURE_COORD("in_TexCoord", 2),
    NORMAL("in_Normal", 3),
    ;
    // @formatter:on

    private static final Logger LOGGER = LoggerFactory.getLogger(PerspectiveProjection.class);


    private final String lookupString;
    private final int floatCount;

    ShaderAttributeHandle(String lookupString, int floatCount) {
        this.lookupString = lookupString;
        this.floatCount = floatCount;
    }

    /**
     * <p>
     * Returns the number of floats this attribute uses.
     * </p>
     *
     * @return a int.
     */
    public int getFloatCount() {
        return floatCount;
    }

    /**
     * <p>
     * Returns the number of bytes this attribute uses.
     * </p>
     *
     * @return a int.
     */
    public int getByteCount() {
        return floatCount * 4;
    }

    /**
     * <p>
     * getLocation.
     * </p>
     *
     * @return a int.
     */
    public int getLocation() {
        return INSTANCE.getAttributeLocation(lookupString);
    }

    /**
     * enable the vertex attribute
     */
    public void enable() {
        GL20.glEnableVertexAttribArray(getLocation());
    }

    /**
     * disable the vertex attribute and set a default null value
     */
    public void disable() {
        GL20.glDisableVertexAttribArray(getLocation());
        switch (floatCount) {
        case 1:
            GL20.glVertexAttrib1f(getLocation(), 0);
            break;
        case 2:
            GL20.glVertexAttrib2f(getLocation(), 0, 0);
            break;
        case 3:
            GL20.glVertexAttrib3f(getLocation(), 0, 0, 0);
            break;
        case 4:
            GL20.glVertexAttrib4f(getLocation(), 0, 0, 0, 0);
            break;
        }
    }

    public Map<String, Integer> getLocationMap(IShaderProgram shaderProgram) {
        HashMap<String, Integer> result = new HashMap<String, Integer>();
        final int location = GL20.glGetAttribLocation(shaderProgram.getProgramId(), lookupString);
        result.put(lookupString, location);
        if (location < 0) {
            LOGGER.debug("location for AttributeHandle '{}' is '{}' wich is <0, the shaderProgram is '{}'",
                    new Object[] { lookupString, location, shaderProgram });
        } else {
            LOGGER.debug("attributeMap lookup: '{}' => '{}'", new Object[] { lookupString, location });
        }
        return result;
    }

}
