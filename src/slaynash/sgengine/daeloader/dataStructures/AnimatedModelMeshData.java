package slaynash.sgengine.daeloader.dataStructures;

/**
 * This object contains all the mesh data for an animated model that is to be loaded into the VAO.
 * 
 * @author Karl
 *
 */
public class AnimatedModelMeshData {

	private static final int DIMENSIONS = 3;

	private float[] vertices;
	private float[] textureCoords;
	private float[] normals;
	private float[] tangents;
	private int[] indices;
	private int[] jointIds;
	private float[] vertexWeights;

	public AnimatedModelMeshData(float[] vertices, float[] textureCoords, float[] normals, float[] tangents, int[] indices,
			int[] jointIds, float[] vertexWeights) {
		this.vertices = vertices;
		this.textureCoords = textureCoords;
		this.normals = normals;
		this.tangents = tangents;
		this.indices = indices;
		this.jointIds = jointIds;
		this.vertexWeights = vertexWeights;
	}

	public int[] getJointIds() {
		return jointIds;
	}
	
	public float[] getVertexWeights(){
		return vertexWeights;
	}

	public float[] getVertices() {
		return vertices;
	}

	public float[] getTextureCoords() {
		return textureCoords;
	}

	public float[] getNormals() {
		return normals;
	}

	public float[] getTangents() {
		return tangents;
	}

	public int[] getIndices() {
		return indices;
	}

	public int getVertexCount() {
		return vertices.length / DIMENSIONS;
	}

}
