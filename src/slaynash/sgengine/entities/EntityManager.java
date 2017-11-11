package slaynash.sgengine.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.lwjgl.util.vector.Vector3f;

import slaynash.sgengine.LogSystem;
import slaynash.sgengine.interactions.InteractionsManager;
import slaynash.sgengine.interactions.logic.Int_Counter;
import slaynash.sgengine.interactions.logic.Int_LogicRelay;
import slaynash.sgengine.world3d.entities.Ent_Box;
import slaynash.sgengine.world3d.entities.Ent_Speaker;
import slaynash.sgengine.world3d.entities.InteractableObject;
import slaynash.sgengine.world3d.entities.Interaction;
import slaynash.sgengine.world3d.entities.TriggerBrush;
import slaynash.sgengine.world3d.entities.TriggerBrushPlayer;
import slaynash.sgengine.world3d.loader.Ent_PointLight;

public class EntityManager {
	
	private static List<EntityDef> entityDefList = new ArrayList<EntityDef>();
	private static List<Entity> entityList = new ArrayList<Entity>();
	private static int nextEntityId = 0;
	private static boolean initialized = false;
	
	public static void init() {
		if(initialized) {
			LogSystem.out_println("[EntityManager] EntityManager.init() as been called but EntityManager is already loaded. Ignoring...");
			return;
		}
		initialized = true;
		
		registerEntityDef(new EntityDef() {
			@Override public String getEntityName() { return "pointlight"; }
			
			@Override
			public Entity createEntity(String id, float posX, float posY, float posZ, float angX, float angY, float angZ, Map<String, String> parameters) {
				float colorR = parameters.get("color") != null ? Float.parseFloat(parameters.get("color").split(" ")[0]) : 0;
				float colorG = parameters.get("color") != null ? Float.parseFloat(parameters.get("color").split(" ")[1]) : 0;
				float colorB = parameters.get("color") != null ? Float.parseFloat(parameters.get("color").split(" ")[2]) : 0;
				float attC = parameters.get("attenuation") != null ? Float.parseFloat(parameters.get("attenuation").split(" ")[0]) : 0;
				float attL = parameters.get("attenuation") != null ? Float.parseFloat(parameters.get("attenuation").split(" ")[1]) : 0;
				float attQ = parameters.get("attenuation") != null ? Float.parseFloat(parameters.get("attenuation").split(" ")[2]) : 0;
				return new Ent_PointLight(id, posX, posY, posZ, colorR, colorG, colorB, attC, attL, attQ);
			}
		});
		
		registerEntityDef(new EntityDef() {
			@Override public String getEntityName() { return "logic_relay"; }
			
			@Override
			public Entity createEntity(String id, float posX, float posY, float posZ, float angX, float angY, float angZ, Map<String, String> parameters) {
				return new Int_LogicRelay(id, new ArrayList<Interaction>());
			}
		});
		
		registerEntityDef(new EntityDef() {
			@Override public String getEntityName() { return "logic_counter"; }
			
			@Override
			public Entity createEntity(String id, float posX, float posY, float posZ, float angX, float angY, float angZ, Map<String, String> parameters) {
				return new Int_Counter(id, new ArrayList<Interaction>());
			}
		});
		
		registerEntityDef(new EntityDef() {
			@Override public String getEntityName() { return "speaker"; }
			
			@Override
			public Entity createEntity(String id, float posX, float posY, float posZ, float angX, float angY, float angZ, Map<String, String> parameters) {
				return new Ent_Speaker(id, posX, posY, posZ, parameters.get("soundPath"));
			}
		});
		
		registerEntityDef(new EntityDef() {
			@Override public String getEntityName() { return "triggerPlayer"; }
			
			@Override
			public Entity createEntity(String id, float posX, float posY, float posZ, float angX, float angY, float angZ, Map<String, String> parameters) {
				Vector3f minAABB = new Vector3f();
				Vector3f maxAABB = new Vector3f();
				if(parameters.get("minaabb") != null) {
					minAABB.x = Float.parseFloat(parameters.get("minaabb").split(" ")[0]);
					minAABB.y = Float.parseFloat(parameters.get("minaabb").split(" ")[1]);
					minAABB.z = Float.parseFloat(parameters.get("minaabb").split(" ")[2]);
				}
				if(parameters.get("maxaabb") != null) {
					maxAABB.x = Float.parseFloat(parameters.get("maxaabb").split(" ")[0]);
					maxAABB.y = Float.parseFloat(parameters.get("maxaabb").split(" ")[1]);
					maxAABB.z = Float.parseFloat(parameters.get("maxaabb").split(" ")[2]);
				}
				return new TriggerBrushPlayer(id, minAABB, maxAABB, new ArrayList<Interaction>());
			}
		});
		
		registerEntityDef(new EntityDef() {
			@Override public String getEntityName() { return "trigger"; }
			
			@Override
			public Entity createEntity(String id, float posX, float posY, float posZ, float angX, float angY, float angZ, Map<String, String> parameters) {
				Vector3f minAABB = new Vector3f();
				Vector3f maxAABB = new Vector3f();
				if(parameters.get("minaabb") != null) {
					minAABB.x = Float.parseFloat(parameters.get("minaabb").split(" ")[0]);
					minAABB.y = Float.parseFloat(parameters.get("minaabb").split(" ")[1]);
					minAABB.z = Float.parseFloat(parameters.get("minaabb").split(" ")[2]);
				}
				if(parameters.get("maxaabb") != null) {
					maxAABB.x = Float.parseFloat(parameters.get("maxaabb").split(" ")[0]);
					maxAABB.y = Float.parseFloat(parameters.get("maxaabb").split(" ")[1]);
					maxAABB.z = Float.parseFloat(parameters.get("maxaabb").split(" ")[2]);
				}
				return new TriggerBrush(id, minAABB, maxAABB, new ArrayList<Interaction>());
			}
		});
		
		registerEntityDef(new EntityDef() {
			@Override public String getEntityName() { return "ent_box"; }
			
			@Override
			public Entity createEntity(String id, float posX, float posY, float posZ, float angX, float angY, float angZ, Map<String, String> parameters) {
				return new Ent_Box(id, posX, posY, posZ);
			}
		});
		
		registerEntityDef(new EntityDef() {
			@Override public String getEntityName() { return "entitySpawner"; }
			
			@Override
			public Entity createEntity(String id, float posX, float posY, float posZ, float angX, float angY, float angZ, Map<String, String> parameters) {
				return new EntitySpawner(id, posX, posY, posZ, parameters.get("createentity"));
			}
		});
		
		registerEntityDef(new EntityDef() {
			@Override public String getEntityName() { return "button_ground"; }
			
			@Override
			public Entity createEntity(String id, float posX, float posY, float posZ, float angX, float angY, float angZ, Map<String, String> parameters) {
				return new ButtonGround(id, posX, posY, posZ, angX, angY, angZ, new ArrayList<Interaction>());
			}
		});
		
		
		//TODO entities load from files
	}
	
