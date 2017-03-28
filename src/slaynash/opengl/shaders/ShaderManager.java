package slaynash.opengl.shaders;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import slaynash.opengl.Infos;
import slaynash.opengl.textureUtils.TextureManager;
import slaynash.opengl.utils.DisplayManager;
import slaynash.world3d.loader.PointLight;

public class ShaderManager {
	
	private static final int MAX_POINTLIGHTS = 4;
	private static String shader2dLocation = Infos.getInstallPath()+"/"+Infos.getShaderPath()+"/gui";
	private static String shader3dLocation = Infos.getInstallPath()+"/"+Infos.getShaderPath()+"/world";
	private static String shaderLabelLocation = Infos.getInstallPath()+"/"+Infos.getShaderPath()+"/label";
	private static String shaderAIOLocation = Infos.getInstallPath()+"/res/shaders_aio/aio";
	
	private static int shader2dP, shader2dV, shader2dF;
	private static int shader3dP, shader3dV, shader3dF;
	private static int shaderLabelP, shaderLabelV, shaderLabelF;
	private static int shaderAIOP, shaderAIOV, shaderAIOF;
	
	private static int shaderLabeltextureUnit = 0;
	private static int shaderLabelTexture_location;
	private static int shaderLabelVisibility_location;
	
	private static int shader2dtextureUnit = 0;
	private static int shader2dTexture_location;
	private static int shader2dTextmode_location;
	private static int shader2dColor_location;
	private static int shader2dTranslation_location;
	private static int shader2dInvertColor_location;
	private static int shader2dCombomode_location;
	private static int shader2dCBCuts_location;
	
	private static int shader3dColorTexture_unit = 0;
	private static int shader3dNormalTexture_unit = 1;
	private static int shader3dSpecularTexture_unit = 2;
	private static int shader3dColorTexture_location;
	private static int shader3dNormalTexture_location;
	private static int shader3dSpecularTexture_location;
	private static int shader3dSpecularFactor_location;
	private static int shader3dPointLightNumber_location;
	private static int[] shader3dPointLightsPosition_location;
	private static int[] shader3dPointLights_color_location;
	private static int[] shader3dPointLights_attenuation_location;
	
	private static int shaderAIOtextureColorUnit = 0;
	private static int shaderAIOtextureNormalUnit = 1;
	private static int shaderAIOtextureSpecularUnit = 2;
	private static int shaderAIORenderMode_location;
	private static int currentShader = 0;
	private static int lastActiveShader = 0;
	private static List<ShaderProgram> customShaders = new ArrayList<ShaderProgram>();
	
	public static void init2DShader() {
		if(Infos.getShaderMode().equals("AIO")) initAIOShader();
		if(shader2dP != 0 || shaderAIOP != 0) return;
		create2dShader(shader2dLocation);
	}
	
	public static void initLabelShader() {
		if(Infos.getShaderMode().equals("AIO"))initAIOShader();
		if(shaderLabelP != 0 || shaderAIOP != 0) return;
		createLabelShader(shaderLabelLocation);
	}
	
	public static void init3DShader() {
		if(Infos.getShaderMode().equals("AIO")) initAIOShader();
		if(shader3dP != 0 || shaderAIOP != 0) return;
		create3dShader(shader3dLocation);
	}
	
	public static void initAIOShader() {
		if(shaderAIOP != 0) return;
		createAIOShader(shaderAIOLocation);
	}

	private static void create2dShader(String shaderLocation) {
		System.out.println("loading 2d shader...");
		shader2dV = loadShader(shader2dLocation+".vs",GL20.GL_VERTEX_SHADER);
		shader2dF = loadShader(shader2dLocation+".fs",GL20.GL_FRAGMENT_SHADER);
		shader2dP = GL20.glCreateProgram();
		System.out.println("2D shader id: "+shader2dP);
		GL20.glAttachShader(shader2dP, shader2dV);
		GL20.glAttachShader(shader2dP, shader2dF);
		bind2dShaderAttributes();
		GL20.glLinkProgram(shader2dP);
		GL20.glValidateProgram(shader2dP);
		getAll2dShaderUniformLocations();
		connectShader2dTextureUnits();
		System.out.println("2d shader loaded !");
	}
	
