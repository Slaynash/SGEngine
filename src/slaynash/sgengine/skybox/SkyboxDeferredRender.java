package slaynash.sgengine.skybox;

import java.util.Map;

import org.lwjgl.opengl.GL11;

import slaynash.sgengine.deferredRender.DeferredModelRenderer;

public class SkyboxDeferredRender  implements DeferredModelRenderer {
	
	private Skybox model;
	Map<String, Object> datas = null;
	private int[] textures;
	private boolean[] texture3ds;
	
	@Override
	public void bindModel(Object model) {
		this.model = (Skybox) model;
		this.textures = this.model.getTextureIds();
		this.texture3ds = this.model.getTexture3ds();
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
		return textures;
	}

	@Override
	public int getModelID() {
		return model.getVao().getVaoID();
	}
	
	@Override
	public boolean[] getTexture3ds() {
		return texture3ds;
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
