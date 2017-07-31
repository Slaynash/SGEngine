package slaynash.sgengine.utils.pageManagerEvent;

import java.util.EventListener;

public interface PageManagerListener extends EventListener{
	void pageChanged(PageManagerEvent e);
	void pageStarted(PageManagerEvent e);
	void pageClosed(PageManagerEvent e);
	void initialized(PageManagerEvent e);
	void exited(PageManagerEvent e);
}
