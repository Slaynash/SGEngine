package slaynash.opengl.shaders;

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

import slaynash.opengl.Infos;
import slaynash.opengl.textureUtils.TextureManager;
import slaynash.opengl.utils.DisplayManager;

public class ShaderManager {
	private static String shader2dLocation = Infos.getInstallPath()+"/"+Infos.getShaderPath()+"/gui";
	private static String shader3dLocation = Infos.getInstallPath()+"/"+Infos.getShaderPath()+"/world";
	private static String shaderLabelLocation = Infos.getInstallPath()+"/"+Infos.getShaderPath()+"/label";
	private static String shaderVRLocation = Infos.getInstallPath()+"/"+Infos.getShaderPath()+"/vr";
	private static String shaderAIOLocation = Infos.getInstallPath()+"/"+Infos.getShaderPath()+"/aio";
	
	private static int shader2dP, shader2dV, shader2dF;
	private static int shader3dP, shader3dV, shader3dF;
	private static int shaderLabelP, shaderLabelV, shaderLabelF;
	private static int shaderVRP, shaderVRV, shaderVRF;
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
	
	private static int shaderVRColorTexture_unit = 0;
	private static int shaderVRNormalTexture_unit = 1;
	private static int shaderVRSpecularTexture_unit = 2;
	private static int shaderVRColorTexture_location;
	private static int shaderVRNormalTexture_location;
	private static int shaderVRSpecularTexture_location;
	private static int shaderVRSpecularFactor_location;
	private static int shaderVRTransformationMatrix_location;
	private static int shaderVRMVPMatrix_location;
	
	private static int shaderAIOtextureColorUnit = 0;
	private static int shaderAIOtextureNormalUnit = 1;
	private static int shaderAIOtextureSpecularUnit = 2;
	private static int shaderAIORenderMode_location;
	private static int currentShader = 0;
	private static int lastActiveShader = 0;
	private static List<ShaderProgram> customShaders = new ArrayList<ShaderProgram>();
	
	private static FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(16);
	
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
	
	public static void initVRShader() {
		if(Infos.getShaderMode().equals("AIO")) initAIOShader();
		if(shaderVRP != 0 || shaderAIOP != 0) return;
		createVRShader(shaderVRLocation);
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
		GL20.glUseProgram(shader2dP);
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
		GL20.glUseProgram(shaderLabelP);
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
		GL20.glUseProgram(shader3dP);
		getAll3dShaderUniformLocations();
		connectShader3dTextureUnits();
		System.out.println("3d shader loaded !");
	}
	
