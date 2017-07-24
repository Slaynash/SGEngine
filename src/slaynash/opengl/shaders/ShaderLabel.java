package slaynash.opengl.shaders;

import org.lwjgl.opengl.GL20;

import slaynash.opengl.Configuration;

public class ShaderLabel extends FreeShader {
	
	private static int texture_location;
	
	public ShaderLabel() {
		super(Configuration.getAbsoluteInstallPath()+"/"+Configuration.getRelativeShaderPath(), "freeLabel.vs", "freeLabel.fs", ShaderProgram.SHADER_LABEL_FREE);
	}
	
	@Override
	protected void getAllUniformLocations() {
		super.getUniformLocation("visibility");
		texture_location = super.getUniformLocation("textureDiffuse");
	}
	
	@Override
	protected void connectTextureUnits() {
		GL20.glUniform1i(texture_location, ShaderManager.TEXTURE_COLOR);
	}
	
	
	@Override
	protected void bindAttributes() {}

	@Override
	protected void prepare() {}

	@Override
	protected void stop() {}
	
}
