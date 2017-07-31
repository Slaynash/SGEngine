package slaynash.sgengine.shaders;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import slaynash.sgengine.Configuration;

public class ShaderGUI extends FreeShader {

	private static int texture_location;
	
	public ShaderGUI() {
		super(Configuration.getAbsoluteInstallPath()+"/"+Configuration.getRelativeShaderPath(), "freeGUI.vs", "freeGUI.fs", ShaderProgram.SHADER_GUI_FREE);
	}
	
	@Override
	protected void connectTextureUnits() {
		GL20.glUniform1i(texture_location, ShaderManager.TEXTURE_COLOR);
	}
	
	@Override
	protected void getAllUniformLocations() {
		texture_location = super.getUniformLocation("textureDiffuse");
		super.getUniformLocation("textmode");
		super.getUniformLocation("colour");
		super.getUniformLocation("translation");
		super.getUniformLocation("invertColor");
		super.getUniformLocation("combomode");
		super.getUniformLocation("cbCuts");
		super.getUniformLocation("screenSize");
	}

	@Override
	protected void bindAttributes() {}

	@Override
	protected void prepare() {
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		
		GL20.glUniform2f(getLocation("screenSize"), Display.getWidth(), Display.getHeight());
	}

	@Override
	protected void stop() {
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
	}
	
}
