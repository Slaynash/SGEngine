package slaynash.sgengine.shaders;

import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import slaynash.sgengine.Configuration;

public class SkyboxShader extends ShaderProgram {
	
	public SkyboxShader() {
		super(Configuration.getAbsoluteInstallPath()+"/"+Configuration.getRelativeShaderPath(), "skybox/skybox.vs", "skybox/skybox.fs", ShaderProgram.SHADER_3D_MODERN);
	}
	
	private int colorTexture1_location;
	private int colorTexture2_location;
	
	
	@Override
	protected void getAllUniformLocations() {
		colorTexture1_location = super.getUniformLocation("textureDiffuse1");
		colorTexture2_location = super.getUniformLocation("textureDiffuse2");
		super.getUniformLocation("transition");
		
		super.getUniformLocation("pMatrix");
		super.getUniformLocation("vMatrix");
	}
	
	@Override
	protected void connectTextureUnits() {
		GL20.glUniform1i(colorTexture1_location, ShaderManager.TEXTURE_COLOR);
		GL20.glUniform1i(colorTexture2_location, ShaderManager.TEXTURE_COLOR+1);
	}
	
	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
	}
	
	@Override
	public void prepare() {
		
	}
	
	@Override
	public void stop() {
		/*
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		*/
		GL30.glBindVertexArray(0);
	}
	
	@Override
	public void bindModel(int modelID) {
		GL30.glBindVertexArray(modelID);
		GL20.glEnableVertexAttribArray(0);
	}
}
