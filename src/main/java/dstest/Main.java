package dstest;

import org.lwjgl.*;

import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import dstest.rendering.RenderWindow;
import dstest.rendering.Renderer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import java.nio.*;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Main {


	public static void main(String[] args) {
		System.setProperty("WM_CLASS", "Floating");
		RenderWindow window = new RenderWindow("Floating", 800, 600);
		window.create();
		Renderer renderer = new Renderer(window);
		System.setProperty("WM_CLASS", "Floating");

		renderer.initOpenGL();
		
		renderer.setUpTestScene();
		renderer.renderTestScene();
		System.out.println("Hello, World!");
	}

}
