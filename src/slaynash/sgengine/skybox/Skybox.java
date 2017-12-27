package slaynash.sgengine.skybox;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import slaynash.sgengine.deferredRender.DeferredModelRenderer;
import slaynash.sgengine.models.RenderableModel;
import slaynash.sgengine.models.utils.Vao;
import slaynash.sgengine.models.utils.VaoManager;
import slaynash.sgengine.shaders.ShaderManager;
import slaynash.sgengine.shaders.SkyboxShader;

public class Skybox extends RenderableModel{
	
	private Vao vao;
	private boolean renderable = true;
	private SkyboxTexture textureColor1;
	private SkyboxTexture textureColor2;
	private float transition = 0;
	private static SkyboxShader shader;
	
	public void setTransition(float transition) {
		if(transition < 0) this.transition = 0;
		else if(transition > 1) this.transition = 1;
		else this.transition = transition;
	}
	
	public float getTransition() {
		return transition;
	}
	
	public Skybox(SkyboxTexture textureColor1, SkyboxTexture textureColor2, float size) {
		this.textureColor1 = textureColor1;
		this.textureColor2 = textureColor2;

		float[] vertices = {
		    -size,  size, -size,
		    -size, -size, -size,
		    size, -size, -size,
		     size, -size, -size,
		     size,  size, -size,
		    -size,  size, -size,

		    -size, -size,  size,
		    -size, -size, -size,
		    -size,  size, -size,
		    -size,  size, -size,
		    -size,  size,  size,
		    -size, -size,  size,

		     size, -size, -size,
		     size, -size,  size,
		     size,  size,  size,
		     size,  size,  size,
		     size,  size, -size,
		     size, -size, -size,

		    -size, -size,  size,
		    -size,  size,  size,
		     size,  size,  size,
		     size,  size,  size,
		     size, -size,  size,
		    -size, -size,  size,

		    -size,  size, -size,
		     size,  size, -size,
		     size,  size,  size,
		     size,  size,  size,
		    -size,  size,  size,
		    -size,  size, -size,

		    -size, -size, -size,
		    -size, -size,  size,
		     size, -size, -size,
		     size, -size, -size,
		    -size, -size,  size,
		     size, -size,  size
		};
		
		vao = VaoManager.loadToVao(vertices, 3);
		
	}

	@Override
	protected void renderToScreen() {
		if(!renderable) return;
		else{
			GL30.glBindVertexArray(vao.getVaoID());
			GL20.glEnableVertexAttribArray(0);
			//TODO add ShaderManager.shader_bindShineDamper(shineDamper);
			//TODO add ShaderManager.shader_bindReflectivity(reflectivity);
			GL13.glActiveTexture(GL13.GL_TEXTURE0+ShaderManager.TEXTURE_COLOR);
			GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, textureColor1.getTextureID());
			GL13.glActiveTexture(GL13.GL_TEXTURE0+ShaderManager.TEXTURE_COLOR+1);
			GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, textureColor2.getTextureID());
			GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, vao.getIndexCount());
		}
	}

	public void dispose() {
		vao.dispose();
		renderable = false;
	}
	
	public Vao getVao(){
		return vao;
	}
	
	public void setPrimaryTextureColor(SkyboxTexture textureColor) {
		this.textureColor1 = textureColor;
	}
	
	public void setSecondaryTextureColor(SkyboxTexture textureColor) {
		this.textureColor2 = textureColor;
	}

	public int[] getTextureIds() {
		return new int[]{textureColor1.getTextureID(), textureColor2.getTextureID()};
	}

	@Override
	public Class<? extends DeferredModelRenderer> getDeferredRenderer() {
		return SkyboxDeferredRender.class;
	}
	
	public static SkyboxShader getShader() {
		if(shader == null) shader = new SkyboxShader();
		return shader;
	}

	public boolean[] getTexture3ds() {
		return new boolean[]{true, true};
	}
	
	@Override
	public void render() {
		ShaderManager.getCurrentShaderProgram().bindData("transition", transition);
		super.render();
	}

}
