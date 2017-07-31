package slaynash.sgengine.utils;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

public class VOLoader {
	
	private static List<Integer> vaos = new ArrayList<Integer>();
	private static List<Integer> vbos = new ArrayList<Integer>();
	
	public static VAO loadToVAO(float[] positions, float[] textureCoords, float[] normals, float[] tangents, int[] indices){
		int vaoID = createVAO();
		int vboIDs[] = new int[5];
		vboIDs[0] = bindIndicesBuffer(indices);
		vboIDs[1] = storeDataInAttributeList(0,3,positions);
		vboIDs[2] = storeDataInAttributeList(1,2,textureCoords);
		vboIDs[3] = storeDataInAttributeList(2,3,normals);
		vboIDs[4] = storeDataInAttributeList(3,3,tangents);
		unbindVAO();
		return new VAO(vaoID, vboIDs,indices.length);
	}
	
	public static VAO loadToVAO(float[] positions, float[] textureCoords, float[] normals, float[] tangents){
		int vaoID = createVAO();
		int vboIDs[] = new int[5];
		vboIDs[0] = storeDataInAttributeList(0,3,positions);
		vboIDs[1] = storeDataInAttributeList(1,2,textureCoords);
		vboIDs[2] = storeDataInAttributeList(2,3,normals);
		vboIDs[3] = storeDataInAttributeList(3,3,tangents);
		unbindVAO();
		return new VAO(vaoID, vboIDs, positions.length);
	}
	
	public static VAO loadToVAO(float[] positions, float[] textureCoords){
		int vaoID = createVAO();
		int vboIDs[] = new int[5];
		vboIDs[0] = storeDataInAttributeList(0,2,positions);
		vboIDs[1] = storeDataInAttributeList(1,2,textureCoords);
		unbindVAO();
		return new VAO(vaoID, vboIDs, positions.length);
	}
	

	
	private static int createVAO(){
		int vaoID = GL30.glGenVertexArrays();
		vaos.add(vaoID);
		GL30.glBindVertexArray(vaoID);
		return vaoID;
	}
	
	private static int storeDataInAttributeList(int attributeNumber, int coordinateSize,float[] data){
		int vboID = GL15.glGenBuffers();
		vbos.add(vboID);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
		FloatBuffer buffer = storeDataInFloatBuffer(data);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
		GL20.glVertexAttribPointer(attributeNumber,coordinateSize,GL11.GL_FLOAT,false,0,0);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		return vboID;
	}
	
	private static void unbindVAO(){
		GL30.glBindVertexArray(0);
	}
	
	private static int bindIndicesBuffer(int[] indices){
		int vboID = GL15.glGenBuffers();
		vbos.add(vboID);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboID);
		IntBuffer buffer = storeDataInIntBuffer(indices);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
		return vboID;
	}
	
	private static IntBuffer storeDataInIntBuffer(int[] data){
		IntBuffer buffer = BufferUtils.createIntBuffer(data.length);
		buffer.put(data);
		buffer.flip();
		return buffer;
	}
	
	private static FloatBuffer storeDataInFloatBuffer(float[] data){
		FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
		buffer.put(data);
		buffer.flip();
		return buffer;
	}
	
	public static void cleanUp(){
		for(int vao:vaos) GL30.glDeleteVertexArrays(vao);
		for(int vbo:vbos) GL15.glDeleteBuffers(vbo);
	}
	
	public static void removeVao(int vao){
		GL30.glDeleteVertexArrays(vao);
	}
	
	public static void removeVbo(int vbo){
		GL15.glDeleteBuffers(vbo);
	}
	
}
