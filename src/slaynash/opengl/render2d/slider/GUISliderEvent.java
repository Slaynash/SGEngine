package slaynash.opengl.render2d.slider;

public class GUISliderEvent {
	
	private float percent;
	
	public GUISliderEvent(float trackPercent) {
		this.percent = trackPercent;
	}
	
	public float getPercent(){
		return percent;
	}

}
