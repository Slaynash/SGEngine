package slaynash.sgengine.deferredRender;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import slaynash.sgengine.Configuration;
import slaynash.sgengine.LogSystem;
import slaynash.sgengine.shaders.ShaderProgram;

public class DeferredRenderer {

	private static ArrayList<ShaderRenderlist> shaderLists = new ArrayList<ShaderRenderlist>();
	private static Map<Integer, ArrayList<DeferredModelRenderer>> currentMap;
	
	private static Map<Object, Class<? extends DeferredModelRenderer>> modelRenderers = new HashMap<Object, Class<? extends DeferredModelRenderer>>();
	private static ArrayList<Integer> bindTextures = new ArrayList<Integer>();
	private static ShaderProgram currentShader;
	
	
	public static void renderWithShadowsAndCleanup() {
		renderShadows();
		render();
		cleanup();
	}

	public static void renderAndCleanup() {
		render();
		cleanup();
	}
	
	public static void renderWithShadows() {
		renderShadows();
		render();
	}
	
	public static void renderShadows() {
		//TODO render shadows from lights
	}
	
	public static void render(){
		switch(Configuration.getDeferredRenderSortingMethod()){
			case TEXTURES_FOR_OBJECTS:
				for(ShaderRenderlist map:shaderLists){//for each shader phase
					ShaderProgram shader = map.getShader();
					shader.useDirect();
					for(Entry<Integer, ArrayList<DeferredModelRenderer>> entry:map.getObjectList().entrySet()){//for each models
						shader.bindModel(entry.getKey());
						for(DeferredModelRenderer dmr:entry.getValue()){//for each textures in model
							shader.bindDatas(dmr.getShaderDatas());
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
					for(Entry<Integer, ArrayList<DeferredModelRenderer>> entry:map.getObjectList().entrySet()){//for each textures
						int textureID = entry.getKey();
						if(textureID != bindTextures.get(0)){
							bindTextures.set(0, textureID);
							GL13.glActiveTexture(GL13.GL_TEXTURE0+0);
							GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);
						}
						for(DeferredModelRenderer dmr:entry.getValue()){//for each models with this texture
							shader.bindDatas(dmr.getShaderDatas());
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
