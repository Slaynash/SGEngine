package slaynash.opengl.textureUtils;

import static org.lwjgl.opengl.GL11.GL_LINEAR_ATTENUATION;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.glTexParameterf;

import java.nio.ByteBuffer;

import org.newdawn.slick.opengl.Texture;

public class TextureDef {
	
	public Texture texture;
	public String path;
	
	public TextureDef(final Texture texture, final String path){
		this.texture = texture;
		this.path = path;
	}
	
	public void generateMipMap(){
		int width = texture.getImageWidth();
		int height = texture.getImageHeight();
		 
		byte[] texbytes = texture.getTextureData();
		int components = texbytes.length / (width*height);
		//System.out.println(components);
		ByteBuffer texdata = ByteBuffer.allocate(texbytes.length);
		texdata.put(texbytes);
		texdata.rewind();
		System.out.println(components+"/"+width+"/"+height);
		//GLU.gluBuild2DMipmaps(GL_TEXTURE_2D, components, width, height, GL_RGB, GL_UNSIGNED_BYTE, texdata);
		//MipMap.gluBuild2DMipmaps(GL_TEXTURE_2D, components, width, height, (components==3 ? GL_RGB : GL_RGBA), GL_UNSIGNED_BYTE, texdata);
		 
		//glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
		//glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR_ATTENUATION);
	}
}
