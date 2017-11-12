package slaynash.sgengine.deferredRender.shaders;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import slaynash.sgengine.deferredRender.FrameBufferedObject;
import slaynash.sgengine.models.utils.Vao;
import slaynash.sgengine.models.utils.VaoManager;
import slaynash.sgengine.shaders.ShaderProgram;

public abstract class DeferredShaderProgram extends ShaderProgram {
	
	FrameBufferedObject fbo;
	private Vao quadVao;
	
	public DeferredShaderProgram(String shaderPath, String vertexShaderName, String fragmentShaderName, int shaderType, int outputWidth, int outputHeight, int outputColorBufferFormat) {
		super(shaderPath, vertexShaderName, fragmentShaderName, shaderType);
		fbo = new FrameBufferedObject(outputWidth, outputHeight, FrameBufferedObject.DEPTH_TEXTURE, false, 1, outputColorBufferFormat);
	}
	
	public DeferredShaderProgram(String shaderPath, String vertexShaderName, String fragmentShaderName, String geometryShaderName, int shaderType, int outputWidth, int outputHeight, int outputColorBufferFormat) {
		super(shaderPath, vertexShaderName, fragmentShaderName, geometryShaderName, shaderType);
		fbo = new FrameBufferedObject(outputWidth, outputHeight, FrameBufferedObject.DEPTH_TEXTURE, false, 1, outputColorBufferFormat);
	}
	
	public abstract void processDirect();
	
	public void process() {
		fbo.bindFrameBuffer();
		processDirect();
		fbo.unbindFrameBuffer();
	}
	
	
	/**
	 * Use bindModel() instead
	 */
	@Override
	@Deprecated
	public void bindModel(int modelID) {
		GL30.glBindVertexArray(modelID);
		GL20.glEnableVertexAttribArray(0);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
	}
	
	public void bindModel() {
		if(quadVao == null) quadVao = VaoManager.loadToVao(new float[]{-1, 1, -1, -1, 1, 1, 1, -1}, 2);
		bindModel(quadVao.getVaoID());
	}
	
	public int getTexture() {
		return fbo.getColourTexture();
	}
	
	public void destroy() {
		fbo.cleanUp();
		quadVao.dispose();
	}
	
}
