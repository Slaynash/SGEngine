package slaynash.sgengine.utils.sceneManagerEvent;

import slaynash.sgengine.utils.Scene;

public class SceneManagerEvent {
	
	private Scene page;
	
	public SceneManagerEvent(Scene page) {
		this.page = page;
	}
	
	public Scene getPage(){
		return page;
	}

}
