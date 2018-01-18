package slaynash.sgengine.deferredRender.shaders;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import slaynash.sgengine.Configuration;
import slaynash.sgengine.shaders.ShaderManager;

public class CombineBloomShader extends DeferredShaderProgram {
	
	private int colourTexture_location;
	private int highlightTexture_location;
	private int colourId;
	private int blurId;

	public CombineBloomShader(int outputWidth, int outputHeight) {
		super(Configuration.getAbsoluteInstallPath()+"/"+Configuration.getRelativeShaderPath(), "combineBloom/combineBloom.vs", "combineBloom/combineBloom.fs", outputWidth, outputHeight, GL30.GL_RGBA16F);
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
	public void prepare() {}

	@Override
	public void stop() {
		GL30.glBindVertexArray(0);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
	}
	
	public void setColourTexture(int textureId) {
		colourId = textureId;
	}
	
	public void setBluredTexture(int textureId) {
		blurId = textureId;
	}

	@Override
	public void processDirect() {
		useDirect();
		bindModel();
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, colourId);
		GL13.glActiveTexture(GL13.GL_TEXTURE1);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, blurId);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
		GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, 4);
		stop();
	}
	
}
