package slaynash.sgengine.world3d;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import slaynash.sgengine.Configuration;
import slaynash.sgengine.models.Renderable3dModel;
import slaynash.sgengine.shaders.ShaderManager;
import slaynash.sgengine.utils.ShapeHelper;
import slaynash.sgengine.world3d.loader.Entity;
import slaynash.sgengine.world3d.loader.TriangleFace;

public class Model3dWorld extends Entity{
	
	private TriangleFace[] faces;
	
	private Renderable3dModel[] models;

	public Model3dWorld(TriangleFace[] faces) {
		super();
		this.faces = faces;
		setPosition(new Vector3f(0, 0, 0));
		
		Map<Integer, List<TriangleFace>> faceGroups = new HashMap<Integer, List<TriangleFace>>();
		
		for(TriangleFace face:faces){
			int texture = face.getTextureColor().getTextureID();
			List<TriangleFace> batch = faceGroups.get(texture);
			if(batch != null){
				batch.add(face);
			}
			else{
				List<TriangleFace> newBatch = new ArrayList<TriangleFace>();
				newBatch.add(face);
				faceGroups.put(texture, newBatch);
			}
		}
		
		models = new Renderable3dModel[faceGroups.size()];
		
		int i=0;
		for(List<TriangleFace> tf:faceGroups.values()){
			float[] vertices = new float[tf.size()*3*3];
			float[] texCoords = new float[tf.size()*2*3];
			float[] normals = new float[tf.size()*3*3];
			for(int j=0;j<tf.size();j++){
				TriangleFace f = tf.get(j);
				vertices[j*3*3+0] = f.getVertices()[0];
				vertices[j*3*3+1] = f.getVertices()[1];
				vertices[j*3*3+2] = f.getVertices()[2];
				vertices[j*3*3+3] = f.getVertices()[3];
				vertices[j*3*3+4] = f.getVertices()[4];
				vertices[j*3*3+5] = f.getVertices()[5];
				vertices[j*3*3+6] = f.getVertices()[6];
				vertices[j*3*3+7] = f.getVertices()[7];
				vertices[j*3*3+8] = f.getVertices()[8];
				
				normals[j*3*3+0] = f.getNormals()[0];
				normals[j*3*3+1] = f.getNormals()[1];
				normals[j*3*3+2] = f.getNormals()[2];
				normals[j*3*3+3] = f.getNormals()[3];
				normals[j*3*3+4] = f.getNormals()[4];
				normals[j*3*3+5] = f.getNormals()[5];
				normals[j*3*3+6] = f.getNormals()[6];
				normals[j*3*3+7] = f.getNormals()[7];
				normals[j*3*3+8] = f.getNormals()[8];
				
				texCoords[j*2*3+0] = f.getUVs()[0];
				texCoords[j*2*3+1] = f.getUVs()[1];
				texCoords[j*2*3+2] = f.getUVs()[2];
				texCoords[j*2*3+3] = f.getUVs()[3];
				texCoords[j*2*3+4] = f.getUVs()[4];
				texCoords[j*2*3+5] = f.getUVs()[5];
			}
			models[i] = new Renderable3dModel(vertices, texCoords, ShapeHelper.calculateNormals(vertices), ShapeHelper.calculateTangents(vertices, texCoords), tf.get(0).getTextureColor(), tf.get(0).getTextureNormal(), tf.get(0).getTextureSpecular());
			i++;
		}
		
		if(Configuration.isCollisionsLoadedWith3dWorldLoad()) CollisionManager3d.addModel3dWorld(this);
	}

	@Override
	public void update() {
		
	}

	@Override
	public void render() {
		
		ShaderManager.shader_loadTransformationMatrix(
				createTransformationMatrix(getPosition(), getAngle().x, getAngle().y, getAngle().z, 1f)
		);
		
		for(Renderable3dModel model:models) model.render();
	}
	


	@Override
	public void renderVR() {
		ShaderManager.shader_loadTransformationMatrix(
				createTransformationMatrix(getPosition(), getAngle().x, getAngle().y, getAngle().z, 1f)
		);
		
		for(Renderable3dModel model:models) model.render();
	}

	public TriangleFace[] getFaces() {
		return faces;
	}
	
	public static Matrix4f createTransformationMatrix(Vector3f translation, float rx, float ry, float rz, float scale) {
		Matrix4f matrix = new Matrix4f();
		matrix.setIdentity();
		Matrix4f.translate(translation, matrix, matrix);
		Matrix4f.rotate((float) Math.toRadians(rx), new Vector3f(1,0,0), matrix, matrix);
		Matrix4f.rotate((float) Math.toRadians(ry), new Vector3f(0,1,0), matrix, matrix);
		Matrix4f.rotate((float) Math.toRadians(rz), new Vector3f(0,0,1), matrix, matrix);
		Matrix4f.scale(new Vector3f(scale,scale,scale), matrix, matrix);
		return matrix;
	}

}
