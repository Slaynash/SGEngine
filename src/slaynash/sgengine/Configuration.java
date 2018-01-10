package slaynash.sgengine;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import slaynash.sgengine.playercharacters.PlayerCharacter;
import slaynash.sgengine.utils.SceneManager;

public abstract class Configuration {

	public static final int MAX_LIGHTS = 8;
	public static final int MAX_WEIGHTS = 40;
	
	private static String installPath = "";
	private static String fontPath = "res/fonts";
	private static String shaderPath = "res/shaders";
	private static String shaderMode = "OK";
	private static String vrFilesPath = "res/vr";
	private static float mouseSensibility = 1;
	private static float fov = 80;
	private static float znear = 0.1f, zfar = 1000f;
	private static float lightZNear = 0.001f, lightZFar = 400f;
	private static boolean vr = false;
	private static boolean collisionsLoadedWith3dWorldLoad = false;
	private static boolean collisionsLoadedWith2dWorldLoad = false;
	private static PlayerCharacter playerCharacter;
	private static boolean loadNatives = true;
	private static int numberOfControllers = 4;
	private static boolean enableControllers = false;
	private static int ssaaSamples = 4;
	private static int vrssaaSamples = 4;
	private static boolean vsync = true;
	private static boolean collisionManager3dEnabled = false;
	private static boolean collisionManager2dEnabled = false;
	private static boolean deferredRender = true;
	private static boolean deferredRenderShadows = true;
	private static boolean cleanBetweenDeferredRendersEnabled = true;
	private static boolean selfUpdateEntities = true;
	private static boolean handRendered = false;
	private static boolean deferredRenderPostProcessing = true;
	private static boolean usingTimingDebug = false;
	private static boolean guiEnabled = true;
	
	public static void loadDefault3DFPSConfigurations() {
		mouseSensibility = 1;
		setFOV(90);
		setZNear(0.1f);
		setZFar(1000f);
		enableVR(false);
		setCollisionManager3dEnabled(true);
		setCollisionsLoadedWith3dWorldLoad(true);
		setCollisionManager2dEnabled(false);
		setCollisionsLoadedWith2dWorldLoad(false);
		setSSAASamples(8);
		setVRSSAASamples(1);
		enableVSync(true);
		useDeferredRender(true);
		useDeferredRenderShadows(true);
		usePostProcessing(true);
		//useDeferredReflections(true);
		setCleanBetweenDeferredRendersEnabled(true);
		setSelfEntitiesUpdateEnabled(true);
		setHandRendered(true);
	}
	
	public static void loadDefaultVRConfigurations() {
		mouseSensibility = 1;
		setFOV(90);
		setZNear(0.1f);
		setZFar(1000f);
		enableVR(true);
		setCollisionManager3dEnabled(true);
		setCollisionsLoadedWith3dWorldLoad(true);
		setCollisionManager2dEnabled(false);
		setCollisionsLoadedWith2dWorldLoad(false);
		setSSAASamples(1);
		setVRSSAASamples(4);
		enableVSync(true);
		useDeferredRender(true);
		useDeferredRenderShadows(true);
		//useDeferredReflections(true);
		usePostProcessing(true);
		setCleanBetweenDeferredRendersEnabled(true);
		setSelfEntitiesUpdateEnabled(true);
		setHandRendered(false);
	}
	
	public static void loadDefault3DRPGConfigurations() {
		mouseSensibility = 1;
		setFOV(90);
		setZNear(0.1f);
		setZFar(1000f);
		enableVR(false);
		setCollisionManager3dEnabled(false);
		setCollisionsLoadedWith3dWorldLoad(true);
		setCollisionManager2dEnabled(false);
		setCollisionsLoadedWith2dWorldLoad(false);
		setSSAASamples(8);
		setVRSSAASamples(1);
		enableVSync(true);
		useDeferredRender(true);
		useDeferredRenderShadows(true);
		usePostProcessing(false);
		//useDeferredReflections(true);
		setCleanBetweenDeferredRendersEnabled(true);
		setSelfEntitiesUpdateEnabled(true);
		setHandRendered(false);
	}
	
	/**
	 * @return The install path of the game.<br>
	 * Default location: jar file location
	 */
	public static String getAbsoluteInstallPath() {
		if(installPath.equals("")){
			try {
				String path = Configuration.class.getProtectionDomain().getCodeSource().getLocation().getPath();
				installPath = new File(URLDecoder.decode(path, "UTF-8")).getParent();
				LogSystem.out_println("[Configuration] Root directory: "+installPath);
			}catch (UnsupportedEncodingException e1) {e1.printStackTrace(LogSystem.getErrStream());}
		}
		if(!installPath.endsWith("\\") && !installPath.endsWith("/")) installPath += "/";
		return installPath;
	}
	
	public static void setInstallPath(String path){
		installPath = path;
	}

	public static String getFontPath() {
		return fontPath;
	}

	public static void setFontPath(String fontPath) {
		Configuration.fontPath = fontPath;
	}

	public static String getRelativeShaderPath() {
		return shaderPath;
	}

	public static void setShaderPath(String shaderPath) {
		Configuration.shaderPath = shaderPath;
	}

	public static String getShaderMode() {
		return shaderMode;
	}

	public static void setShaderMode(String shaderMode) {
		Configuration.shaderMode = shaderMode;
	}
	
	public static void setVRFilesPath(String path){
		vrFilesPath = path;
	}
	
	public static String getVRFilesPath(){
		return vrFilesPath;
	}

	public static float getMouseSensibility() {
		return mouseSensibility;
	}
	
