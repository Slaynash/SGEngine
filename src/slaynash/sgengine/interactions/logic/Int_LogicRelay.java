package slaynash.sgengine.interactions.logic;

import java.util.List;

import slaynash.sgengine.entities.Entity;
import slaynash.sgengine.world3d.entities.InteractableObject;
import slaynash.sgengine.world3d.entities.Interaction;

public class Int_LogicRelay extends Entity implements InteractableObject {
	
	private List<Interaction> interactions;

	public Int_LogicRelay(String id, List<Interaction> interactions) {
		super(id);
		this.interactions = interactions;
	}

	@Override
	public void onInput(String command, String... args) {
		if(command.equals("trigger")) {
			for(Interaction i:interactions) {
				i.trigger();
			}
		}
	}

	@Override
	public void addInteractions(List<Interaction> interactions) {
		this.interactions.addAll(interactions);
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
	public void onAddedToWorldManager() {}

}
