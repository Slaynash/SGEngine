package slaynash.opengl.utils;

import javax.swing.event.EventListenerList;

import org.lwjgl.opengl.Display;

import slaynash.opengl.shaders.ShaderManager;
import slaynash.opengl.utils.pageManagerEvent.PageManagerEvent;
import slaynash.opengl.utils.pageManagerEvent.PageManagerListener;

public class PageManager {
	
	private final static EventListenerList listeners = new EventListenerList();
	
	private static GamePage currentPage;
	private static Class<? extends GamePage> nextPage;
	private static boolean close = false;
	private static boolean render = false;
	private static Thread renderThread;
	
	private static boolean initialized = false;
	private static boolean vrMode = false;
	
	public static void init(final int x, final int y, final boolean fullscreen){
		renderThread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				
				DisplayManager.createDisplay(x,y,fullscreen);
				if(vrMode) if(!VRUtils.initVR()) vrMode = false;
				UserInputUtil.initController();
				while(true){
					while(render){
						if(Display.isCloseRequested() || (vrMode && VRUtils.isCloseRequested())){
							render = false;
							close = true;
						}
						currentPage.render();
						if(vrMode){
							VRUtils.updatePose();
							currentPage.renderVR();
						}
						DisplayManager.updateDisplay(vrMode);
						if(vrMode) VRUtils.sendFramesToCompositor();
						if(nextPage != null) render = false;
					}
					
					if(nextPage != null){
						if(currentPage == null){
							start();
						}
						else{
							changePage();
						}
					}
					else if(close){
						stop();
						UserInputUtil.exitControls();
						if(vrMode) VRUtils.stop();
						ShaderManager.cleanUp();
						DisplayManager.closeDisplay();
						throwExitedEvent();
						break;
					}
					try { Thread.sleep(1); } catch (InterruptedException e) { e.printStackTrace(); }
				}
			}
		});
		renderThread.setName("Render_Thread");
		
		renderThread.start();
		
		initialized = true;
		throwInitializedEvent();
	}
	
	private static void start() {
		try {
			currentPage = nextPage.newInstance();
			nextPage = null;
		}
		catch (InstantiationException e) {e.printStackTrace();}
		catch (IllegalAccessException e) {e.printStackTrace();}
		render = true;
		currentPage.init();
		currentPage.start();
		throwPageStartedEvent();
	}
	
	/**
	 * End current render loop, clear the current GamePage and start a new GamePage instance with his render
	 * @param page is the GamePage to start
	 */
	public static void changePage(Class<? extends GamePage> page){
		nextPage = page;
	}
	
	private static void changePage(){
		System.out.println("Changing page from "+currentPage.getClass()+" to "+nextPage);
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
		render = false;
		close = true;
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
    
    
    
    
    
    
    
    // VR SYSTEM
    
    
    
    public static void enableVR(boolean enable){
    	if(!initialized){
    		vrMode = enable;
    	}
    	else System.out.println("Display manager already initialized ! Please enable or disable VR before !");
    }
    
    public static boolean isVR(){
    	return vrMode;
    }

	public static void setPageName(String title) {
		Display.setTitle(title);
	}
	
}
