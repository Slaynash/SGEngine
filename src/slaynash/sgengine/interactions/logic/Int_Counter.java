package slaynash.sgengine.interactions.logic;

import java.util.List;

import slaynash.sgengine.entities.Entity;
import slaynash.sgengine.world3d.entities.InteractableObject;
import slaynash.sgengine.world3d.entities.Interaction;

public class Int_Counter extends Entity implements InteractableObject {
	
	private List<Interaction> interactions;
	private int value = 0;

	public Int_Counter(String id, List<Interaction> interactions) {
		super(id);
		this.interactions = interactions;
	}

	@Override
	public void onInput(String command, String... args) {
		if(command.equals("add")) {
			value += Integer.parseInt(args[0]);
			for(Interaction i:interactions) {
				if(i.getName().equals("onValue"+value))
				i.trigger();
			}
		}
		
		if(command.equals("reset")) {
			value = 0;
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
