package slaynash.sgengine.shaders;

import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import slaynash.sgengine.Configuration;
import slaynash.sgengine.LogSystem;

public class ShadowShader extends ShaderProgram {

	public ShadowShader() {
		super(Configuration.getAbsoluteInstallPath()+"/"+Configuration.getRelativeShaderPath(), "shadows/shadows.vs", "shadows/shadows.fs", "shadows/shadows.gs", ShaderProgram.SHADER_3D_SHADOWS);
		
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
	}

	@Override
	protected void getAllUniformLocations() {
		super.getUniformLocation("mMatrix");
		super.getUniformLocation("far_plane");
		super.getUniformLocation("lightPos");
		for(int i=0;i<6;i++){
			super.getUniformLocation("pMatrices["+i+"]");
		}
	}

	@Override
	protected void connectTextureUnits() {
		
	}

	@Override
	public void prepare() {
		bindDataDirect("far_plane", Configuration.getZFar());
	}

	@Override
	public void stop() {
		GL30.glBindVertexArray(0);
	}



	@Override
	public void bindModel(int modelID) {
		GL30.glBindVertexArray(modelID);
		GL20.glEnableVertexAttribArray(0);
	}
	
	public void bindDataDirect(String locationName, Object value){
		int location = getLocation(locationName);
		if(location != -1){
			if(value instanceof Matrix4f){
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

	protected int getLocation(String string) {
		Integer key = locations.get(string);
		if(key == null){
			return -1;
		}
		return key;
	}

}
