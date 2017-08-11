package slaynash.sgengine.inputs;

import java.util.ArrayList;

import com.studiohartman.jamepad.ControllerAxis;
import com.studiohartman.jamepad.ControllerButton;
import com.studiohartman.jamepad.ControllerManager;

import slaynash.sgengine.Configuration;
import slaynash.sgengine.LogSystem;

public class ControllersControlManager {
	
	private static boolean initialized = false;
	private static ControllerManager controllerManager;
	private static Controller[] controllers;
	private static ArrayList<ControllerControl> controls = new ArrayList<ControllerControl>();

	public static void init() {
		if(initialized) {
			LogSystem.out_println("[ControllerscontrolManager] Trying to re-initialize Controllers, ingoring.");
			return;
		}
		initialized = true;
		
		controllerManager = new ControllerManager(Configuration.getNumberOfControllers());
		controllerManager.initSDLGamepad();
		
		controllers = new Controller[Configuration.getNumberOfControllers()];
		for(int i=0;i<controllers.length;i++) controllers[i] = new Controller(i, controllerManager.getControllerIndex(i));
	}
	
	public static void update() {
		controllerManager.update();
		for(Controller controller:controllers) {
            controller.update();
        }
		for(ControllerControl control:controls) {
			control.update();
        }
	}
	
	public static void loadDefaultControls(){
		if(!Configuration.isControllersEnabled()) {
			LogSystem.out_println("[ControllerscontrolManager] Controllers are disabled, ignoring the load of default controls");
			return;
		}
		if(!initialized) init();
		
		for(Controller controller:controllers) {
			controls.add(new ControllerControl(controller, "forward"	, ControllerAxis.LEFTY));
			controls.add(new ControllerControl(controller, "right"		, ControllerAxis.LEFTX));
			controls.add(new ControllerControl(controller, "use"		, ControllerButton.X));
			controls.add(new ControllerControl(controller, "jump"		, ControllerButton.A));
			controls.add(new ControllerControl(controller, "crouch"		, ControllerButton.RIGHTSTICK));
			controls.add(new ControllerControl(controller, "cameraX"	, ControllerAxis.RIGHTX));
			controls.add(new ControllerControl(controller, "cameraY"	, ControllerAxis.RIGHTY));
		}
	}
	
	public static Controller getController(int index) {
		return controllers[index];
	}
	
	public static boolean isPressed(int controller, String key){
		for(ControllerControl control:controls) if(key.equals(control.getName()) && controller == control.getControllerId()) return control.isPressed();
		return false;
	}
	
	public static float getValue(int controller, String key){
		for(ControllerControl control:controls) if(key.equals(control.getName()) && controller == control.getControllerId()) return control.getValue();
		return 0;
	}
}
