package slaynash.sgengine.utils;

import javax.swing.event.EventListenerList;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

import slaynash.sgengine.Configuration;
import slaynash.sgengine.LogSystem;
import slaynash.sgengine.SGETitleScene;
import slaynash.sgengine.audio.AudioManager;
import slaynash.sgengine.deferredRender.DeferredRenderer;
import slaynash.sgengine.entities.EntityManager;
import slaynash.sgengine.gui.GUIManager;
import slaynash.sgengine.inputs.ControllersControlManager;
import slaynash.sgengine.inputs.KeyboardControlManager;
import slaynash.sgengine.models.utils.VaoManager;
import slaynash.sgengine.shaders.ShaderManager;
import slaynash.sgengine.textureUtils.TextureManager;
import slaynash.sgengine.utils.sceneManagerEvent.SceneManagerEvent;
import slaynash.sgengine.utils.sceneManagerEvent.SceneManagerListener;
import slaynash.sgengine.world2d.CollisionManager2d;
import slaynash.sgengine.world3d.CollisionManager3d;
import slaynash.sgengine.world3d.weapons.PlayerWeaponsManager;

public class SceneManager {
	
	private final static EventListenerList listeners = new EventListenerList();
	
	private static Scene currentScene;
	private static Class<? extends Scene> nextScene;
	private static boolean close = false;
	private static boolean render = false;
	private static Thread renderThread;
	private static boolean firstRenderNotLabel = false;
	
	private static boolean initialized = false;
	
