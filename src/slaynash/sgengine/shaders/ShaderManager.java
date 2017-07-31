package slaynash.sgengine.shaders;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import slaynash.sgengine.Configuration;
import slaynash.sgengine.textureUtils.TextureManager;
import slaynash.sgengine.utils.DisplayManager;
import slaynash.sgengine.world3d.loader.PointLight;

public class ShaderManager {
	
	public static final int TEXTURE_COLOR = 0;
	public static final int TEXTURE_NORMAL = 1;
	public static final int TEXTURE_SPECULAR = 2;
	
	
	private static List<ShaderProgram> shaders = new ArrayList<ShaderProgram>();
	
	private static ShaderProgram shaderLabel;
	private static ShaderProgram shaderGUI;
	private static ShaderProgram shader3d;
	private static ShaderProgram shaderVR;
	
	private static ShaderProgram currentShader;
	
	
	
	private static FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(16);
	
	public static void initGUIShader() {
		if(shaderGUI == null) shaderGUI = Configuration.getRenderMethod() == Configuration.RENDER_FREE ? new ShaderGUI() : new ModernShaderGUI();
	}
	
	public static void initLabelShader() {
		if(shaderLabel == null) shaderLabel = Configuration.getRenderMethod() == Configuration.RENDER_FREE ? new ShaderLabel() : new ModernShaderLabel();
	}
	
	public static void init3DShader() {
		if(shader3d == null) shader3d = Configuration.getRenderMethod() == Configuration.RENDER_FREE ? new Shader3D() : new ModernShader3D();
	}
	
	public static void initVRShader() {
		if(shaderVR == null) shaderVR = Configuration.getRenderMethod() == Configuration.RENDER_FREE ? new ShaderVR() : new ModernShaderVR();
	}
	
	
	
	
	
	public static void cleanUp() {
		stopShader();
		for(ShaderProgram shader:shaders) shader.cleanup();
	}
	
	static int loadShader(String file, int type){
		StringBuilder shaderSource = new StringBuilder();
		try{
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line;
			while((line = reader.readLine())!=null){
				shaderSource.append(line).append("//\n");
			}
			reader.close();
		}catch(IOException e){
			e.printStackTrace();
			System.exit(-1);
		}
		int shaderID = GL20.glCreateShader(type);
		GL20.glShaderSource(shaderID, shaderSource);
		GL20.glCompileShader(shaderID);
		if(GL20.glGetShaderi(shaderID, GL20.GL_COMPILE_STATUS )== GL11.GL_FALSE){
			System.out.println(GL20.glGetShaderInfoLog(shaderID, 500));
			System.err.println("Could not compile shader! ("+file+")");
			System.exit(-1);
		}
		return shaderID;
	}
	
	
	
	public static void startLabelShader() {
		if(shaderLabel == null) initLabelShader();
		shaderLabel.use();
	}

	public static void start2DShader() {
		if(shaderGUI == null) initGUIShader();
		shaderGUI.use();
	}
	
	public static void start3DShader() {
		if(shader3d == null) init3DShader();
		shader3d.use();
	}
	
	public static void startVRShader() {
		if(shaderVR == null) initVRShader();
		shaderVR.use();
	}
	
	
	
	public static void stopShader() {
		if(currentShader == null) return;
		currentShader.stop();
		currentShader = null;
		GL20.glUseProgram(0);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

	
	

	

	public static void shaderLabel_setVisibility(float vis) {
		GL20.glUniform1f(shaderLabel.getLocation("visibility"), vis);
	}
	public static void shaderLabel_bindTextureID(int textureID, int textureType) {
		GL13.glActiveTexture(GL13.GL_TEXTURE0+textureType);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);
	}
	
	
	
	
	
	
	
	
	
	

	public static void shaderGUI_setTextMode() {
		GL20.glUniform1f(shaderGUI.getLocation("textmode"), 1f);
	}
	
	public static void shaderGUI_exitTextMode() {
		GL20.glUniform1f(shaderGUI.getLocation("textmode"), 0f);
	}
	
	public static void shaderGUI_loadColor(Vector3f colour) {
		GL20.glUniform3f(shaderGUI.getLocation("colour"), colour.x, colour.y, colour.z);
	}
	
	public static void shaderGUI_loadTranslation(Vector2f position) {
		GL20.glUniform2f(shaderGUI.getLocation("translation"), position.x, position.y);
	}
	public static void shaderGUI_setColorsInverted(boolean inverted) {
		GL20.glUniform1f(shaderGUI.getLocation("invertColor"), inverted?1:0);
	}

	public static void shaderGUI_setComboBoxMode(boolean b) {
		GL20.glUniform1f(shaderGUI.getLocation("combomode"), b ? 1f : 0f);
	}

	public static void shaderGUI_loadComboBoxCuts(float top, float bot) {
		GL20.glUniform2f(shaderGUI.getLocation("cbCuts"), -top+DisplayManager.getHeight(), -bot+DisplayManager.getHeight());
	}
	
