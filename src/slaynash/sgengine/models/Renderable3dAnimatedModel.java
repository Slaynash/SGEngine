package slaynash.sgengine.models;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;

import slaynash.sgengine.daeloader.animation.Animation;
import slaynash.sgengine.daeloader.animation.Animator;
import slaynash.sgengine.daeloader.model.Joint;
import slaynash.sgengine.deferredRender.DeferredModelRenderer;
import slaynash.sgengine.models.utils.Vao;
import slaynash.sgengine.shaders.ShaderManager;
import slaynash.sgengine.textureUtils.TextureDef;
import slaynash.sgengine.textureUtils.TextureManager;

public class Renderable3dAnimatedModel extends RenderableModel{

	//private int listId = 0;
	private TextureDef textureColor;
	private TextureDef textureNormal;
	private TextureDef textureSpecular;
	private boolean renderable = true;
	
	private Vao vao;

	// skeleton
	private final Joint rootJoint;
	private final int jointCount;

	private final Animator animator;
	
	public Renderable3dAnimatedModel(Vao vao, Joint rootJoint, int jointCount, TextureDef textureColor, TextureDef textureNormal, TextureDef textureSpecular){
		this.textureColor = textureColor != null ? textureColor : TextureManager.getDefaultTexture();
		this.textureNormal = textureNormal != null ? textureNormal : TextureManager.getDefaultNormalTexture();
		this.textureSpecular = textureSpecular != null ? textureSpecular : TextureManager.getDefaultSpecularTexture();
		this.rootJoint = rootJoint;
		this.jointCount = jointCount;
		this.animator = new Animator(this);
		rootJoint.calcInverseBindTransform(new Matrix4f());
		
		this.vao = vao;
	}

	@Override
	protected void renderToScreen() {
		if(!renderable) return;
		else{
			GL30.glBindVertexArray(vao.getVaoID());
			GL20.glEnableVertexAttribArray(0);
			GL20.glEnableVertexAttribArray(1);
			GL20.glEnableVertexAttribArray(2);
			GL20.glEnableVertexAttribArray(3);
			GL20.glEnableVertexAttribArray(4);
			GL20.glEnableVertexAttribArray(5);
			//TODO add ShaderManager.shader_bindShineDamper(shineDamper);
			//TODO add ShaderManager.shader_bindReflectivity(reflectivity);
			ShaderManager.shader_bindTextureID(textureColor.getTextureID(), ShaderManager.TEXTURE_COLOR);
			ShaderManager.shader_bindTextureID(textureNormal.getTextureID(), ShaderManager.TEXTURE_NORMAL);
			ShaderManager.shader_bindTextureID(textureSpecular.getTextureID(), ShaderManager.TEXTURE_SPECULAR);
			GL11.glDrawElements(GL11.GL_TRIANGLES, vao.getIndexCount(), GL11.GL_UNSIGNED_INT, 0);
			
		}
	}
	
	@Override
	public void renderVREye(int eye) {
		
		if(!renderable) return;
		else{
			GL30.glBindVertexArray(vao.getVaoID());
			GL20.glEnableVertexAttribArray(0);
			GL20.glEnableVertexAttribArray(1);
			GL20.glEnableVertexAttribArray(2);
			GL20.glEnableVertexAttribArray(3);
			GL20.glEnableVertexAttribArray(4);
			GL20.glEnableVertexAttribArray(5);
			//TODO add ShaderManager.shader_bindShineDamper(shineDamper);
			//TODO add ShaderManager.shader_bindReflectivity(reflectivity);
			ShaderManager.shader_bindTextureID(textureColor.getTextureID(), ShaderManager.TEXTURE_COLOR);
			ShaderManager.shader_bindTextureID(textureNormal.getTextureID(), ShaderManager.TEXTURE_NORMAL);
			ShaderManager.shader_bindTextureID(textureSpecular.getTextureID(), ShaderManager.TEXTURE_SPECULAR);
			GL11.glDrawElements(GL11.GL_TRIANGLES, vao.getIndexCount(), GL11.GL_UNSIGNED_INT, 0);
		}
	}
	
	public void setTextureColor(TextureDef textureColor) {
		this.textureColor = textureColor != null ? textureColor : TextureManager.getDefaultTexture();
	}

	public void dispose() {
		vao.dispose();
		renderable = false;
	}
	
	public Vao getVao(){
		return vao;
	}

	public int[] getTextureIds() {
		return new int[]{textureColor.getTextureID(), textureNormal.getTextureID(), textureSpecular.getTextureID()};
	}

	@Override
	public Class<? extends DeferredModelRenderer> getDeferredRenderer() {
		return Renderable3dAnimatedModelDeferredRender.class;
	}

	public boolean[] getTexture3ds() {
		return new boolean[] {false, false, false};
	}
	
	public void update() {
		animator.update();
	}
	
	
	/**
	 * @return The root joint of the joint hierarchy. This joint has no parent,
	 *         and every other joint in the skeleton is a descendant of this
	 *         joint.
	 */
	public Joint getRootJoint() {
		return rootJoint;
	}
	
	/**
	 * Gets an array of the all important model-space transforms of all the
	 * joints (with the current animation pose applied) in the entity. The
	 * joints are ordered in the array based on their joint index. The position
	 * of each joint's transform in the array is equal to the joint's index.
	 * 
	 * @return The array of model-space transforms of the joints in the current
	 *         animation pose.
	 */
	public Matrix4f[] getJointTransforms() {
		Matrix4f[] jointMatrices = new Matrix4f[jointCount];
		addJointsToArray(rootJoint, jointMatrices);
		return jointMatrices;
	}
	
	/**
	 * This adds the current model-space transform of a joint (and all of its
	 * descendants) into an array of transforms. The joint's transform is added
	 * into the array at the position equal to the joint's index.
	 * 
	 * @param headJoint
	 *            - the current joint being added to the array. This method also
	 *            adds the transforms of all the descendents of this joint too.
	 * @param jointMatrices
	 *            - the array of joint transforms that is being filled.
	 */
	private void addJointsToArray(Joint headJoint, Matrix4f[] jointMatrices) {
		jointMatrices[headJoint.index] = headJoint.getAnimatedTransform();
		for (Joint childJoint : headJoint.children) {
			addJointsToArray(childJoint, jointMatrices);
		}
	}
	
	/**
	 * Instructs this entity to carry out a given animation. To do this it
	 * basically sets the chosen animation as the current animation in the
	 * {@link Animator} object.
	 * 
	 * @param animation
	 *            - the animation to be carried out.
	 */
	public void doAnimation(Animation animation) {
		animator.doAnimation(animation);
	}
	
}