	private static void createLabelShader(String shaderLocation) {
		System.out.println("Loading Label shader...");
		shaderLabelV = loadShader(shaderLabelLocation+".vs",GL20.GL_VERTEX_SHADER);
		shaderLabelF = loadShader(shaderLabelLocation+".fs",GL20.GL_FRAGMENT_SHADER);
		shaderLabelP = GL20.glCreateProgram();
		System.out.println("Label shader id: "+shaderLabelP);
		GL20.glAttachShader(shaderLabelP, shaderLabelV);
		GL20.glAttachShader(shaderLabelP, shaderLabelF);
		bindLabelShaderAttributes();
		GL20.glLinkProgram(shaderLabelP);
		GL20.glValidateProgram(shaderLabelP);
		getAllLabelShaderUniformLocations();
		connectShaderLabelTextureUnits();
		System.out.println("Label shader loaded !");
	}
	
	private static void create3dShader(String shaderLocation) {
		System.out.println("loading 3d shader...");
		shader3dV = loadShader(shader3dLocation+".vs",GL20.GL_VERTEX_SHADER);
		shader3dF = loadShader(shader3dLocation+".fs",GL20.GL_FRAGMENT_SHADER);
		shader3dP = GL20.glCreateProgram();
		System.out.println("3D shader id: "+shader3dP);
		GL20.glAttachShader(shader3dP, shader3dV);
		GL20.glAttachShader(shader3dP, shader3dF);
		bind3dShaderAttributes();
		GL20.glLinkProgram(shader3dP);
		GL20.glValidateProgram(shader3dP);
		getAll3dShaderUniformLocations();
		connectShader3dTextureUnits();
		System.out.println("3d shader loaded !");
	}
	
	private static void createAIOShader(String shaderLocation) {
		System.out.println("loading AIO shader...");
		shaderAIOV = loadShader(shaderAIOLocation+".vs",GL20.GL_VERTEX_SHADER);
		shaderAIOF = loadShader(shaderAIOLocation+".fs",GL20.GL_FRAGMENT_SHADER);
		shaderAIOP = GL20.glCreateProgram();
		System.out.println("AIO shader id: "+shaderAIOP);
		GL20.glAttachShader(shaderAIOP, shaderAIOV);
		GL20.glAttachShader(shaderAIOP, shaderAIOF);
		bindAIOShaderAttributes();
		GL20.glLinkProgram(shaderAIOP);
		GL20.glValidateProgram(shaderAIOP);
		getAllLabelShaderUniformLocations();
		getAll2dShaderUniformLocations();
		getAll3dShaderUniformLocations();
		shaderAIORenderMode_location = GL20.glGetUniformLocation(shaderAIOP,"renderMode");
		GL20.glUseProgram(shaderAIOP);
		currentShader = shaderAIOP;
		connectShaderAIOTextureUnits();
		GL20.glUseProgram(0);
		currentShader = 0;
		System.out.println("AIO shader loaded !");
	}
	//Label Shader
	private static void connectShaderLabelTextureUnits() {
		GL20.glUniform1i(shaderLabelTexture_location, shaderLabeltextureUnit);
	}

	private static void getAllLabelShaderUniformLocations() {
		shaderLabelVisibility_location = getLabelShaderUniformLocation("visibility");
	}

	private static void bindLabelShaderAttributes() {
		
	}
	
	protected static int getLabelShaderUniformLocation(String uniformName){
		if(shaderAIOP == 0) return GL20.glGetUniformLocation(shaderLabelP,uniformName);
		return GL20.glGetUniformLocation(shaderAIOP,uniformName);
	}
	
	protected static void bindLabelShaderAttribute(int attribute, String variableName){
		GL20.glBindAttribLocation(shaderLabelP, attribute, variableName);
	}
	
	//2D Shader
	private static void connectShader2dTextureUnits() {
		GL20.glUniform1i(shader2dTexture_location, shader2dtextureUnit);
	}

	private static void getAll2dShaderUniformLocations() {
		shader2dTextmode_location = get2dShaderUniformLocation("textmode");
		shader2dTexture_location = get2dShaderUniformLocation("texture");
		shader2dColor_location = get2dShaderUniformLocation("colour");
		shader2dTranslation_location = get2dShaderUniformLocation("translation");
		shader2dInvertColor_location = get2dShaderUniformLocation("invertColor");
		shader2dCombomode_location = get2dShaderUniformLocation("combomode");
		shader2dCBCuts_location = get2dShaderUniformLocation("cbCuts");
	}

