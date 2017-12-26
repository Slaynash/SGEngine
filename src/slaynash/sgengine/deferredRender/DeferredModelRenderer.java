package slaynash.sgengine.deferredRender;

import java.util.Map;

public interface DeferredModelRenderer {

	public abstract void bindModel(Object model);
	public abstract void render();
	public abstract int[] getTextureIDs();
	public abstract int getModelID();
	public abstract void setShaderDatas(Map<String, Object> datas);
	public abstract Map<String, Object> getShaderDatas();
	public abstract boolean isCastingShadow();
	public abstract void renderVR(int eye);
	public abstract boolean[] getTexture3ds();
	
}
