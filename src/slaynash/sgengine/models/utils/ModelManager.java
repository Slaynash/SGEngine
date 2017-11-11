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
import slaynash.sgengine.daeloader.model.AnimatedModel;
import slaynash.sgengine.daeloader.model.Joint;
import slaynash.sgengine.models.Renderable3dModel;
import slaynash.sgengine.objloader.ObjLoader;
import slaynash.sgengine.objloader.ObjMeshData;
import slaynash.sgengine.textureUtils.TextureDef;
import slaynash.sgengine.textureUtils.TextureManager;

public class ModelManager {
	
	private static Map<String, Vao> models = new HashMap<String, Vao>();
	
	public static Renderable3dModel loadObj(String path, String diffusemap, String normalmap, String specularmap) {
		Vao vao;
		if((vao = models.get(path)) == null) {
			ObjMeshData meshdatas = ObjLoader.loadObj(new File(Configuration.getAbsoluteInstallPath()+"/"+path), diffusemap, normalmap, specularmap);
			vao = VaoManager.loadToVao(meshdatas.getVerticesArray(), meshdatas.getTexturesArray(), meshdatas.getNormalsArray(), meshdatas.getTangentsArray(), meshdatas.getIndicesArray());
			models.put(path, vao);
		}
		return new Renderable3dModel(
				models.get(path),
				diffusemap != null ? TextureManager.getTextureDef(diffusemap, TextureManager.COLOR) : TextureManager.getDefaultTexture(),
				normalmap != null ? TextureManager.getTextureDef(normalmap, TextureManager.NORMAL) : TextureManager.getDefaultNormalTexture(),
				specularmap != null ? TextureManager.getTextureDef(specularmap, TextureManager.SPECULAR) : TextureManager.getDefaultSpecularTexture()
		);
	}
	
	
	public static AnimatedModel loadAnimatedDae(String modelPath, String texturePath) {
		AnimatedModelData entityData = ColladaLoader.loadColladaModel(modelPath, Configuration.MAX_WEIGHTS);
		Vao model;
		if((model = models.get(modelPath)) == null) {
			model = VaoManager.loadToVao(
					entityData.getMeshData().getVertices(),
					entityData.getMeshData().getTextureCoords(),
					entityData.getMeshData().getNormals(),
					entityData.getMeshData().getJointIds(),
					entityData.getMeshData().getVertexWeights(),
					entityData.getMeshData().getIndices()
			);
		}
		
		TextureDef textureDef = TextureManager.getTextureDef(texturePath, TextureManager.COLOR);
		SkeletonData skeletonData = entityData.getJointsData();
		Joint headJoint = createJoints(skeletonData.headJoint);
		return new AnimatedModel(model, textureDef, headJoint, skeletonData.jointCount);
	}
	
	private static Joint createJoints(JointData data) {
		Joint joint = new Joint(data.index, data.nameId+"", new Matrix4f().load(data.bindLocalTransform));
		for (JointData child : data.children) {
			joint.addChild(createJoints(child));
		}
		return joint;
	}
	
}
