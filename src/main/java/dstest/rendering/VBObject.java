package dstest.rendering;

import org.ejml.dense.row.decompose.UtilDecompositons_CDRM;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL46;


import static org.lwjgl.opengl.GL46.*;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.ArrayUtils;
import java.io.BufferedReader;

import java.io.FileReader;

import java.io.IOException;

import java.nio.FloatBuffer;

import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;

import static org.lwjgl.opengl.GL11.*;

import static org.lwjgl.opengl.GL15.*;

import static org.lwjgl.opengl.GL20.*;

import static org.lwjgl.opengl.GL30.*;

public class VBObject extends RenderResource {

	public int vaoId;
	public int vbo;
	public int ibo;
	public int uvbo;
	public int vertexCount;
	public String objFile;
	
	public float[] vertices;
	public float[] texCoords;
	public int[] indices;

	public VBObject(float[] vertices, float[] texCoords, int[] indices) {
		this.vertices = vertices;
		this.texCoords = texCoords;
		this.indices = indices;
		


	}

	public static void checkForGLError(String string) {

		int errorFlag = GL46.glGetError();
		if (errorFlag != GL46.GL_NO_ERROR) {
			System.out.println("OpenGL Error: " + errorFlag + " " + GL46.glGetString(errorFlag) + " " + string);
			// print stack trace
			throw new RuntimeException();
		}
	}

	
	
	public void create() {

	
		super.create();
		
		float[] vertices=this.vertices;
		float[] texCoords=this.texCoords;
		int[]  indices=this.indices;

		
		FloatBuffer verticesBuffer = BufferUtils.createFloatBuffer(vertices.length);
		verticesBuffer.put(vertices);
		verticesBuffer.flip();

		FloatBuffer texCoordsBuffer = BufferUtils.createFloatBuffer(texCoords.length);
		texCoordsBuffer.put(texCoords);
		texCoordsBuffer.flip();

		IntBuffer indicesBuffer = BufferUtils.createIntBuffer(indices.length);
		indicesBuffer.put(indices);
		indicesBuffer.flip();

		vaoId = glGenVertexArrays();
		glBindVertexArray(vaoId);
		checkForGLError("VBObject");

		vbo = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, vbo);
		glBufferData(GL_ARRAY_BUFFER, verticesBuffer, GL_STATIC_DRAW);
		glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
		checkForGLError("VBObject");

		uvbo = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, uvbo);
		glBufferData(GL_ARRAY_BUFFER, texCoordsBuffer, GL_STATIC_DRAW);
		glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);

		ibo = glGenBuffers();
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo);
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW);
		checkForGLError("VBObject");

		glBindBuffer(GL_ARRAY_BUFFER, 0);
		glBindVertexArray(0);
		checkForGLError("VBObject");

		vertexCount = indices.length;
		checkForGLError("VBObject");
		
		//delete buffers
		verticesBuffer.clear();
		texCoordsBuffer.clear();
		indicesBuffer.clear();
		

	}

	public void draw() {
		glBindVertexArray(vaoId);
		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);
		glDrawElements(GL_TRIANGLES, vertexCount, GL_UNSIGNED_INT, 0);
		glDisableVertexAttribArray(0);
		glDisableVertexAttribArray(1);
		glBindVertexArray(0);
		
	}

	public void destroy() {
		
		glDeleteBuffers(vbo);
		checkForGLError("VBObject");



		glDeleteBuffers(uvbo);
		checkForGLError("VBObject");
		
		glDeleteBuffers(ibo);
		checkForGLError("VBObject");

		glDeleteVertexArrays(vaoId);
		checkForGLError("VBObject");

		checkForGLError("VBObject destroy");
		
		
		super.destroy();

	}



}
