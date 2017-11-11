package slaynash.sgengine.world3d.weapons;

import slaynash.sgengine.entities.Entity;

public abstract class WeaponEntity extends Entity {

	private boolean picked = false;

	public WeaponEntity(String id) {
		super(id);
	}
	
	public abstract void renderHand();
	public abstract void renderHandVR(int eye);
	
	public void onPicked() {
		picked = true;
	}
	
	public void onDropped() {
		picked = false;
	}

	public boolean isPicked() {
		return picked;
	}

}
