package slaynash.sgengine.world2d;


import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.World;

public class CollisionManager2d {
	
	private static World world = null;
	private static long lastCall;

	public static void reload(){
		world = new World(new Vec2(0, -9.81f));
	}
	
	public static void start(){
		lastCall = System.nanoTime()/1000000;
	}
	
	public static void update(){
		long ct = System.nanoTime()/1000000;
		float delta = ct-lastCall;
		if(delta < 16f) return;
		lastCall = ct;
		world.step(delta, 2, 6);
	}
	
	public static World getWorld(){
		return world;
	}

	public static void setContactListener(ContactListener contactListener) {
		world.setContactListener(contactListener);
	}

	public static Body createBody(BodyDef bodyDef) {
		return world.createBody(bodyDef);
	}
	
}