	public static void init(final int x, final int y, final boolean fullscreen, Class<? extends Scene> nextScene){
		if(Configuration.loadNatives()) LibraryLoader.loadLibraries();
		SceneManager.nextScene = nextScene;
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
	
	public static void init(Class<? extends Scene> nextScene){
		init(1280, 720, false, nextScene);
	}
	
	private static void createRenderThread(final int x, final int y, final boolean fullscreen) {
		renderThread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				
				DisplayManager.createDisplay(x,y,fullscreen);
				if(Configuration.isVR()) if(!VRUtils.initVR()) {
					Configuration.enableVR(false);
					System.err.println("[SceneManager] Unable to start VR: "+VRUtils.initStatus);
				}
				TextureManager.init();
				UserInputUtil.initController();
				if(Configuration.isControllersEnabled()) ControllersControlManager.init();
				AudioManager.init();
				DeferredRenderer.init();
				EntityManager.init();
				SGETitleScene label = new SGETitleScene();
				currentScene = label;
				label.init();
				label.start();
				render = true;
				initialized = true;
				throwInitializedEvent();
				LogSystem.out_println("[SceneManager] Starting render");
				while(true){
					while(render){
						long pinnedTime = System.nanoTime();
						if(Display.isCloseRequested() || (Configuration.isVR() && VRUtils.isCloseRequested())){
							render = false;
							close = true;
						}
						else if(currentScene != label && nextScene != null) {
							render = false;
							break;
						}
						else{
							long startTime = 0;
							if(Configuration.isUsingTimingDebug()) startTime = System.nanoTime();
							UserInputUtil.update();
							KeyboardControlManager.update();
							if(Configuration.isControllersEnabled()) ControllersControlManager.update();
							if(currentScene != label && Configuration.isCollisionManager3dEnabled()) CollisionManager3d.update();
							if(currentScene != label && Configuration.isCollisionManager2dEnabled()) CollisionManager2d.update();
							if(Configuration.isSelfEntitiesUpdateEnabled()) EntityManager.updateEntities();
							currentScene.update();
							if(Configuration.isHandRendered()) PlayerWeaponsManager.update();
							if(Configuration.getGUIEnabled()) GUIManager.update();
							if(Configuration.isUsingTimingDebug()) {
								LogSystem.out_println("[TIMING] Update time: "+((System.nanoTime()-startTime)/1e6f)+"ms");
								startTime = System.nanoTime();
							}
							
							
							
							
							if(Configuration.isVR()) VRUtils.setCurrentRenderEye(VRUtils.EYE_CENTER);
							currentScene.render();
							if(Configuration.isUsingTimingDebug()) {
								GL11.glFinish();
								LogSystem.out_println("[TIMING] Render time [main]: "+((System.nanoTime()-startTime)/1e6f)+"ms");
								startTime = System.nanoTime();
							}
							deferredRenderCheck(VRUtils.EYE_CENTER);
							if(Configuration.isUsingTimingDebug()) {
								GL11.glFinish();
								LogSystem.out_println("[TIMING] Render time [defe]: "+((System.nanoTime()-startTime)/1e6f)+"ms");
								startTime = System.nanoTime();
							}
							
							boolean iudr = Configuration.isUsingDeferredRender();
							Configuration.useDeferredRender(false);
							if(Configuration.isHandRendered()) {
								VRUtils.setCurrentRenderEye(VRUtils.EYE_CENTER);
								GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
								PlayerWeaponsManager.renderWeapon();
							}
							if(Configuration.getGUIEnabled()) GUIManager.render();
							int err = 0; if((err = GL11.glGetError()) != 0) LogSystem.out_println("[SceneManager] GUI Render error: OpenGL Error "+err);
							Configuration.useDeferredRender(iudr);
							if(Configuration.isUsingTimingDebug()) {
								GL11.glFinish();
								LogSystem.out_println("[TIMING] GUI Render time: "+((System.nanoTime()-startTime)/1e6f)+"ms");
								startTime = System.nanoTime();
							}
							
							if(Configuration.isVR()){
								VRUtils.setCurrentRenderEye(VRUtils.EYE_LEFT);
								currentScene.renderVR(VRUtils.EYE_LEFT);
								if(Configuration.isUsingTimingDebug()) {
									GL11.glFinish();
									LogSystem.out_println("[TIMING] VR Render time [Left][Main]: "+((System.nanoTime()-startTime)/1e6f)+"ms");
									startTime = System.nanoTime();
								}
								deferredRenderCheck(VRUtils.EYE_LEFT);
								Configuration.useDeferredRender(false);
								if(Configuration.isHandRendered()) {
									VRUtils.setCurrentRenderEye(VRUtils.EYE_LEFT);
									GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
									PlayerWeaponsManager.renderWeaponVR(VRUtils.EYE_LEFT);
								}
								Configuration.useDeferredRender(iudr);
								if(Configuration.isUsingTimingDebug()) {
									GL11.glFinish();
									LogSystem.out_println("[TIMING] VR Render time [Left][Defe]: "+((System.nanoTime()-startTime)/1e6f)+"ms");
									startTime = System.nanoTime();
								}
								VRUtils.setCurrentRenderEye(VRUtils.EYE_RIGHT);
								currentScene.renderVR(VRUtils.EYE_RIGHT);
								if(Configuration.isUsingTimingDebug()) {
									GL11.glFinish();
									LogSystem.out_println("[TIMING] VR Render time [Right][Main]: "+((System.nanoTime()-startTime)/1e6f)+"ms");
									startTime = System.nanoTime();
								}
								deferredRenderCheck(VRUtils.EYE_RIGHT);
								Configuration.useDeferredRender(false);
								if(Configuration.isHandRendered()) {
									VRUtils.setCurrentRenderEye(VRUtils.EYE_LEFT);
									GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
									PlayerWeaponsManager.renderWeaponVR(VRUtils.EYE_LEFT);
								}
								Configuration.useDeferredRender(iudr);
								if(Configuration.isUsingTimingDebug()) {
									GL11.glFinish();
									LogSystem.out_println("[TIMING] VR Render time [Right][Defe]: "+((System.nanoTime()-startTime)/1e6f)+"ms");
									startTime = System.nanoTime();
								}
								
								Vector3f cpcPos = Configuration.getPlayerCharacter().getPosition();
								Vector3f cpcDir = Configuration.getPlayerCharacter().getViewDirection();
								Vector3f cpcUp = VRUtils.getUpVector();
								AudioManager.update(cpcPos.x, cpcPos.y, cpcPos.z, cpcDir.x, cpcDir.y, cpcDir.z, cpcUp.x, cpcUp.y, cpcUp.z);
								if(Configuration.isUsingTimingDebug()) {
									LogSystem.out_println("[TIMING] Audio update time: "+((System.nanoTime()-startTime)/1e6f)+"ms");
									startTime = System.nanoTime();
								}
							}
							else{
								Vector3f cpcPos = Configuration.getPlayerCharacter().getPosition();
								Vector3f cpcDir = Configuration.getPlayerCharacter().getViewDirection();
								AudioManager.update(cpcPos.x, cpcPos.y, cpcPos.z, cpcDir.x, cpcDir.y, cpcDir.z, 0, 1, 0);
								if(Configuration.isUsingTimingDebug()) {
									LogSystem.out_println("[TIMING] Audio update time: "+((System.nanoTime()-startTime)/1e6f)+"ms");
									startTime = System.nanoTime();
								}
							}
							
							DeferredRenderer.cleanup();
							if(Configuration.isUsingTimingDebug()) {
								GL11.glFinish();
								LogSystem.out_println("[TIMING] DeferredRenderer cleanup time: "+((System.nanoTime()-startTime)/1e6f)+"ms");
								startTime = System.nanoTime();
							}
							//GL11.glFinish();
							
							DisplayManager.updateDisplay();
							if(Configuration.isUsingTimingDebug()) {
								GL11.glFinish();
								LogSystem.out_println("[TIMING] Update display time: "+((System.nanoTime()-startTime)/1e6f)+"ms");
								startTime = System.nanoTime();
								
								LogSystem.out_println("[TIMING] TOTAL time: "+((System.nanoTime()-pinnedTime)/1e6f)+"ms");
							}
							firstRenderNotLabel = false;
							if(Configuration.isVR()) {
								VRUtils.sendFramesToCompositor();
								VRUtils.updatePose();
							}
							if(currentScene == label && label.isRenderingDone()){
								LogSystem.out_println("[SceneManager] Label rendering done");
								render = false;
								if(nextScene == null) close = true;
								firstRenderNotLabel = true;
							}
						}
					}
					if(close){
						LogSystem.out_println("[SceneManager] Stopping engine...");
						stop();
						UserInputUtil.exitControls();
						if(Configuration.isVR()) VRUtils.stop();
						ShaderManager.cleanUp();
						VaoManager.cleanUp();
						AudioManager.stop();
						DisplayManager.closeDisplay();
						throwExitedEvent();
						break;
					}
					
					else if(nextScene != null){
						if(currentScene == null){
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
	
	private static void deferredRenderCheck(int eye){
		if(Configuration.isUsingDeferredRender()){
			DeferredRenderer.render(eye);
		}
	}

	private static void start() {
		try {
			currentScene = nextScene.newInstance();
			nextScene = null;
		}
		catch (InstantiationException e) {e.printStackTrace(LogSystem.getErrStream());}
		catch (IllegalAccessException e) {e.printStackTrace(LogSystem.getErrStream());}
		render = true;
		currentScene.init();
		if(firstRenderNotLabel && Configuration.isCollisionManager3dEnabled()) CollisionManager3d.reload();
		if(firstRenderNotLabel && Configuration.isCollisionManager2dEnabled()) CollisionManager2d.reload();
		currentScene.start();
		if(firstRenderNotLabel && Configuration.isCollisionManager3dEnabled()) CollisionManager3d.start();
		if(firstRenderNotLabel && Configuration.isCollisionManager2dEnabled()) CollisionManager2d.start();
		throwSceneStartedEvent();
	}
	
	/**
	 * End current render loop, clear the current GamePage and start a new GamePage instance with his render
	 * @param page is the GamePage to start
	 */
	public static void changePage(Class<? extends Scene> page){
		nextScene = page;
	}
	
	private static void changePage(){
		LogSystem.out_println("[SceneManager] Changing page from "+currentScene.getClass()+" to "+nextScene);
		stop();
		start();
		throwSceneChangedEvent();
	}
	
	private static void stop(){
		currentScene.stop();
		throwSceneClosedEvent();
	}

	/**
	 * End current render loop, clear the current GamePage and close the Display
	 */
	public static void close(){
		close = true;
		render = false;
	}

	public static void resize() {
		if(currentScene != null)currentScene.resize();
	}

	public static void addSceneManagerListener(SceneManagerListener listener) {
        listeners.add(SceneManagerListener.class, listener);
    }
 
    public static void removeSceneManagerListener(SceneManagerListener listener) {
        listeners.remove(SceneManagerListener.class, listener);
    }
    
    public static SceneManagerListener[] getSceneManagerListener() {
        return listeners.getListeners(SceneManagerListener.class);
    }
    
    private static void throwExitedEvent(){
    	SceneManagerEvent event = new SceneManagerEvent(currentScene);
    	for(SceneManagerListener l:listeners.getListeners(SceneManagerListener.class)) l.exited(event);
    }
    private static void throwInitializedEvent(){
    	SceneManagerEvent event = new SceneManagerEvent(null);
    	for(SceneManagerListener l:listeners.getListeners(SceneManagerListener.class)) l.initialized(event);
    }
    private static void throwSceneStartedEvent(){
    	SceneManagerEvent event = new SceneManagerEvent(null);
    	for(SceneManagerListener l:listeners.getListeners(SceneManagerListener.class)) l.sceneStarted(event);
    }
    private static void throwSceneChangedEvent(){
    	SceneManagerEvent event = new SceneManagerEvent(null);
    	for(SceneManagerListener l:listeners.getListeners(SceneManagerListener.class)) l.sceneChanged(event);
    }
    private static void throwSceneClosedEvent(){
    	SceneManagerEvent event = new SceneManagerEvent(null);
    	for(SceneManagerListener l:listeners.getListeners(SceneManagerListener.class)) l.sceneClosed(event);
    }

	public static boolean isInitialized() {
		return initialized;
	}
	
	
	
}
