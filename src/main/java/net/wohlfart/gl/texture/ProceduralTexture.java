package net.wohlfart.gl.texture;

import java.awt.Color;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.util.vector.Vector3f;

public abstract class ProceduralTexture implements TextureBuffer {

	protected long seed;
	protected int width;
	protected int height;
	protected IntBuffer buffer;
	protected int id;


	public ProceduralTexture(int width, int height) {
		this.width = width;
		this.height = height;
		this.buffer = BufferUtils.createIntBuffer(width * height);
	}

	@Override
	public void init() {
		int[] data = new int[width * height];
		buffer.get(data);
		buffer.rewind();
		data = initialize(width, height, data);
		buffer.put(data);
		buffer.rewind();
	}

	protected abstract int[] initialize(int width, int height, int[] data);

	protected void setPixel(int x, int y, Color color, int[] data) {
		y = height - y - 1;
		if (x < 0) {
			throw new IllegalArgumentException("x < 0");
		}
		if (y < 0) {
			throw new IllegalArgumentException("y < 0");
		}
		if (x > width - 1) {
			throw new IllegalArgumentException("x > width - 1");
		}
		if (y > height - 1) {
			throw new IllegalArgumentException("y > height - 1");
		}

		int i = (x + y * width);
		int value = 0;
		value = value | ( 0xff & color.getAlpha());
		value = value << 8;
		value = value | ( 0xff & color.getRed());
		value = value << 8;
		value = value | ( 0xff & color.getGreen());
		value = value << 8;
		value = value | ( 0xff & color.getBlue());
		//data[i] =  color.getRGB();
		data[i] =  value;
	}

	/**
	 * 0/0 is top left, the whole texture is wrapped around a sphere
	 *
	 * @return a vector with each element [0..1]
	 */
	protected Vector3f getNormalVector(final int x, final int y) {
		int yRange = height - 1;
		int xRange = width - 1;
		float latitude = ((float)Math.PI * ((float) y / (float) yRange)); // [0 .. PI] (north-south)
		float longitude = ((float)Math.PI * 2 * ((float) x / (float) xRange)); // [0 .. TWO_PI]

		float xx = (float)Math.sin(longitude) * (float)Math.sin(latitude); // 0 -> 0; 1/2pi -> 1 ; pi -> 0
		float yy = (float)Math.cos(latitude); // 0 -> 1; 1/2pi -> 0 ; pi -> -1
		float zz = (float)Math.cos(longitude) * (float)Math.sin(latitude); // 0 -> 1;...

		return new Vector3f(xx, yy, zz);
	}


	@Override
	public IntBuffer getBuffer() {
		return buffer;
	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public int getHeight() {
		return height;
	}

}