	public static void shaderGUI_bindTextureID(int textureID, int textureType) {
		GL13.glActiveTexture(GL13.GL_TEXTURE0+textureType);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);
	}
	
	
	
	
	
	
	
	
	
	
	
	public static void shader3d_bindTextureID(int textureID, int textureType) {
		GL13.glActiveTexture(GL13.GL_TEXTURE0+textureType);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);
	}

	public static void shader3d_bindDefaultColorTexture() {
		shader3d_bindTextureID(TextureManager.getDefaultTextureID(), TEXTURE_COLOR);
	}

	public static void shader3d_bindDefaultNormalTexture() {
		shader3d_bindTextureID(TextureManager.getTextureID("res/textures/default_normal.png"), TEXTURE_NORMAL);
	}

	public static void shader3d_bindDefaultSpecularTexture() {
		shader3d_bindTextureID(TextureManager.getTextureID("res/textures/default_normal.png"), TEXTURE_SPECULAR);
	}

	public static void shader3d_bindSpecularFactor(float specularFactor) {
		if(Configuration.getRenderMethod() != Configuration.RENDER_MODERN) return;
		GL20.glUniform1f(shader3d.getLocation("specularFactor"), specularFactor);
	}
	
	public static void shader3d_setUseNormalMap(boolean useNormalMap) {
		if(Configuration.getRenderMethod() != Configuration.RENDER_MODERN) return;
		GL20.glUniform1f(shader3d.getLocation("usesNormalMap"), useNormalMap?1:0);
	}
	
	public static void shader3d_setUseSpecularMap(boolean useSpecularMap) {
		if(Configuration.getRenderMethod() != Configuration.RENDER_MODERN) return;
		GL20.glUniform1f(shader3d.getLocation("usesSpecularMap"), useSpecularMap?1:0);
	}
	
	public static void shader3d_loadTransformationMatrix(Matrix4f matrix){
		matrix.store(matrixBuffer);
		matrixBuffer.flip();
		GL20.glUniformMatrix4(shader3d.getLocation("mMatrix"), false, matrixBuffer);
	}
	
	public static void shader3d_loadViewMatrix(Matrix4f viewMatrix){
		viewMatrix.store(matrixBuffer);
		matrixBuffer.flip();
		GL20.glUniformMatrix4(shader3d.getLocation("vMatrix"), false, matrixBuffer);
	}
	 
	public static void shader3d_loadProjectionMatrix(Matrix4f projectionMatrix){
		projectionMatrix.store(matrixBuffer);
		matrixBuffer.flip();
		GL20.glUniformMatrix4(shader3d.getLocation("pMatrix"), false, matrixBuffer);
	}
	
	public static void shader3d_bindShineDamper(float shineDamper) {
		if(Configuration.getRenderMethod() != Configuration.RENDER_MODERN) return;
		GL20.glUniform1f(shader3d.getLocation("shineDamper"), shineDamper);
	}
	
	public static void shader3d_bindReflectivity(float reflectivity) {
		if(Configuration.getRenderMethod() != Configuration.RENDER_MODERN) return;
		GL20.glUniform1f(shader3d.getLocation("reflectivity"), reflectivity);
	}
	
	public static void shader3d_loadLights(List<PointLight> lights, Matrix4f viewMatrix){
		if(Configuration.getRenderMethod() != Configuration.RENDER_MODERN) return;
		for(int i=0;i<Configuration.MAX_LIGHTS;i++){
			if(i<lights.size()){
				Vector3f eyeSpacePosition = getEyeSpacePosition(lights.get(i), viewMatrix);
				float[] color = lights.get(i).getColor();
				float[] attenuation = lights.get(i).getAttenuation();
				GL20.glUniform3f(shader3d.getLocation("lightPosition["+i+"]"), eyeSpacePosition.x, eyeSpacePosition.y, eyeSpacePosition.z);
				GL20.glUniform3f(shader3d.getLocation("lightColour["+i+"]"), color[0], color[1], color[2]);
				GL20.glUniform3f(shader3d.getLocation("attenuation["+i+"]"), attenuation[0], attenuation[1], attenuation[2]);
			}else{
				GL20.glUniform3f(shader3d.getLocation("lightPosition["+i+"]"), 0, 0, 0);
				GL20.glUniform3f(shader3d.getLocation("lightColour["+i+"]"), 0, 0, 0);
				GL20.glUniform3f(shader3d.getLocation("attenuation["+i+"]"), 1, 0, 0);
			}
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	public static void shaderVR_bindTextureID(int textureID, int textureType) {
		GL13.glActiveTexture(GL13.GL_TEXTURE0+textureType);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);
	}

	public static void shaderVR_bindDefaultColorTexture() {
		shaderVR_bindTextureID(TextureManager.getDefaultTextureID(), TEXTURE_COLOR);
	}
	
	public static void shaderVR_bindDefaultNormalTexture() {
		shaderVR_bindTextureID(TextureManager.getTextureID("res/textures/default_normal.png"), TEXTURE_NORMAL);
	}
	
	public static void shaderVR_bindDefaultSpecularTexture() {
		shaderVR_bindTextureID(TextureManager.getTextureID("res/textures/default_normal.png"), TEXTURE_SPECULAR);
	}
	
	
	public static void shaderVR_bindSpecularFactor(float specularFactor) {
		if(Configuration.getRenderMethod() != Configuration.RENDER_MODERN) return;
		GL20.glUniform1f(shaderVR.getLocation("specularFactor"), specularFactor);
	}
	
	public static void shaderVR_loadTransformationMatrix(Matrix4f matrix){
		matrix.store(matrixBuffer);
		matrixBuffer.flip();
		GL20.glUniformMatrix4(shaderVR.getLocation("mMatrix"), false, matrixBuffer);
	}
	 
	public static void shaderVR_loadViewMatrix(Matrix4f viewMatrix){
		viewMatrix.store(matrixBuffer);
		matrixBuffer.flip();
		GL20.glUniformMatrix4(shaderVR.getLocation("vMatrix"), false, matrixBuffer);
	}
	 
	public static void shaderVR_loadProjectionMatrix(Matrix4f projectionMatrix){
		projectionMatrix.store(matrixBuffer);
		matrixBuffer.flip();
		GL20.glUniformMatrix4(shaderVR.getLocation("pMatrix"), false, matrixBuffer);
	}
	
	public static void shaderVR_setUseNormalMap(boolean useNormalMap) {
		if(Configuration.getRenderMethod() != Configuration.RENDER_MODERN) return;
		GL20.glUniform1f(shaderVR.getLocation("usesNormalMap"), useNormalMap?1:0);
	}
	
	public static void shaderVR_setUseSpecularMap(boolean useSpecularMap) {
		if(Configuration.getRenderMethod() != Configuration.RENDER_MODERN) return;
		GL20.glUniform1f(shaderVR.getLocation("usesSpecularMap"), useSpecularMap?1:0);
	}
	
	public static void shaderVR_bindShineDamper(float shineDamper) {
		if(Configuration.getRenderMethod() != Configuration.RENDER_MODERN) return;
		GL20.glUniform1f(shaderVR.getLocation("shineDamper"), shineDamper);
	}
	
	public static void shaderVR_bindReflectivity(float reflectivity) {
		if(Configuration.getRenderMethod() != Configuration.RENDER_MODERN) return;
		GL20.glUniform1f(shaderVR.getLocation("reflectivity"), reflectivity);
	}
	
	public static void shaderVR_loadLights(List<PointLight> lights, Matrix4f viewMatrix){
		if(Configuration.getRenderMethod() != Configuration.RENDER_MODERN) return;
		for(int i=0;i<Configuration.MAX_LIGHTS;i++){
			if(i<lights.size()){
				Vector3f eyeSpacePosition = getEyeSpacePosition(lights.get(i), viewMatrix);
				float[] color = lights.get(i).getColor();
				float[] attenuation = lights.get(i).getAttenuation();
				GL20.glUniform3f(shaderVR.getLocation("lightPosition["+i+"]"), eyeSpacePosition.x, eyeSpacePosition.y, eyeSpacePosition.z);
				GL20.glUniform3f(shaderVR.getLocation("lightColour["+i+"]"), color[0], color[1], color[2]);
				GL20.glUniform3f(shaderVR.getLocation("attenuation["+i+"]"), attenuation[0], attenuation[1], attenuation[2]);
			}else{
				GL20.glUniform3f(shaderVR.getLocation("lightPosition["+i+"]"), 0, 0, 0);
				GL20.glUniform3f(shaderVR.getLocation("lightColour["+i+"]"), 0, 0, 0);
				GL20.glUniform3f(shaderVR.getLocation("attenuation["+i+"]"), 1, 0, 0);
			}
		}
	}
	
	
	
	
	
	
	public static void registerShader(ShaderProgram shaderProgram) {
		shaders.add(shaderProgram);
	}
	

	
	private static Vector3f getEyeSpacePosition(PointLight light, Matrix4f viewMatrix){
		Vector3f position = light.getPosition();
		Vector4f eyeSpacePos = new Vector4f(position.x,position.y, position.z, 1f);
		Matrix4f.transform(viewMatrix, eyeSpacePos, eyeSpacePos);
		return new Vector3f(eyeSpacePos);
	}

	public static void useShader(ShaderProgram shaderProgram) {
		if(currentShader != null) currentShader.stop();
		currentShader = shaderProgram;
		GL20.glUseProgram(shaderProgram.programID);
	}

	public static ShaderProgram get3dShaderProgram() {
		return shader3d;
	}

	public static ShaderProgram getGUIShaderProgram() {
		return shaderGUI;
	}

	public static ShaderProgram getVRShaderProgram() {
		return shaderVR;
	}

	public static ShaderProgram getLabelShaderProgram() {
		return shaderLabel;
	}
}
