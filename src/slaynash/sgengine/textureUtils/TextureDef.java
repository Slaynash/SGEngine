package slaynash.sgengine.textureUtils;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import org.lwjgl.opengl.EXTTextureFilterAnisotropic;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL30;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureImpl;
import org.newdawn.slick.opengl.renderer.SGL;

import slaynash.sgengine.LogSystem;

public class TextureDef {
	
	private Texture texture;
	private String path;
	private boolean fakePath = false;
	private int type = TextureManager.COLOR;
	
	public TextureDef(final Texture texture, final String path, final int type){
		this.texture = texture;
		this.path = path;
		this.type = type;
	}
	
	public static TextureDef createTextureFromData(byte[] data, String name, int width, int height){
		int textureId = GL11.glGenTextures();
		Texture textureImpl = new TextureImpl(name, SGL.GL_TEXTURE_2D, textureId);
		
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId);

        ByteBuffer buffer = ByteBuffer.allocate(data.length);
        
        for(int i=0;i<data.length;i++) buffer.put(i,data[i]);

        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height,
                0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);

        // If this renders black ask McJohn what's wrong.
        GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);

        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR);

        FloatBuffer largest = FloatBuffer.allocate(1);
        GL11.glGetFloat(EXTTextureFilterAnisotropic.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT, largest);
        GL11.glTexParameterf(GL11.GL_TEXTURE_2D, EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT, largest.get(0));

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
        
        TextureDef rtd = new TextureDef(textureImpl, name, TextureManager.COLOR);
        rtd.fakePath = true;
		
        return rtd;
	}
	
	public void generateMipMap(){
		int width = texture.getImageWidth();
		int height = texture.getImageHeight();
		 
		byte[] texbytes = texture.getTextureData();
		int components = texbytes.length / (width*height);
		//LogSystem.out_println(components);
		ByteBuffer texdata = ByteBuffer.allocate(texbytes.length);
		texdata.put(texbytes);
		texdata.rewind();
		LogSystem.out_println(components+"/"+width+"/"+height);
		//GLU.gluBuild2DMipmaps(GL_TEXTURE_2D, components, width, height, GL_RGB, GL_UNSIGNED_BYTE, texdata);
		//MipMap.gluBuild2DMipmaps(GL_TEXTURE_2D, components, width, height, (components==3 ? GL_RGB : GL_RGBA), GL_UNSIGNED_BYTE, texdata);
		 
		//glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
		//glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR_ATTENUATION);
	}

	public int getTextureID() {
		return texture.getTextureID();
	}
	
	public Texture getTexture() {
		return texture;
	}
	
	public String getTexturePath() {
		return path;
	}
	
	public boolean isFakePath() {
		return fakePath;
	}

	public int getType() {
		return type;
	}
}
