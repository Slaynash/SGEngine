package slaynash.sgengine.utils;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

public abstract class ShapeHelper {
	public static float[] calculateTangents(float[] vertices, float[] texcoords) {
		
		float[] tangents = new float[vertices.length];
		
		
		for ( int i=0; i < vertices.length/(3*3); i++){

	        // Shortcuts for vertices
	        Vector3f v0 = new Vector3f(vertices[i*9+0], vertices[i*9+1], vertices[i*9+2]);
	        Vector3f v1 = new Vector3f(vertices[i*9+3], vertices[i*9+4], vertices[i*9+5]);
	        Vector3f v2 = new Vector3f(vertices[i*9+6], vertices[i*9+7], vertices[i*9+8]);

	        // Shortcuts for UVs
	        Vector2f uv0 = new Vector2f(texcoords[i*6+0], texcoords[i*6+1]);
	        Vector2f uv1 = new Vector2f(texcoords[i*6+2], texcoords[i*6+3]);
	        Vector2f uv2 = new Vector2f(texcoords[i*6+4], texcoords[i*6+5]);

	        // Edges of the triangle : postion delta
	        Vector3f deltaPos1 = Vector3f.sub(v1, v0, null);
	        Vector3f deltaPos2 = Vector3f.sub(v2, v0, null);

	        // UV delta
	        Vector2f deltaUV1 = Vector2f.sub(uv1, uv0, null);
	        Vector2f deltaUV2 = Vector2f.sub(uv2, uv0, null);
	        
	        float r = 1.0f / (deltaUV1.x * deltaUV2.y - deltaUV1.y * deltaUV2.x);
	        deltaPos1.scale(deltaUV2.y);
	        deltaPos2.scale(deltaUV1.y);
	        Vector3f tangent = Vector3f.sub(deltaPos1, deltaPos2, null);
			tangent.scale(r);
			
			if(tangent.length() != 0) tangent.normalise();
	        
	        tangents[i*3*3+0] = tangent.x;
			tangents[i*3*3+1] = tangent.y;
			tangents[i*3*3+2] = tangent.z;
			tangents[i*3*3+3] = tangent.x;
			tangents[i*3*3+4] = tangent.y;
			tangents[i*3*3+5] = tangent.z;
			tangents[i*3*3+6] = tangent.x;
			tangents[i*3*3+7] = tangent.y;
			tangents[i*3*3+8] = tangent.z;
	        
		}
		
		return tangents;
	}
	
	public static float[] calculateTangents(float[] vertices, float[] texcoords, int[] indices) {
		
		float[] tangents = new float[indices.length*3];
		
		
		for ( int i=0; i < indices.length/3; i++){

	        // Shortcuts for vertices
	        Vector3f v0 = new Vector3f(vertices[indices[i*3+0]*3+0], vertices[indices[i*3+0]*3+1], vertices[indices[i*3+0]*3+2]);
	        Vector3f v1 = new Vector3f(vertices[indices[i*3+1]*3+0], vertices[indices[i*3+1]*3+1], vertices[indices[i*3+1]*3+2]);
	        Vector3f v2 = new Vector3f(vertices[indices[i*3+2]*3+0], vertices[indices[i*3+2]*3+1], vertices[indices[i*3+2]*3+2]);

	        // Shortcuts for UVs
	        Vector2f uv0 = new Vector2f(texcoords[indices[i*3+0]*2+0], texcoords[indices[i*3+0]*2+1]);
	        Vector2f uv1 = new Vector2f(texcoords[indices[i*3+1]*2+0], texcoords[indices[i*3+1]*2+1]);
	        Vector2f uv2 = new Vector2f(texcoords[indices[i*3+2]*2+0], texcoords[indices[i*3+2]*2+1]);

	        // Edges of the triangle : postion delta
	        Vector3f deltaPos1 = Vector3f.sub(v1, v0, null);
	        Vector3f deltaPos2 = Vector3f.sub(v2, v0, null);

	        // UV delta
	        Vector2f deltaUV1 = Vector2f.sub(uv1, uv0, null);
	        Vector2f deltaUV2 = Vector2f.sub(uv2, uv0, null);
	        
	        float r = 1.0f / (deltaUV1.x * deltaUV2.y - deltaUV1.y * deltaUV2.x);
	        deltaPos1.scale(deltaUV2.y);
	        deltaPos2.scale(deltaUV1.y);
	        Vector3f tangent = Vector3f.sub(deltaPos1, deltaPos2, null);
			tangent.scale(r);
			
			if(tangent.length() != 0) tangent.normalise();
	        
	        tangents[i*3*3+0] = tangent.x;
			tangents[i*3*3+1] = tangent.y;
			tangents[i*3*3+2] = tangent.z;
			tangents[i*3*3+3] = tangent.x;
			tangents[i*3*3+4] = tangent.y;
			tangents[i*3*3+5] = tangent.z;
			tangents[i*3*3+6] = tangent.x;
			tangents[i*3*3+7] = tangent.y;
			tangents[i*3*3+8] = tangent.z;
	        
		}
		
		return tangents;
	}

	public static float[] calculateNormals(float[] vertices) {
		float[] normals = new float[vertices.length];
		
		for(int i=0;i<vertices.length/(3*3);i++){
			Vector3f delatPos1 = Vector3f.sub(new Vector3f(vertices[i*3*3+3],vertices[i*3*3+4],vertices[i*3*3+5]), new Vector3f(vertices[i*3*3+0],vertices[i*3*3+1],vertices[i*3*3+2]), null);
			Vector3f delatPos2 = Vector3f.sub(new Vector3f(vertices[i*3*3+6],vertices[i*3*3+7],vertices[i*3*3+8]), new Vector3f(vertices[i*3*3+0],vertices[i*3*3+1],vertices[i*3*3+2]), null);
			
			Vector3f normal = Vector3f.cross(delatPos1, delatPos2, null).normalise(null);
			
			normals[i*3*3+0] = normal.x;
			normals[i*3*3+1] = normal.y;
			normals[i*3*3+2] = normal.z;
			normals[i*3*3+3] = normal.x;
			normals[i*3*3+4] = normal.y;
			normals[i*3*3+5] = normal.z;
			normals[i*3*3+6] = normal.x;
			normals[i*3*3+7] = normal.y;
			normals[i*3*3+8] = normal.z;
		}
		
		
		return normals;
	}
}
