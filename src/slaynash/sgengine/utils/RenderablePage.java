package slaynash.sgengine.utils;

public abstract class RenderablePage {
	
	public abstract void init();
	
	public abstract void start();
	
	public abstract void update();
	
	public abstract void render();
	
	public abstract void renderVR(int eye);
	
	public abstract void stop();

	public abstract void resize();
	
}
