package slaynash.opengl.shaders;

import org.lwjgl.opengl.GL20;

import slaynash.opengl.Configuration;

public class ShaderVR extends FreeShader{
	
	private static int colorTexture_location;
	private static int normalTexture_location;
	private static int specularTexture_location;
	
	public ShaderVR() {
		super(Configuration.getAbsoluteInstallPath()+"/"+Configuration.getRelativeShaderPath(), "freeVR.vs", "freeVR.fs", ShaderProgram.SHADER_VR_FREE);
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
		normalTexture_location = super.getUniformLocation("textureNormal");
		specularTexture_location = super.getUniformLocation("textureSpecular");
		super.getUniformLocation("specularFactor");
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
