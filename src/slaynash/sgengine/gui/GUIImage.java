package slaynash.sgengine.gui;

import slaynash.sgengine.maths.Vector2i;
import slaynash.sgengine.models.Renderable2dModel;
import slaynash.sgengine.models.utils.VaoManager;
import slaynash.sgengine.shaders.ShaderManager;
import slaynash.sgengine.textureUtils.TextureDef;
import slaynash.sgengine.textureUtils.TextureManager;

public class GUIImage extends GUIElement{
	
	private Renderable2dModel model;
	
	//private static float[] uvs = new float[]{0,0,1,0,1,1,1,1,0,1,0,0};

	public GUIImage(String imagePath, int x, int y, int width, int height, GUIElement parent, int location) {
		super(x, y, width, height, parent, false, location);
		
		TextureDef textureDef = TextureManager.getTextureDef(imagePath, TextureManager.TEXTURE_DIFFUSE);
		
		float imageWidth = textureDef.getTexture().getImageWidth();
		float imageHeight = textureDef.getTexture().getImageHeight();
		float maxX = (imageWidth/(textureDef.getTexture().getTextureWidth()));
		float maxY = (imageHeight/(textureDef.getTexture().getTextureHeight()));
		float[] uvs = new float[] {0,0,maxX,0,maxX,maxY,maxX,maxY,0,maxY,0,0};
		
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
		
		
		model = new Renderable2dModel(VaoManager.loadToVao2d(vertices, uvs), textureDef);
	}

	@Override
	public void render() {
		ShaderManager.shader_loadTranslation(getTopLeft());
		model.render();
		ShaderManager.shader_loadTranslation(new Vector2i());
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
