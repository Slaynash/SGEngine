package slaynash.sgengine.world3d;

import javax.vecmath.Vector3f;

import com.bulletphysics.collision.broadphase.CollisionFilterGroups;
import com.bulletphysics.collision.broadphase.DbvtBroadphase;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.collision.dispatch.GhostPairCallback;
import com.bulletphysics.collision.dispatch.PairCachingGhostObject;
import com.bulletphysics.collision.shapes.ConvexHullShape;
import com.bulletphysics.dynamics.ActionInterface;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import com.bulletphysics.linearmath.DebugDrawModes;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.IDebugDraw;
import com.bulletphysics.util.ObjectArrayList;

import slaynash.sgengine.world3d.loader.TriangleFace;

public class CollisionManager3d {
	
	private static DbvtBroadphase broadphase;
	private static DefaultCollisionConfiguration collisionConfiguration;
	private static CollisionDispatcher dispatcher;
	private static SequentialImpulseConstraintSolver solver;
	private static DiscreteDynamicsWorld dynamicsWorld;
	private static long lastCall;
	private static IDebugDraw debugDrawer;
	
	public static void reload(){
		
		if(dynamicsWorld != null) {
			dynamicsWorld.destroy();
			dynamicsWorld = null;
		}
		
		broadphase = new DbvtBroadphase();
		collisionConfiguration = new DefaultCollisionConfiguration();
        dispatcher = new CollisionDispatcher(collisionConfiguration);
        solver = new SequentialImpulseConstraintSolver();
        
        dynamicsWorld = new DiscreteDynamicsWorld(dispatcher, broadphase, solver, collisionConfiguration);
        broadphase.getOverlappingPairCache().setInternalGhostPairCallback(new GhostPairCallback());
        
        
        debugDrawer = new IDebugDraw() {
			
			private int debugMode;

			@Override
			public void setDebugMode(int arg0) {
				debugMode = arg0;
			}
			
			@Override
			public void reportErrorWarning(String arg0) {
				System.err.println("[WorldLoader][DEBUG] "+arg0);
			}
			
			@Override
			public int getDebugMode() {
				return debugMode;
			}
			
			@Override
			public void drawLine(Vector3f arg0, Vector3f arg1, Vector3f arg2) {
				/*
				GL11.glBegin(GL11.GL_LINES);
				GL11.glVertex3f(arg0.x, arg0.y, arg0.z);
				GL11.glVertex3f(arg1.x, arg1.y, arg1.z);
				GL11.glEnd();
				*/
			}
			
			@Override
			public void drawContactPoint(Vector3f arg0, Vector3f arg1, float arg2, int arg3, Vector3f arg4) {
				/*
				GL11.glBegin(GL11.GL_POINT);
				GL11.glVertex3f(arg0.x, arg0.y, arg0.z);
				GL11.glEnd();
				
				GL11.glBegin(GL11.GL_LINES);
				GL11.glVertex3f(arg0.x, arg0.y, arg0.z);
				GL11.glVertex3f(arg0.x+arg1.x*arg2, arg0.y+arg1.y*arg2, arg0.z+arg1.z*arg2);
				GL11.glEnd();
				*/
				
			}
			
			@Override
			public void draw3dText(Vector3f arg0, String arg1) {
				//NONE
			}
		};
		debugDrawer.setDebugMode(DebugDrawModes.DRAW_AABB);
		dynamicsWorld.setDebugDrawer(debugDrawer);
		
        /*
        CollisionShape fallShape = new BoxShape(new Vector3f(1f,1f,1f));
        Transform cubeTransform = new Transform();
        cubeTransform.origin.set(0, 50, 0);
        DefaultMotionState fallMotionState = new DefaultMotionState(cubeTransform);
        float mass = 1;
        Vector3f fallInertia = new Vector3f(0, 0, 0);
        fallShape.calculateLocalInertia(mass, fallInertia);
        RigidBodyConstructionInfo fallRigidBodyCI = new RigidBodyConstructionInfo(mass, fallMotionState, fallShape, fallInertia);
        cubeBody = new RigidBody(fallRigidBodyCI);
        dynamicsWorld.addRigidBody(cubeBody);
        

        CollisionShape ground = new BoxShape(new Vector3f(10f,1f,10f));
        DefaultMotionState groundMotionState = new DefaultMotionState();
		RigidBodyConstructionInfo groundRigidBodyCI = new RigidBodyConstructionInfo(0, groundMotionState, ground, new Vector3f(0, 10, 0));
		RigidBody groundRigidBody = new RigidBody(groundRigidBodyCI);
		groundRigidBody.setActivationState(CollisionObject.DISABLE_SIMULATION);
		dynamicsWorld.addRigidBody(groundRigidBody);
        */
        
        
	}
	
	public static void start(){
		lastCall = System.nanoTime()/1000000;
	}
	
	public static void update() {
		long ct = System.nanoTime()/1000000;
		float delta = ct-lastCall;
		if(delta < 16f) return;
		lastCall = ct;
		dynamicsWorld.stepSimulation(delta/1000, 4);
	}
	
	public static void addModel3dWorld(Model3dWorld model){
		ConvexHullShape cus = new ConvexHullShape(new ObjectArrayList<Vector3f>());
		cus.setMargin(0.01f);
		for(TriangleFace f:model.getFaces()){
			for(int i=0;i<f.getVertices().length;i+=3){
				cus.addPoint(new Vector3f(f.getVertices()[i], f.getVertices()[i+1], f.getVertices()[i+2]));
			}
		}
		DefaultMotionState groundMotionState = new DefaultMotionState();
		RigidBodyConstructionInfo groundRigidBodyCI = new RigidBodyConstructionInfo(0, groundMotionState, cus, new Vector3f(0, 0, 0));
		RigidBody groundRigidBody = new RigidBody(groundRigidBodyCI);
		dynamicsWorld.addRigidBody(groundRigidBody);
	}

	public static void debugDrawWorld(){
		dynamicsWorld.debugDrawWorld();
	}
	
	public static void setDebugDrawMode(int debugDrawMode){
		debugDrawer.setDebugMode(debugDrawMode);
	}
	
	public static void registerThePlayer(PairCachingGhostObject entity, ActionInterface controller){
		dynamicsWorld.addCollisionObject(entity, CollisionFilterGroups.CHARACTER_FILTER, (short)(CollisionFilterGroups.STATIC_FILTER | CollisionFilterGroups.DEFAULT_FILTER));
		dynamicsWorld.addAction(controller);
	}
	
	public static DiscreteDynamicsWorld getDynamicWorld(){
		return dynamicsWorld;
	}
	
	public static void destroyDynamicWorld() {
		dynamicsWorld.destroy();
		dynamicsWorld = null;
	}
}
