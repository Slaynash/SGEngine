package slaynash.sgengine.world3d.loader;

import slaynash.sgengine.textureUtils.TextureDef;
import slaynash.sgengine.textureUtils.TextureManager;
import slaynash.sgengine.utils.ShapeHelper;

public class TriangleFace {

	private float[] vs;
	private float[] vns;
	private float[] uvs;
	private TextureDef texC;
	private TextureDef texN;
	private TextureDef texS;
	private float sf;

	public TriangleFace(float[] vs, float[] uvs, String texC, String texN, String texS, float sf) {
		this.vs = vs;
		this.vns = ShapeHelper.calculateNormals(vs);
		this.uvs = uvs;
		if(!texC.equals("")) this.texC = TextureManager.getTextureDef(texC, TextureManager.COLOR);
		else this.texC = TextureManager.getDefaultTexture();
		if(!texN.equals("")) this.texN = TextureManager.getTextureDef(texN, TextureManager.NORMAL);
		if(!texS.equals("")) this.texS = TextureManager.getTextureDef(texS, TextureManager.SPECULAR);
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

	public TextureDef getTextureColor() {
		return texC;
	}

	public TextureDef getTextureNormal() {
		return texN;
	}

	public TextureDef getTextureSpecular() {
		return texS;
	}

	public float getSpecularFactor() {
		return sf;
	}

}
