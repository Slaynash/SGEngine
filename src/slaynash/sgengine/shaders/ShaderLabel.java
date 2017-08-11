package slaynash.sgengine.shaders;

import org.lwjgl.opengl.GL20;

import slaynash.sgengine.Configuration;

public class ShaderLabel extends FreeShader {
	
	private int texture_location;
	
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
	public void prepare() {}

	@Override
	public void stop() {}

	@Override
	public void bindModel(int modelID) {}
	
}
