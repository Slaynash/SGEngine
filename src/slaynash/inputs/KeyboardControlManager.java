package slaynash.inputs;

import java.util.ArrayList;

import org.lwjgl.input.Keyboard;

public class KeyboardControlManager {
	
	public static ArrayList<KeyboardControl> userControls = new ArrayList<KeyboardControl>();

	public static void update() {
		for(KeyboardControl control:userControls) control.updateState();
	}
	
	public static void loadDefaultControls(){
		userControls.add(new KeyboardControl("forward" ,	Keyboard.KEY_Z));
		userControls.add(new KeyboardControl("backward",	Keyboard.KEY_S));
		userControls.add(new KeyboardControl("left"    ,	Keyboard.KEY_Q));
		userControls.add(new KeyboardControl("right"   ,	Keyboard.KEY_D));
		userControls.add(new KeyboardControl("use"     ,	Keyboard.KEY_E));
		userControls.add(new KeyboardControl("jump"    ,	Keyboard.KEY_SPACE));
		userControls.add(new KeyboardControl("crouch"  ,	Keyboard.KEY_LSHIFT));
		userControls.add(new KeyboardControl("run"     ,	Keyboard.KEY_LCONTROL));
	}
	
	public static boolean isPressed(String key){
		for(KeyboardControl control:userControls) if(key.equals(control.control)) return control.isPressed();
		return false;
	}
	
	public static void registerControl(String name, int key){
		userControls.add(new KeyboardControl(name, key));
	}
	
}
