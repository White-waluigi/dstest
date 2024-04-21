package dstest.rendering;

public class RenderResource {

	public enum State {
		INITIALIZED, CREATED, DESTROYED
	}

	private State state = State.INITIALIZED;

	public RenderResource() {

		ResourceManager.singleton.addResource(this);
		state = State.INITIALIZED;
	}
	StackTraceElement[] stackTrace;

	public void create() {
		if (state != State.INITIALIZED) {

			throw new RuntimeException("Resource not initialized or already created");
			
		}

		state = State.CREATED;
	}


	public void destroy() {

		if (state != State.CREATED) {
			throw new RuntimeException("Resource not created");
		}
		

		state = State.DESTROYED;

	}

	public State getState() {
		return state;
	}

}
