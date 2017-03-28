package slaynash.opengl.utils.pageManagerEvent;

import slaynash.opengl.utils.GamePage;

public class PageManagerEvent {
	
	private GamePage page;
	
	public PageManagerEvent(GamePage page) {
		this.page = page;
	}
	
	public GamePage getPage(){
		return page;
	}

}
