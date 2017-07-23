package slaynash.opengl.utils;

public class VAO {
	private int vaoID;
	private int vertexCount;
	
	public VAO(int vaoID, int vertexCount){
		this.vaoID = vaoID;
		this.vertexCount = vertexCount;
	}
	
	public int getVaoID(){
		return vaoID;
	}
	
	public int getVertexCount(){
		return vertexCount;
	}
}
