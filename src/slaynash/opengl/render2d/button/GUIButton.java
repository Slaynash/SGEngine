package slaynash.opengl.render2d.button;

import java.awt.Dimension;

import javax.swing.event.EventListenerList;

import org.lwjgl.util.vector.Vector2f;
import org.newdawn.slick.Color;

import slaynash.engine.Renderable2dModel;
import slaynash.opengl.render2d.GUIElement;
import slaynash.opengl.render2d.text2d.Text2d;
import slaynash.opengl.shaders.ShaderManager;
import slaynash.opengl.textureUtils.TextureDef;
import slaynash.opengl.textureUtils.TextureManager;
import slaynash.opengl.utils.UserInputUtil;

public class GUIButton extends GUIElement{
	
	private final EventListenerList listeners = new EventListenerList();
	
	protected boolean backgroundEnabled = true;
	protected boolean textEnable = false;
	protected Text2d text;
	protected Color textColor;
	protected Color textColorHover;

	private TextureDef texBack;
	private Renderable2dModel model;
	
	private static float[] uvs = new float[]{0,0,1,0,1,1,1,1,0,1,0,0};
	
	

	public GUIButton(Dimension size, Dimension pos, GUIElement parent, int location){
		super(pos.width, pos.height, size.width, size.height, parent, false, location);
		textColor = new Color(255, 255, 255);
		textColorHover = new Color(140, 140, 140);
		
		texBack = TextureManager.getTextureDef("res/textures/gui/backDef.png", TextureManager.COLOR);
		
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
		
		model = new Renderable2dModel(vertices, uvs, texBack);
		
	}
	
	@Override
	public void render(){
		// DRAW PART
		if(backgroundEnabled){
			
			ShaderManager.shaderGUI_loadTranslation(getTopLeft());
			model.render();
			ShaderManager.shaderGUI_loadTranslation(new Vector2f());
			
		}
		if(textEnable){
			text.render();
		}
		
		
		// LOGIC PART
		GUIButtonEvent event = null;
		if(mouseEntered){
			for(GUIButtonListener listener : getGUIButtonListener()){
				if(event == null) event = new GUIButtonEvent();
				listener.mouseEntered(event);
			}
			text.setColour(textColorHover.r, textColorHover.g, textColorHover.b);
		}
		if(mouseIn && isFocused()){
			if(UserInputUtil.mouseLeftClicked()) for(GUIButtonListener listener : getGUIButtonListener()){
				if(event == null) event = new GUIButtonEvent();
				listener.mousePressed(event);
			}
			if(UserInputUtil.mouseLeftReleased()) for(GUIButtonListener listener : getGUIButtonListener()){
				if(event == null) event = new GUIButtonEvent();
				listener.mouseReleased(event);
			}
		}
		if(mouseExited){
			for(GUIButtonListener listener : getGUIButtonListener()){
				if(event == null) event = new GUIButtonEvent();
				listener.mouseExited(event);
			}
			text.setColour(textColor.r, textColor.g, textColor.b);
		}
	}
	
	public void setBackgroundEnable(boolean enableBackground){
		this.backgroundEnabled = enableBackground;
	}
	
	
	public void addGUIButtonListener(GUIButtonListener listener) {
        listeners.add(GUIButtonListener.class, listener);
    }
 
    public void removeGUIButtonListener(GUIButtonListener listener) {
        listeners.remove(GUIButtonListener.class, listener);
    }
    
    public GUIButtonListener[] getGUIButtonListener() {
        return listeners.getListeners(GUIButtonListener.class);
    }

	public void setText(String string, boolean centered) {
		if(text != null) text.release();
		if(centered)
			text = new Text2d(string, "tahoma", 400, new Vector2f(2,3), getWidth()/2, centered, this);
		else
			text = new Text2d(string, "tahoma", 400, new Vector2f(5,3), getWidth()-3, centered, this);
		textEnable = true;
	}
	
	public void setTextColor(Color color){
		if(text != null && !mouseIn) text.setColour(color.r, color.g, color.b);
		textColor = color;
	}
	public void setTextHoverColor(Color color){
		if(text != null && mouseIn) text.setColour(color.r, color.g, color.b);
		textColorHover = color;
	}
	
	@Override
	public void destroy(){
		super.destroy();
		text.release();
	}
}
