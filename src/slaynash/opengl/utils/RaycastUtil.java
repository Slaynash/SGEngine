package slaynash.opengl.utils;

import javax.vecmath.Vector3f;

import com.bulletphysics.collision.dispatch.CollisionWorld.ClosestRayResultCallback;

import slaynash.opengl.Configuration;
import slaynash.world3d.CollisionManager3d;

public class RaycastUtil {
	
	public static Vector3f intersect3dLoadedWorld(Vector3f start, Vector3f dir, float range){
		if(Configuration.isCollisionLoadedWith3dWorldLoad()){
			
			Vector3f end = new Vector3f(start.x+dir.x*range, start.y+dir.y*range, start.z+dir.z*range);
			ClosestRayResultCallback resultCallback = new ClosestRayResultCallback(start, end);
					
			CollisionManager3d.getDynamicWorld().rayTest(start, end, resultCallback);
			if(resultCallback.hasHit()) {
			    return resultCallback.hitPointWorld;
			}
		}
		return null;
	}
}
