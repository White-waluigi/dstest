package dstest.rendering;

import java.util.Arrays;
import java.util.HashMap;
import java.util.stream.Stream;

import org.ejml.simple.SimpleMatrix;
import scene.SceneElement;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL46;

import scene.Camera;
import scene.SceneGraph;
import scene.SceneNode;
import vectors.Mat4;
import vectors.Vec3;
import vectors.Vec4;
import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryStack.*;

public class Renderer {
	RenderWindow window;
	UniformBuffer worldViewProjBuf;
	double lastTime;

	Camera cam;
	String glvendor;
	String glrenderer;
	HashMap<SceneElement, DisplayCommand> displayCommands = new HashMap<>();
	private SceneNode root;
	private Object gldriver;

	public Renderer(RenderWindow window) {

		this.window = window;

		this.window.registerResizeCallback(new RenderWindow.ResizeCallback() {
			@Override
			public void onResize(int width, int height) {
				GL46.glViewport(0, 0, width, height);
				float aspect = width / (height * 1f);
				cam.aspect = aspect;
			}
		});
	}

	public void registerDisplayCommand(SceneElement element, DisplayCommand command) {
		displayCommands.put(element, command);
	}

	public void registerCamera(Camera cam) {
		this.cam = cam;
	}

	public HashMap<String, Boolean> getKeys() {
		String[] keys = new String[] { "w", "a", "s", "d", "q", "e", "up", "down", "left", "right", "shift" };
		HashMap<String, Boolean> keyMap = new HashMap<String, Boolean>();

		for (String key : keys) {
			keyMap.put(key, window.keyPressed(key));
		}
		return keyMap;
	}

	public void initOpenGL() {

		// capabilities
		GL.createCapabilities();

		// clear color

		GL46.glClearColor(0.2f, 0.2f, 0.3f, 0.0f);

	}

	public void startRenderPass() {
		GL46.glClear(GL46.GL_COLOR_BUFFER_BIT | GL46.GL_DEPTH_BUFFER_BIT);
		GL46.glEnable(GL46.GL_DEPTH_TEST);
		GL46.glDepthFunc(GL46.GL_LESS);
		GL46.glEnable(GL46.GL_CULL_FACE);
		GL46.glCullFace(GL46.GL_BACK);

	}

	public void setWorldViewProj(Mat4 mvp) {
		worldViewProjBuf.update(mvp.toArray());
		worldViewProjBuf.upload();
	}

	public UniformBuffer getWorldViewProjBuf() {
		return worldViewProjBuf;
	}

	public void endRenderPass() {
		window.swapBuffers();
	}

	public void createWorldViewProjBuf() {
		worldViewProjBuf = new UniformBuffer("matrices", new float[16]);
		worldViewProjBuf.create();
	}

	public void setUpTestScene() {
		glvendor = GL46.glGetString(GL46.GL_VENDOR);
		glrenderer = GL46.glGetString(GL46.GL_RENDERER);
		String glversion = GL46.glGetString(GL46.GL_VERSION);

		gldriver = null;

		int i = glversion.indexOf(' ');

		if (i != -1) {

			gldriver = glversion.substring(i + 1);

			glversion = glversion.substring(0, i);

		}
		System.out.println("OpenGL Vendor: " + glvendor);
		System.out.println("OpenGL Renderer: " + glrenderer);

		createWorldViewProjBuf();

		String path = "res/tf2/";
		String file = "2fort.obj";
		this.root = new SceneNode(null, true);

		OBJLoader loader = new OBJLoader();
		System.out.println("Loading " + file);
		loader.load(file,path, root);
		System.out.println("Loaded " + file);
		Camera cam = new Camera(root, new Vec3(-1, 1, 0), 0, 0, 90, 1, 0.1f, 1000);

		this.root.initialize(this);

	}

	public void cleanUp() {
		System.out.println("Cleaning up");
		// clear all display commands
		System.out.println("Cleaning up display commands");
		for (SceneElement element : displayCommands.keySet()) {
			DisplayCommand command = displayCommands.get(element);
			command.destroy();
		}
		System.out.println("Cleaning up world view proj buffer");
		// destroy the window
		System.out.println("Cleaning up window");
		window.destroy();

		// terminate GLFW
		System.out.println("Cleaning up GLFW");
		glfwTerminate();

		// clean up openGL
		System.out.println("Cleaning up OpenGL");
		try {
			GL.destroy();
		} catch (Exception e) {
			System.out.println("Error cleaning up OpenGL");
		}
		System.out.println("Cleaned up");

	}

	public void renderTestScene() {
		int ctr = 0;
		double[] fps = new double[100];
		while (!window.wantsToClose()) {


			double deltaTime = glfwGetTime() - lastTime;
			lastTime = glfwGetTime();
			ctr++;
			fps[ctr % 100] = 1f / ((deltaTime * 1.0f));
			if (ctr % 100 == 0) {
				double sum = Arrays.stream(fps).sum();
				int avg = (int) sum / 100;

				System.out.println(glvendor + " Current FPS: " + avg);
			}
			this.root.update(deltaTime);

			startRenderPass();
			for (SceneElement element : displayCommands.keySet()) {
				DisplayCommand command = displayCommands.get(element);
				command.draw();
			}
			endRenderPass();
		}

		cleanUp();
	}

}
