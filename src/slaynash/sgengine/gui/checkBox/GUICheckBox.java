package slaynash.sgengine.gui.checkBox;

import javax.swing.event.EventListenerList;

import org.lwjgl.util.vector.Vector2f;

import slaynash.sgengine.models.Renderable2dModel;
import slaynash.sgengine.gui.GUIElement;
import slaynash.sgengine.shaders.ShaderManager;
import slaynash.sgengine.textureUtils.TextureDef;
import slaynash.sgengine.textureUtils.TextureManager;

public class GUICheckBox extends GUIElement{
	
	private final EventListenerList listeners = new EventListenerList();

	private boolean checked;
	private TextureDef texY;
	private TextureDef texN;

	private Renderable2dModel model;
	
	private static float[] uvs = new float[]{0,0,1,0,1,1,1,1,0,1,0,0};

	public GUICheckBox(int x, int y, int size, boolean defaultState, GUIElement parent, int location) {
		super(x, y, size, size, parent, false, location);
		checked = defaultState;
		texY = TextureManager.getTextureDef("res/textures/gui/checkBox_checked.png", TextureManager.COLOR);
		texN = TextureManager.getTextureDef("res/textures/gui/checkBox_unchecked.png", TextureManager.COLOR);

		
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
		
		model = new Renderable2dModel(vertices, uvs, texN);
	}

	@Override
	public void render() {
		if(isMouseClickedIn() && isFocused()){
			toggle();
			for(GUICheckBoxListener listener : getGUICheckBoxListener()){
				GUICheckBoxEvent event = new GUICheckBoxEvent(checked);
				listener.stateChanged(event);
			}
		}
		/*
		if(checked) ShaderManager.shader_bindTextureID(texYID, ShaderManager.TEXTURE_COLOR);
		else ShaderManager.shader_bindTextureID(texNID, ShaderManager.TEXTURE_COLOR);
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
		*/
		ShaderManager.shader_loadTranslation(getTopLeft());
		if(checked) model.setTexture(texY);
		else model.setTexture(texN);
		model.render();
		ShaderManager.shader_loadTranslation(new Vector2f());
	}

	private void toggle() {
		checked = !checked ;
	}
	
	public boolean isChecked(){
		return checked;
	}
	
	public void addGUICheckBoxListener(GUICheckBoxListener listener) {
        listeners.add(GUICheckBoxListener.class, listener);
    }
 
    public void removeGUICheckBoxListener(GUICheckBoxListener listener) {
        listeners.remove(GUICheckBoxListener.class, listener);
    }
    
    public GUICheckBoxListener[] getGUICheckBoxListener() {
        return listeners.getListeners(GUICheckBoxListener.class);
    }
	
}