	private static void bind2dShaderAttributes() {
		
	}
	
	protected static int get2dShaderUniformLocation(String uniformName){
		if(shaderAIOP == 0) return GL20.glGetUniformLocation(shader2dP,uniformName);
		return GL20.glGetUniformLocation(shaderAIOP,uniformName);
	}
	
	protected static void bind2dShaderAttribute(int attribute, String variableName){
		GL20.glBindAttribLocation(shader2dP, attribute, variableName);
	}
	//3d shader
	private static void connectShader3dTextureUnits() {
		GL20.glUniform1i(shader3dColorTexture_location, shader3dColorTexture_unit);
		GL20.glUniform1i(shader3dNormalTexture_location, shader3dNormalTexture_unit);
		GL20.glUniform1i(shader3dSpecularTexture_location, shader3dSpecularTexture_unit);
	}

	private static void getAll3dShaderUniformLocations() {
		shader3dColorTexture_location = get3dShaderUniformLocation("texture");
		shader3dNormalTexture_location = get3dShaderUniformLocation("textureNormal");
		shader3dSpecularTexture_location = get3dShaderUniformLocation("textureSpecular");
		shader3dSpecularFactor_location = get3dShaderUniformLocation("specularFactor");
		shader3dPointLightNumber_location = get3dShaderUniformLocation("pointLightNumber");
		shader3dPointLightsPosition_location = new int[MAX_POINTLIGHTS];
		shader3dPointLights_color_location = new int[MAX_POINTLIGHTS];
		shader3dPointLights_attenuation_location = new int[MAX_POINTLIGHTS];
		
		for(int i=0;i<MAX_POINTLIGHTS;i++){
			shader3dPointLightsPosition_location[i] = get3dShaderUniformLocation("pointlightPositions["+i+"]");
			shader3dPointLights_color_location[i] = get3dShaderUniformLocation("pointLights["+i+"].color");
			shader3dPointLights_attenuation_location[i] = get3dShaderUniformLocation("pointLights["+i+"].attenuation");
		}
	}

	private static void bind3dShaderAttributes() {
		
	}
	
	protected static int get3dShaderUniformLocation(String uniformName){
		if(shaderAIOP == 0) return GL20.glGetUniformLocation(shader3dP,uniformName);
		return GL20.glGetUniformLocation(shaderAIOP,uniformName);
	}
	
	protected static void bind3dShaderAttribute(int attribute, String variableName){
		GL20.glBindAttribLocation(shader3dP, attribute, variableName);
	}
	//AIO Shader
	private static void connectShaderAIOTextureUnits() {
		GL20.glUniform1i(shaderLabelTexture_location, shaderAIOtextureColorUnit);
		GL20.glUniform1i(shader2dTexture_location, shaderAIOtextureColorUnit);
		GL20.glUniform1i(shader3dColorTexture_location, shaderAIOtextureColorUnit);
		GL20.glUniform1i(shader3dNormalTexture_location, shaderAIOtextureNormalUnit);
		GL20.glUniform1i(shader3dSpecularTexture_location, shaderAIOtextureSpecularUnit);
	}
	
	private static void bindAIOShaderAttributes() {
		
	}
	
	protected static void bindAIOShaderAttribute(int attribute, String variableName){
		GL20.glBindAttribLocation(shaderAIOP, attribute, variableName);
	}
	//---------------
	public static void cleanUp(int shaderID, int vertexID, int fragmentID){
		stopShader();
		GL20.glDetachShader(shaderID, vertexID);
		GL20.glDetachShader(shaderID, fragmentID);
		GL20.glDeleteShader(vertexID);
		GL20.glDeleteShader(fragmentID);
		GL20.glDeleteProgram(shaderID);
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
		if(shaderAIOP != 0){
			GL20.glUseProgram(shaderAIOP);
			GL20.glUniform1f(shaderAIORenderMode_location, 0f);
			currentShader = shaderAIOP;
			return;
		}
		GL20.glUseProgram(shaderLabelP);
		currentShader = shaderLabelP;
	}

	public static void start2DShader() {
		if(shaderAIOP != 0){
			GL20.glUseProgram(shaderAIOP);
			GL20.glUniform1f(shaderAIORenderMode_location, 1f);
			currentShader = shaderAIOP;
			return;
		}
		GL20.glUseProgram(shader2dP);
		currentShader = shader2dP;
	}
	
