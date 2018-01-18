package slaynash.sgengine.shaders;

import java.io.File;
import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL32;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import slaynash.sgengine.Configuration;
import slaynash.sgengine.LogSystem;

public abstract class ShaderProgram {
	
	private String vertexShaderPath;
	private String geometryShaderPath;
	private String fragmentShaderPath;
	protected int vertexID;
	protected int geometryID;
	protected int fragmentID;
	protected int programID;
	protected Map<String, Integer> locations = new HashMap<String, Integer>();
	private Map<String, Object> datas = new HashMap<String, Object>();
	private ShaderProgram shadowShader = null;
	
	
	protected static FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(16);

	public ShaderProgram(String shaderPath, String vertexShaderName, String fragmentShaderName){
		ShaderManager.registerShader(this);
		vertexShaderPath = shaderPath+File.separator+vertexShaderName;
		fragmentShaderPath = shaderPath+File.separator+fragmentShaderName;
		
		loadShader(false);
	}
	
	public ShaderProgram(String shaderPath, String vertexShaderName, String fragmentShaderName, String geometryShaderName){
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
		LogSystem.out_println("[ShaderProgram] Shader loaded !");
	}

	protected abstract void bindAttributes();
	protected abstract void getAllUniformLocations();
	protected abstract void connectTextureUnits();
	public abstract void prepare();
	public abstract void stop();

	public abstract void bindModel(int modelID);
	
	public void use(){
		ShaderManager.useShader(this);
	}
	
	public void useDirect(){
		ShaderManager.useShaderDirect(this);
	}
	

	protected int getUniformLocation(String uniformName){
		int uniformLocation = GL20.glGetUniformLocation(programID, uniformName);
		if(uniformLocation < 0) System.err.println("[ShaderProgram] Error loading shader: Uniform location not found ("+uniformLocation+"): "+uniformName);
		locations.put(uniformName, uniformLocation);
		return uniformLocation;
	}
	
	protected void bindAttribute(int attribute, String variableName){
		GL20.glBindAttribLocation(programID, attribute, variableName);
	}

	protected int getLocation(String string) {
		Integer key = locations.get(string);
		if(key == null){
			return -1;
		}
		return key;
	}
	
	public void bindData(String locationName, Object value){
		/*
		if(value.equals(datas.get(locationName))) return;
		else datas.put(locationName, value);
		*/
		if(value == null) throw new NullPointerException("Trying to load null value to location "+locationName);
		if(Configuration.isUsingDeferredRender()) datas.put(locationName, value);
		else bindDataDirect(locationName, value);
	}
	
	public void bindDataDirect(String locationName, Object value){
		int location = getLocation(locationName);
		if(location != -1){
			if(value == null) {
				LogSystem.err_println("[ShaderProgram] Unable to bind data of type null in shader "+this.getClass()+".");
			}
			else if(value instanceof Matrix4f){
				((Matrix4f) value).store(matrixBuffer);
				matrixBuffer.flip();
				GL20.glUniformMatrix4(location, false, matrixBuffer.duplicate());
			}else if(value instanceof Float){
				GL20.glUniform1f(location, (Float)value);
			}else if(value instanceof Integer){
				GL20.glUniform1f(location, (Integer)value);
			}else if(value instanceof Vector3f){
				Vector3f v = new Vector3f().set((Vector3f) value);
				GL20.glUniform3f(location, v.x, v.y, v.z);
			}else if(value instanceof Vector2f){
				Vector2f v = new Vector2f().set((Vector2f) value);
				GL20.glUniform2f(location, v.x, v.y);
			}
			else{
				LogSystem.err_println("[ShaderProgram] Unable to bind data of type "+value.getClass()+" in shader "+this.getClass()+".");
			}
		}
	}

	public void bindDatas(Map<String, Object> datas) {
		for(Entry<String, Object> data:datas.entrySet()){
			bindData(data.getKey(), data.getValue());
		}
	}
	
	public void bindDatasDirect(Map<String, Object> datas) {
		for(Entry<String, Object> data:datas.entrySet()){
			bindDataDirect(data.getKey(), data.getValue());
		}
	}

	public Map<String, Object> getDatas() {
		return new HashMap<String, Object>(datas);
	}
	
	public void setShadowShader(ShaderProgram shadowShaderChild) {
		this.shadowShader = shadowShaderChild;
	}
	
	public ShaderProgram getShadowShader() {
		return shadowShader;
	}

	public boolean isCastingShadow() {
		return false;
	}
}
