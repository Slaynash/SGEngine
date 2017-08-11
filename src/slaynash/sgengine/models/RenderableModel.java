package slaynash.sgengine.models;

import slaynash.sgengine.Configuration;
import slaynash.sgengine.deferredRender.DeferredRenderer;

public abstract class RenderableModel {
	
	public void render(){
		if(Configuration.isUsingDeferredRender()) DeferredRenderer.addRenderStep(this);
		else renderDirect();
	}
	
	protected void renderDirect(){
		if(Configuration.getRenderMethod() == Configuration.RENDER_FREE) renderFree();
		else renderModern();
	}
	
	protected abstract void renderFree();
	protected abstract void renderModern();
	
	public void renderVR(){}
	
}
