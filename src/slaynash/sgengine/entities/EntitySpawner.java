package slaynash.sgengine.entities;

import java.util.HashMap;
import java.util.List;

import slaynash.sgengine.world3d.entities.InteractableObject;
import slaynash.sgengine.world3d.entities.Interaction;

public class EntitySpawner extends Entity implements InteractableObject {

	private String createEntity;

	public EntitySpawner(String id, float posX, float posY, float posZ, String createEntity) {
		super(id);
		setPosition(posX, posY, posZ);
		this.createEntity = createEntity;
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

	@Override
	public void addInteractions(List<Interaction> interactions) {}

	@Override
	public void onInput(String command, String... args) {
		EntityManager.createEntity(createEntity, getPosX(), getPosY(), getPosZ(), 0, 0, 0, new HashMap<String, String>());
	}

}
