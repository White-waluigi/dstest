package dstest.rendering;

import java.util.ArrayList;
import java.util.List;

public class DisplayCommand {
	List<Texture> textures;
	List<Shader> shaders;
	VBObject mesh;

	List<UniformBuffer> uniformBuffers;

	public DisplayCommand(List<Texture> textures, Shader shader, VBObject mesh, List<UniformBuffer> uniformBuffers) {

		this.textures = new ArrayList<Texture>();
		for (Texture tex : textures) {

			try {
				tex.create();
				this.textures.add(tex);

			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("Texture not found");
			}

		}

		shader.create();
		mesh.create();

		shaders = new ArrayList<Shader>();
		shaders.add(shader);

		this.mesh = mesh;
		for (UniformBuffer ub : uniformBuffers) {
			ub.linkToShader(shader.program);
		}
		this.uniformBuffers = uniformBuffers;

	}

	public void draw() {

		for (Shader shader : shaders) {
			shader.bind();
		}
		for (Texture tex : textures) {
			tex.bind();
		}
		mesh.draw();
		for (Texture tex : textures) {
			tex.unbind();
		}
		for (Shader shader : shaders) {
			shader.unbind();
		}

	}

	public void destroy() {
		for (Texture tex : textures) {
			if(tex.getState() == Texture.State.CREATED)
				tex.destroy();
		}
		for (Shader shader : shaders) {
			if(shader.getState() == Shader.State.CREATED)
				shader.destroy();
		}
		if(mesh.getState() == VBObject.State.CREATED) {
			mesh.destroy();
		}
		
		for (UniformBuffer ub : uniformBuffers) {
			if(ub.getState() == UniformBuffer.State.CREATED)
				ub.destroy();
		}
		
	}

}
