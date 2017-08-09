package slaynash.sgengine.shaders;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL32;

public abstract class ShaderProgram {

	public static final int SHADER_NONE = -1;
	public static final int SHADER_OTHER = 0;
	public static final int SHADER_3D_FREE = 1;
	public static final int SHADER_GUI_FREE = 2;
	public static final int SHADER_VR_FREE = 3;
	public static final int SHADER_LABEL_FREE = 4;
	
	public static final int SHADER_3D_MODERN = 5;
	public static final int SHADER_GUI_MODERN = 6;
	public static final int SHADER_VR_MODERN = 7;
	public static final int SHADER_LABEL_MODERN = 8;
	
	private String vertexShaderPath;
	private String geometryShaderPath;
	private String fragmentShaderPath;
	protected int vertexID;
	protected int geometryID;
	protected int fragmentID;
	protected int programID;
	private int shaderType = SHADER_NONE;
	private Map<String, Integer> locations = new HashMap<String, Integer>();

	public ShaderProgram(String shaderPath, String vertexShaderName, String fragmentShaderName, int shaderType){
		this.shaderType = shaderType;
		ShaderManager.registerShader(this);
		vertexShaderPath = shaderPath+File.separator+vertexShaderName;
		fragmentShaderPath = shaderPath+File.separator+fragmentShaderName;
		
		loadShader(false);
	}
	
	public ShaderProgram(String shaderPath, String vertexShaderName, String fragmentShaderName, String geometryShaderName, int shaderType){
		this.shaderType = shaderType;
		ShaderManager.registerShader(this);
		vertexShaderPath = shaderPath+File.separator+vertexShaderName;
		fragmentShaderPath = shaderPath+File.separator+fragmentShaderName;
		geometryShaderPath = shaderPath+File.separator+geometryShaderName;
		
		loadShader(true);
	}

	public void cleanup(){
		GL20.glDetachShader(programID, vertexID);
		GL20.glDetachShader(programID, fragmentID);
		GL20.glDeleteShader(vertexID);
		GL20.glDeleteShader(fragmentID);
		GL20.glDeleteProgram(programID);
	}
	
	protected void loadShader(boolean useGeometryShader){
		vertexID = ShaderManager.loadShader(vertexShaderPath, GL20.GL_VERTEX_SHADER);
		fragmentID = ShaderManager.loadShader(fragmentShaderPath, GL20.GL_FRAGMENT_SHADER);
		if(useGeometryShader) geometryID = ShaderManager.loadShader(geometryShaderPath, GL32.GL_GEOMETRY_SHADER);
		programID = GL20.glCreateProgram();
		GL20.glAttachShader(programID, vertexID);
		GL20.glAttachShader(programID, fragmentID);
		if(useGeometryShader) GL20.glAttachShader(programID, geometryID);
		bindAttributes();
		GL20.glLinkProgram(programID);
		GL20.glValidateProgram(programID);
		GL20.glUseProgram(programID);
		getAllUniformLocations();
		connectTextureUnits();
		System.out.println("[ShaderProgram] Shader loaded ! Type: "+shaderType);
	}

	protected abstract void bindAttributes();
	protected abstract void getAllUniformLocations();
	protected abstract void connectTextureUnits();
	protected abstract void prepare();
	protected abstract void stop();
	
	public void use(){
		ShaderManager.useShader(this);
		prepare();
	}
	

	protected int getUniformLocation(String uniformName){
		int uniformLocation = GL20.glGetUniformLocation(programID, uniformName);
		if(uniformLocation < 0) System.err.println("[ShaderProgram] Error loading shader: Uniform location not found ("+uniformLocation+"): "+uniformName);
		else locations.put(uniformName, uniformLocation);
		return uniformLocation;
	}
	
	protected void bindAttribute(int attribute, String variableName){
		GL20.glBindAttribLocation(programID, attribute, variableName);
	}
	
	public int getShaderType(){
		return shaderType;
	}

	public int getLocation(String string) {
		Integer key = locations.get(string);
		if(key == null){
			System.err.println("[ShaderProgram] Uniform "+string+" not found in shader !");
			return -1;
		}
		return key;
	}
}
