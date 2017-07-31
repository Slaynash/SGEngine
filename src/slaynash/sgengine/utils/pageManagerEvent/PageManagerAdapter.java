package slaynash.sgengine.utils.pageManagerEvent;

public abstract class PageManagerAdapter implements PageManagerListener{
	@Override
	public void pageChanged(PageManagerEvent e) {}
	@Override
	public void pageStarted(PageManagerEvent e) {}
	@Override
	public void pageClosed(PageManagerEvent e) {}
}
