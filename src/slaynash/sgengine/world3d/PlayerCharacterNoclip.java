package slaynash.sgengine.world3d;

import org.lwjgl.input.Mouse;

import slaynash.sgengine.Configuration;
import slaynash.sgengine.inputs.KeyboardControlManager;
import slaynash.sgengine.utils.DisplayManager;

public class PlayerCharacterNoclip extends PlayerCharacter{
	
	private static final float DEGREE_TO_RADIANS = (float) (Math.PI/180f);
	private static final float PI = 3.14159f;
	
	private static final float WALK_SPEED = 0.02f;
	private static final float RUN_SPEED = 0.10f;
	
	private static float forward, left, up;
	
	public PlayerCharacterNoclip() {
		super();
	}
	
	@Override
	public void update() {
		updateAimDir();
		checkInputs();
		float dx = (float) (forward * Math.sin(yaw) + left * Math.cos(yaw));
		float dz = (float) (forward * Math.cos(yaw) - left * Math.sin(yaw));
		
		viewDirection.x = (float) -(Math.sin(yaw) * Math.cos(pitch));
		viewDirection.z = (float) -(Math.cos(yaw) * Math.cos(pitch));
		viewDirection.y = (float) Math.sin(pitch);
		
		position.x += dx*DisplayManager.getFrameTime();
		position.z += dz*DisplayManager.getFrameTime();
		position.y += up*DisplayManager.getFrameTime();
		viewPosition.x += dx*DisplayManager.getFrameTime();
		viewPosition.z += dz*DisplayManager.getFrameTime();
		viewPosition.y += up*DisplayManager.getFrameTime();
	}
	
	private void updateAimDir() {
		yaw -= (Mouse.getDX())*0.01f*Configuration.getMouseSensibility();
		pitch += (Mouse.getDY())*0.01f*Configuration.getMouseSensibility();
		
		if (yaw >= PI*2f){
			yaw -= PI*2f;
		}
		else if (yaw < 0f){
			yaw += PI*2f;
		}
		
		if (pitch > PI*0.5f-0.0001f)
			pitch = PI/2-0.0001f;
		else if (pitch < -PI*0.5f+0.0001f)
			pitch = -PI/2+0.0001f;
		
		rotation.x = pitch/DEGREE_TO_RADIANS;
		rotation.y = yaw/DEGREE_TO_RADIANS;
		rotation.z = 0;
		
	}
	
	private void checkInputs(){
		if(KeyboardControlManager.isPressed("run")){
			if(KeyboardControlManager.isPressed("forward"))
				forward = -RUN_SPEED;
			else if(KeyboardControlManager.isPressed("backward"))
				forward = RUN_SPEED;
			else forward = 0;
			
			if(KeyboardControlManager.isPressed("right"))
				left = RUN_SPEED;
			else if(KeyboardControlManager.isPressed("left"))
				left = -RUN_SPEED;
			else left = 0;
			
			if(KeyboardControlManager.isPressed("jump"))
				up = RUN_SPEED;
			else if(KeyboardControlManager.isPressed("crouch"))
				up = -RUN_SPEED;
			else up = 0;
		}
		else{
			if(KeyboardControlManager.isPressed("forward"))
				forward = -WALK_SPEED;
			else if(KeyboardControlManager.isPressed("backward"))
				forward = WALK_SPEED;
			else forward = 0;
			
			if(KeyboardControlManager.isPressed("right"))
				left = WALK_SPEED;
			else if(KeyboardControlManager.isPressed("left"))
				left = -WALK_SPEED;
			else left = 0;
			
			if(KeyboardControlManager.isPressed("jump"))
				up = WALK_SPEED;
			else if(KeyboardControlManager.isPressed("crouch"))
				up = -WALK_SPEED;
			else up = 0;
		}
	}
}
