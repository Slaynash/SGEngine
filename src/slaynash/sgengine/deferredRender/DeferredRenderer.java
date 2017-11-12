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
import slaynash.sgengine.LogSystem;
import slaynash.sgengine.shaders.ShaderManager;
import slaynash.sgengine.shaders.ShaderProgram;
import slaynash.sgengine.utils.LightsManager;
import slaynash.sgengine.utils.VRUtils;

public class DeferredRenderer {

	protected static ArrayList<ShaderRenderlist> shaderLists = new ArrayList<ShaderRenderlist>();
	protected static Map<Integer, ArrayList<DeferredModelRenderer>> currentMap;
	
	protected static Map<Object, Class<? extends DeferredModelRenderer>> modelRenderers = new HashMap<Object, Class<? extends DeferredModelRenderer>>();
	protected static ShaderProgram currentShader;
	protected static FrameBufferedObject[] fbos_colorSSAA = new FrameBufferedObject[3];
	protected static FrameBufferedObject[] fbos_color = new FrameBufferedObject[3];
	
	private static PostProcessingPipeline postProcessingPipeline;
	private static long startTime;
	
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
		
		if(Configuration.isUsingTimingDebug()) startTime = System.nanoTime();
		if(Configuration.isUsingDeferredRenderShadows()) {
			ShadowsRenderer.renderShadows(eye, shaderLists);
			timingdebug("Shadows render time");
		}
		if(Configuration.isPostProcessingEnabled()) {
			
			fbos_colorSSAA[eye].bindFrameBuffer();
			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
			renderColor(eye);
			fbos_colorSSAA[eye].unbindFrameBuffer();
			fbos_colorSSAA[eye].resolveToFbo(fbos_color[eye]);
			

			timingdebug("Deferred render time");
			renderPostProcessing(eye);
			timingdebug("PostProcessing render time");
		}
		else {
			VRUtils.setCurrentRenderEye(eye);
			renderColor(eye);
			timingdebug("Deferred render time");
		}
		if(Configuration.isCleanBetweenDeferredRendersEnabled()) cleanup();
		timingdebug("Deferred cleanup time");
		
		GL11.glDisable(GL11.GL_CULL_FACE);
	}
	
	private static void renderPostProcessing(int eye) {
		GL11.glDisable(GL11.GL_CULL_FACE);
		
		if(eye != VRUtils.EYE_CENTER) postProcessingPipeline.renderVR(fbos_color[eye], eye);
		else postProcessingPipeline.render(fbos_color[VRUtils.EYE_CENTER]);
	}
	
	public static void timingdebug(String text) {
		if(Configuration.isUsingTimingDebug()) {
			GL11.glFinish();
			LogSystem.out_println("[TIMING] > "+text+": "+((System.nanoTime()-startTime)/1e6f)+"ms");
			startTime = System.nanoTime();
		}
	}

	
	
	protected static void renderColor(int eye){
		for(ShaderRenderlist map:shaderLists){//for each shader phase
			ShaderProgram shader = map.getShader();
			shader.useDirect();
			if(shader.getShaderType() == ShaderProgram.SHADER_3D_MODERN  || shader.getShaderType() == ShaderProgram.SHADER_VR_MODERN) ShaderManager.shader_loadLights(LightsManager.getPointlights());
			for(Entry<Integer, ArrayList<DeferredModelRenderer>> entry:map.getObjectList().entrySet()){//for each models of this type
				shader.bindModel(entry.getKey());
				for(DeferredModelRenderer dmr:entry.getValue()){//for each render of this model
					shader.bindDatasDirect(dmr.getShaderDatas());
					for(int i=0;i<dmr.getTextureIDs().length;i++){
						GL13.glActiveTexture(GL13.GL_TEXTURE0+i);
						GL11.glBindTexture(GL11.GL_TEXTURE_2D, dmr.getTextureIDs()[i]);
					}
					if(eye == VRUtils.EYE_CENTER) dmr.render(); else dmr.renderVR(eye);
				}
			}
			shader.stop();
		}
	}
	
	public static void cleanup(){
		currentShader = null;
		currentMap = null;
		shaderLists = new ArrayList<ShaderRenderlist>();
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
