package slaynash.sgengine.gui;

import slaynash.sgengine.maths.Vector2i;
import slaynash.sgengine.models.Renderable2dModel;
import slaynash.sgengine.models.utils.VaoManager;
import slaynash.sgengine.shaders.ShaderManager;
import slaynash.sgengine.textureUtils.TextureDef;
import slaynash.sgengine.textureUtils.TextureManager;

public class GUIMiniPopup extends GUIElement {

	public static final float SHOW_DURATION = 0.3f;
	public static final float STAY_DURATION = 3;
	public static final float HIDE_DURATION = 0.5f;
	public static final int HEIGHT = 150;
	private TextureDef texture;
	private float startTime;
	private Renderable2dModel model;
	
	private static float[] uvs = new float[]{0,0,1,0,1,1,1,1,0,1,0,0};

	public GUIMiniPopup() {
		super(0, 0, 350, HEIGHT, null, true, GUIManager.ELEMENT_POPUP_UP);
		texture = TextureManager.getTextureDef("res/textures/menu/miniPopup.png", TextureManager.TEXTURE_DIFFUSE);
		startTime = System.nanoTime()/1E9f;
		
		float[] vertices = new float[12];
		vertices[0] = 0;
		vertices[1] = 0;
		vertices[2] = getWidth();
		vertices[3] = 0;
		vertices[4] = getWidth();
		vertices[5] = getHeight();

		vertices[6] = getWidth();
		vertices[7] = getHeight();
		vertices[8] = 0;
		vertices[9] = getHeight();
		vertices[10] = 0;
		vertices[11] = 0;
		
		model = new Renderable2dModel(VaoManager.loadToVao2d(vertices, uvs), texture);
		
	}

	@Override
	public void render() {
		
		ShaderManager.shader_loadTranslation(getTopLeft());
		
		model.render();
		ShaderManager.shader_loadTranslation(new Vector2i());
		
		for(GUIElement child:getChildrens()) child.render();
	}

	public void setPosition(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	@Override
	public Vector2i getContainerPos(){
		return new Vector2i(x, y);
	}

	public float getLifeTime() {
		return System.nanoTime()/1E9f-startTime;
	}

}
