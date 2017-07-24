package slaynash.audio;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import paulscode.sound.SoundSystemConfig;
import slaynash.opengl.Configuration;

public class AudioSource {
	
	private String path;
	private String sourcename;
	private boolean destroyed = false;
	
	protected AudioSource(String audioPath, boolean loop, float x, float y, float z){
		this.path = audioPath;
		this.sourcename = new File(audioPath).getName()+"_"+AudioManager.random.nextInt(10000);
		try {
			System.out.println("[AudioSource] file:///"+new File(Configuration.getAbsoluteInstallPath()+"/"+audioPath).getAbsolutePath());
			AudioManager.getSoundSystem().newSource(
					false,
					sourcename,
					new URL("file:///"+new File(Configuration.getAbsoluteInstallPath()+"/"+audioPath).getAbsolutePath()),
					Configuration.getAbsoluteInstallPath()+"/"+audioPath,
					loop,
					x, y, z,
					SoundSystemConfig.ATTENUATION_ROLLOFF,
					SoundSystemConfig.getDefaultRolloff()
			);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}
	
	protected AudioSource(String audioPath, boolean loop){
		this.path = audioPath;
		this.sourcename = new File(audioPath).getName()+"_"+AudioManager.random.nextInt(10000);
		try {
			System.out.println("[AudioSource] file:///"+new File(Configuration.getAbsoluteInstallPath()+"/"+audioPath).getAbsolutePath());
			AudioManager.getSoundSystem().newSource(
					false,
					sourcename,
					new URL("file:///"+new File(Configuration.getAbsoluteInstallPath()+"/"+audioPath).getAbsolutePath()),
					Configuration.getAbsoluteInstallPath()+"/"+audioPath,
					loop,
					0, 0, 0,
					SoundSystemConfig.ATTENUATION_NONE,
					SoundSystemConfig.getDefaultRolloff()
			);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}
	
	public void play(){
		if(!destroyed) AudioManager.getSoundSystem().play(sourcename);
		else System.err.println("[AudioSource] Unable to play destroyed source "+sourcename);
	}
	
	public void stop(){
		if(!destroyed) AudioManager.getSoundSystem().stop(sourcename);
	}
	
	public void destroy(){
		destroyed = true;
		AudioManager.getSoundSystem().removeSource(sourcename);
	}
	
	public String getPath(){
		return path;
	}
}
