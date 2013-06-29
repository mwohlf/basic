package net.wohlfart.gl.spatial;

import java.util.Random;

import net.wohlfart.basic.elements.IsRenderable;
import net.wohlfart.basic.elements.SpatialEntity;
import net.wohlfart.gl.elements.AbstractRenderable;
import net.wohlfart.gl.texture.CelestialType;

public class CelestialBody extends AbstractRenderable implements SpatialEntity {

    private final long seed;
    private Random random;
    private final CelestialType planetType;


    public CelestialBody(long seed) {
        this.seed = seed;
        this.random = new Random(seed);
        // random planet type
        final int index = random.nextInt(CelestialType.values().length);
        this.planetType = CelestialType.values()[index];
    }


    @Override
    public void update(final float tpf) {

    }

    @Override
    public void destroy() {

    }




    @Override
    protected IsRenderable setupMesh() {
        /*
        final CelestialBodyMesh.IcosahedronBuilder icosaederBuilder = new CelestialBodyMesh.IcosahedronBuilder();
        this.random = new Random(seed);
        icosaederBuilder.setRadius(SimpleMath.random(planetType.minRadius, planetType.maxRadius));
        return icosaederBuilder.build();
        */
        final CelestialBodyMesh.RevolvedSphereBuilder icosaederBuilder = new CelestialBodyMesh.RevolvedSphereBuilder();
        this.random = new Random(seed);
        icosaederBuilder.setLod(3);
        return icosaederBuilder.build();
    }

}