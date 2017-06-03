package slaynash.opengl.utils.vr;

import org.lwjgl.util.vector.Matrix4f;

import slaynash.opengl.utils.MatrixUtils;

public class VRController {
	
	private Matrix4f pose;
	private boolean isValid;
	int id = -1;
	private boolean isPoseValid;
	private VRControllerEventListener listener;
	
	public void setPose(Matrix4f pose){
		if(pose == null){
			isPoseValid = false;
			isValid = true;
			return;
		}
		MatrixUtils.copy(pose, this.pose);
	}
	
	public void unValid(){
		isValid = false;
	}
	
	public boolean isValid(){
		return isValid;
	}
	
	public boolean isPoseValid(){
		return isPoseValid;
	}
	
	public void setVRControllerEventListener(VRControllerEventListener listener){
		this.listener = listener;
	}
	
	public void throwEvent(int eventType){
		if(listener != null) listener.onEvent(eventType);
	}
}
