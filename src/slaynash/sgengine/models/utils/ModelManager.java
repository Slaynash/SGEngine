package slaynash.sgengine.models.utils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.util.vector.Matrix4f;

import slaynash.sgengine.Configuration;
import slaynash.sgengine.daeloader.colladaLoader.ColladaLoader;
import slaynash.sgengine.daeloader.dataStructures.AnimatedModelData;
import slaynash.sgengine.daeloader.dataStructures.JointData;
import slaynash.sgengine.daeloader.dataStructures.SkeletonData;
import slaynash.sgengine.daeloader.model.Joint;
import slaynash.sgengine.models.Renderable3dAnimatedModel;
import slaynash.sgengine.models.Renderable3dModel;
import slaynash.sgengine.objloader.ObjLoader;
import slaynash.sgengine.objloader.ObjMeshData;
import slaynash.sgengine.textureUtils.TextureManager;

public class ModelManager {
	
	private static Map<String, Vao> models = new HashMap<String, Vao>();
	
	public static Renderable3dModel loadObj(String path, String diffusemap, String normalmap, String specularmap) {
		Vao vao;
		if((vao = models.get(path)) == null) {
			ObjMeshData meshdatas = ObjLoader.loadObj(new File(Configuration.getAbsoluteInstallPath()+"/"+path), diffusemap, normalmap, specularmap);
			if(meshdatas != null)
				vao = VaoManager.loadToVao3d(meshdatas.getVerticesArray(), meshdatas.getTexturesArray(), meshdatas.getNormalsArray(), meshdatas.getTangentsArray(), meshdatas.getIndicesArray());
			else
				vao = VaoManager.loadToVao3d(new float[] {}, new float[] {}, new float[] {}, new float[] {}, new int[] {});
			models.put(path, vao);
		}
		return new Renderable3dModel(
				vao,
				diffusemap != null ? TextureManager.getTextureDef(diffusemap, TextureManager.TEXTURE_DIFFUSE) : TextureManager.getDefaultTexture(),
				normalmap != null ? TextureManager.getTextureDef(normalmap, TextureManager.TEXTURE_NORMAL) : TextureManager.getDefaultNormalTexture(),
				specularmap != null ? TextureManager.getTextureDef(specularmap, TextureManager.TEXTURE_SPECULAR) : TextureManager.getDefaultSpecularTexture()
		);
	}
	
	
	public static Renderable3dAnimatedModel loadAnimatedDae(String modelPath, String diffusemap, String normalmap, String specularmap) {
		AnimatedModelData entityData = ColladaLoader.loadColladaModel(modelPath, Configuration.MAX_WEIGHTS);
		Vao vao;
		if((vao = models.get(modelPath)) == null) {
			vao = VaoManager.loadToVao3d(
					entityData.getMeshData().getVertices(),
					entityData.getMeshData().getTextureCoords(),
					entityData.getMeshData().getNormals(),
					entityData.getMeshData().getTangents(),
					entityData.getMeshData().getJointIds(),
					entityData.getMeshData().getVertexWeights(),
					entityData.getMeshData().getIndices()
			);
		}
		
		SkeletonData skeletonData = entityData.getJointsData();
		Joint headJoint = createJoints(skeletonData.headJoint);
		return new Renderable3dAnimatedModel(
				vao,
				headJoint, skeletonData.jointCount,
				diffusemap != null ? TextureManager.getTextureDef(diffusemap, TextureManager.TEXTURE_DIFFUSE) : TextureManager.getDefaultTexture(),
				normalmap != null ? TextureManager.getTextureDef(normalmap, TextureManager.TEXTURE_NORMAL) : TextureManager.getDefaultNormalTexture(),
				specularmap != null ? TextureManager.getTextureDef(specularmap, TextureManager.TEXTURE_SPECULAR) : TextureManager.getDefaultSpecularTexture()
		);
	}
	
	private static Joint createJoints(JointData data) {
		Joint joint = new Joint(data.index, data.nameId+"", new Matrix4f().load(data.bindLocalTransform));
		for (JointData child : data.children) {
			joint.addChild(createJoints(child));
		}
		return joint;
	}
	
}
