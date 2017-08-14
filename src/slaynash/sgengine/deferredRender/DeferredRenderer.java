package slaynash.sgengine.deferredRender;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL32;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import slaynash.sgengine.Configuration;
import slaynash.sgengine.LogSystem;
import slaynash.sgengine.shaders.ShaderManager;
import slaynash.sgengine.shaders.ShaderProgram;
import slaynash.sgengine.utils.LightsManager;
import slaynash.sgengine.utils.MatrixUtils;
import slaynash.sgengine.utils.VRUtils;
import slaynash.sgengine.world3d.loader.PointLight;

public class DeferredRenderer {

	private static ArrayList<ShaderRenderlist> shaderLists = new ArrayList<ShaderRenderlist>();
	private static Map<Integer, ArrayList<DeferredModelRenderer>> currentMap;
	
	private static Map<Object, Class<? extends DeferredModelRenderer>> modelRenderers = new HashMap<Object, Class<? extends DeferredModelRenderer>>();
	private static ArrayList<Integer> bindTextures = new ArrayList<Integer>();
	private static ShaderProgram currentShader;
	private static int[] depthCubemap;
	private static int[] depthMapFBO;
	
	public static final int SHADOW_WIDTH = 1024, SHADOW_HEIGHT = 1024;
	
	public static void initShadowMaps() {
		depthCubemap = new int[Configuration.MAX_LIGHTS];
		depthMapFBO = new int[Configuration.MAX_LIGHTS*6];
		
		for(int l=0;l<Configuration.MAX_LIGHTS;l++) {
			depthCubemap[l] = GL11.glGenTextures();
			GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, depthCubemap[l]);
			for (int i=0; i<6; i++) {
		        GL11.glTexImage2D(GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL11.GL_DEPTH_COMPONENT, 
		                     SHADOW_WIDTH, SHADOW_HEIGHT, 0, GL11.GL_DEPTH_COMPONENT, GL11.GL_FLOAT, (FloatBuffer)null);
			}
			GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
			GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
			GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
			GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
			GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL12.GL_TEXTURE_WRAP_R, GL12.GL_CLAMP_TO_EDGE);
			
