package slaynash.opengl;

public class Infos {

	private static String installPath;
	private static String fontPath = "res/fonts";
	private static String shaderPath = "res/shaders";
	private static String shaderMode = "OK";
	private static String vrFilesPath = "res/vr";

	public static String getInstallPath() {
		return installPath;
	}
	
	public static void setInstallPath(String path){
		installPath  = path;
	}

	public static String getFontPath() {
		return fontPath;
	}

	public static void setFontPath(String fontPath) {
		Infos.fontPath = fontPath;
	}

	public static String getShaderPath() {
		return shaderPath;
	}

	public static void setShaderPath(String shaderPath) {
		Infos.shaderPath = shaderPath;
	}

	public static String getShaderMode() {
		return shaderMode;
	}

	public static void setShaderMode(String shaderMode) {
		Infos.shaderMode = shaderMode;
	}
	
	public static void setVRFilesPath(String path){
		vrFilesPath = path;
	}
	
	public static String getVRFilesPath(){
		return vrFilesPath;
	}
	
}
