package slaynash.sgengine.utils;

import org.lwjgl.util.vector.Vector3f;

import com.bulletphysics.collision.dispatch.CollisionWorld.ClosestRayResultCallback;

import slaynash.sgengine.Configuration;
import slaynash.sgengine.world3d.CollisionManager3d;

public class RaycastUtil {

	private static final float EPSILON = 0.000001f;
	
	public static Vector3f intersect3dLoadedWorld(Vector3f start, Vector3f dir, float range){
		if(Configuration.isCollisionManager3dEnabled()){
			
			javax.vecmath.Vector3f end = new javax.vecmath.Vector3f(start.x+dir.x*range, start.y+dir.y*range, start.z+dir.z*range);
			ClosestRayResultCallback resultCallback = new ClosestRayResultCallback(new javax.vecmath.Vector3f(start.x, start.y, start.z), end);
					
			CollisionManager3d.getDynamicWorld().rayTest(new javax.vecmath.Vector3f(start.x, start.y, start.z), end, resultCallback);
			if(resultCallback.hasHit()) {
			    return new Vector3f(resultCallback.hitPointWorld.x, resultCallback.hitPointWorld.y, resultCallback.hitPointWorld.z);
			}
		}
		return null;
	}
	
	public static Vector3f intersect3d(Vector3f start, Vector3f dir, float range, Vector3f p0, Vector3f p1, Vector3f p2) {
		Vector3f e1 = new Vector3f(), e2 = new Vector3f();
		Vector3f P = new Vector3f(), Q = new Vector3f(), T = new Vector3f();
		float det, inv_det, u, v;
		float t;

		Vector3f.sub(p1, p0, e1);
		Vector3f.sub(p2, p0, e2);
		Vector3f.cross(dir, e2, P);

		det = Vector3f.dot(e1, P);

		if (det > -EPSILON && det < EPSILON)
			return null;
		inv_det = 1.0f / det;

		Vector3f.sub(start, p0, T);
		u = Vector3f.dot(T, P) * inv_det;

		if (u < 0 || u > 1)
			return null;

		Vector3f.cross(T, e1, Q);
		v = Vector3f.dot(dir, Q) * inv_det;

		if (v < 0 || u + v > 1)
			return null;

		t = Vector3f.dot(e2, Q) * inv_det;

		if (t > EPSILON)
			return new Vector3f(start.x+dir.x*t, start.y+dir.y*t, start.z+dir.z*t);

		return null;
	}
	
	public static float getIntersectRange(Vector3f v1, Vector3f v2, Vector3f v3, Vector3f O, Vector3f D) {
		Vector3f e1 = new Vector3f(), e2 = new Vector3f();
		Vector3f P = new Vector3f(), Q = new Vector3f(), T = new Vector3f();
		float det, inv_det, u, v;
		float t;

		Vector3f.sub(v2, v1, e1);
		Vector3f.sub(v3, v1, e2);
		Vector3f.cross(D, e2, P);

		det = Vector3f.dot(e1, P);

		if (det > -EPSILON && det < EPSILON)
			return 0;
		inv_det = 1.0f / det;

		Vector3f.sub(O, v1, T);
		u = Vector3f.dot(T, P) * inv_det;

		if (u < 0 || u > 1)
			return 0;

		Vector3f.cross(T, e1, Q);
		v = Vector3f.dot(D, Q) * inv_det;

		if (v < 0 || u + v > 1)
			return 0;

		t = Vector3f.dot(e2, Q) * inv_det;

		if (t > EPSILON)
			return t;

		return 0;
	}
}
