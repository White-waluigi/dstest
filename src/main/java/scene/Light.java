package scene;

import org.ejml.simple.SimpleMatrix;

public class Light {

	private SimpleMatrix position;
	private SimpleMatrix color;
	
	private float constAttenuation;
	private float linearAttenuation;
	private float quadraticAttenuation;
	
	public Light(SimpleMatrix position, SimpleMatrix color, float constAttenuation, float linearAttenuation,
			float quadraticAttenuation) {
		this.position = position;
		this.color = color;
		this.constAttenuation = constAttenuation;
		this.linearAttenuation = linearAttenuation;
		this.quadraticAttenuation = quadraticAttenuation;
	}
	
	public SimpleMatrix getPosition() {
		return position;
	}
	
	
	
}
