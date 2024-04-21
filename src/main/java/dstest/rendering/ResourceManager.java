package dstest.rendering;

import java.util.ArrayList;

public class ResourceManager {
	public static ResourceManager singleton = new ResourceManager();
	
	public ArrayList<RenderResource> resources = new ArrayList<RenderResource>();
	
	
	public void addResource(RenderResource resource) {
		resources.add(resource);
	}
}
