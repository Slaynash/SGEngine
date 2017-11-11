package slaynash.sgengine.interactions.logic;

import java.util.List;

import slaynash.sgengine.LogSystem;
import slaynash.sgengine.world3d.entities.InteractableObject;
import slaynash.sgengine.world3d.entities.Interaction;

public class Int_Console implements InteractableObject {
	
	@Override
	public void onInput(String command, String... args) {
		if(command.equals("output")) {
			LogSystem.out_println("[Game output] "+args[0].toString());
		}
	}

	@Override
	public String getId() {
		return "console";
	}

	@Override
	public void addInteractions(List<Interaction> interactions) {}

}
