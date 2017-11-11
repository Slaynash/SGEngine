package slaynash.sgengine.entities;

import java.util.Map;

public interface EntityDef {

	public String getEntityName();
	public abstract Entity createEntity(String id, float posX, float posY, float posZ, float angX, float angY, float angZ, Map<String, String> parameters);
	
}
