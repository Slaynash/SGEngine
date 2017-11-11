package slaynash.sgengine.interactions.logic;

import java.util.List;

import slaynash.sgengine.entities.Entity;
import slaynash.sgengine.utils.DisplayManager;
import slaynash.sgengine.world3d.entities.InteractableObject;
import slaynash.sgengine.world3d.entities.Interaction;

public class Int_Timer extends Entity implements InteractableObject {
	
	private List<Interaction> interactions;
	private int value = 0;
	private boolean running;

	public Int_Timer(String id, List<Interaction> interactions) {
		super(id);
		this.interactions = interactions;
	}

	@Override
	public void onInput(String command, String... args) {
		if(command.equals("start")) {
			if(!running) {
				for(Interaction i:interactions) if(i.getName().equals("onStart")) i.trigger();
				running = true;
				value = 0;
			}
		}
		if(command.equals("reset")) {
			for(Interaction i:interactions) if(i.getName().equals("onReset")) i.trigger();
			value = 0;
		}
	}

	@Override
	public void addInteractions(List<Interaction> interactions) {
		this.interactions.addAll(interactions);
	}

	@Override
	public void update() {
		if(running) {
			float lastvalue = value;
			value += DisplayManager.getFrameTimeSeconds();
			for(Interaction i:interactions) if(i.getName().startsWith("onTime")) {
				int t = Integer.parseInt(i.getName().substring(6));
				if(lastvalue < t && t < value) i.trigger();
			}
		}
	}

	@Override
	public void render() {}

	@Override
	public void renderVR(int eye) {}

	@Override
	public void destroy() {}

	@Override
	public void onAddedToWorldManager() {}
	
}

