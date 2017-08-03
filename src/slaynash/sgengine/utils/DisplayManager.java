package slaynash.sgengine.utils;

import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.PixelFormat;

import slaynash.sgengine.Configuration;
import slaynash.sgengine.gui.text2d.Text2d;

public class DisplayManager {
	
	private static int w;
	private static int h;
	private static int fps;
	
	private static long lastFrameTime;
	private static float delta;
	private static long deltaMS;
	private static boolean multisample = true;
	private static int bps;
	
	public static DisplayMode[] getAvailableResolutions(){//return an array of x*5
		try {
			return Display.getAvailableDisplayModes();
		}
		catch (LWJGLException e) {e.printStackTrace();}
		return new DisplayMode[]{};
	}
	
	public static void createDisplay(int x, int y, boolean fullscreen){
		if(Display.isCreated()){
			System.err.println("Unable to create display: display already created");
			return;
		}
		resize(x,y,fullscreen);
		
		lastFrameTime = getCurrentTime();
	}

	public static void createDisplay() {
		createDisplay(1280, 720, false);
	}
	
	public static void resize(int x, int y, boolean fullscreen){
		w = x;
		h = y;
		DisplayMode[] ds = getAvailableResolutions();
		DisplayMode displayMode = null;
		
		for(DisplayMode dm:ds){
			if(dm.getWidth() == x && dm.getHeight() == y && (!fullscreen || dm.isFullscreenCapable() == fullscreen) && Display.getDesktopDisplayMode().getFrequency() == dm.getFrequency() && dm.getBitsPerPixel() == Display.getDesktopDisplayMode().getBitsPerPixel()){
				try {
					Display.setDisplayMode(dm);
					Display.setFullscreen(fullscreen);
					if(!Display.isCreated()){
						if(Configuration.getRenderMethod() == Configuration.RENDER_MODERN){
							ContextAttribs attribs = new ContextAttribs(3,3)
									.withForwardCompatible(true)
									.withProfileCore(true);
							Display.create(new PixelFormat().withSamples(Configuration.getSSAASamples()).withDepthBits(24), attribs);
							GL11.glEnable(GL13.GL_MULTISAMPLE);
						}else Display.create(new PixelFormat());
					}
					w = Display.getWidth();
					h = Display.getHeight();
					fps = dm.getFrequency();
					bps = dm.getBitsPerPixel();
					GL11.glViewport(0, 0, w, h);
				} catch (LWJGLException e) {e.printStackTrace();}
				return;
			}
		}
		try {
			displayMode = new DisplayMode(x, y);
			Display.setDisplayMode(displayMode);
			if(!Display.isCreated()){
				if(Configuration.getRenderMethod() == Configuration.RENDER_MODERN){
					ContextAttribs attribs = new ContextAttribs(3,3)
							.withForwardCompatible(true)
							.withProfileCore(true);
					Display.create(new PixelFormat().withSamples(Configuration.getSSAASamples()).withDepthBits(24), attribs);
					GL11.glEnable(GL13.GL_MULTISAMPLE);
				}else Display.create(new PixelFormat());
			}
		} catch (LWJGLException e) {e.printStackTrace();}
		
		w = Display.getWidth();
		h = Display.getHeight();
		fps = displayMode.getFrequency();
		bps = displayMode.getBitsPerPixel();
		GL11.glViewport(0, 0, w, h);
		if(Configuration.isVR()) GL11.glEnable( GL13.GL_MULTISAMPLE );
		PageManager.resize();
		
		
	}
	
	public static void updateDisplay(){
		//System.out.println(UserInputUtil.getMousePos().toString());
		Display.update();
		if(!Configuration.isVR() && Configuration.isVSyncEnabled()) Display.sync(fps);
		long currentFrameTime = getCurrentTime();
		delta = (currentFrameTime - lastFrameTime)/1000f;
		deltaMS = (currentFrameTime - lastFrameTime);
		lastFrameTime = currentFrameTime;
	}
	
	public static float getFrameTimeSeconds(){
		return delta;
	}
	
	public static float getFrameTime(){
		return deltaMS;
	}
	
	public static void closeDisplay(){
		Display.destroy();
	}
	
	public static int getWidth(){
		return w;
	}
	
	public static int getHeight(){
		return h;
	}
	
	public static long getCurrentTime(){
		return Sys.getTime()*1000/Sys.getTimerResolution();
	}
	
	public static boolean isMultisample(){
		return multisample;
	}

	public static void setDisplayMode(DisplayMode displayMode, boolean fullscreen) {
		if(displayMode.getWidth() != w || displayMode.getHeight() != h || displayMode.getBitsPerPixel() != bps || displayMode.getFrequency() != fps){
			try {
				Display.setDisplayMode(displayMode);
				Display.setFullscreen(fullscreen);
			} catch (LWJGLException e) {e.printStackTrace();}
			w = Display.getWidth();
			h = Display.getHeight();
			fps = displayMode.getFrequency();
			bps = displayMode.getBitsPerPixel();
			GL11.glViewport(0, 0, w, h);
			PageManager.resize();
			Text2d.reload();
		}
	}
}