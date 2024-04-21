package dstest.rendering;

import org.ejml.simple.SimpleMatrix;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL46;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class UniformBuffer extends RenderResource {

	public int bufferID;
	public int size;
	public String name;

	public float[] dataf = null;
	public int[] datai = null;
	
	public static int nextBingdingPoint = 0;
	public int bindingPoint = nextBingdingPoint++;

	public enum UBType {
		Float, Int
	};

	private UBType type;

	public void setBuffer(UBType type, int size) {
		this.size = size;
		this.type = type;

		if (type == UBType.Float) {
			dataf = new float[size];
		} else if (type == UBType.Int) {
			datai = new int[size];
		} else {
			throw new RuntimeException("Invalid type");
		}

	}

	public UniformBuffer(String name, UBType type, SimpleMatrix mat) {
		this.name = name;

		this.setBuffer(type, mat.getNumElements());
		this.update(mat);

	}

	public UniformBuffer(String name,  int[] data) {
		
		this.name = name;

		this.setBuffer(UBType.Int, data.length);
		this.update(data);

	}

	public UniformBuffer(String name, float[] data) {

		this.name = name;

		this.setBuffer(UBType.Float, data.length);
		this.update(data);

	}

	public UniformBuffer(String name, UBType type, int[] matdims) {
		this.name = name;

		int size = 1;
		for (int i = 0; i < matdims.length; i++) {
			size *= matdims[i];
		}
		this.setBuffer(type, size);

	}

	public UniformBuffer(String name, UBType type, int size) {
		this.name = name;
		this.setBuffer(type, size);

	}

	public void update(SimpleMatrix mat) {
		if (mat.getNumElements() != this.size)
			throw new RuntimeException("Invalid matrix size");
		if (type == UBType.Float) {
			for (int i = 0; i < mat.getNumElements(); i++) {
				dataf[i] = (float) mat.get(i);
			}
		} else if (type == UBType.Int) {
			for (int i = 0; i < mat.getNumElements(); i++) {
				datai[i] = (int) mat.get(i);

			}
		}
	
	}

	public void update(int[] data) {
		if (this.type != UBType.Int) {
			throw new RuntimeException("Invalid type");
		}
		if (data.length != this.size) {
			throw new RuntimeException("Invalid size");
		}
		this.datai = data;
	
	}

	public void update(float[] data) {
		if (this.type != UBType.Float) {
			throw new RuntimeException("Invalid type");
		}
		if (data.length != this.size) {
			throw new RuntimeException("Invalid size");
		}
		this.dataf = data;
	}

	public void create() {
		super.create();
		bufferID = GL46.glGenBuffers();
		GL46.glBindBuffer(GL46.GL_UNIFORM_BUFFER, bufferID);
		GL46.glBufferData(GL46.GL_UNIFORM_BUFFER, this.size*4, GL46.GL_STATIC_DRAW); // allocate 152 bytes of memory
		GL46.glBindBuffer(GL46.GL_UNIFORM_BUFFER, 0);

		this.upload();

	}

	public void upload() {

		GL46.glBindBuffer(GL46.GL_UNIFORM_BUFFER, bufferID);
		if (type == UBType.Float) {
			GL46.glBufferSubData(GL46.GL_UNIFORM_BUFFER, 0, dataf);
		} else {
			GL46.glBufferSubData(GL46.GL_UNIFORM_BUFFER, 0, datai);
		}
		GL46.glBindBuffer(GL46.GL_UNIFORM_BUFFER, 0);
		checkForGLError("UniformBuffer");

	}

	public void linkToShader(int programID) {
		
		int blockIndex = GL46.glGetUniformBlockIndex(programID, name);
		GL46.glUniformBlockBinding(programID, blockIndex, bindingPoint);
		checkForGLError("UniformBuffer");
		GL46.glBindBufferBase(GL46.GL_UNIFORM_BUFFER, bindingPoint, bufferID);
		checkForGLError("UniformBuffer");
	}

	public void destroy() {
		GL46.glDeleteBuffers(bufferID);
		super.destroy();
	}

	public static void checkForGLError(String string) {

		int errorFlag = GL46.glGetError();
		if (errorFlag != GL46.GL_NO_ERROR) {
			System.out.println("OpenGL Error: " + errorFlag + " " + GL46.glGetString(errorFlag) + " " + string);
			// print stack trace
			throw new RuntimeException();
		}
	}

}
