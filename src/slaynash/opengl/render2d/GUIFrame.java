package slaynash.opengl.render2d;

import java.awt.Dimension;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector2f;

import slaynash.opengl.render2d.comboBox.GUIComboBox;
import slaynash.opengl.render2d.text2d.Text2d;
import slaynash.opengl.shaders.ShaderManager;
import slaynash.opengl.utils.UserInputUtil;

public class GUIFrame extends GUIElement {
	
	private int level;
	private boolean mouseInClose = false;
	protected static int topPadding = 16;
	protected static int bottomPadding = 4;
	protected static int leftPadding = 4;
	protected static int rightPadding = 4;
	private boolean renderInside = true;
	private Text2d title;
	private boolean dragged = false;
	private Vector2f mouseOldPos;

	public GUIFrame(int x, int y, int width, int height, int location) {
		super(x, y, Math.max(width, leftPadding+rightPadding), Math.max(height, topPadding+bottomPadding), null, true, location);
		level = GUIManager.addTopLevel(location);
		containerPadding = new Dimension(leftPadding, topPadding);
		containerSize = new Dimension(this.width-leftPadding-rightPadding, this.height-topPadding-bottomPadding);
	}

	@Override
	public void render() {
		renderInside = false;
		float t = getTopLeft().y;
		float l = getTopLeft().x;
		float b = getBottomRight().y;
		float r = getBottomRight().x;
		ShaderManager.bind2DShaderTexture("res/textures/gui/frame/frame_background.png");
		GL11.glBegin(GL11.GL_QUADS);
			GL11.glTexCoord2f(0      , 0);
			GL11.glVertex2f  (l+leftPadding, t+topPadding);
			GL11.glTexCoord2f(1      , 0);
			GL11.glVertex2f  (r-rightPadding, t+topPadding);
			GL11.glTexCoord2f(1      , 1);
			GL11.glVertex2f  (r-rightPadding, b-bottomPadding);
			GL11.glTexCoord2f(0      , 1);
			GL11.glVertex2f  (l+leftPadding, b-bottomPadding);
		GL11.glEnd();

		renderInside = true;
		Vector2f mousePos = UserInputUtil.getMousePos();
		for(GUIElement child:getChildrens()) if(child.getClass() != GUIComboBox.class || (child.getClass() == GUIComboBox.class && !((GUIComboBox)child).isExpanded() && !isInElement(child, mousePos)) )child.render();
		for(GUIElement child:getChildrens()) if(child.getClass() == GUIComboBox.class && (((GUIComboBox)child).isExpanded()|| isInElement(child, mousePos) )) child.render();
		renderInside = true;
		ShaderManager.bind2DShaderTexture("res/textures/gui/frame/frame_bottom.png");
		GL11.glBegin(GL11.GL_QUADS);
			GL11.glTexCoord2f(0      , 0);
			GL11.glVertex2f  (l      , b-bottomPadding);
			GL11.glTexCoord2f(1      , 0);
			GL11.glVertex2f  (r      , b-bottomPadding);
			GL11.glTexCoord2f(1      , 1);
			GL11.glVertex2f  (r      , b);
			GL11.glTexCoord2f(0      , 1);
			GL11.glVertex2f  (l      , b);
		GL11.glEnd();
		if(!isFocused()) ShaderManager.bind2DShaderTexture("res/textures/gui/frame/frame_top.png");
		else ShaderManager.bind2DShaderTexture("res/textures/gui/frame/frame_top_focused.png");
		GL11.glBegin(GL11.GL_QUADS);
			GL11.glTexCoord2f(0      , 0);
			GL11.glVertex2f  (l      , t);
			GL11.glTexCoord2f(1      , 0);
			GL11.glVertex2f  (r      , t);
			GL11.glTexCoord2f(1      , 1);
			GL11.glVertex2f  (r      , t+topPadding);
			GL11.glTexCoord2f(0      , 1);
			GL11.glVertex2f  (l      , t+topPadding);
		GL11.glEnd();
		ShaderManager.bind2DShaderTexture("res/textures/gui/frame/frame_side.png");
		GL11.glBegin(GL11.GL_QUADS);
			GL11.glTexCoord2f(0      , 0);
			GL11.glVertex2f  (l      , t+topPadding);
			GL11.glTexCoord2f(1      , 0);
			GL11.glVertex2f  (l+leftPadding, t+topPadding);
			GL11.glTexCoord2f(1      , 1);
			GL11.glVertex2f  (l+leftPadding, b-bottomPadding);
			GL11.glTexCoord2f(0      , 1);
			GL11.glVertex2f  (l      , b-bottomPadding);
			
			GL11.glTexCoord2f(1      , 0);
			GL11.glVertex2f  (r-rightPadding, t+topPadding);
			GL11.glTexCoord2f(0      , 0);
			GL11.glVertex2f  (r      , t+topPadding);
			GL11.glTexCoord2f(0      , 1);
			GL11.glVertex2f  (r      , b-bottomPadding);
			GL11.glTexCoord2f(1      , 1);
			GL11.glVertex2f  (r-rightPadding, b-bottomPadding);
		GL11.glEnd();
		
		if(title != null) title.render();
		
		float ctX = r-(topPadding/2)-1;
		float ctY = t+(topPadding/2);
		float hs = topPadding/2-2;
		ShaderManager.bind2DShaderTexture("res/textures/gui/frame/frame_close.png");
		if(mouseInClose) ShaderManager.set2dColorsInverted();
		GL11.glBegin(GL11.GL_QUADS);
			GL11.glTexCoord2f(0      , 0);
			GL11.glVertex2f  (ctX-hs      , ctY-hs);
			GL11.glTexCoord2f(1      , 0);
			GL11.glVertex2f  (ctX+hs, ctY-hs);
			GL11.glTexCoord2f(1      , 1);
			GL11.glVertex2f  (ctX+hs, ctY+hs);
			GL11.glTexCoord2f(0      , 1);
			GL11.glVertex2f  (ctX-hs      , ctY+hs);
		GL11.glEnd();
		if(mouseInClose) ShaderManager.set2dColorsNormal();
		renderInside = true;
		/*
		GL11.glBegin(GL11.GL_QUADS);
			GL11.glTexCoord2f(0      , 0);
			GL11.glVertex2i  (x      , y);
			GL11.glTexCoord2f(1      , 0);
			GL11.glVertex2i  (x+width, y);
			GL11.glTexCoord2f(1      , 1);
			GL11.glVertex2i  (x+width, y+height);
			GL11.glTexCoord2f(0      , 1);
			GL11.glVertex2i  (x      , y+height);
		GL11.glEnd();
		*/
	}
	
