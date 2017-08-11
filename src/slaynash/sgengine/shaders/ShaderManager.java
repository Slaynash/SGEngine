package slaynash.sgengine.shaders;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import slaynash.sgengine.Configuration;
import slaynash.sgengine.LogSystem;
import slaynash.sgengine.deferredRender.DeferredRenderer;
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
	private static ShaderProgram shader2d;
	private static ShaderProgram shaderVR;
	
	private static ShaderProgram currentShader;
	
	
	
	//private static FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(16);
	
	public static void initGUIShader() {
		if(shaderGUI == null) shaderGUI = Configuration.getRenderMethod() == Configuration.RENDER_FREE ? new ShaderGUI() : new ModernShaderGUI();
	}
	
	public static void initLabelShader() {
		if(shaderLabel == null) shaderLabel = Configuration.getRenderMethod() == Configuration.RENDER_FREE ? new ShaderLabel() : new ModernShaderLabel();
	}
	
	public static void init3DShader() {
		if(shader3d == null) shader3d = Configuration.getRenderMethod() == Configuration.RENDER_FREE ? new Shader3D() : new ModernShader3D();
	}
	
	public static void init2DShader() {
		if(shader2d == null) shader2d = Configuration.getRenderMethod() == Configuration.RENDER_FREE ? new Shader2D() : new ModernShader2D();
	}
	
	public static void initVRShader() {
		if(shaderVR == null) shaderVR = Configuration.getRenderMethod() == Configuration.RENDER_FREE ? new ShaderVR() : new ModernShaderVR();
	}
	
	public static void set3dShader(ShaderProgram shader){
		shader3d = shader;
	}
	
	public static void set2dShader(ShaderProgram shader){
		shader2d = shader;
	}
	
	public static void setVRShader(ShaderProgram shader){
		shaderVR = shader;
	}
	
	public static void setGUIShader(ShaderProgram shader){
		shaderGUI = shader;
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
			e.printStackTrace(LogSystem.getErrStream());
			System.exit(-1);
		}
		int shaderID = GL20.glCreateShader(type);
		GL20.glShaderSource(shaderID, shaderSource);
		GL20.glCompileShader(shaderID);
		if(GL20.glGetShaderi(shaderID, GL20.GL_COMPILE_STATUS )== GL11.GL_FALSE){
			LogSystem.out_println(GL20.glGetShaderInfoLog(shaderID, 500));
			System.err.println("Could not compile shader! ("+file+")");
			System.exit(-1);
		}
		return shaderID;
	}
	
	
	
	public static void startLabelShader() {
		if(shaderLabel == null) initLabelShader();
		shaderLabel.use();
	}

	public static void startGUIShader() {
		if(shaderGUI == null) initGUIShader();
		shaderGUI.use();
	}
	
	public static void start3DShader() {
		if(shader3d == null) init3DShader();
		shader3d.use();
	}
	
	public static void start2DShader() {
		if(shader2d == null) init2DShader();
		shader2d.use();
	}
	
	public static void startVRShader() {
		if(shaderVR == null) initVRShader();
		shaderVR.use();
	}
	
	
	public static void startLabelShaderDirect() {
		if(shaderLabel == null) initLabelShader();
		shaderLabel.useDirect();
	}

	public static void startGUIShaderDirect() {
		if(shaderGUI == null) initGUIShader();
		shaderGUI.useDirect();
	}
	
	public static void start3DShaderDirect() {
		if(shader3d == null) init3DShader();
		shader3d.useDirect();
	}
	
	public static void start2DShaderDirect() {
		if(shader2d == null) init2DShader();
		shader2d.useDirect();
	}
	
	public static void startVRShaderDirect() {
		if(shaderVR == null) initVRShader();
		shaderVR.useDirect();
	}
	
	
	
	public static void stopShader() {
		if(Configuration.isUsingDeferredRender()) DeferredRenderer.addRenderStep((ShaderProgram)null);
		else{
			if(currentShader == null) return;
			currentShader.stop();
			currentShader = null;
			GL20.glUseProgram(0);
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

	
	

	

	public static void shader_setVisibility(float vis) {
		currentShader.bindData("visibility", vis);
	}
	public static void shader_bindTextureID(int textureID, int textureType) {
		GL13.glActiveTexture(GL13.GL_TEXTURE0+textureType);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);
	}
	
	
	
	
	
	
	
	
	
	

	public static void shader_setTextMode() {
		currentShader.bindData("textmode", 1);
	}
	
	public static void shader_exitTextMode() {
		currentShader.bindData("textmode", 0);
	}
	
	public static void shader_loadColor(Vector3f colour) {
		currentShader.bindData("colour", colour);
	}
	
	public static void shader_loadTranslation(Vector2f position) {
		currentShader.bindData("translation", position);
	}
	public static void shader_setColorsInverted(boolean inverted) {
		currentShader.bindData("invertColor", inverted?1f:0f);
	}

	public static void shader_setComboBoxMode(boolean b) {
		currentShader.bindData("combomode", b ? 1f : 0f);
	}

	public static void shader_loadComboBoxCuts(float top, float bot) {
		currentShader.bindData("cbCuts", new Vector2f(-top+DisplayManager.getHeight(), -bot+DisplayManager.getHeight()));
	}
	
	
	
	public static void shader_loadTransformationMatrix(Matrix4f matrix){
		currentShader.bindData("mMatrix", matrix);
	}
	
	public static void shader_loadViewMatrix(Matrix4f viewMatrix){
		currentShader.bindData("vMatrix", viewMatrix);
	}
	 
	public static void shader_loadProjectionMatrix(Matrix4f projectionMatrix){
		currentShader.bindData("pMatrix", projectionMatrix);
	}

	public static void shader_bindDefaultColorTexture() {
		shader_bindTextureID(TextureManager.getDefaultTextureID(), TEXTURE_COLOR);
	}
	
	public static void shader_loadZoom(float zoom){
		currentShader.bindData("zoom", zoom);
	}
	
	public static void shader_loadDisplayRatio(float displayRatio){
		currentShader.bindData("displayRatio", displayRatio);
	}
	
	
	
	
	
	
	
	
	

	public static void shader_bindDefaultNormalTexture() {
		shader_bindTextureID(TextureManager.getTextureID("res/textures/default_normal.png"), TEXTURE_NORMAL);
	}

	public static void shader_bindDefaultSpecularTexture() {
		shader_bindTextureID(TextureManager.getTextureID("res/textures/default_normal.png"), TEXTURE_SPECULAR);
	}

	public static void shader_bindSpecularFactor(float specularFactor) {
		if(Configuration.getRenderMethod() != Configuration.RENDER_MODERN) return;
		currentShader.bindData("specularFactor", specularFactor);
	}
	
	public static void shader_setUseNormalMap(boolean useNormalMap) {
		if(Configuration.getRenderMethod() != Configuration.RENDER_MODERN) return;
		currentShader.bindData("usesNormalMap", useNormalMap?1:0);
	}
	
	public static void shader_setUseSpecularMap(boolean useSpecularMap) {
		if(Configuration.getRenderMethod() != Configuration.RENDER_MODERN) return;
		currentShader.bindData("usesSpecularMap", useSpecularMap?1:0);
	}
	
	public static void shader_bindShineDamper(float shineDamper) {
		if(Configuration.getRenderMethod() != Configuration.RENDER_MODERN) return;
		currentShader.bindData("shineDamper", shineDamper);
	}
	
	public static void shader_bindReflectivity(float reflectivity) {
		if(Configuration.getRenderMethod() != Configuration.RENDER_MODERN) return;
		currentShader.bindData("reflectivity", reflectivity);
	}
	
	public static void shader_loadLights(List<PointLight> lights, Matrix4f viewMatrix){
		if(Configuration.getRenderMethod() != Configuration.RENDER_MODERN) return;
		for(int i=0;i<Configuration.MAX_LIGHTS;i++){
			if(i<lights.size()){
				Vector3f eyeSpacePosition = getEyeSpacePosition(lights.get(i), viewMatrix);
				float[] color = lights.get(i).getColor();
				float[] attenuation = lights.get(i).getAttenuation();
				currentShader.bindData("lightPosition["+i+"]", eyeSpacePosition);
				currentShader.bindData("lightColour["+i+"]", new Vector3f(color[0], color[1], color[2]));
				currentShader.bindData("attenuation["+i+"]", new Vector3f(attenuation[0], attenuation[1], attenuation[2]));
			}else{
				currentShader.bindData("lightPosition["+i+"]", new Vector3f(0, 0, 0));
				currentShader.bindData("lightColour["+i+"]", new Vector3f(0, 0, 0));
				currentShader.bindData("attenuation["+i+"]", new Vector3f(1, 0, 0));
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
		currentShader = shaderProgram;
		if(Configuration.isUsingDeferredRender()) DeferredRenderer.addRenderStep(shaderProgram);
		else useShaderDirect(shaderProgram);
	}
	
	public static void useShaderDirect(ShaderProgram shaderProgram) {
		if(currentShader != null) currentShader.stop();
		currentShader = shaderProgram;
		GL20.glUseProgram(shaderProgram.programID);
		shaderProgram.prepare();
	}

	public static ShaderProgram get3dShaderProgram() {
		return shader3d;
	}

	public static ShaderProgram getGUIShaderProgram() {
		return shaderGUI;
	}
	
	public static ShaderProgram get2DShaderProgram() {
		return shader2d;
	}

	public static ShaderProgram getVRShaderProgram() {
		return shaderVR;
	}

	public static ShaderProgram getLabelShaderProgram() {
		return shaderLabel;
	}
	
	public static ShaderProgram getCurrentShaderProgram() {
		return currentShader;
	}
}
