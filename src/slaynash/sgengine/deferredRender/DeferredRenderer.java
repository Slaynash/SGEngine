package slaynash.sgengine.deferredRender;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;

import de.fruitfly.ovr.structs.Vector2i;
import slaynash.sgengine.Configuration;
import slaynash.sgengine.DebugTimer;
import slaynash.sgengine.LogSystem;
import slaynash.sgengine.shaders.ShaderManager;
import slaynash.sgengine.shaders.ShaderProgram;
import slaynash.sgengine.utils.LightsManager;
import slaynash.sgengine.utils.VRUtils;

public class DeferredRenderer {

	protected static ArrayList<ShaderRenderlist> shaderLists = new ArrayList<ShaderRenderlist>();
	protected static Map<Integer, ArrayList<DeferredModelRenderer>> currentMap;
	
	protected static Map<Class<? extends DeferredRenderableModel>, Class<? extends DeferredModelRenderer>> modelRenderers = new HashMap<Class<? extends DeferredRenderableModel>, Class<? extends DeferredModelRenderer>>();
	protected static ShaderProgram currentShader;
	protected static FrameBufferedObject[] fbos_colorSSAA = new FrameBufferedObject[3];
	protected static FrameBufferedObject[] fbos_color = new FrameBufferedObject[3];
	
	private static PostProcessingPipeline postProcessingPipeline;
	private static int[] textures = new int[64];
	
	public static void init() {
		if(postProcessingPipeline == null) postProcessingPipeline = new PostProcessingPipelineDefault();
		ShadowsRenderer.init();
		
		if(Configuration.isVR()) {
			Vector2i vrRendersize = VRUtils.getRendersize();
			fbos_colorSSAA[VRUtils.EYE_LEFT] = new FrameBufferedObject(vrRendersize.x, vrRendersize.y, 0, true, Configuration.getVRSSAASamples(), GL30.GL_RGBA16F);
			fbos_colorSSAA[VRUtils.EYE_RIGHT] = new FrameBufferedObject(vrRendersize.x, vrRendersize.y, 0, true, Configuration.getVRSSAASamples(), GL30.GL_RGBA16F);
			fbos_color[VRUtils.EYE_LEFT] = new FrameBufferedObject(vrRendersize.x, vrRendersize.y, FrameBufferedObject.DEPTH_TEXTURE, false, 1, GL30.GL_RGBA16F);
			fbos_color[VRUtils.EYE_RIGHT] = new FrameBufferedObject(vrRendersize.x, vrRendersize.y, FrameBufferedObject.DEPTH_TEXTURE, false, 1, GL30.GL_RGBA16F);
		}
		
		fbos_colorSSAA[VRUtils.EYE_CENTER] = new FrameBufferedObject(Display.getWidth(), Display.getHeight(), 0, true, Configuration.getSSAASamples(), GL30.GL_RGBA16F);
		fbos_color[VRUtils.EYE_CENTER] = new FrameBufferedObject(Display.getWidth(), Display.getHeight(), FrameBufferedObject.DEPTH_TEXTURE, false, 1, GL30.GL_RGBA16F);
	}
	
	public static void setPostProcessingPipeline(PostProcessingPipeline pipeline) {
		postProcessingPipeline = pipeline;
	}
	
	public static void render(int eye) {
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glCullFace(GL11.GL_BACK);
		
		if(Configuration.isUsingDeferredRenderShadows()) {
			ShadowsRenderer.renderShadows(eye, shaderLists);
			DebugTimer.outputAndUpdateTime("Shadows render time");
		}
		if(Configuration.isPostProcessingEnabled()) {
			
			fbos_colorSSAA[eye].bindFrameBuffer();
			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
			renderColor(eye);
			fbos_colorSSAA[eye].unbindFrameBuffer();
			fbos_colorSSAA[eye].resolveToFbo(fbos_color[eye]);
			

			DebugTimer.outputAndUpdateTime("Deferred render time");
			renderPostProcessing(eye);
			DebugTimer.outputAndUpdateTime("PostProcessing render time");
		}
		else {
			VRUtils.setCurrentRenderEye(eye);
			renderColor(eye);
			DebugTimer.outputAndUpdateTime("Deferred render time");
		}
		if(Configuration.isCleanBetweenDeferredRendersEnabled()) cleanup();
		DebugTimer.outputAndUpdateTime("Deferred cleanup time");
		
		GL11.glDisable(GL11.GL_CULL_FACE);
	}
	
