package slaynash.opengl.utils;

import com.sun.jna.NativeLibrary;

import slaynash.opengl.Configuration;

public class LibraryLoader {
	
	private static void loadNativeLibraries(String path){
		
		System.setProperty("java.library.path", path);
		
		if(System.getProperty("os.name").toLowerCase().equals("mac"))
			System.setProperty("org.lwjgl.librarypath", path+"/natives/macosx");
		else if(System.getProperty("os.name").toLowerCase().contains("nux"))
			System.setProperty("org.lwjgl.librarypath", path+"/natives/linux");
		else
			System.setProperty("org.lwjgl.librarypath", path+"/natives/windows");
		
	}
	
	private static void loadJNANativeLibrary(String libraryName, String path) {
		
		if(System.getProperty("os.name").toLowerCase().equals("mac"))
			NativeLibrary.addSearchPath(libraryName, path+"/jnanatives/darwin_universal");
		else if(System.getProperty("os.name").toLowerCase().contains("nux"))
			NativeLibrary.addSearchPath(libraryName, path+"/jnanatives/linux_x64");
		else if(System.getenv("ProgramFiles(x86)") != null)
			NativeLibrary.addSearchPath(libraryName, path+"/jnanatives/win64");
		else
			NativeLibrary.addSearchPath(libraryName, path+"/jnanatives/win32");
		
	}

	public static void loadLibraries() {
		loadNativeLibraries(Configuration.getAbsoluteInstallPath());
		loadJNANativeLibrary("openvr_api", Configuration.getAbsoluteInstallPath());
	}
	
}
