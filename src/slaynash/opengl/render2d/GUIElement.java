package slaynash.opengl.render2d;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Vector2f;

import slaynash.opengl.utils.UserInputUtil;

public abstract class GUIElement {
	protected int x;
	protected int y;
	protected int width,height;
	private boolean focus = false;
	protected boolean mouseIn = false;
	protected boolean mouseEntered = false;
	protected boolean mouseExited = false;
	private List<GUIElement> childrens;
	private boolean canParent = false;
	private GUIElement parent;
	protected GUIElement focusedChild;
	protected Dimension containerPadding;
	protected Dimension containerSize;
	private boolean canChild;
	private boolean destroyed;
	private int location;
	
	public abstract void render();
	
	
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
			return new Vector2f(x+parent.getContainerPos().width, y+parent.getContainerPos().height);
		else
			return new Vector2f(x, y);
	}
	
	public Vector2f getBottomRight() {
		if(parent != null)
			return new Vector2f(x+parent.getContainerPos().width+width, y+parent.getContainerPos().height+height);
		else
			return new Vector2f(x+width, y+height);
	}

	public GUIElement setFocus() {
		focus = true;
		return this;
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
	
	public Dimension getContainerPos(){
		if(containerPadding == null) return new Dimension(0,0);
		return new Dimension(x+containerPadding.width, y+containerPadding.height);
	}
	public Dimension getContainerSize(){
		if(containerSize == null) return new Dimension(0,0);
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
		//System.out.println(this+">"+mouseIn);
		//System.out.println("b"+b);
		//System.out.println("mouseIn"+mouseIn);
		//System.out.println("mouseEntered"+mouseEntered);
		//System.out.println("mouseExited"+mouseExited);
	}

	public boolean isMouseClickedIn() {
		if(mouseIn && UserInputUtil.mouseLeftClicked())
			return true;
		return false;
	}
	
	public boolean isMousePressedIn() {
		if(mouseIn && UserInputUtil.mouseLeftPressed())
			return true;
		return false;
	}

	public boolean isDestroyed() {
		return destroyed;
	}
	
	public int getLocation(){
		return location;
	}
}
