package net.wohlfart.model;

import net.wohlfart.gl.Camera;
import net.wohlfart.gl.IState;
import net.wohlfart.gl.input.InputSource;
import net.wohlfart.gl.input.KeyTypedEvent;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;

public class CelestialState implements IState {

	protected InputSource inputProcessor;
	protected CelestialScene scene;
	protected Camera camera;
	protected Avatar avatar;


	protected boolean escPressed = false;

	// ui, sounds, cam

	@Override
	public void setup() {
		camera = new Camera();
		scene = new CelestialScene();
		avatar = new Avatar(camera, scene);
		InputSource.INSTANCE.register(new KeyTypedEvent.Listener() {
			@Override
			public void keyEvent(KeyTypedEvent evt) {
				if (evt.getKey() == Keyboard.KEY_ESCAPE) {
					escPressed = true;
				}
			}
		});
	}

	@Override
	public void update(float tpf) {
		scene.update(tpf);
	}

	@Override
	public void render() {
		camera.lookThrough();
		scene.render();
	}

	@Override
	public boolean isDone() {
		return Display.isCloseRequested() || escPressed;
	}

	@Override
	public void teardown() {
		scene.teardown();
	}


}
