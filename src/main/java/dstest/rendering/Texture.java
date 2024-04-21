package dstest.rendering;

import java.nio.ByteBuffer;

import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL46;
import org.lwjgl.stb.STBImage;

import static org.lwjgl.opengl.GL11.*;


public class Texture extends RenderResource{

	public int width;
	public int height;
	public int channels;
	public int textureID;
	
	
	public String file;
	
	public Texture(String file) {
		
		this.file = file;

    }
		
	
	public void create() {
		super.create();
		int[] width = new int[1], height = new int[1], nrChannels = new int[1];
		ByteBuffer data=STBImage.stbi_load(file, width, height, nrChannels, 0);

		if(data == null)
			throw new RuntimeException("Failed to load texture. "+file);
		
		this.width = width[0];
		this.height = height[0];
		this.channels = nrChannels[0];
		
		textureID = glGenTextures();
		glBindTexture(GL20.GL_TEXTURE_2D, textureID);
		
		int format = 0;
		if (channels == 3)
			format = GL_RGB;
		else if (channels == 4)
			format = GL_RGBA;
		else
			throw new RuntimeException("Unsupported number of channels.");
		
		
		glTexImage2D(GL20.GL_TEXTURE_2D, 0, format, width[0], height[0], 0, format, GL20.GL_UNSIGNED_BYTE, data);
			
		
		STBImage.stbi_image_free(data);
			
		
		
	}
	
	public void bind() {
		
		GL46.glActiveTexture(GL46.GL_TEXTURE0);
		glBindTexture(GL_TEXTURE_2D, textureID);
		
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		
		//loop texture
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
	}
	
	public void unbind() {
		glBindTexture(GL_TEXTURE_2D, 0);
	}
	
	public void destroy() {
		
		glDeleteTextures(textureID);
		checkForGLError("Texture destroy");
		super.destroy();
	}


	private void checkForGLError(String string) {
		int errorFlag = GL46.glGetError();
		if (errorFlag != GL46.GL_NO_ERROR) {
			System.out.println("OpenGL Error: " + errorFlag + " " + GL46.glGetString(errorFlag) + " " + string);
			// print stack trace
			throw new RuntimeException();
		}
	}
}
