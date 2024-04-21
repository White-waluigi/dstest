package dstest.rendering;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import org.lwjgl.opengl.GL46;

import vectors.Vec4;

public class Shader extends RenderResource {

	public String vertexShaderFile;
	public String fragmentShaderFile;

	public int program;
	private HashMap<String, float[]> uniforms = new HashMap<String, float[]>();

	public Shader(String vertexFile, String fragmentFile) {
		this.vertexShaderFile = vertexFile;
		this.fragmentShaderFile = fragmentFile;

	}

	public void create() {
		super.create();
		// Load the shaders
		String vertexShader = "";
		String fragmentShader = "";

		try {
			BufferedReader reader = new BufferedReader(new FileReader(vertexShaderFile));
			String line;
			while ((line = reader.readLine()) != null) {
				vertexShader += line + "\n";
			}
			reader.close();

			reader = new BufferedReader(new FileReader(fragmentShaderFile));
			while ((line = reader.readLine()) != null) {
				fragmentShader += line + "\n";
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		program = GL46.glCreateProgram();

		int vertexShaderId = GL46.glCreateShader(GL46.GL_VERTEX_SHADER);
		GL46.glShaderSource(vertexShaderId, vertexShader);
		GL46.glCompileShader(vertexShaderId);
		// Check for errors
		if (GL46.glGetShaderi(vertexShaderId, GL46.GL_COMPILE_STATUS) == GL46.GL_FALSE) {
			System.err.println("Vertex shader not compiled");
			System.err.println(GL46.glGetShaderInfoLog(vertexShaderId));
			throw new RuntimeException();
		}
		GL46.glAttachShader(program, vertexShaderId);

		int fragmentShaderId = GL46.glCreateShader(GL46.GL_FRAGMENT_SHADER);
		GL46.glShaderSource(fragmentShaderId, fragmentShader);
		GL46.glCompileShader(fragmentShaderId);
		// Check for errors

		if (GL46.glGetShaderi(fragmentShaderId, GL46.GL_COMPILE_STATUS) == GL46.GL_FALSE) {
			System.err.println("Fragment shader not compiled");
			System.err.println(GL46.glGetShaderInfoLog(fragmentShaderId));
			throw new RuntimeException();

		}
		GL46.glAttachShader(program, fragmentShaderId);

		GL46.glLinkProgram(program);
	}

	public void setUniform(String name, Vec4 value) {
		uniforms.put(name, value.toArray());

	}

	public void bind() {

		GL46.glUseProgram(program);
		for (String name : uniforms.keySet()) {
			int location = GL46.glGetUniformLocation(program, name);
			GL46.glUniform4fv(location, uniforms.get(name));
		}
	}

	public void unbind() {

		GL46.glUseProgram(0);

	}

	public void destroy() {

		GL46.glDeleteProgram(program);
		checkForGLError("Shader destroy");
		super.destroy();
	}

	private void checkForGLError(String string) {
		// TODO Auto-generated method stub
		int errorFlag = GL46.glGetError();
		if (errorFlag != GL46.GL_NO_ERROR) {
			System.out.println("OpenGL Error: " + errorFlag + " " + GL46.glGetString(errorFlag) + " " + string);
			// print stack trace
			throw new RuntimeException();
		}
	}

}
