package slaynash.sgengine.deferredRender;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL32;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import de.fruitfly.ovr.structs.Vector2i;
import slaynash.sgengine.Configuration;
import slaynash.sgengine.LogSystem;
import slaynash.sgengine.deferredRender.shaders.BrightFilterShader;
import slaynash.sgengine.deferredRender.shaders.CombineBloomShader;
import slaynash.sgengine.deferredRender.shaders.HBlurShader;
import slaynash.sgengine.deferredRender.shaders.VBlurShader;
import slaynash.sgengine.models.utils.Vao;
import slaynash.sgengine.models.utils.VaoManager;
import slaynash.sgengine.shaders.ShaderManager;
import slaynash.sgengine.shaders.ShaderProgram;
import slaynash.sgengine.utils.LightsManager;
import slaynash.sgengine.utils.MatrixUtils;
import slaynash.sgengine.utils.VRUtils;
import slaynash.sgengine.world3d.loader.Ent_PointLight;

public class DeferredRenderer {

	protected static ArrayList<ShaderRenderlist> shaderLists = new ArrayList<ShaderRenderlist>();
	protected static Map<Integer, ArrayList<DeferredModelRenderer>> currentMap;
	
	protected static Map<Object, Class<? extends DeferredModelRenderer>> modelRenderers = new HashMap<Object, Class<? extends DeferredModelRenderer>>();
	//private static ArrayList<Integer> bindTextures = new ArrayList<Integer>();
	protected static ShaderProgram currentShader;
	protected static int[] depthCubemap;
	protected static int[] depthMapFBO;
	protected static FrameBufferedObject[] fbos_colorSSAA = new FrameBufferedObject[3];
	protected static FrameBufferedObject[] fbos_color = new FrameBufferedObject[3];
	protected static FrameBufferedObject[] fbos_bright = new FrameBufferedObject[3];
	protected static FrameBufferedObject[] fbos_Vblur = new FrameBufferedObject[3];
	protected static FrameBufferedObject[] fbos_Hblur = new FrameBufferedObject[3];
	
	
	protected static Vector2i vrRendersize;
	protected static BrightFilterShader brightFilterShader;
	protected static VBlurShader vblurShader;
	protected static HBlurShader hblurShader;
	protected static CombineBloomShader combinebloomShader;
	protected static ImageRenderer quadRenderer;
	protected static Vao quadVAO;
	
	
	public static final int SHADOW_WIDTH = 1024, SHADOW_HEIGHT = 1024;
	
	public static void init() {
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
		
		vrRendersize = Configuration.isVR() ? VRUtils.getRendersize() : new Vector2i();
		if(Configuration.isVR()) {
			fbos_colorSSAA[VRUtils.EYE_LEFT] = new FrameBufferedObject(vrRendersize.x, vrRendersize.y, 0, true, Configuration.getVRSSAASamples(), GL30.GL_RGBA16F);
			fbos_colorSSAA[VRUtils.EYE_RIGHT] = new FrameBufferedObject(vrRendersize.x, vrRendersize.y, 0, true, Configuration.getVRSSAASamples(), GL30.GL_RGBA16F);
			fbos_color[VRUtils.EYE_LEFT] = new FrameBufferedObject(vrRendersize.x, vrRendersize.y, FrameBufferedObject.DEPTH_TEXTURE, false, 1, GL30.GL_RGBA16F);
			fbos_color[VRUtils.EYE_RIGHT] = new FrameBufferedObject(vrRendersize.x, vrRendersize.y, FrameBufferedObject.DEPTH_TEXTURE, false, 1, GL30.GL_RGBA16F);
			fbos_Hblur[VRUtils.EYE_LEFT] = new FrameBufferedObject(vrRendersize.x, vrRendersize.y, FrameBufferedObject.DEPTH_TEXTURE, false, 1, GL30.GL_RGBA16F);
			fbos_Hblur[VRUtils.EYE_RIGHT] = new FrameBufferedObject(vrRendersize.x, vrRendersize.y, FrameBufferedObject.DEPTH_TEXTURE, false, 1, GL30.GL_RGBA16F);
			fbos_Vblur[VRUtils.EYE_LEFT] = new FrameBufferedObject(vrRendersize.x, vrRendersize.y, FrameBufferedObject.DEPTH_TEXTURE, false, 1, GL30.GL_RGBA16F);
			fbos_Vblur[VRUtils.EYE_RIGHT] = new FrameBufferedObject(vrRendersize.x, vrRendersize.y, FrameBufferedObject.DEPTH_TEXTURE, false, 1, GL30.GL_RGBA16F);
			fbos_bright[VRUtils.EYE_LEFT] = new FrameBufferedObject(vrRendersize.x, vrRendersize.y, FrameBufferedObject.DEPTH_TEXTURE, false, 1, GL30.GL_RGBA16F);
			fbos_bright[VRUtils.EYE_RIGHT] = new FrameBufferedObject(vrRendersize.x, vrRendersize.y, FrameBufferedObject.DEPTH_TEXTURE, false, 1, GL30.GL_RGBA16F);
		}
		fbos_colorSSAA[VRUtils.EYE_CENTER] = new FrameBufferedObject(Display.getWidth(), Display.getHeight(), 0, true, Configuration.getSSAASamples(), GL30.GL_RGBA16F);
		fbos_color[VRUtils.EYE_CENTER] = new FrameBufferedObject(Display.getWidth(), Display.getHeight(), FrameBufferedObject.DEPTH_TEXTURE, false, 1, GL30.GL_RGBA16F);
		fbos_Hblur[VRUtils.EYE_CENTER] = new FrameBufferedObject(Display.getWidth(), Display.getHeight(), FrameBufferedObject.DEPTH_TEXTURE, false, 1, GL30.GL_RGBA16F);
		fbos_Vblur[VRUtils.EYE_CENTER] = new FrameBufferedObject(Display.getWidth(), Display.getHeight(), FrameBufferedObject.DEPTH_TEXTURE, false, 1, GL30.GL_RGBA16F);
		fbos_bright[VRUtils.EYE_CENTER] = new FrameBufferedObject(Display.getWidth(), Display.getHeight(), FrameBufferedObject.DEPTH_TEXTURE, false, 1, GL30.GL_RGBA16F);
		
		
		quadVAO = VaoManager.loadToVao(new float[]{-1, 1, -1, -1, 1, 1, 1, -1}, 2);
		
		
		brightFilterShader = new BrightFilterShader();
		hblurShader = new HBlurShader();
		vblurShader = new VBlurShader();
		combinebloomShader = new CombineBloomShader();
		
		quadRenderer = new ImageRenderer();
	}
	
