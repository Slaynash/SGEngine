package slaynash.opengl.utils;

import com.sun.jna.NativeLibrary;

import slaynash.opengl.Configuration;

public class LibraryLoader {
	
	private static boolean alreadyLoaded;

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
		if(alreadyLoaded) {
			System.err.println("[LibraryLoader] Trying to reload libraries, ignored.");
			return;
		}
		alreadyLoaded = true;
		loadNativeLibraries(Configuration.getAbsoluteInstallPath());
		loadJNANativeLibrary("openvr_api", Configuration.getAbsoluteInstallPath());
		loadDirect("jamepad", Configuration.getAbsoluteInstallPath());
	}

	private static void loadDirect(String library, String path) {
		boolean isWindows = System.getProperty("os.name").contains("Windows");
		boolean isLinux = System.getProperty("os.name").contains("Linux");
		boolean isMac = System.getProperty("os.name").contains("Mac");
		boolean is64Bit = System.getProperty("os.arch").equals("amd64") || System.getProperty("os.arch").equals("x86_64");
		
		if (isWindows) {
			if (!is64Bit)
				System.load(path+"/natives/windows/"+library + ".dll");
			else
				System.load(path+"/natives/windows/"+library + "64.dll");
		}
		if (isLinux) {
			if (!is64Bit) {
				System.load(path+"/natives/linux/"+"lib" + library + ".so");
			} else {
				System.load(path+"/natives/linux/"+"lib" + library + "64.so");
			}
		}
		if (isMac) {
			if (!is64Bit)
				System.load(path+"/natives/macosx/"+"lib" + library + ".dylib");
			else
				System.load(path+"/natives/macosx/"+"lib" + library + "64.dylib");
		}
	}
	
}
