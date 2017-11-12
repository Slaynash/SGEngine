package slaynash.sgengine.utils.sceneManagerEvent;

import java.util.EventListener;

public interface SceneManagerListener extends EventListener{
	void sceneChanged(SceneManagerEvent e);
	void sceneStarted(SceneManagerEvent e);
	void sceneClosed(SceneManagerEvent e);
	void initialized(SceneManagerEvent e);
	void exited(SceneManagerEvent e);
}
