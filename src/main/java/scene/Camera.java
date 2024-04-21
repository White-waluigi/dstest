package scene;

import java.util.HashMap;

import org.ejml.simple.SimpleMatrix;

import dstest.rendering.DisplayCommand;
import dstest.rendering.Renderer;
import vectors.Mat4;
import vectors.Vec3;
import vectors.Vec4;

public class Camera extends SceneElement{
	public Vec3 pos;
	public float rotX;
	public float rotY;
	public float fov;
	public float aspect;
	public float near;
	public float far;
	
	
	
	
	public Camera(SceneElement parent, Vec3 pos, float rotX, float rotY, float fov, float aspect, float near,
			float far) {
		super(parent, false);
		this.pos = pos;
		this.rotX = rotX;
		this.rotY = rotY;
		this.fov = fov;
		this.aspect = aspect;
		this.near = near;
		this.far = far;
	}
	
	public Mat4 getViewMatrix() {
		return null;
		
	}

	public void handleInput(double deltaTime) {
		
		HashMap<String, Boolean> keys = renderer.getKeys();
		float moveSpeed = (float) (-40.01f*(keys.get("shift")?3.5f:1)*deltaTime);
		float rotSpeed = (float) (2f*deltaTime);

		Vec3 forward= new Vec3((float)Math.sin(rotY)*moveSpeed,0,(float)Math.cos(rotY)*moveSpeed);
		Vec3 right= new Vec3((float)Math.sin(rotY+(Math.PI/2))*moveSpeed,0,(float)Math.cos(rotY+(Math.PI/2))*moveSpeed);
		Vec3 up= new Vec3(0,moveSpeed,0);
		
		if (keys.get("w")) {
			pos = pos.add(forward);
		}
		if (keys.get("s")) {
			pos = pos.subtract(forward);
		}
		if (keys.get("a")) {
			pos = pos.add(right);
		}
		
			
		if (keys.get("d")) {
			pos = pos.subtract(right);
		}	
		if (keys.get("e")) {
			pos = pos.subtract(up);
		}
		if (keys.get("q")) {
			pos = pos.add(up);
		}
		if (keys.get("down")) {
			rotX -= rotSpeed;
		}
		if (keys.get("up")) {
			rotX += rotSpeed;
		}
		if (keys.get("left")) {
			rotY += rotSpeed;
		}
		if (keys.get("right")) {
			rotY -= rotSpeed;
		}
		
		
		
		
		

	
		
		
		

		
	}
	@Override
	public void update(double deltaTime) {
		handleInput(deltaTime);

		Mat4 rot=Mat4.ID;
		Mat4 rotx=Mat4.multiply(Mat4.rotate(this.rotY,0,1,0),rot);
		rot=Mat4.multiply(rotx,Mat4.rotate(this.rotX,1,0,0));
		Mat4 forward= Mat4.translate(this.pos.x,this.pos.y,this.pos.z);

		
		Mat4 view=Mat4.multiply(forward,rot);
		

		
		Mat4 proj=Mat4.perspective(fov,aspect, 0.1f, 10000f);
		Mat4 mvp=Mat4.multiply(proj, 	view.inverse(), 	Mat4.ID);


		this.renderer.setWorldViewProj(mvp);
	}

	@Override
	public DisplayCommand getDisplayCommand() {
		return null;
	}

	@Override
	public void initialize(Renderer renderer) {
		super.initialize(renderer);
		
        renderer.registerCamera(this);		
	}

}
