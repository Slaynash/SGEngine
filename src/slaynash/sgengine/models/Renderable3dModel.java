package slaynash.sgengine.models;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import slaynash.sgengine.deferredRender.DeferredModelRenderer;
import slaynash.sgengine.models.utils.Vao;
import slaynash.sgengine.shaders.ShaderManager;
import slaynash.sgengine.textureUtils.TextureDef;
import slaynash.sgengine.textureUtils.TextureManager;

public class Renderable3dModel extends RenderableModel {

	//private int listId = 0;
	private TextureDef textureColor;
	private TextureDef textureNormal;
	private TextureDef textureSpecular;
	private Vao vao;
	private boolean renderable = true;
	
	
	public Renderable3dModel(Vao vao, TextureDef textureColor, TextureDef textureNormal, TextureDef textureSpecular){
		this.textureColor = textureColor != null ? textureColor : TextureManager.getDefaultTexture();
		this.textureNormal = textureNormal != null ? textureNormal : TextureManager.getDefaultNormalTexture();
		this.textureSpecular = textureSpecular != null ? textureSpecular : TextureManager.getDefaultSpecularTexture();
		
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
		return Renderable3dModelDeferredRender.class;
	}
	
	
	
}
