package slaynash.sgengine.models;

import slaynash.sgengine.Configuration;
import slaynash.sgengine.deferredRender.DeferredRenderableModel;
import slaynash.sgengine.deferredRender.DeferredRenderer;

public abstract class RenderableModel implements DeferredRenderableModel{
	
	/**
	 * Add to DeferredRender if enabled or call renderDirect
	 */
	public void render(){
		if(Configuration.isUsingDeferredRender()) DeferredRenderer.addRenderStep(this);
		else renderDirect();
	}
	
	/**
	 * Call renderFree() if render method is METHOD_FREE else call renderModern()
	 */
	protected void renderDirect(){
		renderToScreen();
	}
	
	protected abstract void renderToScreen();
	protected void renderVREye(int eye) {}
	
	/**
	 * Add to DeferredRender if enabled or call renderVREye (direct)
	 */
	public void renderVR(int eye){
		if(Configuration.isUsingDeferredRender()) DeferredRenderer.addRenderStep(this);
		else renderVREye(eye);
	}
	
	/**
	 * Call renderVREye
	 */
	protected void renderVRDirect(int eye) {
		renderVREye(eye);
	}
	
}
