package slaynash.sgengine.utils;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector2f;

import slaynash.sgengine.LogSystem;

public class UserInputUtil {
	
	private static boolean button0down = false;
	private static boolean boutton0clicked = false;
	private static boolean boutton0released = false;

	public static void initController() {
		// Init the user inputs
		try {
			Keyboard.create();
			Mouse.create();
		} catch (LWJGLException e) {e.printStackTrace(LogSystem.getErrStream());}
	}

	private static Vector2f mousePos = new Vector2f(0,0);
	
	public static void update(){
		boolean b0 = Mouse.isButtonDown(0);
		if(button0down == b0){
			boutton0clicked = false;
			boutton0released = false;
		}
		else{
			boutton0clicked = b0;
			boutton0released = !b0;
			button0down = b0;
		}
		//LogSystem.out_println(button0down+""+boutton0clicked);
		mousePos.x = Mouse.getX();
		mousePos.y = -Mouse.getY()+DisplayManager.getHeight();
	}
	
	public static void setMouseGrabbed(boolean grabbed){
		Mouse.setGrabbed(grabbed);
	}

	public static Vector2f getMousePos() {
		return mousePos;
	}

	public static boolean mouseLeftClicked() {
		return boutton0clicked;
	}
	
	public static boolean mouseLeftPressed() {
		return button0down;
	}
	
	public static boolean mouseLeftReleased() {
		return boutton0released;
	}
	
	public static boolean mouseRightClicked() {
		return Mouse.isButtonDown(1);
	}

	public static void exitControls() {
		Mouse.setGrabbed(false);
		Mouse.destroy();
		Keyboard.destroy();
	}
}