	private static void renderPostProcessing(int eye) {
		GL11.glDisable(GL11.GL_CULL_FACE);
		
		if(eye != VRUtils.EYE_CENTER) postProcessingPipeline.renderVR(fbos_color[eye], eye);
		else postProcessingPipeline.render(fbos_color[VRUtils.EYE_CENTER]);
	}

	
	//*
	protected static void renderColor(int eye){
		int err = 0;
		
		err = 0; if((err = GL11.glGetError()) != 0) LogSystem.out_println("[DeferredRenderer] Pre-render error: OpenGL Error "+err);
		if(Configuration.isUsingTimingDebug()) LogSystem.out_println("[DR] rendering "+shaderLists.size()+" shader loops:");

		int l1=0;
		for(ShaderRenderlist map:shaderLists)//for each shader phase
		{
			if(Configuration.isUsingTimingDebug()) LogSystem.out_println("[DR] \tShader "+l1+": rendering "+map.getObjectList().size()+" models");
			
			ShaderProgram shader = map.getShader();
			shader.useDirect();
			err = 0; if((err = GL11.glGetError()) != 0) LogSystem.out_println("[DeferredRenderer] OpenGL Error "+err+" with useDirect() call of "+shader.getClass());
			
			if(shader.isCastingShadow()) ShaderManager.shader_loadLights(LightsManager.getPointlights());
			err = 0; if((err = GL11.glGetError()) != 0) LogSystem.out_println("[DeferredRenderer] OpenGL Error "+err+" with shader_loadLights call on "+shader.getClass());
			
			//int l2=0;
			for(Entry<Integer, ArrayList<DeferredModelRenderer>> entry:map.getObjectList().entrySet())//for each models of this type
			{
				
				//if(Configuration.isUsingTimingDebug()) LogSystem.out_println("[DR] \t\tModel "+l2+": rendering "+entry.getValue().size()+" instances");
				shader.bindModel(entry.getKey());
				err = 0; if((err = GL11.glGetError()) != 0) LogSystem.out_println("[DeferredRenderer] OpenGL Error "+err+" with bindModel() call of "+shader.getClass());
				for(DeferredModelRenderer dmr:entry.getValue())//for each render of this model
				{
					
					shader.bindDatasDirect(dmr.getShaderDatas());
					err = 0; if((err = GL11.glGetError()) != 0) LogSystem.out_println("[DeferredRenderer] OpenGL Error "+err+" with bindDatasDirect() call of "+shader.getClass());
					for(int i=0;i<dmr.getTextureIDs().length;i++){
						setTextureId(i, dmr.getTextureIDs()[i], dmr.getTexture3ds()[i]);
					}
					
					if(eye == VRUtils.EYE_CENTER) {
						dmr.render();
						err = 0; if((err = GL11.glGetError()) != 0) LogSystem.out_println("[DeferredRenderer] OpenGL Error "+err+" with render() call of "+dmr.getClass());
					}
					else {
						dmr.renderVR(eye);
						err = 0; if((err = GL11.glGetError()) != 0) LogSystem.out_println("[DeferredRenderer] OpenGL Error "+err+" with renderVR() call of "+dmr.getClass());
					}
					
				}
				//l2++;
			}
			shader.stop();
			l1++;
		}
	}
	
	public static void setTextureId(int index, int id, boolean cubemap) {
		if(textures[index] != id) {
			
			if(index >= ShaderManager.TEXTURE_SHADOWSMIN) {
				GL13.glActiveTexture(GL13.GL_TEXTURE0+index+Configuration.MAX_LIGHTS);
				int err = 0; if((err = GL11.glGetError()) != 0) LogSystem.out_println("[DeferredRenderer] OpenGL Error "+err+" with texture binding call "+(index+Configuration.MAX_LIGHTS));
			}
			else{
				GL13.glActiveTexture(GL13.GL_TEXTURE0+index);
				int err = 0; if((err = GL11.glGetError()) != 0) LogSystem.out_println("[DeferredRenderer] OpenGL Error "+err+" with texture binding call "+index);
			}
			
			textures[index] = id;
			if(cubemap) GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, id);
			else GL11.glBindTexture(GL11.GL_TEXTURE_2D, id);
		}
	}
	
	public static void cleanup(){
		currentShader = null;
		currentMap = null;
		shaderLists.clear();
		for(int i=0;i<textures.length;i++) textures[i] = 0;
	}
	
	public static void addRenderStep(DeferredRenderableModel renderableModel){
		
		Class<? extends DeferredModelRenderer> dmrClass = modelRenderers.get(renderableModel.getClass());
		if(dmrClass == null){
			dmrClass = renderableModel.getDeferredRenderer();
			if(dmrClass != null) {
				modelRenderers.put(renderableModel.getClass(), dmrClass);
			}
			else {
				System.err.println("[DeferredRenderer] No DeferredModelRenderer found for class "+renderableModel.getClass());
				return;
			}
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
		list = currentMap.get(renderModel.getModelID());
		if(list == null){
			list = new ArrayList<DeferredModelRenderer>();
			currentMap.put(renderModel.getModelID(), list);
		}
		renderModel.setShaderDatas(currentShader.getDatas());
		list.add(renderModel);
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
}
