package slaynash.sgengine.daeloader.animation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.util.vector.Matrix4f;

import slaynash.sgengine.daeloader.model.Joint;
import slaynash.sgengine.models.Renderable3dAnimatedModel;

/**
 * 
 * This class contains all the functionality to apply an animation to an
 * animated entity. An Animator instance is associated with just one
 * {@link AnimatedModel}. It also keeps track of the running time (in seconds)
 * of the current animation, along with a reference to the currently playing
 * animation for the corresponding entity.
 * 
 * An Animator instance needs to be updated every frame, in order for it to keep
 * updating the animation pose of the associated entity. The currently playing
 * animation can be changed at any time using the doAnimation() method. The
 * Animator will keep looping the current animation until a new animation is
 * chosen.
 * 
 * The Animator calculates the desired current animation pose by interpolating
 * between the previous and next keyframes of the animation (based on the
 * current animation time). The Animator then updates the transforms all of the
 * joints each frame to match the current desired animation pose.
 * 
 * @author Karl
 *
 */
public class Animator {

	private final Renderable3dAnimatedModel entity;

	private Map<String, AnimationDescriptor> animations = new HashMap<String, AnimationDescriptor>();

	/**
	 * @param entity
	 *            - the entity which will by animated by this animator.
	 */
	public Animator(Renderable3dAnimatedModel entity) {
		this.entity = entity;
	}

	/**
	 * Indicates that the entity should carry out the given animation. Resets
	 * the animation time so that the new animation starts from the beginning.
	 * 
	 * @param animation
	 *            - the new animation to carry out.
	 */
	public void doAnimation(String name, Animation animation, boolean loop) {
		if(!animations.containsKey(name)) animations.put(name, new AnimationDescriptor(animation, 0, loop));
	}

	/**
	 * This method should be called each frame to update the animation currently
	 * being played. This increases the animation time (and loops it back to
	 * zero if necessary), finds the pose that the entity should be in at that
	 * time of the animation, and then applies that pose to all the model's
	 * joints by setting the joint transforms.
	 */
	public void update() {
		increaseAnimationsTime();
		
		Map<String, Matrix4f> currentPose = new HashMap<String, Matrix4f>();
		for(AnimationDescriptor ad:animations.values()) ad.calculateCurrentAnimationPose(currentPose);
		
		applyPoseToJoints(currentPose, entity.getRootJoint(), new Matrix4f());
	}

	/**
	 * Increases the current animation time which allows the animation to
	 * progress. If the current animation has reached the end then the timer is
	 * reset, causing the animation to loop.
	 */
	private void increaseAnimationsTime() {
		int i = 0;
		List<String> keys = new ArrayList<String>(animations.keySet());
		while(i < animations.size()) {
			AnimationDescriptor a = animations.get(keys.get(i));
			a.increaseAnimationTime();
			if(a.isEnded()) {
				animations.remove(keys.get(i));
				keys.remove(i);
			}
			else i++;
		}
	}

	/**
	 * This is the method where the animator calculates and sets those all-
	 * important "joint transforms" that I talked about so much in the tutorial.
	 * 
	 * This method applies the current pose to a given joint, and all of its
	 * descendants. It does this by getting the desired local-transform for the
	 * current joint, before applying it to the joint. Before applying the
	 * transformations it needs to be converted from local-space to model-space
	 * (so that they are relative to the model's origin, rather than relative to
	 * the parent joint). This can be done by multiplying the local-transform of
	 * the joint with the model-space transform of the parent joint.
	 * 
	 * The same thing is then done to all the child joints.
	 * 
	 * Finally the inverse of the joint's bind transform is multiplied with the
	 * model-space transform of the joint. This basically "subtracts" the
	 * joint's original bind (no animation applied) transform from the desired
	 * pose transform. The result of this is then the transform required to move
	 * the joint from its original model-space transform to it's desired
	 * model-space posed transform. This is the transform that needs to be
	 * loaded up to the vertex shader and used to transform the vertices into
	 * the current pose.
	 * 
	 * @param currentPose
	 *            - a map of the local-space transforms for all the joints for
	 *            the desired pose. The map is indexed by the name of the joint
	 *            which the transform corresponds to.
	 * @param joint
	 *            - the current joint which the pose should be applied to.
	 * @param parentTransform
	 *            - the desired model-space transform of the parent joint for
	 *            the pose.
	 */
	private void applyPoseToJoints(Map<String, Matrix4f> currentPose, Joint joint, Matrix4f parentTransform) {
		Matrix4f currentLocalTransform = currentPose.get(joint.name);
		Matrix4f currentTransform = Matrix4f.mul(parentTransform, currentLocalTransform, null);
		for (Joint childJoint : joint.children) {
			applyPoseToJoints(currentPose, childJoint, currentTransform);
		}
		Matrix4f.mul(currentTransform, joint.getInverseBindTransform(), currentTransform);
		joint.setAnimationTransform(currentTransform);
	}

}
