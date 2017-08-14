package slaynash.sgengine.models;

import java.util.Map;

import org.lwjgl.opengl.GL11;

import slaynash.sgengine.Configuration;
import slaynash.sgengine.deferredRender.DeferredModelRenderer;

public class Renderable3dModelDeferredRender extends DeferredModelRenderer {

	private Renderable3dModel model;
	Map<String, Object> datas = null;
	private int[] textures;

	@Override
	public void bindModel(Object model) {
		this.model = (Renderable3dModel) model;
		this.textures = this.model.getTextureIds();
	}

	@Override
	public void render() {
		if(Configuration.getRenderMethod() == Configuration.RENDER_FREE) GL11.glCallList(model.getListId());
		else if(model.isIndexed()) GL11.glDrawElements(GL11.GL_TRIANGLES, model.getVao().getVertexCount(), GL11.GL_UNSIGNED_INT, 0); else GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, model.getVao().getVertexCount());
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
	
}
