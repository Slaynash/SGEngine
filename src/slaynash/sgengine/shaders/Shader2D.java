package slaynash.sgengine.shaders;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import slaynash.sgengine.Configuration;

public class Shader2D extends FreeShader{

	private static int texture_location;

	public Shader2D() {
		super(Configuration.getAbsoluteInstallPath()+"/"+Configuration.getRelativeShaderPath(), "free2D.vs", "free2D.fs", ShaderProgram.SHADER_GUI_FREE);
	}

	@Override
	protected void bindAttributes() {}

	@Override
	protected void getAllUniformLocations() {
		texture_location = super.getUniformLocation("textureDiffuse");
		super.getUniformLocation("mmatrix");
		super.getUniformLocation("vmatrix");
		super.getUniformLocation("pmatrix");
		super.getUniformLocation("zoom");
		super.getUniformLocation("displayRatio");
	}

	@Override
	protected void connectTextureUnits() {
		GL20.glUniform1i(texture_location, ShaderManager.TEXTURE_COLOR);
	}

	@Override
	protected void prepare() {
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		
		GL20.glUniform1f(getLocation("displayRatio"), Display.getWidth()/Display.getHeight());
	}

	@Override
	protected void stop() {
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
	}

}
