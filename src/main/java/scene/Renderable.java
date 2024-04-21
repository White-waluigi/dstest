package scene;

import java.util.ArrayList;
import java.util.List;

import dstest.rendering.DisplayCommand;
import dstest.rendering.OBJLoader;
import dstest.rendering.OBJLoader.WFObject;
import dstest.rendering.Renderer;
import dstest.rendering.Shader;
import dstest.rendering.Texture;
import dstest.rendering.UniformBuffer;
import dstest.rendering.VBObject;
import vectors.Vec4;

public class Renderable extends SceneElement{
	WFObject wfObj;
	public Renderable(SceneElement parent,OBJLoader.WFObject wfObj) {
		super(parent, false);
		
		this.wfObj = wfObj;
		
	}

	@Override
	public void update(double deltaTime) {
		
	}

	public DisplayCommand getDisplayCommand() {
		List<Texture> textures = new ArrayList<Texture>();
		try {
		if(wfObj.mtl != null) {
			textures.add(new Texture(wfObj.mtl.texture));
			}
	} catch (Exception e) {
		System.out.println("Texture not found");
	}
		Shader program = new Shader("res/vert.glsl", "res/frag.glsl");
		
		VBObject mesh = new VBObject(wfObj.vertices, wfObj.uvs, wfObj.indices);
		
		UniformBuffer worldViewProj = this.renderer.getWorldViewProjBuf();
		List<UniformBuffer> uniformBuffers = new ArrayList<UniformBuffer>();
		uniformBuffers.add(worldViewProj);
		//UniformBuffer b=new UniformBuffer("test", new float[] {wfObj.idCoolor[0],wfObj.idCoolor[1],wfObj.idCoolor[2],1});
		//uniformBuffers.add(b);
		
		//program.setUniform("idColor", new Vec4(wfObj.idCoolor[0],wfObj.idCoolor[1],wfObj.idCoolor[2],1));
		

		
		DisplayCommand dc = new DisplayCommand(textures,program,mesh,uniformBuffers);

		wfObj = null;
		return dc;
	}


	@Override
	public void initialize(Renderer renderer) {
		super.initialize(renderer);
		
	}
	
}
