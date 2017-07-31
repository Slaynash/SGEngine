package slaynash.sgengine.world3d.loader;

import org.lwjgl.util.vector.Vector3f;

public class PointLight extends Entity {

	private float[] color;
	private float[] attenuation;

	public PointLight(float posX, float posY, float posZ, float colorR, float colorG,
			float colorB, float attenuationConstant, float attenuationLinear, float attenuationQuadric) {
		
		setPosition(new Vector3f(posX, posY, posZ));
		color = new float[]{colorR, colorG, colorB};
		attenuation = new float[]{attenuationConstant, attenuationLinear, attenuationQuadric};
	}

	@Override
	public void update() { }

	@Override
	public void render() { }
	
	@Override
	public void renderVR() { }

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

}
