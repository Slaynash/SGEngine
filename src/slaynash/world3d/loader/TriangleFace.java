package slaynash.world3d.loader;

import slaynash.opengl.textureUtils.TextureManager;

public class TriangleFace {

	private float[] vs;
	private float[] vns;
	private float[] uvs;
	private int texC;
	private int texN;
	private int texS;
	private float sf;

	public TriangleFace(float[] vs, float[] vns, float[] uvs, String texC, String texN, String texS, float sf) {
		this.vs = vs;
		this.vns = vns;
		this.uvs = uvs;
		if(!texC.equals("")) this.texC = TextureManager.getTextureID(texC);
		else this.texC = TextureManager.getDefaultTextureID();
		if(!texN.equals("")) this.texN = TextureManager.getTextureID(texN);
		if(!texS.equals("")) this.texS = TextureManager.getTextureID(texS);
		this.sf = sf;
	}

	public float[] getVertices() {
		return vs;
	}

	public float[] getNormals() {
		return vns;
	}

	public float[] getUVs() {
		return uvs;
	}

	public int getTextureColorID() {
		return texC;
	}

	public int getTextureNormalID() {
		return texN;
	}

	public int getTextureSpecularID() {
		return texS;
	}

	public float getSpecularFactor() {
		return sf;
	}

}
