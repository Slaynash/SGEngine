package slaynash.sgengine.playercharacters;

import javax.vecmath.Vector3f;

import com.bulletphysics.collision.dispatch.CollisionFlags;
import com.bulletphysics.collision.dispatch.PairCachingGhostObject;
import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.dynamics.ActionInterface;
import com.bulletphysics.linearmath.Transform;

import slaynash.sgengine.inputs.ControllersControlManager;
import slaynash.sgengine.utils.DisplayManager;
import slaynash.sgengine.world3d.CharacterController;
import slaynash.sgengine.world3d.CollisionManager3d;
import slaynash.sgengine.world3d.loader.WorldLoader;

public class PlayerCharacter3dController extends PlayerCharacter {
	
	private static final float DEGREE_TO_RADIANS = (float) (Math.PI/180f);
	private static final float PI = 3.14159f;
	
	private static final float WALK_SPEED = 0.02f;
	
	private static final float PLAYER_WIDTH = 0.40f;
	private static final float PLAYER_HEIGHT = 1.50f;
	private static final float PLAYER_STEP_HEIGHT = 0.4f;
	private static final float GRAVITY = 0.98f;
	
	private static float forward, left;
	
	public static CharacterController controller;
	private static PairCachingGhostObject entity;
	
	public PlayerCharacter3dController(){
		BoxShape playerShape = new BoxShape(new Vector3f(PLAYER_WIDTH, PLAYER_HEIGHT*.5f, PLAYER_STEP_HEIGHT));
		Transform t = new Transform(); t.setIdentity();
		t.origin.set(WorldLoader.getWorldSpawn().x, WorldLoader.getWorldSpawn().y, WorldLoader.getWorldSpawn().z);
		
		entity = new PairCachingGhostObject();
		entity.setCollisionShape(playerShape);
		entity.setCollisionFlags(CollisionFlags.CHARACTER_OBJECT);
		entity.setWorldTransform(t);
		controller = new CharacterController(entity, playerShape, PLAYER_STEP_HEIGHT);
		controller.setGravity(GRAVITY);
		controller.setJumpSpeed(1.2f);
		
		CollisionManager3d.registerThePlayer(getEntity(), getController());
	}
	
	@Override
	public void update(){
		updateAimDir();
		checkInputs();
		
		float dx = (float) (forward * Math.sin(yaw) + left * Math.cos(yaw));
		float dz = (float) (forward * Math.cos(yaw) - left * Math.sin(yaw));
		
		controller.setWalkDirection(new Vector3f(dx, 0f, dz));
		
		viewDirection.x = (float) -(Math.sin(yaw) * Math.cos(pitch));
		viewDirection.z = (float) -(Math.cos(yaw) * Math.cos(pitch));
		viewDirection.y = (float) Math.sin(pitch);
		
		Vector3f p = entity.getWorldTransform(new Transform()).origin;
		position.x = p.x;
		position.y = p.y-PLAYER_HEIGHT*.5f;
		position.z = p.z;
		viewPosition.x = p.x;
		viewPosition.y = p.y+PLAYER_HEIGHT*.5f;
		viewPosition.z = p.z;
	}
	
	private void updateAimDir() {
		yaw -= ControllersControlManager.getValue(0, "cameraX")*.005f*DisplayManager.getFrameTime();
		pitch += ControllersControlManager.getValue(0, "cameraY")*.005f*DisplayManager.getFrameTime();
		
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
	
	private void checkInputs() {
		forward = -ControllersControlManager.getValue(0, "forward")*WALK_SPEED;
		left = ControllersControlManager.getValue(0, "right")*WALK_SPEED;
		if(ControllersControlManager.isPressed(0, "jump"))
			controller.jump();
	}
	
	public ActionInterface getController() {
		return controller;
	}


	public PairCachingGhostObject getEntity() {
		return entity;
	}

	@Override
	public void warp(org.lwjgl.util.vector.Vector3f warp){
		position.x = warp.x;
		position.y = warp.y;
		position.z = warp.z;
		viewPosition.x = warp.x;
		viewPosition.y = warp.y+PLAYER_HEIGHT;
		viewPosition.z = warp.z;
		
		controller.warp(new Vector3f(warp.x, warp.y, warp.z));
	}
	
}
