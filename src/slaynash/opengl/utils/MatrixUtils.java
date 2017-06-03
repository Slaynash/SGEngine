package slaynash.opengl.utils;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector4f;

import jopenvr.HmdMatrix34_t;

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
		dest.m32 = src.m33;
		dest.m33 = 1f;
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
	
}
