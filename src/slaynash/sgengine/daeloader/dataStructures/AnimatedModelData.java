package slaynash.sgengine.daeloader.dataStructures;

/**
 * Contains the extracted data for an animated model, which includes the mesh data, and skeleton (joints heirarchy) data.
 * @author Karl
 *
 */
public class AnimatedModelData {

	private final SkeletonData joints;
	private final AnimatedModelMeshData mesh;
	
	public AnimatedModelData(AnimatedModelMeshData mesh, SkeletonData joints){
		this.joints = joints;
		this.mesh = mesh;
	}
	
	public SkeletonData getJointsData(){
		return joints;
	}
	
	public AnimatedModelMeshData getMeshData(){
		return mesh;
	}
	
}
