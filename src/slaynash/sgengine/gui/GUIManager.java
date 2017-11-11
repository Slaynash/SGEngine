package slaynash.sgengine.gui;

import java.awt.Dimension;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector2f;

import slaynash.sgengine.LogSystem;
import slaynash.sgengine.gui.button.GUIButton;
import slaynash.sgengine.gui.button.GUIButtonEvent;
import slaynash.sgengine.gui.button.GUIButtonListener;
import slaynash.sgengine.gui.comboBox.GUIComboBox;
import slaynash.sgengine.models.Renderable2dModel;
import slaynash.sgengine.models.utils.VaoManager;
import slaynash.sgengine.shaders.ShaderManager;
import slaynash.sgengine.shaders.ShaderProgram;
import slaynash.sgengine.textureUtils.TextureDef;
import slaynash.sgengine.textureUtils.TextureManager;
import slaynash.sgengine.utils.DisplayManager;
import slaynash.sgengine.utils.UserInputUtil;

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
	private static int drawMode;
	protected static boolean isPopup;
	protected static GUIPopup popup;
	private static GUIElement elementUnderMouse;
	private static boolean menuShown = true;
	private static String popupCloseText;
	private static boolean useBackground = false;
	private static Renderable2dModel backgroundModel;
	
	public static void removeElement(GUIElement element){
		if(element.hasChildrens()){
			for(GUIElement childElement:element.getChildrens()) removeElement(childElement);
		}
		if(element.getLocation() == ELEMENT_MENU) menuElementsToRemove.add(element);
		if(element.getLocation() == ELEMENT_GAME) gameElementsToRemove.add(element);
	}
	
	public static void hideMenu(boolean hide){
		menuShown = !hide;
	}
	
	private static void removeElements(){
		for(GUIElement e:menuElementsToRemove){
			if(e != popup){
				menuElements.remove(e);
			}
		}
		menuElementsToRemove.clear();
		for(GUIElement e:gameElementsToRemove){
			if(e != popup){
				gameElements.remove(e);
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
		//LogSystem.out_println(overFound);
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
		ShaderManager.startGUIShaderDirect();
		prepare();
		if(canDrawBackground()) drawBackground();
		Vector2f mousePos = UserInputUtil.getMousePos();
		for(GUIElement element:gameElements) if(element.getClass() != GUIFrame.class && element.getClass() != GUIComboBox.class || (element.getClass() == GUIComboBox.class && !((GUIComboBox)element).isExpanded() && !isInElement(element, mousePos)) )element.render();
		for(GUIElement element:gameElements) if(element.getClass() != GUIFrame.class && element.getClass() == GUIComboBox.class && (((GUIComboBox)element).isExpanded() || isInElement(element, mousePos) )) element.render();
		
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
		ShaderManager.stopShader();
	}

	private static void drawBackground() {
		backgroundModel.render();
	}

	private static boolean canDrawBackground() {
		if((useBackground && drawMode == DRAWMODE_MENU) || (useBackground && drawMode == DRAWMODE_GAME && menuShown)) return true;
		return false;
	}
	
	public static void setBackground(String backgroundPath){
		if(backgroundPath == null) {
			useBackground = false;
			return;
		}
		TextureDef background = TextureManager.getTextureDef(backgroundPath, TextureManager.COLOR);
		
		float[] vertices = new float[2*3*2];
		float[] textCoords = new float[2*3*2];
		
		
		float imageWidth = background.getTexture().getImageWidth();
		float imageHeight = background.getTexture().getImageHeight();
		
		float aspectRatio = DisplayManager.getWidth()/((float)DisplayManager.getHeight());
		float imgAspectRatio = imageWidth/imageHeight;
		
		float maxX = (imageWidth/(background.getTexture().getTextureWidth()));
		float maxY = (imageHeight/(background.getTexture().getTextureHeight()));
		float centerx = maxX*0.5f;
		float centery = maxY*0.5f;
		
		if(imgAspectRatio > aspectRatio){
			float hs = (aspectRatio/imgAspectRatio)/2*maxX;
			textCoords[0] = centerx-hs;
			textCoords[1] = 0;
			vertices[0] = 0;
			vertices[1] = 0;
				
			textCoords[2] = centerx+hs;
			textCoords[3] = 0;
			vertices[2] = Display.getWidth();
			vertices[3] = 0;
			
			textCoords[4] = centerx+hs;
			textCoords[5] = maxY;
			vertices[4] = Display.getWidth();
			vertices[5] = Display.getHeight();
			
			textCoords[6] = centerx+hs;
			textCoords[7] = maxY;
			vertices[6] = Display.getWidth();
			vertices[7] = Display.getHeight();
			
			textCoords[8] = centerx-hs;
			textCoords[9] = maxY;
			vertices[8] = 0;
			vertices[9] = Display.getHeight();
			
			textCoords[10] = centerx-hs;
			textCoords[11] = 0;
			vertices[10] = 0;
			vertices[11] = 0;
		}else{
			float hs = (imgAspectRatio/aspectRatio)/2*maxY;
			
			textCoords[0] = 0;
			textCoords[1] = centery-hs;
			vertices[0] = 0;
			vertices[1] = 0;
				
			textCoords[2] = maxX;
			textCoords[3] = centery-hs;
			vertices[2] = Display.getWidth();
			vertices[3] = 0;
			
			textCoords[4] = maxX;
			textCoords[5] = centery+hs;
			vertices[4] = Display.getWidth();
			vertices[5] = Display.getHeight();
			
			textCoords[6] = maxX;
			textCoords[7] = centery+hs;
			vertices[6] = Display.getWidth();
			vertices[7] = Display.getHeight();
			
			textCoords[8] = 0;
			textCoords[9] = centery+hs;
			vertices[8] = 0;
			vertices[9] = Display.getHeight();
			
			textCoords[10] = 0;
			textCoords[11] = centery-hs;
			vertices[10] = 0;
			vertices[11] = 0;
		}
		
		backgroundModel = new Renderable2dModel(VaoManager.loadToVao(vertices, textCoords), background);
		useBackground = true;
	}
	
	public static void removeBackground(){
		useBackground = false;
	}

	private static void prepare() {
		if(drawMode == DRAWMODE_GAME){
			GL11.glDisable(GL11.GL_DEPTH_TEST);
			GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
			/* Useless, it's using a projectionless shader
			glMatrixMode(GL_PROJECTION);
			glPushMatrix();
			glLoadIdentity();
			glOrtho(0, DisplayManager.getWidth(), DisplayManager.getHeight(), 0, -1, 1);
			glMatrixMode(GL_MODELVIEW);
			*/
		}
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
	}
	private static void restore(){
		if(drawMode == DRAWMODE_GAME){
			/* Useless, it's using a projectionless shader
			glMatrixMode(GL_PROJECTION);
			glPopMatrix();
			glMatrixMode(GL_MODELVIEW);
			*/
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
						}
					}
				}
			}
			frame.setLevel(gameTopLevel);
		}
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
			    Method getText = c.getDeclaredMethod("getTranslation", argTypes);
			    popupCloseText = (String)getText.invoke(null, (Object)new String("POPUP_BUTTON_CLOSE"));
			} catch (Exception e) {
				popupCloseText = "Close";
				LogSystem.out_println("[GUIManager] Class slaynash.text.utils.Localization not found, popup closebutton text is now \""+popupCloseText+"\".");
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
	
	public static boolean isMenuShown() {
		return menuShown;
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
	
	@Deprecated
	public static ShaderProgram getShader() {
		return ShaderManager.getGUIShaderProgram();
	}
	
	@Deprecated
	public static void setShader(ShaderProgram shader) {
		ShaderManager.setGUIShader(shader);
	}
	
}
