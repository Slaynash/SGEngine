package slaynash.opengl.render2d;

import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_PROJECTION;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glOrtho;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;

import java.awt.Dimension;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector2f;
import org.newdawn.slick.opengl.Texture;

import slaynash.opengl.render2d.button.GUIButton;
import slaynash.opengl.render2d.button.GUIButtonEvent;
import slaynash.opengl.render2d.button.GUIButtonListener;
import slaynash.opengl.render2d.comboBox.GUIComboBox;
import slaynash.opengl.shaders.ShaderManager;
import slaynash.opengl.textureUtils.TextureManager;
import slaynash.opengl.utils.DisplayManager;
import slaynash.opengl.utils.UserInputUtil;

public class GUIManager {

	public static final int POPUP_INFO = 0;
	public static final int POPUP_WARNING = 1;
	public static final int POPUP_ERROR = 2;
	public static final int DRAWMODE_MENU = 0;
	public static final int DRAWMODE_GAME = 1;
	
	public static final int ELEMENT_MENU = 0;
	public static final int ELEMENT_GAME = 1;
	protected static final int ELEMENT_POPUP_UP = 2;

	private static List<GUIMiniPopup> popups = new ArrayList<GUIMiniPopup>();
	
	private static List<GUIElement> menuElements = new ArrayList<GUIElement>();
	private static List<GUIElement> menuElementsToAdd = new ArrayList<GUIElement>();
	private static List<GUIElement> menuElementsToRemove = new ArrayList<GUIElement>();
	
	private static List<GUIElement> gameElements = new ArrayList<GUIElement>();
	private static List<GUIElement> gameElementsToAdd = new ArrayList<GUIElement>();
	private static List<GUIElement> gameElementsToRemove = new ArrayList<GUIElement>();
	private static int menuTopLevel = 0;
	private static int gameTopLevel = 0;
	private static GUIElement focusedElement;
	private static Texture background;
	private static int drawMode;
	protected static boolean isPopup;
	protected static GUIPopup popup;
	private static GUIElement elementUnderMouse;
	private static boolean menuShown = true;
	private static String popupCloseText;
	
	public static void removeElement(GUIElement element){
		if(element.hasChildrens()){
			for(GUIElement childElement:element.getChildrens()) removeElement(childElement);
		}
		if(element.getLocation() == ELEMENT_MENU) menuElementsToRemove.add(element);
		if(element.getLocation() == ELEMENT_GAME) gameElementsToRemove.add(element);
		//System.out.println("added "+element+" to remove list");
	}
	
	public static void hideMenu(boolean hide){
		menuShown = !hide;
	}
	
	private static void removeElements(){
		for(GUIElement e:menuElementsToRemove){
			if(e != popup){
				menuElements.remove(e);
				//System.out.println(e+" removed !");
			}
		}
		menuElementsToRemove.clear();
		for(GUIElement e:gameElementsToRemove){
			if(e != popup){
				gameElements.remove(e);
				//System.out.println(e+" removed !");
			}
		}
		gameElementsToRemove.clear();
	}

	public static void add(GUIElement guiElement, int location) {
		if(location == ELEMENT_MENU) menuElementsToAdd.add(guiElement);
		else if(location == ELEMENT_GAME) gameElementsToAdd.add(guiElement);
		else if(location == ELEMENT_POPUP_UP) popups.add((GUIMiniPopup)guiElement);
	}
	
