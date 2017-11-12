package slaynash.sgengine.utils.sceneManagerEvent;

public abstract class SceneManagerAdapter implements SceneManagerListener{
	@Override
	public void sceneChanged(SceneManagerEvent e) {}
	@Override
	public void sceneStarted(SceneManagerEvent e) {}
	@Override
	public void sceneClosed(SceneManagerEvent e) {}
}
