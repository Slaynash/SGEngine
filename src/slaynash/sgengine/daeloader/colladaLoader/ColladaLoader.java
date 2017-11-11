package slaynash.sgengine.daeloader.colladaLoader;

import java.io.File;

import slaynash.sgengine.Configuration;
import slaynash.sgengine.daeloader.dataStructures.AnimatedModelData;
import slaynash.sgengine.daeloader.dataStructures.AnimationData;
import slaynash.sgengine.daeloader.dataStructures.AnimatedModelMeshData;
import slaynash.sgengine.daeloader.dataStructures.SkeletonData;
import slaynash.sgengine.daeloader.dataStructures.SkinningData;
import slaynash.sgengine.daeloader.xmlParser.XmlNode;
import slaynash.sgengine.daeloader.xmlParser.XmlParser;

public class ColladaLoader {

	public static AnimatedModelData loadColladaModel(String colladaPath, int maxWeights) {
		XmlNode node = XmlParser.loadXmlFile(new File(Configuration.getAbsoluteInstallPath()+"/"+colladaPath));

		SkinLoader skinLoader = new SkinLoader(node.getChild("library_controllers"), maxWeights);
		SkinningData skinningData = skinLoader.extractSkinData();

		SkeletonLoader jointsLoader = new SkeletonLoader(node.getChild("library_visual_scenes"), skinningData.jointOrder);
		SkeletonData jointsData = jointsLoader.extractBoneData();

		GeometryLoader g = new GeometryLoader(node.getChild("library_geometries"), skinningData.verticesSkinData);
		AnimatedModelMeshData meshData = g.extractModelData();

		return new AnimatedModelData(meshData, jointsData);
	}

	public static AnimationData loadColladaAnimation(File colladaFile) {
		XmlNode node = XmlParser.loadXmlFile(colladaFile);
		XmlNode animNode = node.getChild("library_animations");
		XmlNode jointsNode = node.getChild("library_visual_scenes");
		AnimationLoader loader = new AnimationLoader(animNode, jointsNode);
		AnimationData animData = loader.extractAnimation();
		return animData;
	}

}