			depthMapFBO[l] = GL30.glGenFramebuffers();
			GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, depthMapFBO[l]);
			GL32.glFramebufferTexture(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, depthCubemap[l], 0);
			GL11.glDrawBuffer(GL11.GL_NONE);
			GL11.glReadBuffer(GL11.GL_NONE);
			GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
		}
	}
	
	public static void renderWithShadowsAndCleanup(int eye) {
		renderShadows();
		VRUtils.setCurrentRenderEye(eye);
		render();
		cleanup();
	}

	public static void renderAndCleanup() {
		render();
		cleanup();
	}
	
	public static void renderWithShadows(int eye) {
		renderShadows();
		VRUtils.setCurrentRenderEye(eye);
		render();
	}
	
	public static void renderShadows() {
		
		Matrix4f projMat = MatrixUtils.createProjectionMatrix(Configuration.getZNear(), Configuration.getZFar(), 90f, (float)SHADOW_WIDTH/(float)SHADOW_HEIGHT);
		
		GL11.glViewport(0, 0, SHADOW_WIDTH, SHADOW_HEIGHT);
		for(int i=0;i<LightsManager.getPointlights().size();i++) {
			PointLight light = LightsManager.getPointlights().get(i);
			
			Matrix4f[] pMatrices = new Matrix4f[6];
			
			pMatrices[0] = Matrix4f.mul(
					projMat, MatrixUtils.createViewMatrix(light.getPosition(), Vector3f.add(new Vector3f(1,0,0), light.getPosition(), null), new Vector3f(0,-1,0)), null
			);
			pMatrices[1] = Matrix4f.mul(
					projMat, MatrixUtils.createViewMatrix(light.getPosition(), Vector3f.add(new Vector3f(-1,0,0), light.getPosition(), null), new Vector3f(0,-1,0)), null
			);
			pMatrices[2] = Matrix4f.mul(
					projMat, MatrixUtils.createViewMatrix(light.getPosition(), Vector3f.add(new Vector3f(0,1,0), light.getPosition(), null), new Vector3f(0,0,1)), null
			);
			pMatrices[3] = Matrix4f.mul(
					projMat, MatrixUtils.createViewMatrix(light.getPosition(), Vector3f.add(new Vector3f(0,-1,0), light.getPosition(), null), new Vector3f(0,0,-1)), null
			);
			pMatrices[4] = Matrix4f.mul(
					projMat, MatrixUtils.createViewMatrix(light.getPosition(), Vector3f.add(new Vector3f(0,0,1), light.getPosition(), null), new Vector3f(0,-1,0)), null
			);
			pMatrices[5] = Matrix4f.mul(
					projMat, MatrixUtils.createViewMatrix(light.getPosition(), Vector3f.add(new Vector3f(0,0,-1), light.getPosition(), null), new Vector3f(0,-1,0)), null
			);
			
			
			
			
			
			
			
			GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, depthMapFBO[i]);
			GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
			for(ShaderRenderlist map:shaderLists){//for each shader phase
				ShaderProgram shadowShader = map.getShader().getShadowShader();
				if(shadowShader == null) continue;
				shadowShader.useDirect();
				shadowShader.bindDataDirect("lightPos", light.getPosition());
				
				for(int j=0;j<6;j++) shadowShader.bindDataDirect("pMatrices["+j+"]", pMatrices[j]);
				
				for(Entry<Integer, ArrayList<DeferredModelRenderer>> entry:map.getObjectList().entrySet()){//for each models
					if(!entry.getValue().get(0).isCastingShadow()) break;
					shadowShader.bindModel(entry.getKey());
					for(DeferredModelRenderer dmr:entry.getValue()){
						shadowShader.bindDatasDirect(dmr.getShaderDatas());
						dmr.render();
					}
				}
				shadowShader.stop();
			}
			
			
		}
		
		
		for(int i=0;i<LightsManager.getPointlights().size();i++) {
			GL13.glActiveTexture(GL13.GL_TEXTURE0+ShaderManager.TEXTURE_SHADOWSMIN+i);
			GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, depthCubemap[i]);
		}
	}
	
	public static void render(){
		switch(Configuration.getDeferredRenderSortingMethod()){
			case TEXTURES_FOR_OBJECTS:
				for(ShaderRenderlist map:shaderLists){//for each shader phase
					ShaderProgram shader = map.getShader();
					shader.useDirect();
					if(shader.getShaderType() == ShaderProgram.SHADER_3D_MODERN) ShaderManager.shader_loadLights(LightsManager.getPointlights());
					for(Entry<Integer, ArrayList<DeferredModelRenderer>> entry:map.getObjectList().entrySet()){//for each models of this type
						shader.bindModel(entry.getKey());
						for(DeferredModelRenderer dmr:entry.getValue()){//for each render of this model
							shader.bindDatasDirect(dmr.getShaderDatas());
							for(int i=0;i<dmr.getTextureIDs().length;i++){
								GL13.glActiveTexture(GL13.GL_TEXTURE0+i);
								GL11.glBindTexture(GL11.GL_TEXTURE_2D, dmr.getTextureIDs()[i]);
							}
							dmr.render();
						}
					}
					shader.stop();
				}
				break;
			case OBJECTS_FOR_TEXTURES:
				for(ShaderRenderlist map:shaderLists){//for each shader phase
					bindTextures.clear();
					ShaderProgram shader = map.getShader();
					shader.useDirect();
					if(shader.getShaderType() == ShaderProgram.SHADER_3D_MODERN) ShaderManager.shader_loadLights(LightsManager.getPointlights());
					for(Entry<Integer, ArrayList<DeferredModelRenderer>> entry:map.getObjectList().entrySet()){//for each textures
						int textureID = entry.getKey();
						if(textureID != bindTextures.get(0)){
							bindTextures.set(0, textureID);
							GL13.glActiveTexture(GL13.GL_TEXTURE0+0);
							GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);
						}
						for(DeferredModelRenderer dmr:entry.getValue()){//for each models with this texture
							shader.bindDatasDirect(dmr.getShaderDatas());
							for(int i=1;i<dmr.getTextureIDs().length;i++){
								if(dmr.getTextureIDs()[i] != bindTextures.get(i)){
									bindTextures.set(i, dmr.getTextureIDs()[i]);
									GL13.glActiveTexture(GL13.GL_TEXTURE0+i);
									GL11.glBindTexture(GL11.GL_TEXTURE_2D, dmr.getTextureIDs()[i]);
								}
							}
							shader.bindModel(entry.getKey());
							dmr.render();
						}
					}
					shader.stop();
				}
				break;
		}
	}
	
	public static void cleanup(){
		currentShader = null;
		currentMap = null;
		bindTextures = new ArrayList<Integer>();
		shaderLists = new ArrayList<ShaderRenderlist>();
	}
	
	public static void addRenderStep(Object renderableModel){
		
		Class<? extends DeferredModelRenderer> dmrClass = modelRenderers.get(renderableModel.getClass());
		if(dmrClass == null){
			System.err.println("[DeferredRenderer] No DeferredModelRenderer found for class "+renderableModel.getClass());
			return;
		}
		DeferredModelRenderer renderModel = null;
		
		try {
			renderModel = dmrClass.newInstance();
			renderModel.bindModel(renderableModel);
		} catch (InstantiationException e) {
			e.printStackTrace(LogSystem.getErrStream());
		} catch (IllegalAccessException e) {
			e.printStackTrace(LogSystem.getErrStream());
		}
		
		ArrayList<DeferredModelRenderer> list = null;
		switch(Configuration.getDeferredRenderSortingMethod()){
			case TEXTURES_FOR_OBJECTS:
				list = currentMap.get(renderModel.getModelID());
				if(list == null){
					list = new ArrayList<DeferredModelRenderer>();
					currentMap.put(renderModel.getModelID(), list);
				}
				renderModel.setShaderDatas(currentShader.getDatas());
				list.add(renderModel);
				break;
			case OBJECTS_FOR_TEXTURES:
				list = currentMap.get(renderModel.getTextureIDs()[0]);
				if(list == null){
					list = new ArrayList<DeferredModelRenderer>();
					currentMap.put(renderModel.getTextureIDs()[0], list);
				}
				renderModel.setShaderDatas(currentShader.getDatas());
				list.add(renderModel);
				break;
		}
	}
	
	public static void addRenderStep(ShaderProgram shader){
		if(shader != null){
			currentMap = new HashMap<Integer, ArrayList<DeferredModelRenderer>>();
			currentShader = shader;
			shaderLists.add(new ShaderRenderlist(shader, currentMap));
		}
		else{
			currentMap = null;
			currentShader = null;
		}
	}
	
	public static void registerModelRenderer(Object modelrendererClass, Class<? extends DeferredModelRenderer> dmr){
		LogSystem.out_println("Registered DMR for class "+modelrendererClass.getClass());
		modelRenderers.put(modelrendererClass.getClass(), dmr);
	}
	
}
