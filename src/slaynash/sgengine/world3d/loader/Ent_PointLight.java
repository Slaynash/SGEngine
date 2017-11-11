package slaynash.sgengine.world3d.loader;

import java.util.List;

import org.lwjgl.util.vector.Vector3f;

import slaynash.sgengine.entities.Entity;
import slaynash.sgengine.utils.LightsManager;
import slaynash.sgengine.world3d.entities.InteractableObject;
import slaynash.sgengine.world3d.entities.Interaction;

public class Ent_PointLight extends Entity implements InteractableObject {

	private float[] color;
	private float[] attenuation;
	private Vector3f lightPos3d = new Vector3f();

	public Ent_PointLight(String id, float posX, float posY, float posZ, float colorR, float colorG,
			float colorB, float attenuationConstant, float attenuationLinear, float attenuationQuadric) {
		super(id);
		setPosition(posX, posY, posZ);
		color = new float[]{colorR, colorG, colorB};
		attenuation = new float[]{attenuationConstant, attenuationLinear, attenuationQuadric};
	}

	@Override
	public void update() { }

	@Override
	public void render() { }
	
	@Override
	public void renderVR(int eye) { }

	public float[] getColor() {
		return color;
	}

	public void setColor(float[] color) {
		this.color = color;
	}

	public float[] getAttenuation() {
		return attenuation;
	}

	public void setAttenuation(float[] attenuation) {
		this.attenuation = attenuation;
	}

	@Override
	public void destroy() {
		LightsManager.removePointlight(this);
	}

	public Vector3f getPosition() {
		return lightPos3d;
	}
	
	@Override
	public void setPosition(float posX, float posY, float posZ) {
		super.setPosition(posX, posY, posZ);
		lightPos3d.x = posX;
		lightPos3d.y = posY;
		lightPos3d.z = posZ;
	}

	@Override
	public void onAddedToWorldManager() {
		LightsManager.addPointlight(this);
	}

	@Override
	public void addInteractions(List<Interaction> interactions) {}

	@Override
	public void onInput(String command, String... args) {
		if(command.equals("setColor")) {
			color[0] = Float.parseFloat(args[0]);
			color[1] = Float.parseFloat(args[1]);
			color[2] = Float.parseFloat(args[2]);
		}
	}

}
