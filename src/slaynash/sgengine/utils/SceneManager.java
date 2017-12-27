package slaynash.sgengine.utils;

import javax.swing.event.EventListenerList;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

import slaynash.sgengine.Configuration;
import slaynash.sgengine.DebugTimer;
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
			
			private int err = 0;

			@Override
			public void run() {
				
				DisplayManager.createDisplay(x,y,fullscreen);
				if(Configuration.isVR()) if(!VRUtils.initVR()) {
					Configuration.enableVR(false);
					System.err.println("[SceneManager] Unable to start VR: "+VRUtils.initStatus);
				}
				EngineUpdateThread.setRenderthreadAsCurrent();
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
						if(Display.isCloseRequested() || (Configuration.isVR() && VRUtils.isCloseRequested())){
							render = false;
							close = true;
						}
						else if(currentScene != label && nextScene != null) {
							render = false;
							break;
						}
						else{
							DebugTimer.restart();
							EngineUpdateThread.runAll();
							UserInputUtil.update();
							KeyboardControlManager.update();
							if(Configuration.isControllersEnabled()) ControllersControlManager.update();
							if(currentScene != label && Configuration.isCollisionManager3dEnabled()) CollisionManager3d.update();
							if(currentScene != label && Configuration.isCollisionManager2dEnabled()) CollisionManager2d.update();
							if(Configuration.isSelfEntitiesUpdateEnabled()) EntityManager.updateEntities();
							currentScene.update();
							if(Configuration.isHandRendered()) PlayerWeaponsManager.update();
							if(Configuration.getGUIEnabled()) GUIManager.update();
							DebugTimer.outputAndUpdateTime("Update time");
							
							
							
							
							if(Configuration.isVR()) VRUtils.setCurrentRenderEye(VRUtils.EYE_CENTER);
							currentScene.render();
							int err = 0; if((err = GL11.glGetError()) != 0) LogSystem.out_println("[SceneManager] Scene Render error: OpenGL Error "+err);
							DebugTimer.outputAndUpdateTime("Render time [main]");
							deferredRenderCheck(VRUtils.EYE_CENTER);
							err = 0; if((err = GL11.glGetError()) != 0) LogSystem.out_println("[SceneManager] Deferred Render error: OpenGL Error "+err);
							DebugTimer.outputAndUpdateTime("Render time [defe]");
							
							
							boolean iudr = Configuration.isUsingDeferredRender();
							Configuration.useDeferredRender(false);
							if(Configuration.isHandRendered()) {
								VRUtils.setCurrentRenderEye(VRUtils.EYE_CENTER);
								GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
								PlayerWeaponsManager.renderWeapon();
							}
							err = 0; if((err = GL11.glGetError()) != 0) LogSystem.out_println("[SceneManager] Weapon/Hand Render error: OpenGL Error "+err);
							if(Configuration.getGUIEnabled()) GUIManager.render();
							err = 0; if((err = GL11.glGetError()) != 0) LogSystem.out_println("[SceneManager] GUI Render error: OpenGL Error "+err);
							Configuration.useDeferredRender(iudr);
							DebugTimer.outputAndUpdateTime("GUI Render time");
							
							if(Configuration.isVR()){
								VRUtils.setCurrentRenderEye(VRUtils.EYE_LEFT);
								currentScene.renderVR(VRUtils.EYE_LEFT);
								DebugTimer.outputAndUpdateTime("VR Render time [Left][Main]");
								deferredRenderCheck(VRUtils.EYE_LEFT);
								Configuration.useDeferredRender(false);
								if(Configuration.isHandRendered()) {
									VRUtils.setCurrentRenderEye(VRUtils.EYE_LEFT);
									GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
									PlayerWeaponsManager.renderWeaponVR(VRUtils.EYE_LEFT);
								}
								Configuration.useDeferredRender(iudr);
								DebugTimer.outputAndUpdateTime("VR Render time[Left][Defe]");
								VRUtils.setCurrentRenderEye(VRUtils.EYE_RIGHT);
								currentScene.renderVR(VRUtils.EYE_RIGHT);
								DebugTimer.outputAndUpdateTime("VR Render time [Right][Main]");
								deferredRenderCheck(VRUtils.EYE_RIGHT);
								Configuration.useDeferredRender(false);
								if(Configuration.isHandRendered()) {
									VRUtils.setCurrentRenderEye(VRUtils.EYE_LEFT);
									GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
									PlayerWeaponsManager.renderWeaponVR(VRUtils.EYE_LEFT);
								}
								Configuration.useDeferredRender(iudr);
								DebugTimer.outputAndUpdateTime("VR Render time [Right][Defe]");
								
								Vector3f cpcPos = Configuration.getPlayerCharacter().getPosition();
								Vector3f cpcDir = Configuration.getPlayerCharacter().getViewDirection();
								Vector3f cpcUp = VRUtils.getUpVector();
								AudioManager.update(cpcPos.x, cpcPos.y, cpcPos.z, cpcDir.x, cpcDir.y, cpcDir.z, cpcUp.x, cpcUp.y, cpcUp.z);
								DebugTimer.outputAndUpdateTime("Audio update time");
							}
							else{
								Vector3f cpcPos = Configuration.getPlayerCharacter().getPosition();
								Vector3f cpcDir = Configuration.getPlayerCharacter().getViewDirection();
								AudioManager.update(cpcPos.x, cpcPos.y, cpcPos.z, cpcDir.x, cpcDir.y, cpcDir.z, 0, 1, 0);
								DebugTimer.outputAndUpdateTime("Audio update time");
							}
							err = 0; if((err = GL11.glGetError()) != 0) LogSystem.out_println("[SceneManager] VR Render error: OpenGL Error "+err);
							
							DeferredRenderer.cleanup();
							DebugTimer.outputAndUpdateTime("DeferredRenderer cleanup time");
							//GL11.glFinish();
							
							DisplayManager.updateDisplay();
							if(Configuration.isUsingTimingDebug()) {
								DebugTimer.outputAndUpdateTime("Update display time");
								
								DebugTimer.finishUpdate();
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
							changeScene();
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
	public static void changeScene(Class<? extends Scene> page){
		nextScene = page;
	}
	
	private static void changeScene(){
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
