package slaynash.sgengine.entities;

public abstract class Entity {
	
	private String id;
	
	private float posX;
	private float posY;
	private float posZ;

	private float angX;
	private float angY;
	private float angZ;
	
	public Entity(String id) {
		this.id = id;
	}
	
	public abstract void update();
	public abstract void render();
	public abstract void renderVR(int eye);
	public abstract void destroy();
	public abstract void onAddedToWorldManager();

	public String getId() {
		return id;
	}

	public void setPosition(float posX, float posY, float posZ) {
		this.posX = posX;
		this.posY = posY;
		this.posZ = posZ;
	}

	public void setRotation(float rotX, float rotY, float rotZ) {
		this.angX = rotX;
		this.angY = rotY;
		this.angZ = rotZ;
	}

	public float getPosX() {
		return posX;
	}

	public float getPosY() {
		return posY;
	}

	public float getPosZ() {
		return posZ;
	}

	public float getAngX() {
		return angX;
	}

	public float getAngY() {
		return angY;
	}

	public float getAngZ() {
		return angZ;
	}
	
}
