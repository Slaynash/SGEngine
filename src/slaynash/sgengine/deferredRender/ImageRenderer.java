package slaynash.sgengine.deferredRender;

import org.lwjgl.opengl.GL11;

public class ImageRenderer {

	private FrameBufferedObject fbo;

	protected ImageRenderer(int width, int height) {
		this.fbo = new FrameBufferedObject(width, height, FrameBufferedObject.NONE, false, 1, GL11.GL_RGBA8);
	}

	protected ImageRenderer() {}

	protected void renderQuad() {
		if (fbo != null) {
			fbo.bindFrameBuffer();
		}
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
		GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, 4);
		if (fbo != null) {
			fbo.unbindFrameBuffer();
		}
	}

	protected int getOutputTexture() {
		return fbo.getColourTexture();
	}

	protected void cleanUp() {
		if (fbo != null) {
			fbo.cleanUp();
		}
	}

}

