package slaynash.opengl.utils.pageManagerEvent;

import slaynash.opengl.utils.RenderablePage;

public class PageManagerEvent {
	
	private RenderablePage page;
	
	public PageManagerEvent(RenderablePage page) {
		this.page = page;
	}
	
	public RenderablePage getPage(){
		return page;
	}

}