	public static void setMouseSensibility(float sensibility){
		mouseSensibility = sensibility;
	}

	public static float getFOV() {
		return fov;
	}
	
	public static void setFOV(float fov){
		Configuration.fov = fov;
	}

	public static float getZNear() {
		return znear;
	}
	
	public static void setZNear(float znear){
		Configuration.znear = znear;
	}

	public static float getZFar() {
		return zfar;
	}
	
	public static void setZFar(float zfar){
		Configuration.zfar = zfar;
	}
	
    public static void enableVR(boolean enable){
    	if(!SceneManager.isInitialized()){
    		vr = enable;
    	}
    	else LogSystem.out_println("[PageManager] Display manager already initialized ! Please enable or disable VR before !");
    }
    
    public static boolean isVR(){
    	return vr;
    }

	public static void setCollisionsLoadedWith3dWorldLoad(boolean collisionsLoadedWith3dWorldLoad) {
		Configuration.collisionsLoadedWith3dWorldLoad = collisionsLoadedWith3dWorldLoad;
	}
	
	public static boolean isCollisionsLoadedWith3dWorldLoad() {
		return collisionsLoadedWith3dWorldLoad;
	}

	public static void setCollisionsLoadedWith2dWorldLoad(boolean collisionsLoadedWith2dWorldLoad) {
		Configuration.collisionsLoadedWith2dWorldLoad = collisionsLoadedWith2dWorldLoad;
	}
	
	public static boolean isCollisionsLoadedWith2dWorldLoad() {
		return collisionsLoadedWith2dWorldLoad;
	}
	
	public static void setCollisionManager3dEnabled(boolean collisionManager3dEnabled){
		Configuration.collisionManager3dEnabled = collisionManager3dEnabled;
	}
	
	public static boolean isCollisionManager3dEnabled(){
		return collisionManager3dEnabled;
	}
	
	public static void setCollisionManager2dEnabled(boolean collisionManager2dEnabled){
		Configuration.collisionManager2dEnabled = collisionManager2dEnabled;
	}
	
	public static boolean isCollisionManager2dEnabled(){
		return collisionManager2dEnabled;
	}

	public static PlayerCharacter getPlayerCharacter() {
		if(playerCharacter == null) playerCharacter = new PlayerCharacter() {};
		return playerCharacter;
	}

	public static void setPlayerCharacter(PlayerCharacter playerCharacter) {
		Configuration.playerCharacter = playerCharacter;
	}

	public static boolean loadNatives() {
		return loadNatives;
	}

	public static void setLoadNatives(boolean loadNatives) {
		Configuration.loadNatives = loadNatives;
	}

	public static int getNumberOfControllers() {
		return numberOfControllers;
	}

	public static void setNumberOfControllers(int numbersOfControllers) {
		Configuration.numberOfControllers = numbersOfControllers;
	}

	public static boolean isControllersEnabled() {
		return enableControllers;
	}

	public static void enableControllers(boolean enableControllers) {
		Configuration.enableControllers = enableControllers;
	}

	public static void setSSAASamples(int samples) {
		Configuration.ssaaSamples = samples;
	}
	
	public static int getSSAASamples() {
		return Configuration.ssaaSamples;
	}

	public static int getVRSSAASamples() {
		return vrssaaSamples;
	}

	public static void setVRSSAASamples(int vrssaaSamples) {
		Configuration.vrssaaSamples = vrssaaSamples;
	}

	public static boolean isVSyncEnabled() {
		return vsync;
	}

	public static void enableVSync(boolean vsync) {
		Configuration.vsync = vsync;
	}

	public static boolean isUsingDeferredRender() {
		return deferredRender;
	}

	public static void useDeferredRender(boolean deferredRender) {
		Configuration.deferredRender = deferredRender;
	}

	public static boolean isUsingDeferredRenderShadows() {
		return deferredRenderShadows;
	}

	public static void useDeferredRenderShadows(boolean deferredRenderShadows) {
		Configuration.deferredRenderShadows = deferredRenderShadows;
	}

	public static boolean isCleanBetweenDeferredRendersEnabled() {
		return cleanBetweenDeferredRendersEnabled;
	}

	public static void setCleanBetweenDeferredRendersEnabled(boolean cleanBetweenDeferredRenderEnabled) {
		Configuration.cleanBetweenDeferredRendersEnabled = cleanBetweenDeferredRenderEnabled;
	}

	public static boolean isSelfEntitiesUpdateEnabled() {
		return selfUpdateEntities;
	}
	
	public static void setSelfEntitiesUpdateEnabled(boolean selfUpdateEntities) {
		Configuration.selfUpdateEntities = selfUpdateEntities;
	}
	
	public static boolean isHandRendered() {
		return handRendered;
	}
	
	public static void setHandRendered(boolean handrendered) {
		Configuration.handRendered = handrendered;
	}

	public static boolean isPostProcessingEnabled() {
		return deferredRenderPostProcessing;
	}

	public static void usePostProcessing(boolean deferredRenderBloom) {
		Configuration.deferredRenderPostProcessing = deferredRenderBloom;
	}

	public static boolean isUsingTimingDebug() {
		return usingTimingDebug;
	}

	public static void setUsingTimingDebug(boolean timingDebug) {
		Configuration.usingTimingDebug = timingDebug;
	}

	public static float getLightsZNear() {
		return lightZNear;
	}

	public static float getLightsZFar() {
		return lightZFar;
	}
	
	public static void setGUIEnabled(boolean enableGUI) {
		guiEnabled = enableGUI;
	}

	public static boolean getGUIEnabled() {
		return guiEnabled;
	}
	
}
