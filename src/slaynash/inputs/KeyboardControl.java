package slaynash.inputs;

import org.lwjgl.input.Keyboard;

public class KeyboardControl {
	public String control = "<none>";
	private boolean activated = false;
	public int key;
	
	public KeyboardControl(String control, int key){
		this.control = control;
		this.key = key;
	}
	
	public boolean isPressed(){
		return activated;
	}
	
	public void setPressed(boolean pressed){
		activated = pressed;
		//System.out.println("["+control+" pressed");
	}
	
	public void updateState(){
		if(Keyboard.isKeyDown(key)){
			if(!activated){
				activated = true;
				//ConnectionManager.sendKey(control, true);
			}
			return;
		}
		if(activated){
			activated = false;
			//ConnectionManager.sendKey(control, false);
		}
		//System.out.println(control+": "+activated);
	}
}
