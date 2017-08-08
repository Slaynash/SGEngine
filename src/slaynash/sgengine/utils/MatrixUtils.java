package slaynash.sgengine.utils;

import org.jbox2d.common.Vec2;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import jopenvr.HmdMatrix34_t;
import slaynash.sgengine.Configuration;

public class MatrixUtils {
	
	public static Matrix4f inverse(Matrix4f src, Matrix4f dest){
        if(dest == null) dest = new Matrix4f();
		float a = src.m00 * src.m11 - src.m01 * src.m10;
        float b = src.m00 * src.m12 - src.m02 * src.m10;
        float c = src.m00 * src.m13 - src.m03 * src.m10;
        float d = src.m01 * src.m12 - src.m02 * src.m11;
        float e = src.m01 * src.m13 - src.m03 * src.m11;
        float f = src.m02 * src.m13 - src.m03 * src.m12;
        float g = src.m20 * src.m31 - src.m21 * src.m30;
        float h = src.m20 * src.m32 - src.m22 * src.m30;
        float i = src.m20 * src.m33 - src.m23 * src.m30;
        float j = src.m21 * src.m32 - src.m22 * src.m31;
        float k = src.m21 * src.m33 - src.m23 * src.m31;
        float l = src.m22 * src.m33 - src.m23 * src.m32;
        float det = a * l - b * k + c * j + d * i - e * h + f * g;
        det = 1.0f / det;
        dest.m00 = (+src.m11 * l - src.m12 * k + src.m13 * j) * det;
        dest.m01 = (-src.m01 * l + src.m02 * k - src.m03 * j) * det;
		dest.m02 = (+src.m31 * f - src.m32 * e + src.m33 * d) * det;
		dest.m03 = (-src.m21 * f + src.m22 * e - src.m23 * d) * det;
		dest.m10 = (-src.m10 * l + src.m12 * i - src.m13 * h) * det;
		dest.m11 = (+src.m00 * l - src.m02 * i + src.m03 * h) * det;
		dest.m12 = (-src.m30 * f + src.m32 * c - src.m33 * b) * det;
		dest.m13 = (+src.m20 * f - src.m22 * c + src.m23 * b) * det;
		dest.m20 = (+src.m10 * k - src.m11 * i + src.m13 * g) * det;
		dest.m21 = (-src.m00 * k + src.m01 * i - src.m03 * g) * det;
		dest.m22 = (+src.m30 * e - src.m31 * c + src.m33 * a) * det;
		dest.m23 = (-src.m20 * e + src.m21 * c - src.m23 * a) * det;
		dest.m30 = (-src.m10 * j + src.m11 * h - src.m12 * g) * det;
		dest.m31 = (+src.m00 * j - src.m01 * h + src.m02 * g) * det;
		dest.m32 = (-src.m30 * d + src.m31 * b - src.m32 * a) * det;
		dest.m33 = (+src.m20 * d - src.m21 * b + src.m22 * a) * det;
        return dest;
	}

	public static Matrix4f copy(HmdMatrix34_t src, Matrix4f dest) {
		if(dest == null) dest = new Matrix4f();
		dest.m00 = src.m[0];
		dest.m01 = src.m[4];
		dest.m02 = src.m[8];
		dest.m03 = 0f;
		dest.m10 = src.m[1];
		dest.m11 = src.m[5];
		dest.m12 = src.m[9];
		dest.m13 = 0f;
		dest.m20 = src.m[2];
		dest.m21 = src.m[6];
		dest.m22 = src.m[10];
		dest.m23 = 0f;
		dest.m30 = src.m[3];
		dest.m31 = src.m[7];
		dest.m32 = src.m[11];
		dest.m33 = 1f;
		return dest;
	}

	public static Matrix4f copy(Matrix4f src, Matrix4f dest) {
		if(dest == null) dest = new Matrix4f();
		dest.m00 = src.m00;
		dest.m01 = src.m01;
		dest.m02 = src.m02;
		dest.m03 = src.m03;
		dest.m10 = src.m10;
		dest.m11 = src.m11;
		dest.m12 = src.m12;
		dest.m13 = src.m13;
		dest.m20 = src.m20;
		dest.m21 = src.m21;
		dest.m22 = src.m22;
		dest.m23 = src.m23;
		dest.m30 = src.m30;
		dest.m31 = src.m31;
		dest.m32 = src.m32;
		dest.m33 = src.m33;
		return dest;
	}

