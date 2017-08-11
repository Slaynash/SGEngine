package slaynash.sgengine.audio;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import paulscode.sound.Library;
import paulscode.sound.SoundSystem;
import paulscode.sound.SoundSystemConfig;
import paulscode.sound.SoundSystemException;
import paulscode.sound.codecs.CodecWav;
import paulscode.sound.libraries.LibraryLWJGLOpenAL;
import slaynash.sgengine.Configuration;
import slaynash.sgengine.LogSystem;

public class AudioManager {
	
	public static Random random = null;
	private static boolean init = false;
	private static boolean stopped = false;
	
	private static List<AudioSource> sources = new ArrayList<AudioSource>();
	
	private static SoundSystem soundSystem;
	
	public static AudioSource createAudioSource(String audioPath, boolean loop, float x, float y, float z){
		if(!init) init();
		if(stopped ){ System.err.println("[AudioManager] SoundSystem has already been stopped !"); return null;}
		if(!new File(Configuration.getAbsoluteInstallPath()+"/"+audioPath).exists()){ System.err.println("[AudioManager] Invalid audio path \""+audioPath+"\" !"); return null;}
		AudioSource as = new AudioSource(audioPath, loop, x, y, z);
		sources.add(as);
		return as;
	}
	
	public static AudioSource createBackgroundMusic(String audioPath, boolean loop){
		if(!init) init();
		if(stopped ){ System.err.println("[AudioManager] SoundSystem has already been stopped !"); return null;}
		if(!new File(Configuration.getAbsoluteInstallPath()+"/"+audioPath).exists()){ System.err.println("[AudioManager] Invalid audio path \""+audioPath+"\" !"); return null;}
		AudioSource as = new AudioSource(audioPath, loop);
		sources.add(as);
		return as;
	}
	
	public static void init() {
		if(init){
			LogSystem.out_println("[AudioManager] Trying to re-init AudioManager, ignoring.");
			return;
		}
		random = new Random();
		SoundSystemConfig.setLogger(new AudioLogger());
		boolean aLCompatible = SoundSystem.libraryCompatible( LibraryLWJGLOpenAL.class ); 
		try {
			Class<? extends Library> libraryType = Library.class;
			SoundSystemConfig.addLibrary( LibraryLWJGLOpenAL.class );
			SoundSystemConfig.setCodec( "wav", CodecWav.class );
			if( aLCompatible ) 
				libraryType = LibraryLWJGLOpenAL.class;
			try 
			{ 
			    soundSystem = new SoundSystem( libraryType ); 
			}catch(SoundSystemException sse){sse.printStackTrace(LogSystem.getErrStream());}
		} catch (SoundSystemException e) {
			e.printStackTrace(LogSystem.getErrStream());
		}
		init = true;
		LogSystem.out_println("[AudioManager] SoundSystem started !");
		soundSystem.setMasterVolume(0.5f);
	}
	
	public static void stop(){
		stopped = false;
		soundSystem.cleanup();
	}
	
	public static void update(float listenerX, float listenerY, float listenerZ, float dirX, float dirY, float dirZ, float upX, float upY, float upZ){
		soundSystem.setListenerPosition(listenerX, listenerY, listenerZ);
		soundSystem.setListenerOrientation(dirX, dirY, dirZ, upX, upY, upZ);
	}
	
	public static SoundSystem getSoundSystem(){
		return soundSystem;
	}
	
	public static void playSoundQuick(String audioPath, float x, float y, float z){
		try {
			soundSystem.quickPlay(false, new URL("file:///"+new File(Configuration.getAbsoluteInstallPath()+"/"+audioPath).getAbsolutePath()), Configuration.getAbsoluteInstallPath()+"/"+audioPath, false, x, y, z, SoundSystemConfig.getDefaultAttenuation(), SoundSystemConfig.getDefaultRolloff());
		} catch (MalformedURLException e) {
			e.printStackTrace(LogSystem.getErrStream());
		}
	}
	
	public static void playSoundQuickOnCamera(String audioPath){
		try {
			soundSystem.quickPlay(false, new URL("file:///"+new File(Configuration.getAbsoluteInstallPath()+"/"+audioPath).getAbsolutePath()), Configuration.getAbsoluteInstallPath()+"/"+audioPath, false, 0, 0, 0, SoundSystemConfig.ATTENUATION_NONE, SoundSystemConfig.getDefaultRolloff());
		} catch (MalformedURLException e) {
			e.printStackTrace(LogSystem.getErrStream());
		}
	}

	public static boolean isInitialized() {
		return init;
	}
	
}
