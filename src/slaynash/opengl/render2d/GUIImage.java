package slaynash.opengl.render2d;

import org.lwjgl.util.vector.Vector2f;

import slaynash.engine.Renderable2dModel;
import slaynash.opengl.shaders.ShaderManager;
import slaynash.opengl.textureUtils.TextureManager;

public class GUIImage extends GUIElement{
	
	private Renderable2dModel model;
	
	private static float[] uvs = new float[]{0,0,1,0,1,1,1,1,0,1,0,0};

	public GUIImage(String imagePath, int x, int y, int width, int height, GUIElement parent, int location) {
		super(x, y, width, height, parent, false, location);
		
		float[] vertices = new float[12];
		vertices[0] = 0;
		vertices[1] = 0;
		vertices[2] = width;
		vertices[3] = 0;
		vertices[4] = width;
		vertices[5] = height;

		vertices[6] = width;
		vertices[7] = height;
		vertices[8] = 0;
		vertices[9] = height;
		vertices[10] = 0;
		vertices[11] = 0;

		
		
		model = new Renderable2dModel(vertices, uvs, TextureManager.getTextureDef(imagePath, TextureManager.COLOR));
	}

	@Override
	public void render() {
		ShaderManager.shaderGUI_loadTranslation(getTopLeft());
		model.render();
		ShaderManager.shaderGUI_loadTranslation(new Vector2f());
		/*
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
		*/
	}
	
}