	public static Vector4f mul(Matrix4f src, Vector4f vec) {
		Vector4f dest = new Vector4f();
		dest.x = src.m00 * vec.x + src.m10 * vec.y + src.m20 * vec.z + src.m30 * vec.w;
		dest.y = src.m01 * vec.x + src.m11 * vec.y + src.m21 * vec.z + src.m31 * vec.w;
		dest.z = src.m02 * vec.x + src.m12 * vec.y + src.m22 * vec.z + src.m32 * vec.w;
		dest.w = src.m03 * vec.x + src.m13 * vec.y + src.m23 * vec.z + src.m33 * vec.w;
		return dest;
	}
	
	
	
	public static Matrix4f createTransformationMatrix(Vector2f translation, Vector2f scale) {
		Matrix4f matrix = new Matrix4f();
		matrix.setIdentity();
		Matrix4f.translate(translation, matrix, matrix);
		Matrix4f.scale(new Vector3f(scale.x, scale.y, 1f), matrix, matrix);
		return matrix;
	}

	public static Matrix4f createTransformationMatrix(Vec2 position, int scale) {
		Matrix4f matrix = new Matrix4f();
		matrix.setIdentity();
		Matrix4f.translate(new Vector2f(position.x, position.y), matrix, matrix);
		Matrix4f.scale(new Vector3f(scale, scale, 1f), matrix, matrix);
		return matrix;
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
	
	public static Matrix4f createCharacterViewMatrix() {
		Matrix4f viewMatrix = new Matrix4f();
		viewMatrix.setIdentity();
		if(Configuration.getPlayerCharacter().isUsingPitchRoll()){
			Matrix4f.rotate(-Configuration.getPlayerCharacter().getPitch(), new Vector3f(1, 0, 0), viewMatrix, viewMatrix);
			Matrix4f.rotate(-Configuration.getPlayerCharacter().getYaw(), new Vector3f(0, 1, 0), viewMatrix, viewMatrix);
			Vector3f cameraPos = Configuration.getPlayerCharacter().getViewPosition();
			Vector3f negativeCameraPos = new Vector3f(-cameraPos.x,-cameraPos.y,-cameraPos.z);
			Matrix4f.translate(negativeCameraPos, viewMatrix, viewMatrix);
		}
		else{
			Matrix4f.load(Configuration.getPlayerCharacter().getViewMatrix(), viewMatrix);
		}
		return viewMatrix;
	}
	
	public static Matrix4f createViewMatrix(Vector3f position, float pitch, float yaw) {
		Matrix4f viewMatrix = new Matrix4f();
		viewMatrix.setIdentity();
		Matrix4f.rotate((float) Math.toRadians(pitch), new Vector3f(1, 0, 0), viewMatrix, viewMatrix);
		Matrix4f.rotate((float) Math.toRadians(yaw), new Vector3f(0, 1, 0), viewMatrix, viewMatrix);
		Vector3f cameraPos = position;
		Vector3f negativeCameraPos = new Vector3f(-cameraPos.x,-cameraPos.y,-cameraPos.z);
		Matrix4f.translate(negativeCameraPos, viewMatrix, viewMatrix);
		return viewMatrix;
	}
	
	public static Matrix4f createProjectionMatrix(float near, float far, float fov){
    	Matrix4f projectionMatrix = new Matrix4f();
		float aspectRatio = (float) Display.getWidth() / (float) Display.getHeight();
		float y_scale = (float) ((1f / Math.tan(Math.toRadians(fov / 2f))));
		float x_scale = y_scale / aspectRatio;
		float frustum_length = far - near;

		projectionMatrix.m00 = x_scale;
		projectionMatrix.m11 = y_scale;
		projectionMatrix.m22 = -((far + near) / frustum_length);
		projectionMatrix.m23 = -1;
		projectionMatrix.m32 = -((2 * near * far) / frustum_length);
		projectionMatrix.m33 = 0;
		
		return projectionMatrix;
    }

	public static Matrix4f create2dProjectionMatrix() {
		return new Matrix4f();
	}

	public static Matrix4f createCharacterViewMatrix2d() {
		Matrix4f viewMatrix = new Matrix4f();
		viewMatrix.setIdentity();
		Vector3f cameraPos = Configuration.getPlayerCharacter().getViewPosition();
		Vector3f negativeCameraPos = new Vector3f(-cameraPos.x,-cameraPos.y, 0);
		Matrix4f.translate(negativeCameraPos, viewMatrix, viewMatrix);
		return viewMatrix;
	}
	
}
