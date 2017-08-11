package slaynash.sgengine.utils;

import javax.swing.event.EventListenerList;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

import slaynash.sgengine.Configuration;
import slaynash.sgengine.LogSystem;
import slaynash.sgengine.SGELabelPage;
import slaynash.sgengine.audio.AudioManager;
import slaynash.sgengine.deferredRender.DeferredRenderer;
import slaynash.sgengine.inputs.ControllersControlManager;
import slaynash.sgengine.inputs.KeyboardControlManager;
import slaynash.sgengine.shaders.ShaderManager;
import slaynash.sgengine.textureUtils.TextureManager;
import slaynash.sgengine.utils.pageManagerEvent.PageManagerEvent;
import slaynash.sgengine.utils.pageManagerEvent.PageManagerListener;
import slaynash.sgengine.world2d.CollisionManager2d;
import slaynash.sgengine.world3d.CollisionManager3d;

public class PageManager {
	
	private final static EventListenerList listeners = new EventListenerList();
	
	private static RenderablePage currentPage;
	private static Class<? extends RenderablePage> nextPage;
	private static boolean close = false;
	private static boolean render = false;
	private static Thread renderThread;
	private static boolean firstRenderNotLabel = false;
	
	private static boolean initialized = false;
	
	public static void init(final int x, final int y, final boolean fullscreen, Class<? extends RenderablePage> nextPage){
		if(Configuration.loadNatives()) LibraryLoader.loadLibraries();
		PageManager.nextPage = nextPage;
		createRenderThread(x,y,fullscreen);
		renderThread.setName("Render_Thread");
		
		renderThread.start();
		while(!initialized) {
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace(LogSystem.getErrStream());
			}
		}
	}
	
	public static void init(Class<? extends RenderablePage> nextPage){
		init(1280, 720, false, nextPage);
	}
	
	private static void createRenderThread(final int x, final int y, final boolean fullscreen) {
		renderThread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				
				DisplayManager.createDisplay(x,y,fullscreen);
				if(Configuration.isVR()) if(!VRUtils.initVR()) {
					Configuration.enableVR(false);
					System.err.println("[PageManager] Unable to start VR: "+VRUtils.initStatus);
				}
				TextureManager.init();
				UserInputUtil.initController();
				if(Configuration.isControllersEnabled()) ControllersControlManager.init();
				AudioManager.init();
				SGELabelPage label = new SGELabelPage();
				currentPage = label;
				label.init();
				label.start();
				render = true;
				initialized = true;
				throwInitializedEvent();
				LogSystem.out_println("[PageManager] Starting render");
				while(true){
					while(render){
						if(Display.isCloseRequested() || (Configuration.isVR() && VRUtils.isCloseRequested())){
							render = false;
							close = true;
						}
						else if(currentPage != label && nextPage != null) {
							render = false;
							break;
						}
						else{
							UserInputUtil.update();
							KeyboardControlManager.update();
							if(Configuration.isControllersEnabled()) ControllersControlManager.update();
							if(currentPage != label && Configuration.isCollisionManager3dEnabled()) CollisionManager3d.update();
							if(currentPage != label && Configuration.isCollisionManager2dEnabled()) CollisionManager2d.update();
							currentPage.update();
							if(Configuration.isVR()) VRUtils.setCurrentRenderEye(VRUtils.EYE_CENTER);
							currentPage.render();
							deferredRenderCheck();
							
							if(Configuration.isVR()){
								VRUtils.updatePose();
								VRUtils.setCurrentRenderEye(VRUtils.EYE_LEFT);
								currentPage.renderVR(VRUtils.EYE_LEFT);
								deferredRenderCheck();
								VRUtils.setCurrentRenderEye(VRUtils.EYE_RIGHT);
								currentPage.renderVR(VRUtils.EYE_RIGHT);
								deferredRenderCheck();
								
								Vector3f cpcPos = Configuration.getPlayerCharacter().getPosition();
								Vector3f cpcDir = Configuration.getPlayerCharacter().getViewDirection();
								Vector3f cpcUp = VRUtils.getUpVector();
								AudioManager.update(cpcPos.x, cpcPos.y, cpcPos.z, cpcDir.x, cpcDir.y, cpcDir.z, cpcUp.x, cpcUp.y, cpcUp.z);
							}
							else{
								Vector3f cpcPos = Configuration.getPlayerCharacter().getPosition();
								Vector3f cpcDir = Configuration.getPlayerCharacter().getViewDirection();
								AudioManager.update(cpcPos.x, cpcPos.y, cpcPos.z, cpcDir.x, cpcDir.y, cpcDir.z, 0, 1, 0);
							}
							
							DeferredRenderer.cleanup();
							DisplayManager.updateDisplay();
							firstRenderNotLabel = false;
							if(Configuration.isVR()) {
								VRUtils.sendFramesToCompositor();
							}
							if(currentPage == label && label.isRenderingDone()){
								LogSystem.out_println("[PageManager] Label rendering done");
								render = false;
								if(nextPage == null) close = true;
								firstRenderNotLabel = true;
							}
						}
					}
					if(close){
						LogSystem.out_println("[PageManager] Stopping engine...");
						stop();
						UserInputUtil.exitControls();
						if(Configuration.isVR()) VRUtils.stop();
						ShaderManager.cleanUp();
						VOLoader.cleanUp();
						AudioManager.stop();
						DisplayManager.closeDisplay();
						throwExitedEvent();
						break;
					}
					
					else if(nextPage != null){
						if(currentPage == null){
							start();
						}
						else{
							changePage();
						}
					}
					try { Thread.sleep(1); } catch (InterruptedException e) { e.printStackTrace(LogSystem.getErrStream()); }
				}
			}
		});
	}
	
	private static void deferredRenderCheck(){
		if(Configuration.isUsingDeferredRender()){
			if(Configuration.isUsingDeferredRenderShadows())
				DeferredRenderer.renderWithShadows();
			else DeferredRenderer.render();
			if(Configuration.isCleanBetweenDeferredRenderEnabled()) DeferredRenderer.cleanup();
		}
	}

	private static void start() {
		try {
			currentPage = nextPage.newInstance();
			nextPage = null;
		}
		catch (InstantiationException e) {e.printStackTrace(LogSystem.getErrStream());}
		catch (IllegalAccessException e) {e.printStackTrace(LogSystem.getErrStream());}
		render = true;
		currentPage.init();
		if(firstRenderNotLabel && Configuration.isCollisionManager3dEnabled()) CollisionManager3d.reload();
		if(firstRenderNotLabel && Configuration.isCollisionManager2dEnabled()) CollisionManager2d.reload();
		currentPage.start();
		if(firstRenderNotLabel && Configuration.isCollisionManager3dEnabled()) CollisionManager3d.start();
		if(firstRenderNotLabel && Configuration.isCollisionManager2dEnabled()) CollisionManager2d.start();
		throwPageStartedEvent();
	}
	
	/**
	 * End current render loop, clear the current GamePage and start a new GamePage instance with his render
	 * @param page is the GamePage to start
	 */
	public static void changePage(Class<? extends RenderablePage> page){
		nextPage = page;
	}
	
	private static void changePage(){
		LogSystem.out_println("[PageManager] Changing page from "+currentPage.getClass()+" to "+nextPage);
		stop();
		start();
		throwPageChangedEvent();
	}
	
	private static void stop(){
		currentPage.stop();
		throwPageClosedEvent();
	}

	/**
	 * End current render loop, clear the current GamePage and close the Display
	 */
	public static void close(){
		close = true;
		render = false;
	}

	public static void resize() {
		if(currentPage != null)currentPage.resize();
	}

	public static void addPageManagerListener(PageManagerListener listener) {
        listeners.add(PageManagerListener.class, listener);
    }
 
    public static void removePageManagerListener(PageManagerListener listener) {
        listeners.remove(PageManagerListener.class, listener);
    }
    
    public static PageManagerListener[] getPageManagerListener() {
        return listeners.getListeners(PageManagerListener.class);
    }
    
    private static void throwExitedEvent(){
    	PageManagerEvent event = new PageManagerEvent(currentPage);
    	for(PageManagerListener l:listeners.getListeners(PageManagerListener.class)) l.exited(event);
    }
    private static void throwInitializedEvent(){
    	PageManagerEvent event = new PageManagerEvent(null);
    	for(PageManagerListener l:listeners.getListeners(PageManagerListener.class)) l.initialized(event);
    }
    private static void throwPageStartedEvent(){
    	PageManagerEvent event = new PageManagerEvent(null);
    	for(PageManagerListener l:listeners.getListeners(PageManagerListener.class)) l.pageStarted(event);
    }
    private static void throwPageChangedEvent(){
    	PageManagerEvent event = new PageManagerEvent(null);
    	for(PageManagerListener l:listeners.getListeners(PageManagerListener.class)) l.pageChanged(event);
    }
    private static void throwPageClosedEvent(){
    	PageManagerEvent event = new PageManagerEvent(null);
    	for(PageManagerListener l:listeners.getListeners(PageManagerListener.class)) l.pageClosed(event);
    }

	public static void setPageName(String title) {
		Display.setTitle(title);
	}

	public static boolean isInitialized() {
		return initialized;
	}
	
	
	
}