	public static void render(int eye) {
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glCullFace(GL11.GL_BACK);
		long startTime = 0;
		if(Configuration.isUsingTimingDebug()) startTime = System.nanoTime();
		if(Configuration.isUsingDeferredRenderShadows()) {
			renderShadows(eye);
			if(Configuration.isUsingTimingDebug()) {
				GL11.glFinish();
				LogSystem.out_println("[TIMING] > Shadows render time: "+((System.nanoTime()-startTime)/1e6f)+"ms");
				startTime = System.nanoTime();
			}
		}
		if(Configuration.isDeferredRenderBloomEnabled()) {
			
			fbos_colorSSAA[eye].bindFrameBuffer();
			
			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
			
			renderColor(eye);
			fbos_colorSSAA[eye].unbindFrameBuffer();
			
			fbos_colorSSAA[eye].resolveToFbo(fbos_color[eye]);
			
			if(Configuration.isUsingTimingDebug()) {
				GL11.glFinish();
				LogSystem.out_println("[TIMING] > Deferred render time: "+((System.nanoTime()-startTime)/1e6f)+"ms");
				startTime = System.nanoTime();
			}
			renderBloom(eye);
			if(Configuration.isUsingTimingDebug()) {
				GL11.glFinish();
				LogSystem.out_println("[TIMING] > Bloom render time: "+((System.nanoTime()-startTime)/1e6f)+"ms");
				startTime = System.nanoTime();
			}
		}
		else {
			VRUtils.setCurrentRenderEye(eye);
			renderColor(eye);
			if(Configuration.isUsingTimingDebug()) {
				GL11.glFinish();
				LogSystem.out_println("[TIMING] > Deferred render time: "+((System.nanoTime()-startTime)/1e6f)+"ms");
				startTime = System.nanoTime();
			}
		}
		if(Configuration.isCleanBetweenDeferredRendersEnabled()) cleanup();
		if(Configuration.isUsingTimingDebug()) {
			GL11.glFinish();
			LogSystem.out_println("[TIMING] > Deferred cleanup time: "+((System.nanoTime()-startTime)/1e6f)+"ms");
			startTime = System.nanoTime();
		}
		GL11.glDisable(GL11.GL_CULL_FACE);
	}
	
