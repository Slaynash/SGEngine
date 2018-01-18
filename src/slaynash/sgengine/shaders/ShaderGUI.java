package slaynash.sgengine.shaders;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import slaynash.sgengine.Configuration;

public class ShaderGUI extends ShaderProgram {

	private int texture_location;

	public ShaderGUI() {
		super(Configuration.getAbsoluteInstallPath()+"/"+Configuration.getRelativeShaderPath(), "modern/modernGUI.vs", "modern/modernGUI.fs");
	}

	@Override
	protected void getAllUniformLocations() {
		texture_location = super.getUniformLocation("textureDiffuse");
		super.getUniformLocation("textmode");
		super.getUniformLocation("colour");
		super.getUniformLocation("translation");
		super.getUniformLocation("invertColor");
		super.getUniformLocation("combomode");
		super.getUniformLocation("cbCuts");
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
		GL11.glDisable(GL11.GL_DEPTH_TEST);
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
		GL30.glBindVertexArray(modelID);
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
	}
}
