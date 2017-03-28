package slaynash.world3d.loader;

import org.lwjgl.util.vector.Vector3f;

public abstract class Entity {
	
	private Vector3f position;
	private Vector3f angle;
	
	public Entity(){
		position = new Vector3f();
		angle = new Vector3f();
	}
	
	public abstract void update();
	
	public abstract void render();

	public Vector3f getPosition() {
		return position;
	}

	public void setPosition(Vector3f position) {
		this.position = position;
	}

	public Vector3f getAngle() {
		return angle;
	}

	public void setAngle(Vector3f angle) {
		this.angle = angle;
	}
	
}
