package slaynash.sgengine.world3d.entities;

import java.util.List;

import org.lwjgl.util.vector.Vector3f;

import slaynash.sgengine.Configuration;
import slaynash.sgengine.entities.Entity;

public class TriggerBrushPlayer extends Entity implements InteractableObject {
	
	private static boolean playerInAABB = false;
	Vector3f minAABB = new Vector3f();
	Vector3f maxAABB = new Vector3f();
	
	private List<Interaction> interactions;

	public TriggerBrushPlayer(String id, Vector3f minAABB, Vector3f maxAABB, List<Interaction> interactions) {
		super(id);
		this.interactions = interactions;
		this.minAABB = minAABB;
		this.maxAABB = maxAABB;
	}
	
	@Override
	public void onInput(String command, String... args) {}

	@Override
	public void update() {
		if(isPlayerInAABB()) {
			if(!playerInAABB) {
				for(Interaction interaction:interactions) {
					if(interaction.getName().equals("onPlayerEnter")) {
						interaction.trigger();
					}
				}
			}
			playerInAABB = true;
		}
		if(!isPlayerInAABB()) {
			if(playerInAABB) {
				for(Interaction interaction:interactions) {
					if(interaction.getName().equals("onPlayerExit")) {
						interaction.trigger();
					}
				}
			}
			playerInAABB = false;
		}
	}

	private boolean isPlayerInAABB() {
		Vector3f playerPos = Configuration.getPlayerCharacter().getPosition();
		return (minAABB.x < playerPos.x && playerPos.x < maxAABB.x && minAABB.y < playerPos.y && playerPos.y < maxAABB.y && minAABB.z < playerPos.z && playerPos.z < maxAABB.z);
	}

	@Override
	public void render() {
		
	}

	@Override
	public void renderVR(int eye) {
		
	}

	@Override
	public void destroy() {}

	@Override
	public void onAddedToWorldManager() {}

	@Override
	public void addInteractions(List<Interaction> interactions) {
		this.interactions.addAll(interactions);
	}
	
}
