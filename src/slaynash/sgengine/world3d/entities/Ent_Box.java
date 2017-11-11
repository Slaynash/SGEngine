package slaynash.sgengine.world3d.entities;

import javax.vecmath.Quat4f;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.MotionState;
import com.bulletphysics.linearmath.Transform;

import slaynash.sgengine.Configuration;
import slaynash.sgengine.models.Renderable3dModel;
import slaynash.sgengine.models.utils.ModelManager;
import slaynash.sgengine.shaders.ShaderManager;
import slaynash.sgengine.utils.MatrixUtils;
import slaynash.sgengine.world3d.CollisionManager3d;

public class Ent_Box extends DraggableEntity {

	private RigidBody body = null;
	private static Renderable3dModel model = null;
	private Matrix4f transMatrix = new Matrix4f();
	private javax.vecmath.Vector3f lastGravity = new javax.vecmath.Vector3f();

	public Ent_Box(String id, float posX, float posY, float posZ) {
		super(id);
		setPosition(posX, posY, posZ);
		if(model == null) model = ModelManager.loadObj("res/models/pl_cube.obj", "res/textures/cubedev1.png", null, null);
	}

	@Override
	public void update() {
		if(isDragged()) {
			Vector3f targetPos = Vector3f.add(Configuration.getPlayerCharacter().getViewPosition(), Configuration.getPlayerCharacter().getViewDirection(), null);
			body.setLinearVelocity(new javax.vecmath.Vector3f((targetPos.x-getPosX()), (targetPos.y-getPosY()), (targetPos.z-getPosZ())));
		}
		
		//body.applyCentralForce(new javax.vecmath.Vector3f(0, 9.81f, 0));
		
		Transform trans = body.getWorldTransform(new Transform());
		
		Matrix4f tmogl = new Matrix4f();
		tmogl.translate(new Vector3f(trans.origin.x, trans.origin.y, trans.origin.z), tmogl);
		MatrixUtils.quatToMatrix(trans.getRotation(new Quat4f()), tmogl);
		
		setPosition(trans.origin.x, trans.origin.y, trans.origin.z);
		
		transMatrix = tmogl;
	}

	@Override
	public void render() {
		
		ShaderManager.shader_loadTransformationMatrix(transMatrix);
		model.render();
	}

	@Override
	public void renderVR(int eye) {
		
		ShaderManager.shader_loadTransformationMatrix(transMatrix);
		model.render();
	}

	@Override
	public void destroy() {
		
	}

	@Override
	public void onAddedToWorldManager() {
		CollisionShape shape = new BoxShape(new javax.vecmath.Vector3f(0.632f*.5f, 0.632f*.5f, 0.632f*.5f));
		
		Transform cubeTransform = new Transform();
		cubeTransform.origin.set(getPosX(), getPosY(), getPosZ());
		MotionState motionState = new DefaultMotionState(cubeTransform);
		float mass = 1;
		javax.vecmath.Vector3f fallInertia = new javax.vecmath.Vector3f(0, 0, 0);
		shape.calculateLocalInertia(mass, fallInertia);
		RigidBodyConstructionInfo fallRigidBodyCI = new RigidBodyConstructionInfo(mass, motionState, shape, fallInertia);
		body = new RigidBody(fallRigidBodyCI);
		body.setFriction(1);
		CollisionManager3d.getDynamicWorld().addRigidBody(body);
		body.setActivationState(RigidBody.DISABLE_DEACTIVATION);
		body.setUserPointer(this);
	}
	
	@Override
	public void onDragged() {
		super.onDragged();
		lastGravity.set(body.getGravity(new javax.vecmath.Vector3f()));
		body.setGravity(new javax.vecmath.Vector3f(0,0,0));
	}
	
	@Override
	public void onReleased() {
		super.onReleased();
		body.setGravity(new javax.vecmath.Vector3f(lastGravity.x, lastGravity.y, lastGravity.z));
	}
	
}