	private static void createVRShader(String shaderLocation) {
		System.out.println("loading VR shader...");
		shaderVRV = loadShader(shaderVRLocation+".vs",GL20.GL_VERTEX_SHADER);
		shaderVRF = loadShader(shaderVRLocation+".fs",GL20.GL_FRAGMENT_SHADER);
		shaderVRP = GL20.glCreateProgram();
		System.out.println("VR shader id: "+shaderVRP);
		GL20.glAttachShader(shaderVRP, shaderVRV);
		GL20.glAttachShader(shaderVRP, shaderVRF);
		bindVRShaderAttributes();
		GL20.glLinkProgram(shaderVRP);
		GL20.glValidateProgram(shaderVRP);
		GL20.glUseProgram(shaderVRP);
		getAllVRShaderUniformLocations();
		connectShaderVRTextureUnits();
		System.out.println("VR shader loaded !");
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
		GL20.glUseProgram(shaderAIOP);
		getAllLabelShaderUniformLocations();
		getAll2dShaderUniformLocations();
		getAll3dShaderUniformLocations();
		getAllVRShaderUniformLocations();
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
	
	
	
	
	
	
	
	
	
	//3D shader
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
	
	
	
	
	
	
	
	
	
	
	
	
	//VR Shader
	private static void connectShaderVRTextureUnits() {
		GL20.glUniform1i(shaderVRColorTexture_location, shaderVRColorTexture_unit);
		GL20.glUniform1i(shaderVRNormalTexture_location, shaderVRNormalTexture_unit);
		GL20.glUniform1i(shaderVRSpecularTexture_location, shaderVRSpecularTexture_unit);
	}

	private static void getAllVRShaderUniformLocations() {
		shaderVRColorTexture_location = get3dShaderUniformLocation("texture");
		shaderVRNormalTexture_location = get3dShaderUniformLocation("textureNormal");
		shaderVRSpecularTexture_location = get3dShaderUniformLocation("textureSpecular");
		shaderVRSpecularFactor_location = get3dShaderUniformLocation("specularFactor");
		shaderVRMVPMatrix_location = getVRShaderUniformLocation("mvpMatrix");
		shaderVRTransformationMatrix_location = getVRShaderUniformLocation("mMatrix");
	}

	private static void bindVRShaderAttributes() {
		
	}
	
	protected static int getVRShaderUniformLocation(String uniformName){
		if(shaderAIOP == 0) return GL20.glGetUniformLocation(shaderVRP,uniformName);
		return GL20.glGetUniformLocation(shaderAIOP,uniformName);
	}
	
	protected static void bindVRShaderAttribute(int attribute, String variableName){
		GL20.glBindAttribLocation(shaderVRP, attribute, variableName);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	//AIO Shader
	private static void connectShaderAIOTextureUnits() {
		GL20.glUniform1i(shaderLabelTexture_location, shaderAIOtextureColorUnit);
		GL20.glUniform1i(shader2dTexture_location, shaderAIOtextureColorUnit);
		GL20.glUniform1i(shader3dColorTexture_location, shaderAIOtextureColorUnit);
		GL20.glUniform1i(shader3dNormalTexture_location, shaderAIOtextureNormalUnit);
		GL20.glUniform1i(shader3dSpecularTexture_location, shaderAIOtextureSpecularUnit);
		GL20.glUniform1i(shaderVRColorTexture_location, shaderAIOtextureColorUnit);
	}
	
	private static void bindAIOShaderAttributes() {
		
	}
	
	protected static void bindAIOShaderAttribute(int attribute, String variableName){
		GL20.glBindAttribLocation(shaderAIOP, attribute, variableName);
	}
	
	
	
	
	
	
	//---------------
	
	public static void cleanUp() {
		if(shader2dP != 0) cleanUp(shader2dP, shader2dV, shader2dF);
		if(shaderLabelP != 0) cleanUp(shaderLabelP, shaderLabelV, shaderLabelF);
		if(shader3dP != 0) cleanUp(shader3dP, shader3dV, shader3dF);
		if(shaderVRP != 0) cleanUp(shaderVRP, shaderVRV, shaderVRF);
		if(shaderAIOP != 0) cleanUp(shaderAIOP, shaderAIOV, shaderAIOF);
		for(ShaderProgram shader:customShaders) shader.cleanup();
	}
	
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
	
	public static void startVRShader() {
		if(shaderAIOP != 0){
			GL20.glUseProgram(shaderAIOP);
			GL20.glUniform1f(shaderAIORenderMode_location, 3f);
			currentShader = shaderAIOP;
			return;
		}
		GL20.glUseProgram(shaderVRP);
		currentShader = shaderVRP;
	}

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

	
	
	
	
	
	public static int bindLabelTextureUnit() {
		return shaderLabeltextureUnit;
	}
	
	
	public static int bind2DTextureUnit() {
		return shader2dtextureUnit;
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
	

	public static int bindVRTextureColorUnit() {
		if(shaderAIOP != 0) return shaderAIOtextureColorUnit;
		return shaderVRColorTexture_unit;
	}
	
	public static int bindVRTextureNormalUnit() {
		if(shaderAIOP != 0) return shaderAIOtextureNormalUnit;
		return shaderVRNormalTexture_unit;
	}
	
	public static int bindVRTextureSpecularUnit() {
		if(shaderAIOP != 0) return shaderAIOtextureSpecularUnit;
		return shaderVRSpecularTexture_unit;
	}

	
	

	

	public static void setLabelVisibility(float vis) {
		GL20.glUniform1f(shaderLabelVisibility_location, vis);
	}
	public static void bindLabelShaderTextureID(int textureID) {
		int unit = ShaderManager.bindLabelTextureUnit();
		GL13.glActiveTexture(GL13.GL_TEXTURE0+unit);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);
	}
	
	public static void load2dColor(Vector3f colour) {
		GL20.glUniform3f(shader2dColor_location, colour.x, colour.y, colour.z);
	}

	public static void load2dTranslation(Vector2f position) {
		GL20.glUniform2f(shader2dTranslation_location, position.x, position.y);
	}
	public static void set2dColorsInverted() {
		GL20.glUniform1f(shader2dInvertColor_location, 1f);
	}
	
	public static void set2dColorsNormal() {
		GL20.glUniform1f(shader2dInvertColor_location, 0f);
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
	
	public static void bind2DShaderTextureID(int textureID) {
		int unit = ShaderManager.bind2DTextureUnit();
		GL13.glActiveTexture(GL13.GL_TEXTURE0+unit);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);
	}
	
	
	
	
	public static void bind3DShaderColorTextureID(int textureID) {
		int unit = ShaderManager.bind3DTextureColorUnit();
		GL13.glActiveTexture(GL13.GL_TEXTURE0+unit);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);
	}

	public static void bind3DShaderDefaultColorTexture() {
		int unit = ShaderManager.bind3DTextureColorUnit();
		GL13.glActiveTexture(GL13.GL_TEXTURE0+unit);
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
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, TextureManager.getTextureID("res/textures/default_specular.png"));
	}