	public static void registerEntityDef(EntityDef entity) {
		for(EntityDef ent:entityDefList) if(ent.getEntityName().equals(entity.getEntityName())) {
			LogSystem.err_println("[EntityManager] Unable to register entity "+entity.getEntityName()+": This name is already used.");
			return;
		}
		LogSystem.out_println("[EntityManager] Registering entity with name "+entity.getEntityName());
		entityDefList.add(entity);
	}
	
	public static EntityDef getEntityDef(String entityName) {
		synchronized (entityDefList) {
			for(EntityDef entity:entityDefList) if(entity.getEntityName().equals(entityName)) return entity;
		}
		return null;
	}
	
	public static Entity createEntity(String entityName, float posX, float posY, float posZ, float angX, float angY, float angZ, Map<String, String> parameters) {
		synchronized (entityDefList) {
			for(EntityDef entity:entityDefList) if(entity.getEntityName().equals(entityName)) {
				Entity e = entity.createEntity("entity_"+(nextEntityId+=1), posX, posY, posZ, angX, angY, angZ, parameters);
				return addEntity(e);
			}
		}
		LogSystem.err_println("[EntityManager] Unable to find entity with name "+entityName);
		return null;
	}
	
	public static Entity createEntity(String id, String entityName, float posX, float posY, float posZ, float angX, float angY, float angZ, Map<String, String> parameters) {
		synchronized (entityDefList) {
			for(EntityDef entity:entityDefList) if(entity.getEntityName().equals(entityName)) {
				Entity e = entity.createEntity(id, posX, posY, posZ, angX, angY, angZ, parameters);
				return addEntity(e);
			}
		}
		LogSystem.err_println("[EntityManager] Unable to find entity with name "+entityName);
		return null;
	}
	
	public static Entity addEntity(Entity entity) {
		synchronized (entityList) {
			for(Entity e:entityList) if(e.getId().equals(entity.getId())) {
				LogSystem.err_println("[EntityManager] Unable to create entity with identifier "+entity.getId());
				return null;
			}
			entityList.add(entity);
			if(entity instanceof InteractableObject)
				InteractionsManager.addInteractableObject((InteractableObject) entity);
			entity.onAddedToWorldManager();
			return entity;
		}
	}
	
	public static void updateEntities() {
		synchronized (entityList) {
			for(int i=0;i<entityList.size();i++) entityList.get(i).update();
		}
	}
	
	public static void renderEntities() {
		synchronized (entityList) {
			for(Entity entity:entityList) entity.render();
		}
	}
	
	public static void renderEntitiesVR(int eye) {
		synchronized (entityList) {
			for(Entity entity:entityList) entity.renderVR(eye);
		}
	}

	public static Entity getEntity(String entityID) {
		synchronized (entityList) {
			for(Entity entity:entityList) if(entity.getId().equals(entityID)) return entity;
		}
		return null;
	}
	
	public static List<Entity> getEntities() {
		synchronized (entityList) {
			return entityList;
		}
	}

	public static void clear() {
		synchronized (entityList) {
			for(int i=0;i<entityList.size();i++) {
				if(entityList.get(i) instanceof InteractableObject) InteractionsManager.removeInteractableObject(((InteractableObject)entityList.get(i)));
				entityList.get(i).destroy();
			}
			entityList.clear();
		}
	}
	
	public static void clearWithDefs() {
		clear();
		synchronized (entityDefList) {
			entityDefList.clear();
		}
		initialized = false;
	}

	public static boolean removeEntity(String entityId) {
		synchronized (entityList) {
			for(int i=0;i<entityList.size();i++) {
				if(entityList.get(i).getId().equals(entityId)) {
					if(entityList.get(i) instanceof InteractableObject) InteractionsManager.removeInteractableObject(((InteractableObject)entityList.get(i)));
					entityList.get(i).destroy();
					entityList.remove(i);
					return true;
				}
			}
		}
		return false;
	}
	
}
