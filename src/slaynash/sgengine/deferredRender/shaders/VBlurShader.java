package slaynash.sgengine.deferredRender.shaders;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import slaynash.sgengine.Configuration;
import slaynash.sgengine.shaders.ShaderManager;
import slaynash.sgengine.shaders.ShaderProgram;

public class VBlurShader extends ShaderProgram {
	
	private int colorTexture_location;

	public VBlurShader() {
		super(Configuration.getAbsoluteInstallPath()+"/"+Configuration.getRelativeShaderPath(), "blur/verticalBlur.vs", "blur/blur.fs", ShaderProgram.SHADER_OTHER);
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
	}

	@Override
	protected void getAllUniformLocations() {
		super.getUniformLocation("targetHeight");
		colorTexture_location = super.getUniformLocation("originalTexture");
		
	}

	@Override
	protected void connectTextureUnits() {
		GL20.glUniform1i(colorTexture_location, ShaderManager.TEXTURE_COLOR);
		
	}

	@Override
	public void prepare() {
		
	}

	@Override
	public void stop() {
		GL30.glBindVertexArray(0);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
	}

	@Override
	public void bindModel(int modelID) {
		GL30.glBindVertexArray(modelID);
		GL20.glEnableVertexAttribArray(0);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
	}

	public void loadTargetHeight(float height){
		bindDataDirect("targetHeight", height);
	}

}
