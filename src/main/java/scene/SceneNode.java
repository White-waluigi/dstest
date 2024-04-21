package scene;

import java.util.ArrayList;
import java.util.List;

import dstest.rendering.DisplayCommand;
import dstest.rendering.Renderer;

public class SceneNode extends SceneElement {
	public List<SceneElement> children;

	public SceneNode(SceneElement parent, boolean isRoot) {
		super(parent, isRoot);
		children = new ArrayList<SceneElement>();

	}
	@Override
	public void addChild(SceneElement child) {
		//check if child already exists
		if (children.contains(child))
			throw new IllegalArgumentException("Child already exists");
		
		children.add(child);
	}

	public void removeChild(SceneElement child) {
		if (children.contains(child))
			children.remove(child);
		else
			throw new IllegalArgumentException("Child not found");
	}
	
	@Override
	public void update(double deltaTime) {
		
		for (SceneElement child : children) {
			child.update(deltaTime);
		}
	}
	
	@Override
	public void initialize(Renderer renderer) {
	
		super.initialize(renderer);
		for (SceneElement child : children) {
			child.initialize(renderer);
		}
		
	}

	@Override
	public DisplayCommand getDisplayCommand() {
		return null;
	}

	
	


}