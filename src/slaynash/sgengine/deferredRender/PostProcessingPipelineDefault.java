package slaynash.sgengine.deferredRender;

import slaynash.sgengine.deferredRender.shaders.BrightFilterShader;
import slaynash.sgengine.deferredRender.shaders.CombineBloomShader;
import slaynash.sgengine.deferredRender.shaders.HBlurShader;
import slaynash.sgengine.deferredRender.shaders.VBlurShader;
import slaynash.sgengine.utils.VRUtils;

public class PostProcessingPipelineDefault extends PostProcessingPipeline {
	
	private BrightFilterShader brightFilterShader;
	private HBlurShader hblurShader;
	private VBlurShader vblurShader;
	private CombineBloomShader combinebloomShader;
	
	private BrightFilterShader brightFilterShaderVR;
	private HBlurShader hblurShaderVR;
	private VBlurShader vblurShaderVR;
	private CombineBloomShader combinebloomShaderVR;

	@Override
	public void init(int width, int height) {
		brightFilterShader = new BrightFilterShader(width, height);
		hblurShader = new HBlurShader(width, height);
		vblurShader = new VBlurShader(width, height);
		combinebloomShader = new CombineBloomShader(width, height);
	}
	
	@Override
	public void initVR(int width, int height) {
		brightFilterShaderVR = new BrightFilterShader(width, height);
		hblurShaderVR = new HBlurShader(width, height);
		vblurShaderVR = new VBlurShader(width, height);
		combinebloomShaderVR = new CombineBloomShader(width, height);
	}
	
	@Override
	public void render(FrameBufferedObject inputFbo) {
		super.render(inputFbo);
		
		brightFilterShader.setInputTexture(inputFbo.getColourTexture());
		brightFilterShader.processDirect();

		hblurShader.setInputTexture(brightFilterShader.getTexture());
		hblurShader.process();
		
		vblurShader.setInputTexture(hblurShader.getTexture());
		vblurShader.process();
		
		VRUtils.setCurrentRenderEye(VRUtils.EYE_CENTER);
		combinebloomShader.setColourTexture(inputFbo.getColourTexture());
		combinebloomShader.setBluredTexture(vblurShader.getTexture());
		combinebloomShader.processDirect();
	}
	
	@Override
	public void renderVR(FrameBufferedObject inputFbo, int eye) {
		super.renderVR(inputFbo, eye);
		
		brightFilterShaderVR.setInputTexture(inputFbo.getColourTexture());
		brightFilterShaderVR.process();
		
		hblurShaderVR.setInputTexture(brightFilterShaderVR.getTexture());
		hblurShaderVR.processDirect();
		
		vblurShaderVR.setInputTexture(vblurShaderVR.getTexture());
		vblurShaderVR.processDirect();

		VRUtils.setCurrentRenderEye(eye);
		combinebloomShaderVR.setColourTexture(inputFbo.getColourTexture());
		combinebloomShaderVR.setBluredTexture(vblurShaderVR.getTexture());
		combinebloomShaderVR.processDirect();
	}

	@Override
	public void destroy() {
		if(isInitialised()) {
			brightFilterShader.destroy();
			hblurShader.destroy();
			vblurShader.destroy();
			combinebloomShader.destroy();
		}
		if(isVRInitialised()) {
			brightFilterShaderVR.destroy();
			hblurShaderVR.destroy();
			vblurShaderVR.destroy();
			combinebloomShaderVR.destroy();
		}
	}
	
}
