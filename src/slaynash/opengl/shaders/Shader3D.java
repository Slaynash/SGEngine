package slaynash.opengl.shaders;

import org.lwjgl.opengl.GL20;

import slaynash.opengl.Configuration;

public class Shader3D extends FreeShader{
	
	private static int colorTexture_location;
	private static int normalTexture_location;
	private static int specularTexture_location;
	
	public Shader3D() {
		super(Configuration.getAbsoluteInstallPath()+"/"+Configuration.getRelativeShaderPath(), "free3d.vs", "free3d.fs", ShaderProgram.SHADER_3D_FREE);
	}

	@Override
	protected void connectTextureUnits() {
		GL20.glUniform1i(colorTexture_location, ShaderManager.TEXTURE_COLOR);
		GL20.glUniform1i(normalTexture_location, ShaderManager.TEXTURE_NORMAL);
		GL20.glUniform1i(specularTexture_location, ShaderManager.TEXTURE_SPECULAR);
	}
	
	@Override
	protected void getAllUniformLocations() {
		colorTexture_location = super.getUniformLocation("textureDiffuse");
		super.getUniformLocation("mMatrix");
		super.getUniformLocation("vMatrix");
		super.getUniformLocation("pMatrix");
	}

	@Override
	protected void bindAttributes() {}

	@Override
	protected void prepare() {}

	@Override
	protected void stop() {}
	
}