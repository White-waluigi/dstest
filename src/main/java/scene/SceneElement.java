package scene;

import dstest.rendering.DisplayCommand;
import dstest.rendering.Renderer;

public abstract class SceneElement {

	private SceneElement parent;
	protected Renderer renderer;
	protected boolean isInitialized = false;
	
	@SuppressWarnings("unused")
	private SceneElement() {
		throw new AssertionError("This class should not be instantiated without a parent node.");
	}

	protected SceneElement(SceneElement parent,boolean isRoot) {
		if (parent == null && !isRoot) {
			throw new AssertionError("This class should not be instantiated without a parent node.");
		}
		this.parent = parent;
		if (!isRoot)
			parent.addChild(this);
	}
	
	

	public void addChild(SceneElement child) {
		throw new AssertionError("Not a composite node.");
	}
	public SceneElement getParent() {
		return parent;
	}
	
	//virtual methods
	
	public abstract void update(double deltaTime);
	
	public abstract DisplayCommand getDisplayCommand();

	public final void updateDisplayCommand() {
		if (this.renderer == null) {
			throw new AssertionError("Renderer not initialized");
		}
		DisplayCommand dc = this.getDisplayCommand();
		if (dc != null) {
			this.renderer.registerDisplayCommand(this, dc);
		}
	}

	public void initialize(Renderer renderer) {
		if (isInitialized) {
			throw new AssertionError("Already initialized.");
		}
		isInitialized = true;
		
        this.renderer = renderer;
        updateDisplayCommand();
    }


	
}
