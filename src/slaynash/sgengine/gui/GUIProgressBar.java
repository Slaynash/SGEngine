package slaynash.sgengine.gui;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import slaynash.sgengine.maths.Vector2i;
import slaynash.sgengine.models.Renderable2dModel;
import slaynash.sgengine.models.utils.VaoManager;
import slaynash.sgengine.shaders.ShaderManager;
import slaynash.sgengine.textureUtils.TextureDef;
import slaynash.sgengine.textureUtils.TextureManager;
import slaynash.sgengine.utils.Vbo;

public class GUIProgressBar extends GUIElement{
	
	private Renderable2dModel model;
	
	private float value, max;
	
	private static float[] uvs = new float[]{0,0,1,0,1,1,1,1,0,1,0,0};

	public GUIProgressBar(float value, float max, int x, int y, int width, int height, GUIElement parent, int location) {
		super(x, y, width, height, parent, false, location);

		this.max = max;
		this.value = value;
		
		float[] vertices = new float[12];
		vertices[0] = 0;
		vertices[1] = 0;
		vertices[2] = (value/max)*width;
		vertices[3] = 0;
		vertices[4] = (value/max)*width;
		vertices[5] = height;

		vertices[6] = (value/max)*width;
		vertices[7] = height;
		vertices[8] = 0;
		vertices[9] = height;
		vertices[10] = 0;
		vertices[11] = 0;

		
		
		model = new Renderable2dModel(VaoManager.loadToVao2d(vertices, uvs), TextureManager.getTextureDef("res/textures/white.png", TextureManager.TEXTURE_DIFFUSE));
		//model.getVao().editAttribute();
		
	}
	
	public void setTexture(TextureDef texture) {
		model.setTexture(texture);
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
	
	
	public void changeValue(float newValue) {
		if(!(newValue/max-0.001f <= value/max && value/max <= newValue/max+0.001f)) {
			value = newValue;
			
			float renderableValue = value/max;
			if(renderableValue > 1) renderableValue = 1;
			if(renderableValue < 0) renderableValue = 0;
			
			float[] vertices = new float[12];
			vertices[0] = 0;
			vertices[1] = 0;
			vertices[2] = renderableValue*getWidth();
			vertices[3] = 0;
			vertices[4] = renderableValue*getWidth();
			vertices[5] = getHeight();

			vertices[6] = renderableValue*getWidth();
			vertices[7] = getHeight();
			vertices[8] = 0;
			vertices[9] = getHeight();
			vertices[10] = 0;
			vertices[11] = 0;
			
			model.getVao().bind();
			Vbo verticesVbo = model.getVao().getVbo(0);
			verticesVbo.bind();
			verticesVbo.storeData(vertices);
			GL30.glVertexAttribIPointer(0, 2, GL11.GL_INT, 2 * 4, 0);
			verticesVbo.unbind();
			model.getVao().unbind();
		}
	}

	public float getMax() {
		return max;
	}
	
	public void setMax(float max) {
		this.max = max;
		changeValue(value);
	}
	
}
