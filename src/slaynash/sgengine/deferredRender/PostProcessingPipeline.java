package slaynash.sgengine.deferredRender;

import org.lwjgl.opengl.Display;

import slaynash.sgengine.utils.VRUtils;

public abstract class PostProcessingPipeline {
	
	private boolean initialised = false;
	private boolean initialisedVR = false;
	
	public abstract void init(int width, int height);
	public abstract void initVR(int width, int height);
	public abstract void destroy();
	
	public void render(FrameBufferedObject inputFbo) {
		if(!initialised) {
			initialised = true;
			init(Display.getWidth(), Display.getHeight());
		}
	}
	
	public void renderVR(FrameBufferedObject inputFbo, int eye) {
		if(!initialisedVR) {
			initialisedVR = true;
			initVR(VRUtils.getRendersize().x, VRUtils.getRendersize().y);
		}
	}
	
	public boolean isInitialised() {
		return initialised;
	}
	public boolean isVRInitialised() {
		return initialisedVR;
	}
	
	
	
}
