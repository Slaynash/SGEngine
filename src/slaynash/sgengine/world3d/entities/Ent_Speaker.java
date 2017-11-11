package slaynash.sgengine.world3d.entities;

import java.util.List;

import slaynash.sgengine.audio.AudioManager;
import slaynash.sgengine.entities.Entity;

public class Ent_Speaker extends Entity implements InteractableObject {
	
	private String soundPath;

	public Ent_Speaker(String name, float posX, float posY, float posZ, String soundPath) {
		super(name);
		this.soundPath = soundPath;
		setPosition(posX, posY, posZ);
	}

	@Override
	public void onInput(String command, String... args) {
		if(command.equals("playSound")) {
			AudioManager.playSoundQuick(soundPath, getPosX(), getPosY(), getPosZ());
		}
	}

	@Override
	public void update() {}

	@Override
	public void render() {}

	@Override
	public void renderVR(int eye) {}

	@Override
	public void destroy() {}

	@Override
	public void onAddedToWorldManager() {
		//TODO preload sounds
	}

	@Override
	public void addInteractions(List<Interaction> interactions) {}
	
}