	public static void update() {
		menuElements.addAll(menuElementsToAdd);
		menuElementsToAdd.clear();
		gameElements.addAll(gameElementsToAdd);
		gameElementsToAdd.clear();
		removeElements();
		
		Vector2f mousePos = UserInputUtil.getMousePos();
		boolean overFound = false;
		//check popup
		if(isPopup){
			if(!elementDragged()){
				if(isInElement(popup, mousePos)){
					setMouseInElement(popup);
					overFound = true;
				}
				else{
					popup.setMouseIn(false);
				}
			}
			popup.update();
		}
		//check menu
		if(!elementDragged() && menuShown){
			for(int i=menuTopLevel;i>=0;i--){
				for(GUIElement e:menuElements){
					if(e.getClass() == GUIFrame.class && ((GUIFrame)e).getLevel() == i){
						if(!overFound && isInElement(e, mousePos)){
							setMouseInElement(e);
							overFound = true;
						}
						else e.setMouseIn(false);
					}
				}
			}
			for(GUIElement e:menuElements){
				if(e.getClass() != GUIFrame.class){
					if(!overFound && isInElement(e, mousePos)){
						setMouseInElement(e);
						overFound = true;
					}
					else e.setMouseIn(false);
				}
			}
			for(GUIElement e:menuElements){
				if(e.getClass() == GUIComboBox.class && ((GUIComboBox)e).isExpanded()){
					if(!overFound && isInElement(e, mousePos)){
						setMouseInElement(e);
						overFound = true;
					}
					else e.setMouseIn(false);
				}
			}
		}
		//check in-game
		if(!elementDragged()){
			for(int i=gameTopLevel;i>=0;i--){
				for(GUIElement e:gameElements){
					if(e.getClass() == GUIFrame.class && ((GUIFrame)e).getLevel() == i){
						if(!overFound && isInElement(e, mousePos)){
							setMouseInElement(e);
							overFound = true;
						}
						else e.setMouseIn(false);
					}
				}
			}
			for(GUIElement e:gameElements){
				if(e.getClass() == GUIComboBox.class && ((GUIComboBox)e).isExpanded()){
					if(!overFound && isInElement(e, mousePos)){
						setMouseInElement(e);
						overFound = true;
					}
					else e.setMouseIn(false);
				}
			}
			for(GUIElement e:gameElements){
				if(e.getClass() != GUIFrame.class){
					if(!overFound && isInElement(e, mousePos)){
						setMouseInElement(e);
						overFound = true;
					}
					else e.setMouseIn(false);
				}
			}
		}
		//System.out.println(overFound);
		if(!overFound && !elementDragged()) setMouseInElement(null);
		if(UserInputUtil.mouseLeftClicked()){
			if(isPopup){
				popup.setFocus();
			}
			else{
				if(focusedElement != null && focusedElement != elementUnderMouse){
					focusedElement.resetFocus();
				}
				if(elementUnderMouse != focusedElement){
					focusedElement = elementUnderMouse;
					if(elementUnderMouse != null){
						elementUnderMouse.setFocus();
						if(elementUnderMouse.getClass() == GUIFrame.class){
							setTopFrame((GUIFrame) elementUnderMouse, elementUnderMouse.getLocation());
						}
					}
				}
			}
		}
			/*
			boolean breakAll = false;
			for(int i=topLevel;i>0;i--){
				if(breakAll) break;
				for(GUIElement element:elements){
					if(element.getClass() == GUIFrame.class){
						if(((GUIFrame)element).getLevel() == i && isInElement(element, mousePos)){
							if(focusedElement != null) focusedElement.resetFocus();
							focusedElement = element.setFocus();
							setTopFrame(((GUIFrame)focusedElement));
							breakAll = true;
							break;
						}
					}
				}
			}
			if (!breakAll) for(GUIElement element:elements){
				if(isInElement(element, mousePos)){
					if(focusedElement != null) focusedElement.resetFocus();
					element.setFocus();
					break;
				}
			}
			*/
		for(GUIElement e:gameElements) if(e.getClass() == GUIFrame.class) ((GUIFrame)e).update();
		for(GUIElement e:menuElements) if(e.getClass() == GUIFrame.class) ((GUIFrame)e).update();
	}

	private static boolean elementDragged() {
		if(elementUnderMouse == null) return false;
		if(elementUnderMouse.getClass() == GUIFrame.class) return ((GUIFrame)elementUnderMouse).isDragged();
		if(elementUnderMouse.getClass() == GUIPopup.class) return ((GUIPopup)elementUnderMouse).isDragged();
		return false;
	}

	private static void setMouseInElement(GUIElement e) {
		if(e != null) e.setMouseIn(true);
		if(e == elementUnderMouse) return;
		elementUnderMouse = e;
	}

