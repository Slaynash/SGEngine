package slaynash.sgengine.objloader;

public class ObjMeshData {

	private float[] verticesArray;
	private float[] texturesArray;
	private float[] normalsArray;
	private float[] tangentsArray;
	private int[] indicesArray;

	public ObjMeshData(float[] verticesArray, float[] texturesArray, float[] normalsArray, float[] tangentsArray,
			int[] indicesArray) {
		this.verticesArray = verticesArray;
		this.texturesArray = texturesArray;
		this.normalsArray = normalsArray;
		this.tangentsArray = tangentsArray;
		this.indicesArray = indicesArray;
	}

	public float[] getVerticesArray() {
		return verticesArray;
	}

	public float[] getTexturesArray() {
		return texturesArray;
	}

	public float[] getNormalsArray() {
		return normalsArray;
	}

	public float[] getTangentsArray() {
		return tangentsArray;
	}

	public int[] getIndicesArray() {
		return indicesArray;
	}
	
	

}
