package slaynash.sgengine.models.utils;

import java.util.ArrayList;
import java.util.List;

public class VaoManager {
	
	private static List<Vao> vaos = new ArrayList<Vao>();
	
	public static Vao loadToVao3d(float[] positions, float[] textureCoords, float[] normals, float[] tangents, int[] indices){
		Vao vao = new Vao();
		vao.bind();
		vao.createIndexBuffer(indices);
		vao.createAttribute(0, positions, 3);
		vao.createAttribute(1, textureCoords, 2);
		vao.createAttribute(2, normals, 3);
		vao.createAttribute(3, tangents, 3);
		vao.unbind();
		return vao;
	}
	
	public static Vao loadToVao2d(float[] positions, float[] textureCoords){
		int[] indices = new int[positions.length/2];
		for(int i=0;i<positions.length/2;i++) indices[i] = i;
		
		Vao vao = new Vao();
		vao.bind();
		vao.createIndexBuffer(indices);
		vao.createAttribute(0, positions, 2);
		vao.createAttribute(1, textureCoords, 2);
		vao.unbind();
		return vao;
		
	}
	
	public static Vao loadToVao(float[] position, int dimensions){
		int[] indices = new int[position.length/dimensions];
		for(int i=0;i<position.length/dimensions;i++) indices[i] = i;
		
		Vao vao = new Vao();
		vao.bind();
		vao.createIndexBuffer(indices);
		vao.createAttribute(0, position, dimensions);
		vao.unbind();
		return vao;
	}
	
	public static void cleanUp(){
		for(Vao vao:vaos) vao.dispose();
	}

	public static void addVAO(Vao vao) {
		vaos.add(vao);
	}

	public static Vao loadToVao3d(float[] vertices, float[] textureCoords, float[] normal, float[] tangents, int[] jointIds, float[] vertexWeights, int[] indices) {
		Vao vao = new Vao();
		vao.bind();
		vao.createIndexBuffer(indices);
		vao.createAttribute(0, vertices, 3);
		vao.createAttribute(1, textureCoords, 2);
		vao.createAttribute(2, normal, 3);
		vao.createAttribute(3, tangents, 3);
		vao.createIntAttribute(4, jointIds, 3);
		vao.createAttribute(5, vertexWeights, 3);
		vao.unbind();
		return vao;
	}
}
