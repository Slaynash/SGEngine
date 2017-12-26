package slaynash.sgengine.gui;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Vector2f;

import slaynash.sgengine.maths.Vector2i;
import slaynash.sgengine.utils.UserInputUtil;

public abstract class GUIElement {
	protected int x;
	protected int y;
	private int width,height;
	private boolean focus = false;
	protected boolean mouseIn = false;
	protected boolean mouseEntered = false;
	protected boolean mouseExited = false;
	private List<GUIElement> childrens;
	private boolean canParent = false;
	private GUIElement parent;
	protected GUIElement focusedChild;
	protected Vector2i containerPadding;
	protected Vector2i containerSize;
	private boolean canChild;
	private boolean destroyed;
	private int location;
	
	public abstract void render();
	public void update() {}
	
	
	/**
	 * If parent is set, location will be ignored.
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param parent
	 * @param canParent
	 * @param location
	 */
	public GUIElement(int x, int y, int width, int height, GUIElement parent, boolean canParent, int location) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.canParent = canParent;
		this.canChild = true;
		if(canParent) childrens = new ArrayList<GUIElement>();
		if(parent == null){
			GUIManager.add(this, location);
			this.location = location;
		}
		else{
			this.location = parent.getLocation();
			this.parent = parent;
			parent.addChild(this);
		}
	}
	
	public GUIElement(int x, int y, int width, int height, GUIElement parent, boolean canParent, boolean canChild, int location) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.canParent = canParent;
		this.canChild = canChild;
		this.location = location;
		if(canParent) childrens = new ArrayList<GUIElement>();
		if(canChild){
			if(parent == null){
				GUIManager.add(this, location);
				this.location = location;
			}
			else{
				this.location = parent.getLocation();
				this.parent = parent;
				parent.addChild(this);
			}
		}
	}
	
	private void addChild(GUIElement guiElement) {
		if(!canParent){
			System.err.println("[GUIElement] bad parent ! (Adding "+guiElement.getClass()+" to "+this.getClass()+")");
			return;
		}
		childrens.add(guiElement);
	}

	public void destroy(){
		destroyed = true;
		if(!canChild) return;
		if(parent == null) GUIManager.removeElement(this);
		else parent.removeElement(this);
	}

	private void removeElement(GUIElement guiElement) {
		childrens.remove(guiElement);
	}

	public boolean hasChildrens() {
		return canParent;
	}

	public List<GUIElement> getChildrens() {
		return childrens;
	}

	public Vector2f getTopLeft() {
		if(parent != null)
			return new Vector2f(x+parent.getContainerPos().x, y+parent.getContainerPos().y);
		else
			return new Vector2f(x, y);
	}
	
	public Vector2f getBottomRight() {
		if(parent != null)
			return new Vector2f(x+parent.getContainerPos().x+width, y+parent.getContainerPos().y+height);
		else
			return new Vector2f(x+width, y+height);
	}

	public void setFocus() {
		focus = true;
	}

	public void resetFocus() {
		focus = false;
	}
	
	public boolean isFocused() {
		return focus;
	}
	
	public GUIElement getParent(){
		return parent;
	}
	
	public Vector2i getContainerPos(){
		if(containerPadding == null) return new Vector2i(x,y);
		return new Vector2i(x+containerPadding.x, y+containerPadding.y);
	}
	public Vector2i getContainerSize(){
		if(containerSize == null) return new Vector2i(0,0);
		return containerSize;
	}

	public void setMouseIn(boolean b) {
		if(b == mouseIn){
			mouseEntered = false;
			mouseExited = false;
		}
		else{
			mouseEntered = b;
			mouseExited = !b;
			mouseIn = b;
		}
	}

	public boolean isMouseClickedIn() {
		return mouseIn && UserInputUtil.mouseLeftClicked();
	}
	
	public boolean isMousePressedIn() {
		return mouseIn && UserInputUtil.mouseLeftPressed();
	}

	public boolean isDestroyed() {
		return destroyed;
	}
	
	public int getLocation(){
		return location;
	}
	
	public int getWidth(){
		return width;
	}
	
	public int getHeight(){
		return height;
	}
	
	public void setPosition(Vector2i position) {
		this.x = position.x;
		this.y = position.y;
	}
	
	public void setWidth(int width){
		this.width = width;
		redraw();
	}
	
	public void setHeight(int height){
		this.height = height;
		redraw();
	}
	
	public void redraw(){}


	public boolean isDraggable() {
		return false;
	}


	public boolean isDragged() {
		return false;
	}


	public boolean isLevelable() {
		return false;
	}


	public int getLevel() {
		return 0;
	}


	public void setLevel(int menuTopLevel) {}


	public void reduceLevel(int i) {}


	public boolean isExpandable() {
		return false;
	}


	public boolean isExpanded() {
		return false;
	}
}
