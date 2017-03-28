package slaynash.opengl.render2d.button;

import java.awt.Dimension;

import javax.swing.event.EventListenerList;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector2f;
import org.newdawn.slick.Color;

import slaynash.opengl.render2d.GUIElement;
import slaynash.opengl.render2d.text2d.Text2d;
import slaynash.opengl.shaders.ShaderManager;
import slaynash.opengl.utils.UserInputUtil;

public class GUIButton extends GUIElement{
	
	private final EventListenerList listeners = new EventListenerList();
	
	
	private boolean backgroundEnabled = true;
	private boolean textEnable = false;
	private Text2d text;
	private Color textColor;
	private Color textColorHover;
	
	

	public GUIButton(Dimension size, Dimension pos, GUIElement parent, int location){
		super(pos.width, pos.height, size.width, size.height, parent, false, location);
		textColor = new Color(255, 255, 255);
		textColorHover = new Color(140, 140, 140);
	}
	
	public void render(){
		// DRAW PART
		if(backgroundEnabled){

			//GL11.glDisable(GL11.GL_TEXTURE_2D);
			ShaderManager.bind2DShaderTexture("res/textures/gui/backDef.png");
			//new Color(40, 40, 40, 170).bind();
			float t = getTopLeft().y;
			float l = getTopLeft().x;
			float b = getBottomRight().y;
			float r = getBottomRight().x;
			GL11.glBegin(GL11.GL_QUADS);
				GL11.glTexCoord2f(0, 0);
				GL11.glVertex2f(l, t);
				GL11.glTexCoord2f(1, 0);
				GL11.glVertex2f(r, t);
				GL11.glTexCoord2f(1, 1);
				GL11.glVertex2f(r, b);
				GL11.glTexCoord2f(0, 1);
				GL11.glVertex2f(l, b);
			GL11.glEnd();
			//GL11.glEnable(GL11.GL_TEXTURE_2D);
		}
		if(textEnable){
			text.render();
		}
		

		//System.out.println(this+">"+UserInputUtil.mouseLeftClicked()+", "+mouseIn);
		
		
		// LOGIC PART
		//System.out.println(mouseEntered);
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
			text = new Text2d(string, "tahoma", 400, new Vector2f(2,3), width/2, centered, this);
		else
			text = new Text2d(string, "tahoma", 400, new Vector2f(5,3), width-3, centered, this);
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
