package slaynash.sgengine.gui.slider;

import javax.swing.event.EventListenerList;

import org.lwjgl.util.vector.Vector2f;

import slaynash.sgengine.models.Renderable2dModel;
import slaynash.sgengine.gui.GUIElement;
import slaynash.sgengine.shaders.ShaderManager;
import slaynash.sgengine.textureUtils.TextureDef;
import slaynash.sgengine.textureUtils.TextureManager;
import slaynash.sgengine.utils.UserInputUtil;

public class GUISlider extends GUIElement{
	
	private final EventListenerList listeners = new EventListenerList();
	
	private TextureDef track;
	private TextureDef thumb;
	private float trackPercent;
	
	private Renderable2dModel trackModel;
	private Renderable2dModel thumbModel;
	
	private static float[] uvs = new float[]{0,0,1,0,1,1,1,1,0,1,0,0};

	public GUISlider(int value, int x, int y, int width, int height, GUIElement parent, int location) {
		super(x, y, width, height, parent, false, location);
		
		track = TextureManager.getTextureDef("res/textures/gui/slider_track.png", TextureManager.COLOR);
		thumb = TextureManager.getTextureDef("res/textures/gui/slider_thumb.png", TextureManager.COLOR);
		trackPercent = value;
		
		
		float[] verticesTrack = new float[12];
		verticesTrack[0] = 0;
		verticesTrack[1] = getHeight()/2+2;
		verticesTrack[2] = getWidth();
		verticesTrack[3] = getHeight()/2+2;
		verticesTrack[4] = getWidth();
		verticesTrack[5] = getHeight()/2-2;

		verticesTrack[6] = getWidth();
		verticesTrack[7] = getHeight()/2-2;
		verticesTrack[8] = 0;
		verticesTrack[9] = getHeight()/2-2;
		verticesTrack[10] = 0;
		verticesTrack[11] = getHeight()/2+2;
		
		trackModel = new Renderable2dModel(verticesTrack, uvs, track);
		

		float[] verticesThumb = new float[12];
		verticesThumb[0] = +2;
		verticesThumb[1] = 0;
		verticesThumb[2] = -2;
		verticesThumb[3] = 0;
		verticesThumb[4] = -2;
		verticesThumb[5] = getHeight();

		verticesThumb[6] = -2;
		verticesThumb[7] = getHeight();
		verticesThumb[8] = +2;
		verticesThumb[9] = getHeight();
		verticesThumb[10] = +2;
		verticesThumb[11] = 0;
		
		thumbModel = new Renderable2dModel(verticesThumb, uvs, thumb);
	}

	@Override
	public void render() {
		if(isMousePressedIn() && isFocused()){
			float otp = trackPercent;
			trackPercent = (UserInputUtil.getMousePos().x-getTopLeft().x)/getWidth()*100;
			if(trackPercent != otp) for(GUISliderListener listener : getGUISliderListener()){
				GUISliderEvent event = new GUISliderEvent(trackPercent);
				listener.sliderChanged(event);
			}
		}
		ShaderManager.shaderGUI_loadTranslation(getTopLeft());
		trackModel.render();
		ShaderManager.shaderGUI_loadTranslation(new Vector2f(getTopLeft().x+(trackPercent/100)*getWidth(), getTopLeft().y));
		thumbModel.render();
		ShaderManager.shaderGUI_loadTranslation(new Vector2f());
		/*
		
		float cy = getTopLeft().y+getHeight()/2;
		
		ShaderManager.shader2d_bindTextureID(track, ShaderManager.TEXTURE_COLOR);
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
		*/
		/*
		ShaderManager.shader2d_bindTextureID(thumb, ShaderManager.TEXTURE_COLOR);
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
		*/
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
