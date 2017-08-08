package slaynash.sgengine;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import slaynash.sgengine.playercharacters.PlayerCharacter;
import slaynash.sgengine.utils.PageManager;

public class Configuration {

	public static final int RENDER_MODERN = 0;
	public static final int RENDER_FREE = 1;

	public static final int MAX_LIGHTS = 8;
	
	private static String installPath = "";
	private static String fontPath = "res/fonts";
	private static String shaderPath = "res/shaders";
	private static String shaderMode = "OK";
	private static String vrFilesPath = "res/vr";
	private static int renderMethod = RENDER_MODERN;
	private static float mouseSensibility = 1;
	private static float fov = 80;
	private static float znear = 0.1f, zfar = 1000f;
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

	public static String getAbsoluteInstallPath() {
		if(installPath.equals("")){
			try {
				String path = Configuration.class.getProtectionDomain().getCodeSource().getLocation().getPath();
				installPath = new File(URLDecoder.decode(path, "UTF-8")).getParent();
				System.out.println("[Configuration] Root directory: "+installPath);
			}catch (UnsupportedEncodingException e1) {e1.printStackTrace();}
		}
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

	public static void setRenderMethod(int method) {
		renderMethod = method;
	}
	
	public static int getRenderMethod() {
		return renderMethod;
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
    	if(!PageManager.isInitialized()){
    		vr = enable;
    	}
    	else System.out.println("[PageManager] Display manager already initialized ! Please enable or disable VR before !");
    }
    
    public static boolean isVR(){
    	return vr;
    }

	public static void setCollisionLoadedWith3dWorldLoad(boolean collisionsLoadedWith3dWorldLoad) {
		Configuration.collisionsLoadedWith3dWorldLoad = collisionsLoadedWith3dWorldLoad;
	}
	
	public static boolean isCollisionsLoadedWith3dWorldLoad() {
		return collisionsLoadedWith3dWorldLoad;
	}

	public static void setCollisionLoadedWith2dWorldLoad(boolean collisionsLoadedWith2dWorldLoad) {
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
	
}
