package slaynash.sgengine.models;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import slaynash.sgengine.deferredRender.DeferredModelRenderer;
import slaynash.sgengine.models.utils.Vao;
import slaynash.sgengine.shaders.ShaderManager;
import slaynash.sgengine.textureUtils.TextureDef;
import slaynash.sgengine.textureUtils.TextureManager;

public class Renderable2dModel extends RenderableModel {
	
	private int listId = 0;
	private TextureDef texture;
	private Vao vao;
	
	/*
	public Renderable2dModel(float[] vertices, float[] textureCoords, TextureDef texture){
		
		this.texture = texture != null ? texture : TextureManager.getDefaultTexture();
		
		vao = VaoManager.loadToVAO(vertices, textureCoords);
		
		if(Configuration.isUsingDeferredRender() && !drRegistered){
			drRegistered = true;
			DeferredRenderer.registerModelRenderer(this, Renderable2dModelDeferredRender.class);
		}
		
	}
	*/
	
	public Renderable2dModel(Vao vao, TextureDef texture){
		this.vao = vao;
		
		this.texture = texture != null ? texture : TextureManager.getDefaultTexture();
		
		//vao = VaoManager.loadToVAO(vertices, textureCoords);
	}

	@Override
	protected void renderToScreen() {
		GL30.glBindVertexArray(vao.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		ShaderManager.shader_bindTextureID(texture.getTextureID(), ShaderManager.TEXTURE_COLOR);
		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, vao.getIndexCount());
	}

	public void setTexture(TextureDef texture) {
		this.texture = texture;
	}

	public Vao getVao() {
		return vao;
	}

	public int[] getTextureIds() {
		return new int[]{texture.getTextureID()};
	}

	public int getListId() {
		return listId;
	}

	@Override
	public Class<? extends DeferredModelRenderer> getDeferredRenderer() {
		return Renderable2dModelDeferredRender.class;
	}

	public boolean[] getTexture3ds() {
		return new boolean[] {false, false};
	}
}
