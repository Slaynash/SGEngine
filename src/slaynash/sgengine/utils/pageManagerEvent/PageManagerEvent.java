package slaynash.sgengine.utils.pageManagerEvent;

import slaynash.sgengine.utils.RenderablePage;

public class PageManagerEvent {
	
	private RenderablePage page;
	
	public PageManagerEvent(RenderablePage page) {
		this.page = page;
	}
	
	public RenderablePage getPage(){
		return page;
	}

}
