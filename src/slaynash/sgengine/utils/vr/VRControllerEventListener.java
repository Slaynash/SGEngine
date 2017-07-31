package slaynash.sgengine.utils.vr;

import jopenvr.VREvent_Data_t;

public interface VRControllerEventListener {
	public void onEvent(int deviceIndex, int eventType, VREvent_Data_t data);
}
