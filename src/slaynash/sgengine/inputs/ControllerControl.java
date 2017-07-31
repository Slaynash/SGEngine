package slaynash.sgengine.inputs;

import com.studiohartman.jamepad.ControllerAxis;
import com.studiohartman.jamepad.ControllerButton;

public class ControllerControl {
	
	private String name = "";
	private boolean isAxis;
	
	private ControllerAxis axis;
	private ControllerButton button;
	
	private boolean isPressed = false;
	private float value = 0;
	private Controller controller;
	
	public ControllerControl(Controller controller, String name, ControllerAxis axis) {
		isAxis = true;
		this.axis = axis;
		this.controller = controller;
		this.name = name;
	}
	
	public ControllerControl(Controller controller, String name, ControllerButton button) {
		isAxis = false;
		this.button = button;
		this.controller = controller;
		this.name = name;
	}
	
	public float getValue() {
		return value;
	}
	
	public boolean isPressed() {
		return isPressed;
	}
	
	public void update() {
		if(isAxis) {
			float v = controller.getAxis(axis);
			isPressed = v > .5f ? true : v < -.5f ? true : false;
			value = v;
		}
		else {
			boolean v = controller.getButton(button);
			isPressed = v;
			value = v ? 1 : 0;
		}
	}

	public int getControllerId() {
		return controller.getId();
	}

	public String getName() {
		return name;
	}
	
}
