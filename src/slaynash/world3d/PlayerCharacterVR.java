package slaynash.world3d;

import org.lwjgl.util.vector.Vector3f;

import slaynash.opengl.utils.VRUtils;

public class PlayerCharacterVR extends PlayerCharacter {
	
	Vector3f envPos = new Vector3f(0,0,0);
	
	public PlayerCharacterVR() {
		super();
		this.isUsingPitchRoll = false;
	}
	
	@Override
	public void update() {
		Vector3f p = VRUtils.getPosition();
		position.x = envPos.x+p.x;
		position.y = envPos.y;
		position.z = envPos.z+p.z;
		viewPosition.x = envPos.x+p.x;
		viewPosition.y = envPos.y+p.y;
		viewPosition.z = envPos.z+p.z;
		
		Vector3f vd = VRUtils.getForward();
		
		viewDirection.x = vd.x;
		viewDirection.y = vd.y;
		viewDirection.z = vd.z;
		
		viewMatrix = VRUtils.getViewMatrix(VRUtils.EYE_CENTER);
	}
	
	@Override
	public void warp(Vector3f warp) {
		Vector3f p = VRUtils.getPosition();
		envPos.x = warp.x-p.x;
		envPos.y = warp.y;
		envPos.z = warp.z-p.z;
	}
	
}
