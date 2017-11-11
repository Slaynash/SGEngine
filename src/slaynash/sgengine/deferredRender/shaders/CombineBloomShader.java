package slaynash.sgengine.deferredRender.shaders;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import slaynash.sgengine.Configuration;
import slaynash.sgengine.shaders.ShaderManager;
import slaynash.sgengine.shaders.ShaderProgram;

public class CombineBloomShader extends ShaderProgram {
	
	private int colourTexture_location;
	private int highlightTexture_location;

	public CombineBloomShader() {
		super(Configuration.getAbsoluteInstallPath()+"/"+Configuration.getRelativeShaderPath(), "combineBloom/combineBloom.vs", "combineBloom/combineBloom.fs", ShaderProgram.SHADER_OTHER);
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
	}

	@Override
	protected void getAllUniformLocations() {
		colourTexture_location = super.getUniformLocation("colourTexture");
		highlightTexture_location = super.getUniformLocation("highlightTexture");
	}

	@Override
	protected void connectTextureUnits() {
		GL20.glUniform1i(colourTexture_location, ShaderManager.TEXTURE_COLOR);
		GL20.glUniform1i(highlightTexture_location, ShaderManager.TEXTURE_COLOR+1);
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
	
}
