package slaynash.opengl.render2d;

import org.lwjgl.opengl.GL11;

import slaynash.opengl.shaders.ShaderManager;
import slaynash.opengl.textureUtils.TextureManager;

public class GUIMiniPopup extends GUIElement {

	public static final float SHOW_DURATION = 0.3f;
	public static final float STAY_DURATION = 3;
	public static final float HIDE_DURATION = 0.5f;
	public static final int HEIGHT = 150;
	private int textureID;
	private float startTime;

	public GUIMiniPopup() {
		super(0, 0, 350, HEIGHT, null, true, GUIManager.ELEMENT_POPUP_UP);
		textureID = TextureManager.getTextureID("res/textures/menu/miniPopup.png");
		startTime = System.nanoTime()/1E9f;
	}

	@Override
	public void render() {
		//System.out.println("rendering popup at "+getTopLeft().toString());
		ShaderManager.bind2DShaderTextureID(textureID);
		GL11.glBegin(GL11.GL_QUADS);
			GL11.glTexCoord2f(0      , 0);
			GL11.glVertex2f  (getTopLeft().x, getTopLeft().y);
			GL11.glTexCoord2f(1      , 0);
			GL11.glVertex2f  (getBottomRight().x, getTopLeft().y);
			GL11.glTexCoord2f(1      , 1);
			GL11.glVertex2f  (getBottomRight().x, getBottomRight().y);
			GL11.glTexCoord2f(0      , 1);
			GL11.glVertex2f  (getTopLeft().x, getBottomRight().y);
		GL11.glEnd();
		
		for(GUIElement child:getChildrens()) child.render();
	}

	public void setPosition(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public float getLifeTime() {
		return System.nanoTime()/1E9f-startTime;
	}

}
