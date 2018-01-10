package slaynash.sgengine.gui;

import org.lwjgl.util.vector.Vector2f;

import slaynash.sgengine.gui.text2d.Text2d;

public class GUIText extends GUIElement{
	
	Text2d text2d;
	
	private float[] color = new float[]{1,1,1};
	private float fontSize = 300;
	private String font = "tahoma";
	private boolean centered = false;
	boolean tdd = false;
	
	public GUIText(String text, String font, float fontSize, int x, int y, int width, boolean centered, GUIElement parent, int location) {
		super(x, y, width, (int)fontSize/20, parent, false, location);
		this.fontSize = fontSize;
		this.font = font;
		this.centered = centered;
		this.text2d = new Text2d(text, font, fontSize, new Vector2f(0,0), width/2, centered, this);
	}
	
	public GUIText(String text, int x, int y, int width, GUIElement parent, int location) {
		super(x, y, width, 20, parent, false, location);			 //y+17
		this.text2d = new Text2d(text, "tahoma", 300, new Vector2f(0,0), width/2, false, this);
	}

	@Override
	public void render() {
		if(!tdd) text2d.render();
	}
	
	public void setColor(float r, float g, float b){
		this.color = new float[]{r,g,b};
		text2d.setColour(r, g, b);
	}
	
	
	@Override
	public void destroy(){
		super.destroy();
		text2d.release();
		tdd = true;
	}
	
	public void setText(String text) {
		if(text2d.getTextString().equals(text)) return;
		if(text2d != null) text2d.release();
		this.text2d = new Text2d(text, font, fontSize, new Vector2f(0,0), getWidth()/2, centered, this);
		text2d.setColour(color[0], color[1], color[2]);
	}

}
