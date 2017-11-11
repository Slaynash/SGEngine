package slaynash.sgengine.world3d.entities.dev;

import org.lwjgl.util.vector.Vector3f;

import slaynash.sgengine.Configuration;
import slaynash.sgengine.entities.Entity;
import slaynash.sgengine.models.Renderable3dModel;
import slaynash.sgengine.models.utils.ModelManager;

public class EntityTurretTest extends Entity {
	
	public static Renderable3dModel model;
	private Vector3f dir;
	
	public EntityTurretTest(String id, Vector3f pos, Vector3f dir) {
		super(id);
		if(model == null) model = ModelManager.loadObj("res/entities/turrettest.obj", "res/entities/turrettest_diffuse.png", null, "res/entities/turrettest_specular.png");
		setPosition(pos.x, pos.y, pos.z);
		this.dir = dir;
	}

	@SuppressWarnings("unused")
	@Override
	public void update() {
		Vector3f targetPos = Configuration.getPlayerCharacter().getViewPosition();
		Vector3f localTargetPos = Vector3f.sub(targetPos, new Vector3f(getPosX(), getPosY(), getPosZ()), new Vector3f());
		float angle = Vector3f.angle(dir, localTargetPos);
		float dist = (float) Math.sqrt(
				(targetPos.x-getPosX())*(targetPos.x-getPosX())+
				(targetPos.z-getPosY())*(targetPos.z-getPosZ())
		);
		float aimDist = Vector3f.sub(new Vector3f(getPosX(), getPosY(), getPosZ()), new Vector3f(getPosX()+dir.x*dist, getPosY()+dir.y*dist, getPosZ()+dir.z*dist), new Vector3f()).length();
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
	public void renderVR(int eye) {
		
	}

	@Override
	public void destroy() {
		
	}

	@Override
	public void onAddedToWorldManager() {
		
	}
	
}
