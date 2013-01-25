package net.wohlfart.basic.states;

import java.util.HashSet;
import java.util.Set;

import net.wohlfart.basic.Game;
import net.wohlfart.gl.CanMoveImpl;
import net.wohlfart.gl.CanRotateImpl;
import net.wohlfart.gl.elements.ColoredQuad;
import net.wohlfart.gl.elements.Renderable;
import net.wohlfart.gl.elements.Skybox;
import net.wohlfart.gl.elements.TexturedQuad;
import net.wohlfart.gl.elements.debug.Arrow;
import net.wohlfart.gl.elements.debug.Circle;
import net.wohlfart.gl.elements.debug.CubeMesh;
import net.wohlfart.gl.elements.debug.IcosphereMesh;
import net.wohlfart.gl.elements.debug.TerahedronRefinedMesh;
import net.wohlfart.gl.elements.debug.TetrahedronMesh;
import net.wohlfart.gl.input.InputSource;
import net.wohlfart.gl.input.KeyPressedEvent;
import net.wohlfart.gl.renderer.DefaultRenderer;
import net.wohlfart.gl.renderer.Renderer;
import net.wohlfart.gl.shader.UniformHandle;
import net.wohlfart.model.Avatar;
import net.wohlfart.tools.SimpleMath;
import net.wohlfart.tools.SimpleMatrix4f;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.ReadableColor;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;

public class SimpleState implements GameState {

    private Renderer renderer;
    private boolean quit = false;

    private final CanMoveImpl canMove = new CanMoveImpl();
    private final CanRotateImpl canRotate = new CanRotateImpl();
    private final Avatar avatar = new Avatar(canRotate, canMove);

    private final Set<Renderable> skyboxBucket = new HashSet<Renderable>();
    private final Set<Renderable> elemBucket = new HashSet<Renderable>();
    private final Set<Renderable> uiBucket = new HashSet<Renderable>();


    SimpleState() {}


    @Override
    public void setup(final Game game) {

        avatar.setInputSource(InputSource.INSTANCE);

        renderer = new DefaultRenderer();
        renderer.setup();
        renderer.set(UniformHandle.CAM_TO_CLIP, game.getProjectionMatrix());

        // render the skybox first
        skyboxBucket.add(new Skybox());


        elemBucket.add(new Arrow(new Vector3f(1,0,0)).color(ReadableColor.RED));
        elemBucket.add(new Arrow(new Vector3f(0,1,0)).color(ReadableColor.GREEN));
        elemBucket.add(new Arrow(new Vector3f(0,0,1)).color(ReadableColor.BLUE));

        elemBucket.add(new TerahedronRefinedMesh(2, 1)
        .lineWidth(1)
        .color(ReadableColor.RED)
        .translate(new Vector3f(3,5,0)));
        elemBucket.add(new TerahedronRefinedMesh(2, 2)
        .lineWidth(2)
        .color(ReadableColor.GREEN)
        .translate(new Vector3f(0,5,0)) );
        elemBucket.add(new TerahedronRefinedMesh(2, 1)
        .lineWidth(2)
        .color(ReadableColor.BLUE)
        .translate(new Vector3f(-3,5,0)) );

        elemBucket.add(new TetrahedronMesh(3)
        .lineWidth(2)
        .color(ReadableColor.WHITE)
        .translate(new Vector3f(-3,-5,0)) );

        elemBucket.add(new CubeMesh(1)
        .lineWidth(1)
        .color(ReadableColor.ORANGE)
        .translate(new Vector3f(-3,-2,0))
        .rotate(SimpleMath.createQuaternion(new Vector3f(1,0,-1), new Vector3f(0,1,0), new Quaternion())));

        elemBucket.add(new Circle(1)
        .lineWidth(2)
        .translate(new Vector3f(3,2,0)));

        elemBucket.add(new IcosphereMesh(1, 1)
        .lineWidth(1)
        .color(ReadableColor.RED)
        .translate(new Vector3f(5,-7,0)));
        elemBucket.add(new IcosphereMesh(1, 2)
        .lineWidth(2)
        .color(ReadableColor.GREEN)
        .translate(new Vector3f(0,-7,0)) );
        elemBucket.add(new IcosphereMesh(1, 1)
        .lineWidth(2)
        .color(ReadableColor.BLUE)
        .translate(new Vector3f(-5,-7,0)) );

        elemBucket.add(new TexturedQuad()
        .translate(new Vector3f(-1,5,0)));



        elemBucket.add(new ColoredQuad()
        .translate(new Vector3f(-1,5,0)));



        InputSource.INSTANCE.register(new KeyPressedEvent.Listener(){
            @Override
            public void keyEvent(KeyPressedEvent evt) {
                if (Keyboard.KEY_ESCAPE == evt.getKey()) {
                    quit();
                }
            }
        });

        //Font awtFont = new Font("Times New Roman", Font.PLAIN, 24);
        //trueTypeFont = new TrueTypeFont(awtFont, false);
    }


    public synchronized void quit() {
        quit = true;
    }


    @Override
    public void update(float tpf) {

        // rotate the view
        Matrix4f viewMatrix = SimpleMatrix4f.create(canRotate);
        renderer.set(UniformHandle.WORLD_TO_CAM, viewMatrix);
        //renderer.set(UniformHandle.MODEL_TO_WORLD, viewMatrix);

        // move the object
        Matrix4f modelMatrix = SimpleMatrix4f.create(canMove);
        renderer.set(UniformHandle.MODEL_TO_WORLD, modelMatrix);


        /*
        Matrix4f result = new Matrix4f();
        Matrix4f.mul(modelMatrix, viewMatrix, result);
        renderer.set(MatrixHandle.MODEL_TO_WORLD, result);
         */

    }


    @Override
    public void render() {

		renderer.set(UniformHandle.MODEL_TO_WORLD, SimpleMath.UNION_MATRIX); // no move
        for (Renderable renderable : skyboxBucket) {
            renderable.render(renderer);
        }

        // move the object
        Matrix4f modelMatrix = SimpleMatrix4f.create(canMove);
        renderer.set(UniformHandle.MODEL_TO_WORLD, modelMatrix);
        for (Renderable renderable : elemBucket) {
            renderable.render(renderer);
        }

        for (Renderable renderable : uiBucket) {
            renderable.render(renderer);
        }

    }


    @Override
    public boolean isDone() {
        return Display.isCloseRequested() || quit;
    }



    @Override
    public void dispose(Game game) {
        renderer.dispose();
    }

}
