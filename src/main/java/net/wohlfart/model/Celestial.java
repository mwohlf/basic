package net.wohlfart.model;

import java.util.Random;

import net.wohlfart.gl.texture.CelestialTexture;
import net.wohlfart.gl.texture.CelestialType;
import net.wohlfart.gl.texture.ITexture;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.glu.Sphere;
import org.lwjgl.util.vector.Vector3f;

class Celestial {

    protected final Sphere sphere = new Sphere();
    protected final Vector3f position = new Vector3f();
    protected final float radius;
    // protected final IntBuffer texture;

    protected final int lod = 64;
    private final ITexture texture;
    private final long seed;
    private final Random random;
    private final CelestialType planetType;
    private final float pathArc;
    private final float pathRadius;
    private final float rotSpeed;
    private final Vector3f rotAxis;

    public Celestial(long seed) {
        this.seed = seed;
        this.random = new Random(seed);

        // random planet type
        final int index = random.nextInt(CelestialType.values().length);
        planetType = CelestialType.values()[index];

        radius = getRandom(planetType.minRadius, planetType.maxRadius);

        rotSpeed = getRandom(planetType.minRot, planetType.maxRot);

        pathRadius = getRandom(planetType.minPathRadius, planetType.maxPathRadius);

        pathArc = getRandom((float) -Math.PI, (float) Math.PI); // location on the path

        final float f = planetType.maxAxisDeplacement;
        // rotAxis = new Vector3f(0,0,1);
        rotAxis = new Vector3f(getRandom(-f, f), getRandom(-f, f), 1);

        sphere.setDrawStyle(GLU.GLU_FILL);
        sphere.setNormals(GLU.GLU_SMOOTH);
        sphere.setOrientation(GLU.GLU_OUTSIDE);

        texture = new CelestialTexture(radius, planetType, 2);
        texture.init();
    }

    public void setPosition(final Vector3f position) {
        this.position.set(position);
    }

    public void setPosition(float x, float y, float z) {
        this.position.x = x;
        this.position.y = y;
        this.position.z = z;
    }

    public void render() {
        texture.bind();

        GL11.glPushMatrix();
        GL11.glTranslatef(position.x, position.y, position.z);
        GL11.glColor3f(0.1f, 0.4f, 0.9f);
        GL11.glMaterialf(GL11.GL_FRONT, GL11.GL_SHININESS, 50.0f);

        sphere.setDrawStyle(GLU.GLU_FILL);
        sphere.setNormals(GLU.GLU_SMOOTH);
        sphere.setOrientation(GLU.GLU_OUTSIDE);
        sphere.setTextureFlag(true);
        sphere.draw(radius, lod, lod);
        GL11.glPopMatrix();
    }

    public void update(final float tpf) {

    }

    public void distroy() {

    }

    protected float getRandom(float min, float max) {
        return (1f - random.nextFloat()) * (max - min) + min;
    }

    protected long getSeed() {
        return seed;
    }

    protected Vector3f getRotAxis() {
        return rotAxis;
    }

    public float getRadius() {
        return radius;
    }

    public float getRotSpeed() {
        return rotSpeed;
    }

    public CelestialType getType() {
        return planetType;
    }

    public float getPathRadius() {
        return pathRadius;
    }

    public float getPathArc() {
        return pathArc;
    }

}
