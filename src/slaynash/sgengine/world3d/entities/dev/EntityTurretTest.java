package slaynash.sgengine.world3d.entities.dev;

import java.io.File;

import org.lwjgl.util.vector.Vector3f;

import slaynash.sgengine.Configuration;
import slaynash.sgengine.models.Renderable3dModel;
import slaynash.sgengine.objloader.ObjLoader;
import slaynash.sgengine.world3d.loader.Entity;

public class EntityTurretTest extends Entity{
	
	public static Renderable3dModel model;
	private Vector3f pos;
	private Vector3f dir;
	
	public EntityTurretTest(Vector3f pos, Vector3f dir) {
		if(model == null) model = ObjLoader.loadRenderable3dModel(new File(Configuration.getAbsoluteInstallPath()+"res/entities/turrettest.obj"), "res/entities/turrettest_diffuse.png", null, "res/entities/turrettest_specular.png");
		this.pos = pos;
		this.dir = dir;
	}

	@Override
	public void update() {
		Vector3f targetPos = Configuration.getPlayerCharacter().getViewPosition();
		Vector3f localTargetPos = Vector3f.sub(targetPos, pos, new Vector3f());
		float angle = Vector3f.angle(dir, localTargetPos);
		float dist = (float) Math.sqrt(
				(targetPos.x-pos.x)*(targetPos.x-pos.x)+
				(targetPos.z-pos.z)*(targetPos.z-pos.z)
		);
		float aimDist = Vector3f.sub(pos, new Vector3f(pos.x+dir.x*dist, pos.y+dir.y*dist, pos.z+dir.z*dist), new Vector3f()).length();
		//slowly focus
		//if dist<x lock -> emmit sounds and change laser color
		//after X seconds shoot
		//unlock
	}

	@Override
	public void render() {
		//translate
		//render
		//render laser
	}

	@Override
	public void renderVR() {
		
	}
	
}
