package slaynash.sgengine.world3d.entities;

import java.util.List;

public interface InteractableObject {

	public String getId();
	public void addInteractions(List<Interaction> interactions);
	public void onInput(String command, String... args);
	
}
