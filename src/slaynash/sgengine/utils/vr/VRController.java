package slaynash.sgengine.utils.vr;

import org.lwjgl.util.vector.Matrix4f;

import jopenvr.VREvent_Data_t;
import slaynash.sgengine.utils.MatrixUtils;

public class VRController {
	
	private Matrix4f pose = new Matrix4f();
	private boolean isValid = false;
	private int id = -1;
	private boolean isPoseValid = false;
	private VRControllerEventListener listener = null;
	
	public VRController(int id){
		this.id = id;
	}
	
	public void setPose(Matrix4f pose){
		isValid = true;
		if(pose == null){
			isPoseValid = false;
			return;
		}
		isPoseValid = true;
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
	
	public Matrix4f getPose(){
		return pose;
	}
	
	public void setVRControllerEventListener(VRControllerEventListener listener){
		this.listener = listener;
	}
	
	public void throwEvent(int deviceIndex, int eventType, VREvent_Data_t data){
		if(listener != null) listener.onEvent(deviceIndex, eventType, data);
	}
	
	public int getId(){
		return id;
	}
}