	public static void render() {
		prepare();
		if(canDrawBackground()) drawBackground();
		Vector2f mousePos = UserInputUtil.getMousePos();
		for(GUIElement element:gameElements) if(element.getClass() != GUIFrame.class && element.getClass() != GUIComboBox.class || (element.getClass() == GUIComboBox.class && !((GUIComboBox)element).isExpanded() && !isInElement(element, mousePos)) )element.render();
		for(GUIElement element:gameElements) if(element.getClass() != GUIFrame.class && element.getClass() == GUIComboBox.class && (((GUIComboBox)element).isExpanded()|| isInElement(element, mousePos) )) element.render();
		
		for(int i=0;i<=gameTopLevel;i++){
			for(GUIElement element:gameElements){
				if(element.getClass() == GUIFrame.class){
					if(((GUIFrame)element).getLevel() == i){
						element.render();
					}
				}
			}
		}
		if(menuShown){
			for(GUIElement element:menuElements) if(element.getClass() != GUIFrame.class && element.getClass() != GUIComboBox.class || (element.getClass() == GUIComboBox.class && !((GUIComboBox)element).isExpanded() && !isInElement(element, mousePos)) )element.render();
			for(GUIElement element:menuElements) if(element.getClass() != GUIFrame.class && element.getClass() == GUIComboBox.class && (((GUIComboBox)element).isExpanded()|| isInElement(element, mousePos) )) element.render();
			
			for(int i=0;i<=menuTopLevel;i++){
				for(GUIElement element:menuElements){
					if(element.getClass() == GUIFrame.class){
						if(((GUIFrame)element).getLevel() == i){
							element.render();
						}
					}
				}
			}
		}
		if(isPopup) popup.render();
		int pn = -GUIMiniPopup.HEIGHT;
		List<GUIMiniPopup> rml = new ArrayList<GUIMiniPopup>();
		for(GUIMiniPopup p:popups){
			if(p.getLifeTime() < GUIMiniPopup.SHOW_DURATION){
				pn += (p.getLifeTime()/GUIMiniPopup.SHOW_DURATION)*GUIMiniPopup.HEIGHT;
			}
			else if(p.getLifeTime() < GUIMiniPopup.SHOW_DURATION+GUIMiniPopup.STAY_DURATION){
				pn += GUIMiniPopup.HEIGHT;
			}
			else if(p.getLifeTime() < GUIMiniPopup.SHOW_DURATION+GUIMiniPopup.STAY_DURATION+GUIMiniPopup.HIDE_DURATION){
				pn += (-(p.getLifeTime()-GUIMiniPopup.SHOW_DURATION-GUIMiniPopup.STAY_DURATION)/GUIMiniPopup.HIDE_DURATION+1)*GUIMiniPopup.HEIGHT;
			}
			else{
				rml.add(p);
				p.destroy();
			}
			p.setPosition(DisplayManager.getWidth()-350, pn);
		}
		popups.removeAll(rml);
		for(int i=popups.size()-1;i>=0;i--){
			popups.get(i).render();
		}
		restore();
	}

	private static void drawBackground() {
		float imageWidth = background.getImageWidth();
		float imageHeight = background.getImageHeight();
		
		float aspectRatio = DisplayManager.getWidth()/((float)DisplayManager.getHeight());
		float imgAspectRatio = imageWidth/imageHeight;
		
		float maxX = (imageWidth/(float)(background.getTextureWidth()));
		float maxY = (imageHeight/(float)(background.getTextureHeight()));
		float centerx = maxX*0.5f;
		float centery = maxY*0.5f;
		
		ShaderManager.bind2DShaderTextureID(background.getTextureID());
		
		if(imgAspectRatio > aspectRatio){
			float hs = (aspectRatio/imgAspectRatio)/2*maxX;
			GL11.glBegin(GL11.GL_TRIANGLES);
				GL11.glTexCoord2f(centerx-hs, 0);
				GL11.glVertex2f  (0, 0);
				
				GL11.glTexCoord2f(centerx+hs, 0);
				GL11.glVertex2f  (Display.getWidth(), 0);
				
				GL11.glTexCoord2f(centerx+hs, maxY);
				GL11.glVertex2f  (Display.getWidth(), Display.getHeight());
				
	
				GL11.glTexCoord2f(centerx+hs, maxY);
				GL11.glVertex2f  (Display.getWidth(), Display.getHeight());
				
				GL11.glTexCoord2f(centerx-hs, maxY);
				GL11.glVertex2f  (0, Display.getHeight());
	
				GL11.glTexCoord2f(centerx-hs, 0);
				GL11.glVertex2f  (0, 0);
				
			GL11.glEnd();
		}else{
			float hs = (imgAspectRatio/aspectRatio)/2*maxY;
			GL11.glBegin(GL11.GL_TRIANGLES);
				GL11.glTexCoord2f(0, centery-hs);
				GL11.glVertex2f  (0, 0);
				
				GL11.glTexCoord2f(maxX, centery-hs);
				GL11.glVertex2f  (Display.getWidth(), 0);
				
				GL11.glTexCoord2f(maxX, centery+hs);
				GL11.glVertex2f  (Display.getWidth(), Display.getHeight());
				
	
				GL11.glTexCoord2f(maxX, centery+hs);
				GL11.glVertex2f  (Display.getWidth(), Display.getHeight());
				
				GL11.glTexCoord2f(0, centery+hs);
				GL11.glVertex2f  (0, Display.getHeight());
	
				GL11.glTexCoord2f(0, centery-hs);
				GL11.glVertex2f  (0, 0);
				
			GL11.glEnd();
		}
	}

	private static boolean canDrawBackground() {
		if(background != null && drawMode == DRAWMODE_MENU) return true;
		return false;
	}
	
	public static void setBackground(String backgroundPath){
		GUIManager.background = TextureManager.getTexture(backgroundPath);
	}
	
	public static void removeBackground(){
		GUIManager.background = null;
	}

