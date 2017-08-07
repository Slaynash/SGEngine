package slaynash.sgengine.shaders;

import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import slaynash.sgengine.Configuration;

public class ModernShader3D extends ModernShader {
	
	public ModernShader3D() {
		super(Configuration.getAbsoluteInstallPath()+"/"+Configuration.getRelativeShaderPath(), "modern3d.vs", "modern3d.fs", ShaderProgram.SHADER_3D_MODERN);
	}
	
	private int colorTexture_location;
	private int normalTexture_location;
	private int specularTexture_location;
	
	@Override
	protected void getAllUniformLocations() {
		colorTexture_location = super.getUniformLocation("textureDiffuse");
		normalTexture_location = super.getUniformLocation("textureNormal");
		specularTexture_location = super.getUniformLocation("textureSpecular");
		
		super.getUniformLocation("mMatrix");
		super.getUniformLocation("vMatrix");
		super.getUniformLocation("pMatrix");
		
		super.getUniformLocation("shineDamper");
		super.getUniformLocation("reflectivity");
		
		for(int i=0;i<Configuration.MAX_LIGHTS;i++){
			super.getUniformLocation("lightPosition["+i+"]");
			super.getUniformLocation("lightColour["+i+"]");
			super.getUniformLocation("attenuation["+i+"]");
		}
	}
	
	@Override
	protected void connectTextureUnits() {
		GL20.glUniform1i(colorTexture_location, ShaderManager.TEXTURE_COLOR);
		GL20.glUniform1i(normalTexture_location, ShaderManager.TEXTURE_NORMAL);
		GL20.glUniform1i(specularTexture_location, ShaderManager.TEXTURE_SPECULAR);
	}
	
	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
		super.bindAttribute(1, "textureCoordinates");
		super.bindAttribute(2, "normal");
		super.bindAttribute(3, "tangent");
	}

	@Override
	protected void prepare() {
		
	}

	@Override
	protected void stop() {
		/*
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(2);
		GL20.glDisableVertexAttribArray(3);
		*/
		GL30.glBindVertexArray(0);
	}
}