	public static void start3DShader() {
		if(shaderAIOP != 0){
			GL20.glUseProgram(shaderAIOP);
			GL20.glUniform1f(shaderAIORenderMode_location, 2f);
			currentShader = shaderAIOP;
			return;
		}
		GL20.glUseProgram(shader3dP);
		currentShader = shader3dP;
	}
	/*
	public static void start3DShader() {
		GL20.glUseProgram(shader2dP);
		currentShader = shader2dP;
	}
	*/

	public static void stopShader() {
		GL20.glUseProgram(0);
		currentShader = 0;
	}

	public static void setTextMode() {
		GL20.glUniform1f(shader2dTextmode_location, 1f);
	}
	
	public static void exitTextMode() {
		GL20.glUniform1f(shader2dTextmode_location, 0f);
	}

	public static int bind2DTextureUnit() {
		return shader2dtextureUnit;
	}
	
	public static int bindLabelTextureUnit() {
		return shaderLabeltextureUnit;
	}
	
	public static int bind3DTextureColorUnit() {
		if(shaderAIOP != 0) return shaderAIOtextureColorUnit;
		return shader3dColorTexture_unit;
	}
	
	public static int bind3DTextureNormalUnit() {
		if(shaderAIOP != 0) return shaderAIOtextureNormalUnit;
		return shader3dNormalTexture_unit;
	}
	
	public static int bind3DTextureSpecularUnit() {
		if(shaderAIOP != 0) return shaderAIOtextureSpecularUnit;
		return shader3dSpecularTexture_unit;
	}

	public static void load2dColor(Vector3f colour) {
		GL20.glUniform3f(shader2dColor_location, colour.x, colour.y, colour.z);
	}

	public static void load2dTranslation(Vector2f position) {
		GL20.glUniform2f(shader2dTranslation_location, position.x, position.y);
	}

	public static void cleanUp() {
		if(shader2dP != 0) cleanUp(shader2dP, shader2dV, shader2dF);
		if(shaderLabelP != 0) cleanUp(shaderLabelP, shaderLabelV, shaderLabelF);
		if(shader3dP != 0) cleanUp(shader3dP, shader3dV, shader3dF);
		for(ShaderProgram shader:customShaders) shader.cleanup();
	}

	public static void set2dColorsInverted() {
		GL20.glUniform1f(shader2dInvertColor_location, 1f);
	}
	
	public static void set2dColorsNormal() {
		GL20.glUniform1f(shader2dInvertColor_location, 0f);
	}

	public static void setLabelVisibility(float vis) {
		GL20.glUniform1f(shaderLabelVisibility_location, vis);
	}

	public static void setComboBoxMode(boolean b) {
		GL20.glUniform1f(shader2dCombomode_location, b ? 1f : 0f);
		
	}

	public static void loadComboBoxCuts(float top, float bot) {
		GL20.glUniform2f(shader2dCBCuts_location, -top+DisplayManager.getHeight(), -bot+DisplayManager.getHeight());
		//System.out.println(top+">P<"+bot);
	}
	


	public static void bind2DShaderTexture(String textureLocation) {
		bind2DShaderTextureID(TextureManager.getTextureID(textureLocation));
	}
	
	public static void bind3DShaderTexture(String textureLocation) {
		bind3DShaderColorTextureID(TextureManager.getTextureID(textureLocation));
	}
	
