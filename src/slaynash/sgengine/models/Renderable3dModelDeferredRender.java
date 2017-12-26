package slaynash.sgengine.models;

import java.util.Map;

import org.lwjgl.opengl.GL11;

import slaynash.sgengine.deferredRender.DeferredModelRenderer;

public class Renderable3dModelDeferredRender implements DeferredModelRenderer {

	private Renderable3dModel model;
	Map<String, Object> datas = null;
	private int[] textures;
	private boolean[] texture3ds;

	@Override
	public void bindModel(Object model) {
		this.model = (Renderable3dModel) model;
		this.textures = this.model.getTextureIds();
		this.texture3ds = this.model.getTexture3ds();
	}

	@Override
	public void render() {
		GL11.glDrawElements(GL11.GL_TRIANGLES, model.getVao().getIndexCount(), GL11.GL_UNSIGNED_INT, 0);
	}

	@Override
	public void renderVR(int eye) {
		GL11.glDrawElements(GL11.GL_TRIANGLES, model.getVao().getIndexCount(), GL11.GL_UNSIGNED_INT, 0);
	}

	@Override
	public int[] getTextureIDs() {
		return textures;
	}

	@Override
	public int getModelID() {
		return model.getVao().getVaoID();
	}

	@Override
	public void setShaderDatas(Map<String, Object> datas) {
		this.datas = datas;
	}

	@Override
	public Map<String, Object> getShaderDatas() {
		return datas;
	}

	@Override
	public boolean isCastingShadow() {
		return true;
	}

	@Override
	public boolean[] getTexture3ds() {
		return texture3ds;
	}
	
}
