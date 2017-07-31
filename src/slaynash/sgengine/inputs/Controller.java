package slaynash.sgengine.inputs;

import com.studiohartman.jamepad.ControllerAxis;
import com.studiohartman.jamepad.ControllerButton;
import com.studiohartman.jamepad.ControllerIndex;
import com.studiohartman.jamepad.ControllerUnpluggedException;

public class Controller {
	
	private int index = 0;
	private ControllerIndex controllerIndex;
	
	private boolean connected = false;
	private float[] axes;
	private boolean[] buttons;
	
	public Controller(int index, ControllerIndex controllerIndex) {
		this.controllerIndex = controllerIndex;
		this.index = index;
		axes = new float[ControllerAxis.values().length];
		buttons = new boolean[ControllerButton.values().length];
		
	}
	
	public void update(){
		if(controllerIndex.isConnected()) {
			try {
				connected = true;
				for (ControllerAxis a : ControllerAxis.values()) {
					axes[a.ordinal()] = controllerIndex.getAxisState(a);
					if(axes[a.ordinal()] > -0.2f && axes[a.ordinal()] < 0.2f) axes[a.ordinal()] = 0;
				}
				for (ControllerButton b : ControllerButton.values()) {
					buttons[b.ordinal()] = controllerIndex.isButtonPressed(b);
				}
			} catch (ControllerUnpluggedException e) {}
		}
		else {
			connected = false;
			for(int i=0;i<axes.length;i++) axes[i] = 0;
			for(int i=0;i<buttons.length;i++) buttons[i] = false;
		}
	}
	
	public void startVibration() {
		startVibration(1,1);
	}
	
	public void startVibration(float magLeft, float magRight) {
		if(!connected) return;
		try {
			controllerIndex.startVibration(magLeft, magRight);
		} catch (ControllerUnpluggedException e) {}
	}
	
	public void stopVibration() {
		if(!connected) return;
		controllerIndex.stopVibration();
	}

	public float getAxis(ControllerAxis axis) {
		return axes[axis.ordinal()];
	}

	public boolean getButton(ControllerButton button) {
		return buttons[button.ordinal()];
	}

	public int getId() {
		return index;
	}
	
}
