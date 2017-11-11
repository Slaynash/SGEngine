package slaynash.sgengine.interactions;

import java.util.HashMap;
import java.util.Map;

import slaynash.sgengine.LogSystem;
import slaynash.sgengine.interactions.logic.Int_Console;
import slaynash.sgengine.world3d.entities.InteractableObject;
import slaynash.sgengine.world3d.entities.Interaction;

public class InteractionsManager {
	
	private static Map<String, InteractableObject> interactableObjects = new HashMap<String, InteractableObject>();
	
	static {
		interactableObjects.put("console", new Int_Console());
	}

	public static void trigger(Interaction interaction) {
		synchronized(interactableObjects) {
			InteractableObject obj = interactableObjects.get(interaction.getTargetName());
			if(obj == null) {
				LogSystem.err_println("[InteractionsManager] Unable to found interactable target "+interaction.getTargetName());
				return;
			}
			try {
				obj.onInput(interaction.getTargetAction(), interaction.getArgs());
			}catch(Exception e) {
				LogSystem.err_println("[InteractionsManager] Unable to run target interaction "+interaction.getTargetAction()+" of object "+interaction.getTargetName());
				e.printStackTrace(LogSystem.getErrStream());
			}
		}
	}
	
	public static void addInteractableObject(InteractableObject interactableObject) {
		synchronized (interactableObjects) {
			if(interactableObjects.get(interactableObject.getId()) != null) {
				LogSystem.err_println("[InteractionsManager] Unable to register entity "+interactableObject.getId()+": An entity with this name already exist.");
				return;
			}
			interactableObjects.put(interactableObject.getId(), interactableObject);
		}
	}
	
	public static void removeInteractableObject(InteractableObject interactableObject) {
		synchronized (interactableObjects) {
			interactableObjects.remove(interactableObject.getId());
		}
	}
	
}
