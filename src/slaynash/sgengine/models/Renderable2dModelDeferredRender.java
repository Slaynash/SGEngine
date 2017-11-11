package slaynash.sgengine.models;

import java.util.Map;

import org.lwjgl.opengl.GL11;

import slaynash.sgengine.deferredRender.DeferredModelRenderer;

public class Renderable2dModelDeferredRender implements DeferredModelRenderer{

	private Renderable2dModel model;
	Map<String, Object> datas = null;

	@Override
	public void bindModel(Object model) {
		this.model = (Renderable2dModel) model;
	}

	@Override
	public void render() {
		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, model.getVao().getIndexCount());
	}

	@Override
	public void renderVR(int eye) {
		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, model.getVao().getIndexCount());
	}

	@Override
	public int[] getTextureIDs() {
		return model.getTextureIds();
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
		return false;
	}

}
