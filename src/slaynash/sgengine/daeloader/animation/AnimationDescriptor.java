package slaynash.sgengine.daeloader.animation;

import java.util.Map;

import org.lwjgl.util.vector.Matrix4f;

import slaynash.sgengine.utils.DisplayManager;

public class AnimationDescriptor {
	
	public final Animation currentAnimation;
	public float animationTime;
	public final boolean loop;
	private boolean ended = false;
	
	public AnimationDescriptor(Animation animation, float currentTime, boolean loop) {
		this.currentAnimation = animation;
		this.animationTime = currentTime;
		this.loop = loop;
	}
	
	
	
	/**
	 * Increases the current animation time which allows the animation to
	 * progress. If the current animation has reached the end then the timer is
	 * reset, causing the animation to loop.
	 */
	void increaseAnimationTime() {
		if(ended) return;
		animationTime += DisplayManager.getFrameTimeSeconds();
		if (animationTime > currentAnimation.getLength()) {
			if(loop) this.animationTime %= currentAnimation.getLength();
			else ended = true;
		}
	}
	
	public boolean isEnded() {
		return ended;
	}
	


	/**
	 * This method returns the current animation pose of the entity. It returns
	 * the desired local-space transforms for all the joints in a map, indexed
	 * by the name of the joint that they correspond to.
	 * 
	 * The pose is calculated based on the previous and next keyframes in the
	 * current animation. Each keyframe provides the desired pose at a certain
	 * time in the animation, so the animated pose for the current time can be
	 * calculated by interpolating between the previous and next keyframe.
	 * 
	 * This method first finds the preious and next keyframe, calculates how far
	 * between the two the current animation is, and then calculated the pose
	 * for the current animation time by interpolating between the transforms at
	 * those keyframes.
	 * 
	 * @param poses
	 *         - The current pose as a map of the desired local-space transforms
	 *         for all the joints. The transforms are indexed by the name ID of
	 *         the joint that they should be applied to.
	 */
	void calculateCurrentAnimationPose(Map<String, Matrix4f> poses) {
		KeyFrame[] frames = getPreviousAndNextFrames();
		float progression = calculateProgression(frames[0], frames[1]);
		interpolatePoses(frames[0], frames[1], progression, poses);
	}
	
	/**
	 * Finds the previous keyframe in the animation and the next keyframe in the
	 * animation, and returns them in an array of length 2. If there is no
	 * previous frame (perhaps current animation time is 0.5 and the first
	 * keyframe is at time 1.5) then the first keyframe is used as both the
	 * previous and next keyframe. The last keyframe is used for both next and
	 * previous if there is no next keyframe.
	 * 
	 * @return The previous and next keyframes, in an array which therefore will
	 *         always have a length of 2.
	 */
	private KeyFrame[] getPreviousAndNextFrames() {
		KeyFrame[] allFrames = currentAnimation.getKeyFrames();
		KeyFrame previousFrame = allFrames[0];
		KeyFrame nextFrame = allFrames[0];
		for (int i = 1; i < allFrames.length; i++) {
			nextFrame = allFrames[i];
			if (nextFrame.getTimeStamp() > animationTime) {
				break;
			}
			previousFrame = allFrames[i];
		}
		return new KeyFrame[] { previousFrame, nextFrame };
	}

	/**
	 * Calculates how far between the previous and next keyframe the current
	 * animation time is, and returns it as a value between 0 and 1.
	 * 
	 * @param previousFrame
	 *            - the previous keyframe in the animation.
	 * @param nextFrame
	 *            - the next keyframe in the animation.
	 * @return A number between 0 and 1 indicating how far between the two
	 *         keyframes the current animation time is.
	 */
	private float calculateProgression(KeyFrame previousFrame, KeyFrame nextFrame) {
		float totalTime = nextFrame.getTimeStamp() - previousFrame.getTimeStamp();
		float currentTime = animationTime - previousFrame.getTimeStamp();
		return currentTime / totalTime;
	}

	/**
	 * Calculates all the local-space joint transforms for the desired current
	 * pose by interpolating between the transforms at the previous and next
	 * keyframes.
	 * 
	 * @param previousFrame
	 *            - the previous keyframe in the animation.
	 * @param nextFrame
	 *            - the next keyframe in the animation.
	 * @param progression
	 *            - a number between 0 and 1 indicating how far between the
	 *            previous and next keyframes the current animation time is.
	 * @param poses
	 *            - The local-space transforms for all the joints for the desired
	 *            current pose. They are returned in a map, indexed by the name of
	 *            the joint to which they should be applied.
	 */
	private void interpolatePoses(KeyFrame previousFrame, KeyFrame nextFrame, float progression, Map<String, Matrix4f> poses) {
		for (String jointName : previousFrame.getJointKeyFrames().keySet()) {
			JointTransform previousTransform = previousFrame.getJointKeyFrames().get(jointName);
			JointTransform nextTransform = nextFrame.getJointKeyFrames().get(jointName);
			JointTransform currentTransform = JointTransform.interpolate(previousTransform, nextTransform, progression);
			Matrix4f pot = poses.get(jointName);
			if(pot == null) poses.put(jointName, currentTransform.getLocalTransform());
			else poses.put(jointName, Matrix4f.mul(pot, currentTransform.getLocalTransform(), new Matrix4f()));
		}
	}
}
