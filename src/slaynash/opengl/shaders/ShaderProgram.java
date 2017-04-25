package slaynash.opengl.shaders;

import java.io.File;

import org.lwjgl.opengl.GL20;

public abstract class ShaderProgram {
	
	private String vertexShaderPath;
	private String fragmentShaderPath;
	protected int shaderVertexID;
	protected int shaderFragmentID;
	protected int shaderProgramID;

	public ShaderProgram(String shaderPath, String vertexShaderName, String fragmentShaderName){
		ShaderManager.registerShader(this);
		vertexShaderPath = shaderPath+File.separator+vertexShaderName;
		fragmentShaderPath = shaderPath+File.separator+fragmentShaderName;
		
		loadShader();
	}
	
	public void cleanup(){
		ShaderManager.stopShader();
		GL20.glDetachShader(shaderProgramID, shaderVertexID);
		GL20.glDetachShader(shaderProgramID, shaderFragmentID);
		GL20.glDeleteShader(shaderVertexID);
		GL20.glDeleteShader(shaderFragmentID);
		GL20.glDeleteProgram(shaderProgramID);
	}
	
	protected void loadShader(){
		shaderVertexID = ShaderManager.loadShader(vertexShaderPath, GL20.GL_VERTEX_SHADER);
		shaderFragmentID = ShaderManager.loadShader(fragmentShaderPath, GL20.GL_FRAGMENT_SHADER);
		shaderProgramID = GL20.glCreateProgram();
		GL20.glAttachShader(shaderProgramID, shaderVertexID);
		GL20.glAttachShader(shaderProgramID, shaderFragmentID);
		bindShaderAttributes();
		GL20.glLinkProgram(shaderProgramID);
		GL20.glValidateProgram(shaderProgramID);
		getAllShaderUniformLocations();
		connectShaderTextureUnits();
	}

	protected abstract void bindShaderAttributes();
	protected abstract void getAllShaderUniformLocations();
	protected abstract void connectShaderTextureUnits();
	
	public void use(){
		GL20.glUseProgram(shaderProgramID);
	}
}
