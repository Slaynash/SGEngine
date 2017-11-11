package slaynash.sgengine.textureUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.lwjgl.opengl.EXTTextureFilterAnisotropic;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GLContext;
import org.newdawn.slick.opengl.InternalTextureLoader;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.Log;

import slaynash.sgengine.Configuration;
import slaynash.sgengine.LogSystem;

public class TextureManager {

	public static Map<String, TextureDef> textureList = new HashMap<String, TextureDef>();
	private static int defaultTextureID = 0;
	private static int defaultTextureNormalID = 0;
	private static int defaultTextureSpecularID = 0;
	
	public static final int COLOR = 0;
	public static final int NORMAL = 1;
	public static final int SPECULAR = 2;
	
	private static boolean init = false;
	
	public static void init(){
		if(init){
			LogSystem.out_println("[TextureManager] Trying to re-init AudioManager, ignoring.");
			return;
		}
		Log.setLogSystem(new TextureManagerLogSystem());
		init = true;
	}
	
	public static TextureDef getTextureDef(String texturePath, int type) {
		TextureDef tex;
		if((tex = textureList.get(texturePath)) != null) return tex;
		
		try {
			LogSystem.out_println("[TextureManager] Loading texture: "+texturePath);
			TextureDef textureDef = new TextureDef(getSlickTexture(new File(Configuration.getAbsoluteInstallPath()+"/"+texturePath)), texturePath, type);
			textureList.put(texturePath, textureDef);
			return textureDef;
		}
		catch (Exception e) {
			System.err.println("[TextureManager] Unable to load texture "+texturePath+" ("+e.getMessage()+")");
			switch(type){
				case NORMAL:
					if((tex = textureList.get("res/textures/default_normal.png")) != null) return tex;
					try {
						TextureDef textureDef = new TextureDef(getSlickTexture(new File(Configuration.getAbsoluteInstallPath()+"/"+"res/textures/default_normal.png")), texturePath, type);
						textureList.put(texturePath, textureDef);
						return textureDef;
					}
					catch (FileNotFoundException e1) {e1.printStackTrace(LogSystem.getErrStream());}
					catch (IOException e1) {e1.printStackTrace(LogSystem.getErrStream());}
					break;
				case SPECULAR:
					if((tex = textureList.get("res/textures/default_specular.png")) != null) return tex;
					try {
						TextureDef textureDef = new TextureDef(getSlickTexture(new File(Configuration.getAbsoluteInstallPath()+"/"+"res/textures/default_specular.png")), texturePath, type);
						textureList.put(texturePath, textureDef);
						return textureDef;
					}
					catch (FileNotFoundException e1) {e1.printStackTrace(LogSystem.getErrStream());}
					catch (IOException e1) {e1.printStackTrace(LogSystem.getErrStream());}
					break;
				default:
					if((tex = textureList.get("res/textures/default.png")) != null) return tex;
					try {
						TextureDef textureDef = new TextureDef(getSlickTexture(new File(Configuration.getAbsoluteInstallPath()+"/"+"res/textures/tile_white.png")), texturePath, type);
						textureList.put(texturePath, textureDef);
						return textureDef;
					}
					catch (FileNotFoundException e1) {e1.printStackTrace(LogSystem.getErrStream());}
					catch (IOException e1) {e1.printStackTrace(LogSystem.getErrStream());}
			}
		}
		return null;
	}
	
	public static int getTextureID(String texture, int type){
		return getTextureDef(texture, type).getTextureID();
	}





	public static int getDefaultTextureID() {
		if(defaultTextureID == 0) defaultTextureID = TextureManager.getTextureID("res/textures/default.png", COLOR);
		return defaultTextureID;
	}
	public static int getDefaultNormalTextureID() {
		if(defaultTextureNormalID == 0) defaultTextureNormalID = TextureManager.getTextureID("res/textures/default_normal.png", NORMAL);
		return defaultTextureNormalID;
	}
	public static int getDefaultSpecularTextureID() {
		if(defaultTextureSpecularID == 0) defaultTextureSpecularID = TextureManager.getTextureID("res/textures/default_specular.png", SPECULAR);
		return defaultTextureSpecularID;
	}
	
	public static TextureDef getDefaultTexture() {
		return TextureManager.getTextureDef("res/textures/default.png", COLOR);
	}
	public static TextureDef getDefaultNormalTexture() {
		return TextureManager.getTextureDef("res/textures/default_normal.png", NORMAL);
	}
	public static TextureDef getDefaultSpecularTexture() {
		return TextureManager.getTextureDef("res/textures/default_specular.png", SPECULAR);
	}





	public static void reloadTextures() {
		InternalTextureLoader.get().clear();
		Map<String, TextureDef> textures = new HashMap<String, TextureDef>();
		for(Entry<String, TextureDef> tex:textureList.entrySet()) textures.put(tex.getKey(), getTextureDef(tex.getKey(), tex.getValue().getType()));
		textureList = textures;
	}
	
	private static Texture getSlickTexture(File path) throws FileNotFoundException, IOException{
		String[] fn = path.getName().split("\\.");
		Texture texture = TextureLoader.getTexture(fn[fn.length-1], new FileInputStream(path));
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.getTextureID());
		GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL14.GL_TEXTURE_LOD_BIAS, 0);
		if(GLContext.getCapabilities().GL_EXT_texture_filter_anisotropic){
			float amount = Math.min(16f, GL11.glGetFloat(EXTTextureFilterAnisotropic.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT));
			GL11.glTexParameterf(GL11.GL_TEXTURE_2D, EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT, amount);
		}
		else{
			LogSystem.out_println("[TextureManager] Anisotropic Filtering not supported, ignoring.");
		}
		return texture;
	}
}
