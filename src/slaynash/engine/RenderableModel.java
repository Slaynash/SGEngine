package slaynash.engine;

import slaynash.opengl.Configuration;

public abstract class RenderableModel {
	
	public void render(){
		if(Configuration.getRenderMethod() == Configuration.RENDER_FREE) renderFree();
		else renderModern();
	}
	
	protected abstract void renderFree();
	protected abstract void renderModern();
	
	public void renderVR(){}
	
}