	public static void bind2DShaderTextureID(int textureID) {
		int unit = ShaderManager.bind2DTextureUnit();
		GL13.glActiveTexture(GL13.GL_TEXTURE0+unit);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);
	}
	
	public static void bindLabelShaderTextureID(int textureID) {
		int unit = ShaderManager.bindLabelTextureUnit();
		GL13.glActiveTexture(GL13.GL_TEXTURE0+unit);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);
	}

	public static void bind3DShaderColorTextureID(int textureID) {
		int unit = ShaderManager.bind3DTextureColorUnit();
		//System.out.println("D:"+unit);
		GL13.glActiveTexture(GL13.GL_TEXTURE0+unit);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);
	}

	public static void bind3DShaderDefaultColorTexture() {
		int unit = ShaderManager.bind3DTextureColorUnit();
		GL13.glActiveTexture(GL13.GL_TEXTURE0+unit);
		//System.out.println("D:"+unit);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, TextureManager.getDefaultTextureID());
	}
	
	public static void bind3DShaderNormalTextureID(int textureID) {
		int unit = ShaderManager.bind3DTextureNormalUnit();
		GL13.glActiveTexture(GL13.GL_TEXTURE0+unit);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);
	}

	public static void bind3DShaderDefaultNormalTexture() {
		int unit = ShaderManager.bind3DTextureNormalUnit();
		GL13.glActiveTexture(GL13.GL_TEXTURE0+unit);
		//System.out.println("N:"+unit);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, TextureManager.getTextureID("res/textures/default_normal.png"));
	}
	
	public static void bind3DShaderSpecularTextureID(int textureID) {
		int unit = ShaderManager.bind3DTextureSpecularUnit();
		GL13.glActiveTexture(GL13.GL_TEXTURE0+unit);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);
	}

	public static void bind3DShaderDefaultSpecularTexture() {
		int unit = ShaderManager.bind3DTextureSpecularUnit();
		GL13.glActiveTexture(GL13.GL_TEXTURE0+unit);
		//System.out.println("S:"+unit);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, TextureManager.getTextureID("res/textures/default_specular.png"));
	}

	public static void bind3DSpecularFactor(float specularFactor) {
		GL20.glUniform1f(shader3dSpecularFactor_location, specularFactor);
	}

	public static void bind3DShaderLights(List<PointLight> lights) {
		GL20.glUniform1f(shader3dPointLightNumber_location, lights.size());
		for(int i=0;i<lights.size() && i<MAX_POINTLIGHTS;i++){
			PointLight l = lights.get(i);
			GL20.glUniform3f(shader3dPointLightsPosition_location[i], l.getPosition().x, l.getPosition().y, l.getPosition().z);
			GL20.glUniform3f(shader3dPointLights_color_location[i], l.getColor()[0], l.getColor()[1], l.getColor()[2]);
			GL20.glUniform3f(shader3dPointLights_attenuation_location[i], l.getAttenuation()[0], l.getAttenuation()[1], l.getAttenuation()[2]);
		}
	}
	
	public static void setSpecialShaderMode(boolean specialShaderMode){
		if(specialShaderMode) lastActiveShader = currentShader;
		else{
			currentShader = lastActiveShader;
			GL20.glUseProgram(currentShader);
		}
	}

	public static void registerShader(ShaderProgram shaderProgram) {
		customShaders.add(shaderProgram);
	}
	
	/*
	protected void loadTransformationMatrix(Matrix4f matrix){
		super.loadMatrix(location_transformationMatrix, matrix);
	}
	
	protected void loadViewMatrix(Matrix4f viewMatrix){
		super.loadMatrix(location_viewMatrix, viewMatrix);
	}
	
	protected void loadProjectionMatrix(Matrix4f projection){
		super.loadMatrix(location_projectionMatrix, projection);
	}
	
	
	
	
	public static Matrix4f createViewMatrix(Vector3f pos, Vector3f ang) {
		Matrix4f viewMatrix = new Matrix4f();
		viewMatrix.setIdentity();
		Matrix4f.rotate((float) Math.toRadians(ang.x), new Vector3f(1, 0, 0), viewMatrix,
				viewMatrix);
		Matrix4f.rotate((float) Math.toRadians(ang.y), new Vector3f(0, 1, 0), viewMatrix, viewMatrix);
		Vector3f cameraPos = pos;
		Vector3f negativeCameraPos = new Vector3f(-cameraPos.x,-cameraPos.y,-cameraPos.z);
		Matrix4f.translate(negativeCameraPos, viewMatrix, viewMatrix);
		return viewMatrix;
	}
	
	public static Matrix4f createTransformationMatrix(Vector3f translation, float rx, float ry,
			float rz, float scale) {
		Matrix4f matrix = new Matrix4f();
		matrix.setIdentity();
		Matrix4f.translate(translation, matrix, matrix);
		Matrix4f.rotate((float) Math.toRadians(rx), new Vector3f(1,0,0), matrix, matrix);
		Matrix4f.rotate((float) Math.toRadians(ry), new Vector3f(0,1,0), matrix, matrix);
		Matrix4f.rotate((float) Math.toRadians(rz), new Vector3f(0,0,1), matrix, matrix);
		Matrix4f.scale(new Vector3f(scale,scale,scale), matrix, matrix);
		return matrix;
	}
	*/
}