	public static void bind3DSpecularFactor(float specularFactor) {
		GL20.glUniform1f(shader3dSpecularFactor_location, specularFactor);
	}
	
	
	
	
	public static void bindVRShaderColorTextureID(int textureID) {
		int unit = ShaderManager.bindVRTextureColorUnit();
		GL13.glActiveTexture(GL13.GL_TEXTURE0+unit);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);
	}

	public static void bindVRShaderDefaultColorTexture() {
		int unit = ShaderManager.bindVRTextureColorUnit();
		GL13.glActiveTexture(GL13.GL_TEXTURE0+unit);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, TextureManager.getDefaultTextureID());
	}
	
	public static void bindVRShaderNormalTextureID(int textureID) {
		int unit = ShaderManager.bindVRTextureNormalUnit();
		GL13.glActiveTexture(GL13.GL_TEXTURE0+unit);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);
	}
	public static void bindVRShaderDefaultNormalTexture() {
		int unit = ShaderManager.bindVRTextureNormalUnit();
		GL13.glActiveTexture(GL13.GL_TEXTURE0+unit);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, TextureManager.getTextureID("res/textures/default_normal.png"));
	}
	public static void bindVRShaderSpecularTextureID(int textureID) {
		int unit = ShaderManager.bindVRTextureSpecularUnit();
		GL13.glActiveTexture(GL13.GL_TEXTURE0+unit);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);
	}
	public static void bindVRShaderDefaultSpecularTexture() {
		int unit = ShaderManager.bindVRTextureSpecularUnit();
		GL13.glActiveTexture(GL13.GL_TEXTURE0+unit);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, TextureManager.getTextureID("res/textures/default_specular.png"));
	}
	public static void bindVRSpecularFactor(float specularFactor) {
		GL20.glUniform1f(shaderVRSpecularFactor_location, specularFactor);
	}
	
	public static void loadVRShaderTransformationMatrix(Matrix4f matrix){
		matrix.store(matrixBuffer);
		matrixBuffer.flip();
		GL20.glUniformMatrix4(shaderVRTransformationMatrix_location, false, matrixBuffer);
	}
	 
	public static void loadVRShaderMVPMatrix(Matrix4f projection){
		projection.store(matrixBuffer);
		matrixBuffer.flip();
		GL20.glUniformMatrix4(shaderVRMVPMatrix_location, false, matrixBuffer);
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
}
