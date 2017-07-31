package slaynash.sgengine.world3d;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

public abstract class PlayerCharacter {
	
	public static PlayerCharacter instance;
	
	protected Vector3f position = new Vector3f();
	protected Vector3f viewPosition = new Vector3f();
	protected Vector3f rotation = new Vector3f();
	protected Vector3f viewDirection = new Vector3f();

	protected float pitch, yaw;
	protected Matrix4f viewMatrix;
	protected boolean isUsingPitchRoll = true;
	
	public Vector3f getPosition(){
		return position;
	}
	
	public Vector3f getViewPosition(){
		return viewPosition;
	}

	public float getPitch() {
		return pitch;
	}
	
	public float getYaw() {
		return yaw;
	}

	public Vector3f getViewDirection() {
		return viewDirection;
	}

	public void warp(Vector3f warp) {
		position.x = warp.x;
		position.y = warp.y;
		position.z = warp.z;
		viewPosition.x = warp.x;
		viewPosition.y = warp.y;
		viewPosition.z = warp.z;
	}
	
	public PlayerCharacter(){
		instance = this;
	}
	
	public void start(){}

	public void update(){}

	public Matrix4f getViewMatrix() {
		return viewMatrix;
	}

	public boolean isUsingPitchRoll() {
		return isUsingPitchRoll;
	}
}
