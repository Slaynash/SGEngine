package slaynash.sgengine.world3d.weapons;

import java.util.ArrayList;
import java.util.List;

import slaynash.sgengine.entities.EntityManager;

public class PlayerWeaponsManager {
	
	private static List<WeaponEntity> weapons = new ArrayList<WeaponEntity>();
	private static int currentWeapon = 0;
	
	public static void pickupWeapon(WeaponEntity weapon) {
		EntityManager.removeEntity(weapon.getId());
		weapons.add(weapon);
		weapon.onPicked();
	}
	
	public static void dropWeapon() {
		EntityManager.addEntity(weapons.get(currentWeapon));
		WeaponEntity weapon = weapons.remove(currentWeapon);
		if(currentWeapon > 0) currentWeapon--;
		weapon.onDropped();
	}
	
	public static void changeWeapon(int scroll) {
		if(scroll < 0) currentWeapon -= Math.floorMod(-scroll, weapons.size());
		if(scroll > 0) currentWeapon += Math.floorMod(scroll, weapons.size());
		if(currentWeapon < 0) currentWeapon += weapons.size();
		if(currentWeapon > 0) currentWeapon -= weapons.size();
		
	}
	
	public static void renderWeapon() {
		if(weapons.size() != 0) {
			weapons.get(currentWeapon).render();
		}
	}
	
	public static void renderWeaponVR(int eye) {
		if(weapons.size() != 0) {
			weapons.get(currentWeapon).renderVR(eye);
		}
	}
	
	public static void update() {
		if(weapons.size() != 0) {
			weapons.get(currentWeapon).update();
		}
	}
	
}
