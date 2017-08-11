package slaynash.sgengine.shaders;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import slaynash.sgengine.Configuration;

public class ModernShaderLabel extends ModernShader{
	
	private int texture_location;

	public ModernShaderLabel() {
		super(Configuration.getAbsoluteInstallPath()+"/"+Configuration.getRelativeShaderPath(), "modernLabel.vs", "modernLabel.fs", ShaderProgram.SHADER_LABEL_MODERN);
	}

	@Override
	protected void getAllUniformLocations() {
		super.getUniformLocation("visibility");
		texture_location = super.getUniformLocation("textureDiffuse");
		super.getUniformLocation("screenSize");
	}

	@Override
	protected void connectTextureUnits() {
		GL20.glUniform1i(texture_location, ShaderManager.TEXTURE_COLOR);
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
		super.bindAttribute(1, "textureCoordinates");
	}
/*
	@Override
	public void prepare() {
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
	}

	@Override
	public void stop() {
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL30.glBindVertexArray(0);
	}
	*/

	@Override
	public void prepare() {
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		
		GL20.glUniform2f(getLocation("screenSize"), Display.getWidth(), Display.getHeight());
		
	}

	@Override
	public void stop() {
		/*
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		*/
		GL30.glBindVertexArray(0);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
	}

	@Override
	public void bindModel(int modelID) {
		if(Configuration.getRenderMethod() == Configuration.RENDER_MODERN){
			GL30.glBindVertexArray(modelID);
			GL20.glEnableVertexAttribArray(0);
			GL20.glEnableVertexAttribArray(1);
		}
	}
}
