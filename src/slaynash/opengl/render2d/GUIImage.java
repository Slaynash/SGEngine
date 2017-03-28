package slaynash.opengl.render2d;

import org.lwjgl.opengl.GL11;

import slaynash.opengl.shaders.ShaderManager;
import slaynash.opengl.textureUtils.TextureManager;

public class GUIImage extends GUIElement{
	
	private int textureID;

	public GUIImage(String imagePath, int x, int y, int width, int height, GUIElement parent, int location) {
		super(x, y, width, height, parent, false, location);
		this.textureID = TextureManager.getTextureID(imagePath);
	}

	@Override
	public void render() {
		ShaderManager.bind2DShaderTextureID(textureID);
		GL11.glBegin(GL11.GL_QUADS);
			GL11.glTexCoord2f(0      , 0);
			GL11.glVertex2f  (getTopLeft().x, getTopLeft().y);
			GL11.glTexCoord2f(1      , 0);
			GL11.glVertex2f  (getBottomRight().x, getTopLeft().y);
			GL11.glTexCoord2f(1      , 1);
			GL11.glVertex2f  (getBottomRight().x, getBottomRight().y);
			GL11.glTexCoord2f(0      , 1);
			GL11.glVertex2f  (getTopLeft().x, getBottomRight().y);
		GL11.glEnd();
	}
	
}
