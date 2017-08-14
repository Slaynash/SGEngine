package slaynash.sgengine.deferredRender;

import java.util.Map;

public abstract class DeferredModelRenderer {

	public abstract void bindModel(Object model);
	public abstract void render();
	public abstract int[] getTextureIDs();
	public abstract int getModelID();
	public abstract void setShaderDatas(Map<String, Object> datas);
	public abstract Map<String, Object> getShaderDatas();
	public abstract boolean isCastingShadow();
	
}
