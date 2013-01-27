package net.wohlfart.gl.shader;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL20;
import org.lwjgl.util.ReadableColor;
import org.lwjgl.util.vector.Matrix4f;


// handlers for uniforms used in the shader
public enum ShaderUniformHandle {
	MODEL_TO_WORLD("modelToWorldMatrix"),
	WORLD_TO_CAM("worldToCameraMatrix"),
	CAM_TO_CLIP("cameraToClipMatrix");

	private final String lookupString;

	ShaderUniformHandle(final String lookupString) {
		this.lookupString = lookupString;
	}

	String getLookupString() {
		return lookupString;
	}



	public void setValue(final FloatBuffer colorBuffer) {
		GL20.glUniform4(GraphicContextManager.INSTANCE.getLocation(this), colorBuffer);
	}

	public int getLocation() {
		return GraphicContextManager.INSTANCE.getLocation(this);
	}

	public void set(Matrix4f matrix) {
		FloatBuffer matrix44Buffer = BufferUtils.createFloatBuffer(16);
		matrix.store(matrix44Buffer);
		matrix44Buffer.flip();
		GL20.glUniformMatrix4(getLocation(), false, matrix44Buffer);
	}

	public void set(ReadableColor readableColor) {
		FloatBuffer colorBuffer = BufferUtils.createFloatBuffer(4);
		colorBuffer.put(new float[] {
				readableColor.getRed()/255f,
				readableColor.getGreen()/255f,
				readableColor.getBlue()/255f,
				readableColor.getAlpha()/255f,
		});
		colorBuffer.flip();
		GL20.glUniform4(getLocation(), colorBuffer);
	}

}