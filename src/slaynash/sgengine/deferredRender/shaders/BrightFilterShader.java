package slaynash.sgengine.deferredRender.shaders;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import slaynash.sgengine.Configuration;
import slaynash.sgengine.shaders.ShaderManager;
import slaynash.sgengine.shaders.ShaderProgram;

public class BrightFilterShader extends DeferredShaderProgram {

	private int colorTexture_location;
	private int texId;

	public BrightFilterShader(int outputWidth, int outputHeight) {
		super(Configuration.getAbsoluteInstallPath()+"/"+Configuration.getRelativeShaderPath(), "brightFilter/brightFilter.vs", "brightFilter/brightFilter.fs", ShaderProgram.SHADER_OTHER, outputWidth, outputHeight, GL30.GL_RGBA16F);
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
	}

	@Override
	protected void getAllUniformLocations() {
		colorTexture_location = super.getUniformLocation("colourTexture");
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
	
	public void setInputTexture(int textureId) {
		texId = textureId;
	}

	@Override
	public void processDirect() {
		useDirect();
		bindModel();
		GL13.glActiveTexture(GL13.GL_TEXTURE0+ShaderManager.TEXTURE_COLOR);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texId);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
		GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, 4);
		stop();
	}

}