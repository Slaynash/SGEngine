package slaynash.opengl.textureUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import org.newdawn.slick.opengl.InternalTextureLoader;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

import slaynash.opengl.Infos;

public class TextureManager {

	public static ArrayList<TextureDef> textureList = new ArrayList<TextureDef>();
	private static int defaultTextureID = 0;
	
	public static Texture getTexture(String texturePath){
		
		for(TextureDef tex:textureList) if(texturePath.equals(tex.path)) return tex.texture;
		try {
			System.out.println("Loading texture: "+texturePath);
			Texture texture = TextureLoader.getTexture("PNG", new FileInputStream(new File(Infos.getInstallPath()+"/"+texturePath)));
			textureList.add(new TextureDef(texture, texturePath));
			return texture;
		}
		catch (FileNotFoundException e) {e.printStackTrace();}
		catch (IOException e) {e.printStackTrace();}
		return null;
	}
	
	
	
	
	
	public static TextureDef getTextureDef(String texturePath) {
		for(TextureDef tex:textureList) if(texturePath.equals(tex.path)) return tex;
		try {
			System.out.println("Loading texture: "+texturePath);
			TextureDef textureDef = new TextureDef(TextureLoader.getTexture("PNG", new FileInputStream(new File(Infos.getInstallPath()+"/"+texturePath))), texturePath);
			textureList.add(textureDef);
			return textureDef;
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
			for(TextureDef tex:textureList) if(texturePath.equals("res/textures/default.png")) return tex;
			try {
				TextureDef textureDef = new TextureDef(TextureLoader.getTexture("PNG", new FileInputStream(new File(Infos.getInstallPath()+"/"+"res/textures/default.png"))), texturePath);
				textureList.add(textureDef);
				return textureDef;
			}
			catch (FileNotFoundException e1) {e1.printStackTrace();}
			catch (IOException e1) {e1.printStackTrace();}
		}
		catch (IOException e) {e.printStackTrace();}
		return null;
	}
	
	public static int getTextureID(String texture){
		return getTextureDef(texture).texture.getTextureID();
	}





	public static int getDefaultTextureID() {
		if(defaultTextureID == 0) defaultTextureID  = TextureManager.getTextureID("res/textures/default.png");
		return defaultTextureID;
	}





	public static void reloadTextures() {
		InternalTextureLoader.get().clear();
		String[] names = new String[textureList.size()];
		for(int i=0;i<textureList.size();i++) names[i] = textureList.get(i).path;
		
		textureList.clear();
		for(String path:names) getTextureDef(path);
	}
}