	private static void prepare() {
		if(drawMode == DRAWMODE_GAME){
			glDisable(GL_DEPTH_TEST);
			glClear(GL_DEPTH_BUFFER_BIT);
			
			glMatrixMode(GL_PROJECTION);
			glPushMatrix();
			glLoadIdentity();
			glOrtho(0, DisplayManager.getWidth(), DisplayManager.getHeight(), 0, -1, 1);
			
			glMatrixMode(GL_MODELVIEW);
			glPushMatrix();
			glLoadIdentity();
		}
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
	}
	private static void restore(){
		if(drawMode == DRAWMODE_GAME){
			glMatrixMode(GL_PROJECTION);
			glPopMatrix();
			glMatrixMode(GL_MODELVIEW);
			glPopMatrix();
			glEnable(GL_DEPTH_TEST);
		}
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
	}

	public static int getTopLevel(int location) {
		if(location == ELEMENT_MENU) return menuTopLevel;
		else return gameTopLevel;
	}
	
	public static int addTopLevel(int location) {
		if(location == ELEMENT_MENU){
			menuTopLevel++;
			return menuTopLevel;
		}
		else{
			gameTopLevel++;
			return gameTopLevel;
		}
	}
	
	private static void setTopFrame(GUIFrame frame, int location){
		int baseLevel = frame.getLevel();
		if(location == ELEMENT_MENU){
			for(int i=baseLevel+1;i<=menuTopLevel;i++){
				for(GUIElement element:menuElements){
					if(element.getClass() == GUIFrame.class){
						if(((GUIFrame)element).getLevel() == i){
							((GUIFrame)element).reduceLevel(1);
							//System.out.println(((GUIFrame)element)+" deranked to level "+((GUIFrame)element).getLevel());
						}
					}
				}
			}
			frame.setLevel(menuTopLevel);
		}
		else{
			for(int i=baseLevel+1;i<=gameTopLevel;i++){
				for(GUIElement element:gameElements){
					if(element.getClass() == GUIFrame.class){
						if(((GUIFrame)element).getLevel() == i){
							((GUIFrame)element).reduceLevel(1);
							//System.out.println(((GUIFrame)element)+" deranked to level "+((GUIFrame)element).getLevel());
						}
					}
				}
			}
			frame.setLevel(gameTopLevel);
		}
		//System.out.println(frame+" is now on the top ! ("+frame.getLevel()+")");
	}
	
	private static boolean isInElement(GUIElement element, Vector2f pos) {
		Vector2f tl = element.getTopLeft();
		Vector2f br = element.getBottomRight();
		if(tl.x < pos.x && tl.y < pos.y && br.x > pos.x && br.y > pos.y)
			return true;
		return false;
	}

	public static void showPopup(int popupType, String text) {
		if(popupCloseText == null){
			try {
				Class<?> c = Class.forName("slaynash.text.utils.Localization");
			    Class<?>[] argTypes = new Class[] { String.class };
			    Method getText = c.getDeclaredMethod("getText", argTypes);
			    popupCloseText = (String)getText.invoke(null, (Object)new String("POPUP_BUTTON_CLOSE"));
			} catch (Exception e) {
				System.out.println("[GUIManager.class/INFO] Class slaynash.text.utils.Localization, popup closebutton text is now \""+popupCloseText+"\".");
				popupCloseText = "Close";
				e.printStackTrace();
			}
		}
		popup = new GUIPopup(400, 150, text, text, popupType);
		final GUIButton close = new GUIButton(new Dimension(100, 30), new Dimension(200-50, 100), popup, 0);
		close.setText(popupCloseText, true);
		close.addGUIButtonListener(new GUIButtonListener() {
			
			@Override
			public void mouseReleased(GUIButtonEvent e) {
				popup.destroy();
			}
			
			@Override public void mousePressed(GUIButtonEvent e) { }
			
			@Override public void mouseExited(GUIButtonEvent e) { }
			
			@Override public void mouseEntered(GUIButtonEvent e) { }
		});
		isPopup = true;
		if(focusedElement != null)focusedElement.resetFocus();
		focusedElement = popup;
		popup.setFocus();
	}

	public static void setDrawMode(int drawmode) {
		drawMode = drawmode;
	}
	
	public static int getDrawMode() {
		return drawMode;
	}

	public static void removePopup() {
		isPopup = false;
	}
	
	public static void reset() {
		for(GUIElement e:menuElements) {e.destroy();}
			menuElements.clear();
			menuElementsToRemove.clear();
			menuElementsToAdd.clear();
		for(GUIElement e:gameElements) {e.destroy();}
			gameElements.clear();
			gameElementsToRemove.clear();
			gameElementsToAdd.clear();
		
	}
	
}
