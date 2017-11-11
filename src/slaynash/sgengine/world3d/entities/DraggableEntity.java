package slaynash.sgengine.world3d.entities;

import slaynash.sgengine.entities.Entity;

public abstract class DraggableEntity extends Entity{
	
	private boolean dragged = false;

	public DraggableEntity(String id) {
		super(id);
	}
	
	public void onDragged() {
		dragged = true;
	}
	
	public void onReleased() {
		dragged = false;
	}
	
	public boolean isDragged() {
		return dragged;
	}
	
}
