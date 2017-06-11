package slaynash.opengl.render2d.textField;

import javax.swing.event.EventListenerList;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector2f;

import slaynash.opengl.render2d.GUIElement;
import slaynash.opengl.render2d.text2d.Text2d;
import slaynash.opengl.shaders.ShaderManager;
import slaynash.opengl.textureUtils.TextureManager;

public class GUITextField extends GUIElement{
	
	private final EventListenerList listeners = new EventListenerList();

	private Text2d text2d;
	
	private static boolean shift = false;
	private static boolean numlock = false;
	private static boolean numPressed = false;
	private int maxChar = 0;
	private String text = "";
	private boolean centerText = false;
	
	private int backgroundID = 0;


	public GUITextField(int x, int y, int width, String text, int maxChar, GUIElement parent, int location) {
		super(x, y, width, 20, parent, false, location);
		setText(text);
		this.maxChar = maxChar;
		backgroundID = TextureManager.getTextureID("res/textures/gui/textfield.png");
	}
	
	public GUITextField(int x, int y, int width, String text, GUIElement parent, int location) {
		super(x, y, width, 20, parent, false, location);
		setText(text);
		
		backgroundID = TextureManager.getTextureID("res/textures/gui/textfield.png");
	}
	
	public GUITextField(int x, int y, int width, String text, boolean centerText, int maxChar, GUIElement parent, int location) {
		super(x, y, width, 20, parent, centerText, location);
		setText(text);
		this.centerText = centerText;
		this.maxChar = maxChar;
		backgroundID = TextureManager.getTextureID("res/textures/gui/textfield.png");
	}
	
	public GUITextField(int x, int y, int width, String text, boolean centerText, GUIElement parent, int location) {
		super(x, y, width, 20, parent, centerText, location);
		setText(text);
		this.centerText = centerText;
		
		backgroundID = TextureManager.getTextureID("res/textures/gui/textfield.png");
	}

	@Override
	public void render() {
		if(isFocused()){
			updateText();
		}
		ShaderManager.bind2DShaderTextureID(backgroundID);
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
		
		if((!isFocused() && !text.equals(text2d.getTextString())) || (isFocused() && !text2d.getTextString().equals(text+"|"))){
			setTextInternal(text);
		}
		
		text2d.render();
	}
	private void setTextInternal(String text) {
		if(text2d != null) text2d.release();
		if(isFocused()){
			this.text2d = new Text2d(text+"|", "tahoma", 300, new Vector2f(4, 2), width/2-2, centerText, this);
		}
		else{
			this.text2d = new Text2d(text, "tahoma", 300, new Vector2f(4, 2), width/2-2, centerText, this);
		}
	}
	private void setText(String text) {
		this.text = text;
		setTextInternal(text);
		for(GUITextFieldListener listener : getGUITextFieldListener()){
			GUITextFieldEvent event = new GUITextFieldEvent(text);
			listener.textChanged(event);
		}
	}

	public void updateText() {
		if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) && !Keyboard.isKeyDown(Keyboard.KEY_RSHIFT))
	        shift=true;
		else if(!Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) && Keyboard.isKeyDown(Keyboard.KEY_RSHIFT))
	        shift=true;
		else shift = false;
		
		while (Keyboard.next()) {
			//System.out.println(Keyboard.getEventKey());
            if (Keyboard.isKeyDown(Keyboard.KEY_RETURN)) {
                return;
            } else if (Keyboard.isKeyDown(Keyboard.KEY_NUMLOCK)) {
	            if(!numPressed){
	            	numlock = !numlock;
	            }
	        } else{
	        	numPressed = false;
	        	if (Keyboard.isKeyDown(Keyboard.KEY_DELETE)) {
	                return;
	            } else if (Keyboard.isKeyDown(Keyboard.KEY_BACK) && Keyboard.getEventKeyState()) {
	                if(text.length() >= 1){
	                	text = text.substring(0, text.length()-1);
	                }
	            } else if (Keyboard.getEventKey() == Keyboard.KEY_LSHIFT   ||
	            		   Keyboard.getEventKey() == Keyboard.KEY_RSHIFT   ||
	            		   Keyboard.getEventKey() == Keyboard.KEY_LCONTROL ||
	            		   Keyboard.getEventKey() == Keyboard.KEY_RCONTROL ||
	            		   Keyboard.getEventKey() == Keyboard.KEY_LMETA    ||
	    	               Keyboard.getEventKey() == Keyboard.KEY_RMETA    ||
	            		   Keyboard.getEventKey() == Keyboard.KEY_CAPITAL) {
	            	
	            } else if (Keyboard.getEventKeyState()) {
	            	if(text.length()<maxChar || maxChar == 0){
		                if (shift) {
		                	text += Character.toUpperCase(Keyboard.getEventCharacter());
		                } else {
		                	text += String.valueOf(Keyboard.getEventCharacter());
		                    //jtLetter = true;
		                }
	            	}
	            }
            }
        }
	}
	
	public void addGUITextFieldListener(GUITextFieldListener listener) {
        listeners.add(GUITextFieldListener.class, listener);
    }
 
    public void removeGUITextFieldListener(GUITextFieldListener listener) {
        listeners.remove(GUITextFieldListener.class, listener);
    }
    
    public GUITextFieldListener[] getGUITextFieldListener() {
        return listeners.getListeners(GUITextFieldListener.class);
    }
	
    @Override
    public GUIElement setFocus(){
    	super.setFocus();
    	for(GUITextFieldListener listener : getGUITextFieldListener()){
			GUITextFieldEvent event = new GUITextFieldEvent(text);
			listener.focusAcquired(event);
		}
    	return this;
    }
    
    @Override
    public void resetFocus(){
    	super.resetFocus();
    	for(GUITextFieldListener listener : getGUITextFieldListener()){
			GUITextFieldEvent event = new GUITextFieldEvent(text);
			listener.focusLost(event);
		}
    }
    
    @Override
    public void destroy(){
    	super.destroy();
    	text2d.release();
    }
    
    public String getText(){
    	return text;
    }
    
}
