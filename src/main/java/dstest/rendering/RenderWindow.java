package dstest.rendering;
import dstest.rendering.RenderWindow;
import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

import java.nio.IntBuffer;
import java.util.Arrays;
import java.util.HashMap;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.system.MemoryStack;

public class RenderWindow {

	public int width;
	public int height;
	public String title;
	private long window;
	
	public RenderWindow(String title, int width, int height) {
		this.title = title;
		this.width = width;
		this.height = height;
	}
	
	
	public void create() {
		
		// Setup an error callback. The default implementation
		// will print the error message in System.err.
		GLFWErrorCallback.createPrint(System.err).set();

		// Initialize GLFW. Most GLFW functions will not work before doing this.
		if ( !glfwInit() )
			throw new IllegalStateException("Unable to initialize GLFW");

		// Configure GLFW
		glfwDefaultWindowHints(); // optional, the current window hints are already the default
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
		glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable
		glfwWindowHintString(GLFW_X11_CLASS_NAME, "Floating");
		glfwWindowHintString(GLFW_X11_INSTANCE_NAME, "Floating");
		// 
		// Create the window
		window = glfwCreateWindow(300, 300, "Hello World!", NULL, NULL);
		if ( window == NULL )
			throw new RuntimeException("Failed to create the GLFW window");

		// Setup a key callback. It will be called every time a key is pressed, repeated or released.
		glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
			if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
				glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
		});

		// Get the thread stack and push a new frame
		try ( MemoryStack stack = stackPush() ) {
			IntBuffer pWidth = stack.mallocInt(1); // int*
			IntBuffer pHeight = stack.mallocInt(1); // int*

			// Get the window size passed to glfwCreateWindow
			glfwGetWindowSize(window, pWidth, pHeight);

			// Get the resolution of the primary monitor
			GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

			// Center the window
			glfwSetWindowPos(
				window,
				(vidmode.width() - pWidth.get(0)) / 2,
				(vidmode.height() - pHeight.get(0)) / 2
			);
		} // the stack frame is popped automatically

		// Make the OpenGL context current
		glfwMakeContextCurrent(window);
		// Enable v-sync
		glfwSwapInterval(0);
 
		// add Title
		glfwSetWindowTitle(window, title);
		// Set window size
		glfwSetWindowSize(window, width, height);
		
		// Make the window visible
		glfwShowWindow(window);
		
		
		

		
		

	}
	
	public interface ResizeCallback {
		public void onResize(int width, int height);
	}
	
	public void registerResizeCallback( ResizeCallback callback) {

		glfwSetWindowSizeCallback(window, (window, width, height) -> {
			callback.onResize(width, height);
		});

	}
	


	public void destroy() {
		
		// Free the window callbacks and destroy the window
		glfwFreeCallbacks(window);
		glfwDestroyWindow(window);

		// Terminate GLFW and free the error callback
		glfwTerminate();
		glfwSetErrorCallback(null).free();
	}


	public void swapBuffers() {
		
		glfwSwapBuffers(window); // swap the color buffers

		// Poll for window events. The key callback above will only be
		// invoked during this call.
		glfwPollEvents();
	}


	public boolean wantsToClose() {
		return glfwWindowShouldClose(window); 
   
	}


	public boolean keyPressed(String key) {
		String[] validKeys=new String[] {"up","down","left","right","space","enter","escape",	"shift"};
		//check if between a-z
		
		if ((key.length() == 1 && key.charAt(0) >= 'a')) {
			
				
				return glfwGetKey(window, GLFW_KEY_A + key.charAt(0) - 'a') == GLFW_PRESS;
			
		}else if(Arrays.asList(validKeys).contains(key)) {
			HashMap<String, Integer> keyMap=new HashMap<String, Integer>();
			keyMap.put("up", GLFW_KEY_UP);
			keyMap.put("down", GLFW_KEY_DOWN);
			keyMap.put("left", GLFW_KEY_LEFT);
			keyMap.put("right", GLFW_KEY_RIGHT);
			keyMap.put("space", GLFW_KEY_SPACE);
			keyMap.put("enter", GLFW_KEY_ENTER);
			keyMap.put("escape", GLFW_KEY_ESCAPE);
			keyMap.put("shift", GLFW_KEY_LEFT_SHIFT);
			return glfwGetKey(window, keyMap.get(key)) == GLFW_PRESS;
		}
		else {
			throw new RuntimeException("Invalid key");
		}
		
	}
}
