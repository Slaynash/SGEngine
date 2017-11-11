package slaynash.sgengine.world3d.entities;

import java.util.List;

import org.lwjgl.util.vector.Vector3f;

import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.collision.dispatch.GhostObject;
import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.linearmath.Transform;
import com.bulletphysics.util.ObjectArrayList;

import slaynash.sgengine.entities.Entity;
import slaynash.sgengine.world3d.CollisionManager3d;
import slaynash.sgengine.world3d.Model3dWorld;

public class TriggerBrush extends Entity implements InteractableObject {
	
	Vector3f minAABB = new Vector3f();
	Vector3f maxAABB = new Vector3f();
	
	private GhostObject triggerGhostObject;
	List<CollisionObject> colist = new ObjectArrayList<CollisionObject>();
	
	private List<Interaction> interactions;

	public TriggerBrush(String id, Vector3f minAABB, Vector3f maxAABB, List<Interaction> interactions) {
		super(id);
		this.interactions = interactions;
		this.minAABB = minAABB;
		this.maxAABB = maxAABB;
		
		
	}
	
	@Override
	public void onInput(String command, String... args) {}

	@Override
	public void update() {
		
		List<CollisionObject> overlappingObjects = triggerGhostObject.getOverlappingPairs();
		List<CollisionObject> currentObjects = new ObjectArrayList<CollisionObject>();

		for(CollisionObject obj:overlappingObjects) {
			if(obj.getUserPointer() != null && !(obj.getUserPointer() instanceof Model3dWorld)) currentObjects.add(obj);
		}
		

		boolean triggerEnter = false;
		for(CollisionObject co:currentObjects) {
			if(!colist.contains(co)) {
				triggerEnter = true;
				colist.add(co);
			}
		}
		
		if(triggerEnter) {
			for(Interaction interaction:interactions) {
				if(interaction.getName().equals("onEntityEnter")) {
					interaction.trigger();
				}
			}
		}
		
		boolean triggerExit = false;
		for(int i=0;i<colist.size();i++) {
			if(!currentObjects.contains(colist.get(i))) {
				triggerExit = true;
				colist.remove(i);
				i--;
			}
		}
		
		if(triggerExit) {
			for(Interaction interaction:interactions) {
				if(interaction.getName().equals("onEntityExit")) {
					interaction.trigger();
				}
			}
		}
	}

	@Override
	public void render() {
		
	}

	@Override
	public void renderVR(int eye) {
		
	}

	@Override
	public void destroy() {
		CollisionManager3d.getDynamicWorld().removeCollisionObject(triggerGhostObject);
	}

	@Override
	public void onAddedToWorldManager() {
		javax.vecmath.Vector3f halfExtents = new javax.vecmath.Vector3f((maxAABB.x-minAABB.x)*.5f, (maxAABB.y-minAABB.y)*.5f, (maxAABB.z-minAABB.z)*.5f);
		triggerGhostObject = new GhostObject();
		triggerGhostObject.setCollisionShape(new BoxShape(halfExtents));
		
		Transform transform = new Transform();
		transform.origin.x = (minAABB.x+maxAABB.x)*.5f;
		transform.origin.y = (minAABB.y+maxAABB.y)*.5f;
		transform.origin.z = (minAABB.z+maxAABB.z)*.5f;
		triggerGhostObject.setWorldTransform(transform);
		CollisionManager3d.getDynamicWorld().addCollisionObject(triggerGhostObject);
	}

	@Override
	public void addInteractions(List<Interaction> interactions) {
		this.interactions.addAll(interactions);
	}
	
}
