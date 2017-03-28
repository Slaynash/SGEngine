package slaynash.opengl.render2d.slider;

import javax.swing.event.EventListenerList;

import org.lwjgl.opengl.GL11;

import slaynash.opengl.render2d.GUIElement;
import slaynash.opengl.shaders.ShaderManager;
import slaynash.opengl.textureUtils.TextureManager;
import slaynash.opengl.utils.UserInputUtil;

public class GUISlider extends GUIElement{
	
	private final EventListenerList listeners = new EventListenerList();
	
	private int trackID;
	private int thumbID;
	private float trackPos;
	private float trackPercent;

	public GUISlider(int value, int x, int y, int width, int height, GUIElement parent, int location) {
		super(x, y, width, height, parent, false, location);
		
		trackID = TextureManager.getTextureID("res/textures/gui/slider_track.png");
		thumbID = TextureManager.getTextureID("res/textures/gui/slider_thumb.png");
		trackPercent = value;
		trackPos = getTopLeft().x+(trackPercent/100)*width;
	}

	@Override
	public void render() {
		if(isMousePressedIn() && isFocused()){
			float otp = trackPercent;
			trackPercent = (UserInputUtil.getMousePos().x-getTopLeft().x)/width*100;
			if(trackPercent != otp) for(GUISliderListener listener : getGUISliderListener()){
				GUISliderEvent event = new GUISliderEvent(trackPercent);
				listener.sliderChanged(event);
			}
		}
		trackPos = getTopLeft().x+(trackPercent/100)*width;
		float cy = getTopLeft().y+height/2;
		
		ShaderManager.bind2DShaderTextureID(trackID);
		GL11.glBegin(GL11.GL_QUADS);
			GL11.glTexCoord2f(0      , 0);
			GL11.glVertex2f  (getTopLeft().x, cy+2);
			GL11.glTexCoord2f(1      , 0);
			GL11.glVertex2f  (getBottomRight().x, cy+2);
			GL11.glTexCoord2f(1      , 1);
			GL11.glVertex2f  (getBottomRight().x, cy-2);
			GL11.glTexCoord2f(0      , 1);
			GL11.glVertex2f  (getTopLeft().x, cy-2);
		GL11.glEnd();
		
		ShaderManager.bind2DShaderTextureID(thumbID);
		GL11.glBegin(GL11.GL_QUADS);
			GL11.glTexCoord2f(0      , 0);
			GL11.glVertex2f  (trackPos+2, getTopLeft().y);
			GL11.glTexCoord2f(1      , 0);
			GL11.glVertex2f  (trackPos-2, getTopLeft().y);
			GL11.glTexCoord2f(1      , 1);
			GL11.glVertex2f  (trackPos-2, getBottomRight().y);
			GL11.glTexCoord2f(0      , 1);
			GL11.glVertex2f  (trackPos+2, getBottomRight().y);
		GL11.glEnd();
	}
	
	public float getPercent(){
		return trackPercent;
	}
	
	
	
	
	public void addGUISliderListener(GUISliderListener listener) {
        listeners.add(GUISliderListener.class, listener);
    }
 
    public void removeGUISliderListener(GUISliderListener listener) {
        listeners.remove(GUISliderListener.class, listener);
    }
    
    public GUISliderListener[] getGUISliderListener() {
        return listeners.getListeners(GUISliderListener.class);
    }
	
}
