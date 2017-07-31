package slaynash.sgengine.utils;

public class VAO {
	private int vaoID;
	private int[] vboIDs;
	private int vertexCount;
	private boolean destroyed = false;
	
	public VAO(int vaoID, int[] vboIDs, int vertexCount){
		this.vaoID = vaoID;
		this.vboIDs = vboIDs;
		this.vertexCount = vertexCount;
	}
	
	public int getVaoID(){
		return vaoID;
	}
	
	public int[] getVboIDs(){
		return vboIDs;
	}
	
	public int getVertexCount(){
		return vertexCount;
	}

	public void dispose() {
		if(destroyed) return;
		destroyed = true;
		VOLoader.removeVao(vaoID);
		for(int vbo:vboIDs) VOLoader.removeVbo(vbo);
	}
}