	public void setTitle(String title){
		if(this.title != null) this.title.release();
		this.title = new Text2d(title, "tahoma", 250, new Vector2f(5,1), width-topPadding, false, this);
		this.title.setNumberOfLines(1);
	}

	public int getLevel() {
		return level;
	}
	
	public void setLevel(int level) {
		this.level = level;
	}

	public void reduceLevel(int levelReduction) {
		this.level -= levelReduction;
	}
	
	@Override
	public void resetFocus(){
		super.resetFocus();
		if(focusedChild != null) focusedChild.resetFocus();
	}

	public void update() {
		Vector2f mousePos = UserInputUtil.getMousePos();
		
		for(GUIElement element:getChildrens()){
			if(mouseIn && isInElement(element, mousePos)){
				element.setMouseIn(true);
			}
			else{
				element.setMouseIn(false);
			}
		}
		
		float ctX = getBottomRight().x-(topPadding/2)-1;
		float ctY = getTopLeft().y+(topPadding/2);
		float hs = topPadding/2-2;
		if(!dragged && isFocused() && UserInputUtil.mouseLeftClicked()){
			if(ctX-hs < mousePos.x && ctX+hs > mousePos.x && ctY-hs < mousePos.y && ctY+hs > mousePos.y){
				destroy();
				return;
			}
			if(!dragged && getTopLeft().x < mousePos.x && mousePos.x < getBottomRight().x && getTopLeft().y < mousePos.y && mousePos.y < getTopLeft().y+topPadding){
				dragged = true;
				mouseOldPos = new Vector2f(mousePos);
			}
		}
		if(isFocused() && UserInputUtil.mouseLeftPressed()){
			if(dragged){
				Vector2f mouseD = new Vector2f(mousePos.x - mouseOldPos.x, mousePos.y - mouseOldPos.y);
				translate(mouseD);
				mouseOldPos = new Vector2f(mousePos);
				//System.out.println("frame moved to "+mouseD.x+";"+mouseD.y);
				return;
			}
		}
		else{
			dragged = false;
		}
		if(isFocused() && UserInputUtil.mouseLeftClicked()){
			boolean focusFound = false;
			for(GUIElement element:getChildrens()){
				if(element.getClass() == GUIComboBox.class && ((GUIComboBox)element).isExpanded()){
					if(!focusFound && isInElement(element, mousePos)){
						element.setFocus();
						focusFound = true;
					}
					else{
						if(element.isFocused()) element.resetFocus();
					}
				}
			}
			for(GUIElement element:getChildrens()){
				if(element.getClass() != GUIComboBox.class || !((GUIComboBox)element).isExpanded())
				if(!focusFound && isInElement(element, mousePos)){
					element.setFocus();
					focusFound = true;
				}
				else{
					if(element.isFocused()) element.resetFocus();
				}
			}
		}
		if(!isFocused()){
			for(GUIElement element:getChildrens()) element.resetFocus();
		}
		if(mouseIn && ctX-hs < mousePos.x && ctX+hs > mousePos.x && ctY-hs < mousePos.y && ctY+hs > mousePos.y){
			mouseInClose = true;
			return;
		}
		else{
			mouseInClose = false;
		}
	}

	private void translate(Vector2f translation) {
		this.x += translation.x;
		this.y += translation.y;
	}

	private static boolean isInElement(GUIElement element, Vector2f pos) {
		Vector2f tl = element.getTopLeft();
		Vector2f br = element.getBottomRight();
		if(tl.x < pos.x && tl.y < pos.y && br.x > pos.x && br.y > pos.y)
			return true;
		return false;
	}
	
	@Override
	public Dimension getContainerPos(){
		if(containerPadding == null) return new Dimension(0,0);
		if(!renderInside) return new Dimension(x, y);
		return new Dimension(x+containerPadding.width, y+containerPadding.height);
	}

	public boolean isDragged() {
		return dragged;
	}
	
	@Override
	public void destroy(){
		super.destroy();
		if(title != null) title.release();
	}

}
