package slaynash.sgengine.interactions.logic;

import java.util.List;

import slaynash.sgengine.audio.AudioManager;
import slaynash.sgengine.audio.AudioSource;
import slaynash.sgengine.entities.Entity;
import slaynash.sgengine.world3d.entities.InteractableObject;
import slaynash.sgengine.world3d.entities.Interaction;

public class Int_AmbiantSound extends Entity implements InteractableObject {

	private String soundPath;
	private AudioSource source;
	
	

	public Int_AmbiantSound(String id, String soundPath) {
		super(id);
		this.soundPath = soundPath;
	}

	@Override
	public void onInput(String command, String... args) {
		if(command.equals("playBackgroundMusic")) {
			if(source == null) {
				source.stop();
				source.destroy();
			}
			source = AudioManager.createBackgroundMusic(soundPath, true);
			source.play();
		}
	}

	@Override
	public void addInteractions(List<Interaction> interactions) {}

	@Override
	public void update() {}

	@Override
	public void render() {}

	@Override
	public void renderVR(int eye) {}

	@Override
	public void destroy() {}

	@Override
	public void onAddedToWorldManager() {}
	
}
