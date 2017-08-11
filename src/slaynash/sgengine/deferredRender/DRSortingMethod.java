package slaynash.sgengine.deferredRender;

public enum DRSortingMethod {
	/**
	 * render each textures and model instance with the same model
	 */
	TEXTURES_FOR_OBJECTS,
	/**
	 * render each models with the same texture
	 */
	OBJECTS_FOR_TEXTURES
}
