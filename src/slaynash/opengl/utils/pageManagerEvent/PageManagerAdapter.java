package slaynash.opengl.utils.pageManagerEvent;

public abstract class PageManagerAdapter implements PageManagerListener{
	public void pageChanged(PageManagerEvent e) {}
	public void pageStarted(PageManagerEvent e) {}
	public void pageClosed(PageManagerEvent e) {}
}
