package slaynash.sgengine.deferredRender;

public interface DeferredRenderableModel {

	public Class<? extends DeferredModelRenderer> getDeferredRenderer();

}