	private static void renderBloom(int eye) {
		GL11.glDisable(GL11.GL_CULL_FACE);
		
		/*
		VRUtils.setCurrentRenderEye(eye);
		brightFilterShader.useDirect();
		brightFilterShader.bindModel(quadVAO.getVaoID());
		GL13.glActiveTexture(GL13.GL_TEXTURE0+ShaderManager.TEXTURE_COLOR);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, fbos_color[eye].getColourTexture());
		quadRenderer.renderQuad();
		//*/
		
		/*
		fbos_bright[eye].bindFrameBuffer();
		
		brightFilterShader.useDirect();
		brightFilterShader.bindModel(quadVAO.getVaoID());
		GL13.glActiveTexture(GL13.GL_TEXTURE0+ShaderManager.TEXTURE_COLOR);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, fbos_color[eye].getColourTexture());
		quadRenderer.renderQuad();
		brightFilterShader.stop();
		
		fbos_bright[eye].unbindFrameBuffer();
		fbos_Hblur[eye].bindFrameBuffer();
		
		hblurShader.useDirect();
		hblurShader.loadTargetWidth(eye == VRUtils.EYE_CENTER ? Display.getWidth() : vrRendersize.x);
		hblurShader.bindModel(quadVAO.getVaoID());
		GL13.glActiveTexture(GL13.GL_TEXTURE0+ShaderManager.TEXTURE_COLOR);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, fbos_bright[eye].getColourTexture());
		quadRenderer.renderQuad();
		hblurShader.stop();
		
		fbos_Hblur[eye].unbindFrameBuffer();
		VRUtils.setCurrentRenderEye(eye);
		
		vblurShader.useDirect();
		vblurShader.loadTargetHeight(eye == VRUtils.EYE_CENTER ? Display.getHeight() : vrRendersize.y);
		vblurShader.bindModel(quadVAO.getVaoID());
		GL13.glActiveTexture(GL13.GL_TEXTURE0+ShaderManager.TEXTURE_COLOR);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, fbos_Hblur[eye].getColourTexture());
		quadRenderer.renderQuad();
		vblurShader.stop();
		//*/
		
		//*
		fbos_bright[eye].bindFrameBuffer();
		
		brightFilterShader.useDirect();
		brightFilterShader.bindModel(quadVAO.getVaoID());
		GL13.glActiveTexture(GL13.GL_TEXTURE0+ShaderManager.TEXTURE_COLOR);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, fbos_color[eye].getColourTexture());
		quadRenderer.renderQuad();
		brightFilterShader.stop();
		
		fbos_bright[eye].unbindFrameBuffer();
		fbos_Hblur[eye].bindFrameBuffer();
		
		hblurShader.useDirect();
		hblurShader.loadTargetWidth(eye == VRUtils.EYE_CENTER ? Display.getWidth() : vrRendersize.x);
		hblurShader.bindModel(quadVAO.getVaoID());
		GL13.glActiveTexture(GL13.GL_TEXTURE0+ShaderManager.TEXTURE_COLOR);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, fbos_bright[eye].getColourTexture());
		quadRenderer.renderQuad();
		hblurShader.stop();
		
		fbos_Hblur[eye].unbindFrameBuffer();
		fbos_Vblur[eye].bindFrameBuffer();
		
		vblurShader.useDirect();
		vblurShader.loadTargetHeight(eye == VRUtils.EYE_CENTER ? Display.getHeight() : vrRendersize.y);
		vblurShader.bindModel(quadVAO.getVaoID());
		GL13.glActiveTexture(GL13.GL_TEXTURE0+ShaderManager.TEXTURE_COLOR);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, fbos_Hblur[eye].getColourTexture());
		quadRenderer.renderQuad();
		vblurShader.stop();

		fbos_Vblur[eye].unbindFrameBuffer();
		VRUtils.setCurrentRenderEye(eye);
		
		combinebloomShader.useDirect();
		combinebloomShader.bindModel(quadVAO.getVaoID());
		GL13.glActiveTexture(GL13.GL_TEXTURE0+ShaderManager.TEXTURE_COLOR);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, fbos_color[eye].getColourTexture());
		GL13.glActiveTexture(GL13.GL_TEXTURE0+ShaderManager.TEXTURE_COLOR+1);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, fbos_Vblur[eye].getColourTexture());
		quadRenderer.renderQuad();
		combinebloomShader.stop();
		//*/
	}

	private static void renderShadows(int eye) {
		
		Matrix4f projMat = MatrixUtils.createProjectionMatrix(Configuration.getLightsZNear(), Configuration.getLightsZFar(), 90f, (float)SHADOW_WIDTH/(float)SHADOW_HEIGHT);
		
		GL11.glViewport(0, 0, SHADOW_WIDTH, SHADOW_HEIGHT);
		for(int i=0;i<LightsManager.getPointlights().size();i++) {
			Ent_PointLight light = LightsManager.getPointlights().get(i);
			if(light.getColor()[0]+light.getColor()[1]+light.getColor()[2] < 0.0001f) continue;
			
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
						if(eye == VRUtils.EYE_CENTER) dmr.render(); else dmr.renderVR(eye);
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
		//bindTextures = new ArrayList<Integer>();
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
	/*
	public static void registerModelRenderer(Object modelrendererClass, Class<? extends DeferredModelRenderer> dmr){
		LogSystem.out_println("[DeferredRenderer] Registered DMR for class "+modelrendererClass.getClass());
		modelRenderers.put(modelrendererClass.getClass(), dmr);
	}
	*/
}
