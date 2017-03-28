package slaynash.opengl.render2d.checkBox;

import javax.swing.event.EventListenerList;

import org.lwjgl.opengl.GL11;

import slaynash.opengl.render2d.GUIElement;
import slaynash.opengl.shaders.ShaderManager;
import slaynash.opengl.textureUtils.TextureManager;

public class GUICheckBox extends GUIElement{
	
	private final EventListenerList listeners = new EventListenerList();

	private boolean checked;
	private int texYID;
	private int texNID;

	public GUICheckBox(int x, int y, int size, boolean defaultState, GUIElement parent, int location) {
		super(x, y, size, size, parent, false, location);
		checked = defaultState;
		texYID = TextureManager.getTextureID("res/textures/gui/checkBox_checked.png");
		texNID = TextureManager.getTextureID("res/textures/gui/checkBox_unchecked.png");
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
		
		if(checked) ShaderManager.bind2DShaderTextureID(texYID);
		else ShaderManager.bind2DShaderTextureID(texNID);
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
