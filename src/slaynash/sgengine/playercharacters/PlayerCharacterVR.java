package slaynash.sgengine.playercharacters;

import org.lwjgl.util.vector.Vector3f;

import slaynash.sgengine.utils.VRUtils;

public class PlayerCharacterVR extends PlayerCharacter {
	
	public PlayerCharacterVR() {
		this.isUsingPitchRoll = false;
	}
	
	@Override
	public void update() {
		Vector3f p = VRUtils.getPosition();
		position.x = p.x;
		position.y = VRUtils.getRawPosition().y;
		position.z = p.z;
		viewPosition.x = p.x;
		viewPosition.y = p.y;
		viewPosition.z = p.z;
		
		Vector3f vd = VRUtils.getForward();
		
		viewDirection.x = vd.x;
		viewDirection.y = vd.y;
		viewDirection.z = vd.z;
		
		viewMatrix = VRUtils.getViewMatrix(VRUtils.EYE_CENTER);
		
		//Matrix4f tm = Matrix4f.translate(envPos, new Matrix4f(), new Matrix4f());
		//LogSystem.out_println("ENVPOS: "+tm);
		
		//viewMatrix = Matrix4f.mul(tm, VRUtils.getViewMatrix(VRUtils.EYE_CENTER), new Matrix4f());
	}
	
	@Override
	public void warp(Vector3f warp) {
		Vector3f envPos = new Vector3f();
		Vector3f p = VRUtils.getRawPosition();
		envPos.x = warp.x-p.x;
		envPos.y = warp.y;
		envPos.z = warp.z-p.z;
		
		VRUtils.setEnvPosition(envPos);
		
		update();
	}
	
}
