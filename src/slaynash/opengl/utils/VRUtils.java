package slaynash.opengl.utils;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.util.concurrent.TimeUnit;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;

import de.fruitfly.ovr.structs.Vector2i;
import jopenvr.HmdMatrix34_t;
import jopenvr.HmdMatrix44_t;
import jopenvr.JOpenVRLibrary;
import jopenvr.Texture_t;
import jopenvr.TrackedDevicePose_t;
import jopenvr.VRTextureBounds_t;
import jopenvr.VR_IVRCompositor_FnTable;
import jopenvr.VR_IVRSystem_FnTable;

public class VRUtils {
	
	public static final int EYE_LEFT = 0;
	public static final int EYE_RIGHT = 1;
	public static final int EYE_CENTER = 2;
	
	
	static String initStatus;
	private static boolean initialized;
	static boolean initSuccess = false;
	
	
	private static VR_IVRSystem_FnTable vrsystem;
	private static VR_IVRCompositor_FnTable vrCompositor;
	
	
	private static IntBuffer hmdErrorStore;
	private static TrackedDevicePose_t.ByReference hmdTrackedDevicePoseReference;
	private static TrackedDevicePose_t[] hmdTrackedDevicePoses;
	//private static TrackedDevicePose_t.ByReference hmdGamePoseReference;
	//private static TrackedDevicePose_t[] hmdGamePoses;
	
	private static Matrix4f[] poseMatrices;
	
	static Matrix4f leftEyeProjectionMatrix = new Matrix4f();
	static Matrix4f rightEyeProjectionMatrix = new Matrix4f();
	static Matrix4f leftEyePose = new Matrix4f();
	static Matrix4f rightEyePose = new Matrix4f();
	

	final static VRTextureBounds_t texBounds = new VRTextureBounds_t();
	final static Texture_t texType0 = new Texture_t();
	final static Texture_t texType1 = new Texture_t();
	
	private static IntBuffer hmdDisplayFrequency;

	private static FloatBuffer tlastVsync;
	private static LongBuffer _tframeCount;
	
	
	
	private static int[] fbos = new int[2];
	private static int leftEyeTextureId, rightEyeTextureId;
	private static Vector2i rendersize = new Vector2i();
	
	
	private static Matrix4f hmdPose = new Matrix4f();
	
	private static org.lwjgl.util.vector.Vector3f leftEyeTransform;
	private static org.lwjgl.util.vector.Vector3f rightEyeTransform;
	
	private static final float ZNEAR = 0.1f, ZFAR = 30f;
	
	
	

	private static final long SLEEP_PRECISION = TimeUnit.MILLISECONDS.toNanos(4);
	private static final long SPIN_YIELD_PRECISION = TimeUnit.MILLISECONDS.toNanos(2);
	private static long latencyWaitTime;
	private static boolean enableDebugLatency;
	private static int frames;
	private static float vsyncToPhotons;
    private static double timePerFrame, frameCountRun;
    private static long frameCount;
	
	
	
	static boolean initVR() {
		System.out.println("[VRUtils] initializing OpenVR...");
		if(JOpenVRLibrary.VR_IsHmdPresent() == 0){
			initStatus =  "VR Headset not detected.";
			return false;
		}

		try {
			initializeJOpenVR();
			initOpenVRCompositor(true);
		} catch (Exception e) {
			e.printStackTrace();
			initSuccess = false;
			initStatus = e.getLocalizedMessage();
			return false;
		}
		
		initTextureSubmitStructs();
		
		System.out.println( "OpenVR initialized & VR connected." );
		
		/*
		deviceVelocity = new Vector3f[JOpenVRLibrary.k_unMaxTrackedDeviceCount];

		for(int i=0;i<poseMatrices.length;i++)
		{
			poseMatrices[i] = new Matrix4f();
			deviceVelocity[i] = new Vector3f(0,0,0);
		}

		HmdMatrix34_t leftEyeTransformMatrix = vrsystem.GetEyeToHeadTransform.apply(JOpenVRLibrary.EVREye.EVREye_Eye_Left);
		printMatrix(convertToFruitflyMatrix(leftEyeTransformMatrix), "EYEPOS_LEFT");
		OpenVRUtil.convertSteamVRMatrix3ToMatrix4f(leftEyeTransformMatrix, hmdPoseLeftEye);
		hmdPoseLeftEye = hmdPoseLeftEye.inverted();
		//System.out.println("left eye matrix:");
		//for(float f:leftEyeTransformMatrix.m)
		//	System.out.println(f);
		/*
		 * 1 0 0 -0.030599998 
		 * 0 1 0 0.0
		 * 0 0 1 0.015
		 * /
		leftEyeTransform = new org.lwjgl.util.vector.Vector3f(leftEyeTransformMatrix.m[3], leftEyeTransformMatrix.m[7], leftEyeTransformMatrix.m[11]);
		
		HmdMatrix34_t rightEyeTransformMatrix = vrsystem.GetEyeToHeadTransform.apply(JOpenVRLibrary.EVREye.EVREye_Eye_Right);
		OpenVRUtil.convertSteamVRMatrix3ToMatrix4f(rightEyeTransformMatrix, hmdPoseRightEye);
		hmdPoseRightEye = hmdPoseRightEye.inverted();
		
		rightEyeTransform = new org.lwjgl.util.vector.Vector3f(rightEyeTransformMatrix.m[3], rightEyeTransformMatrix.m[7], rightEyeTransformMatrix.m[11]);
		
		IntBuffer rrtsx = IntBuffer.allocate(1);
		IntBuffer rrtsy = IntBuffer.allocate(2);
		
		vrsystem.GetRecommendedRenderTargetSize.apply(rrtsx, rrtsy);
		System.out.println("Recommended Render Target Size :"+rrtsx.get()+"x"+rrtsy.get());
		createRenderTexture(rrtsx.get(0), rrtsy.get(0));
		*/
		rendersize = getRenderSize();
	    initBuffers(rendersize.x, rendersize.y, rendersize.x, rendersize.y);
		System.out.println( "Render buffers/textures created" );
		//hmdProjectionLeftEye = initProjectionMatrix(EYE_LEFT);
		//hmdProjectionRightEye = initProjectionMatrix(EYE_RIGHT);
		//initProjectionsMatricesInfos();
		setupCameras();
		System.out.println( "Matrices created" );
		initialized = true;
		return true;
	}
	
	private static void setupCameras()
    {
    	leftEyeProjectionMatrix = getHMDMatrixProjectionEye(JOpenVRLibrary.EVREye.EVREye_Eye_Left);
    	rightEyeProjectionMatrix = getHMDMatrixProjectionEye(JOpenVRLibrary.EVREye.EVREye_Eye_Right);
    	leftEyePose = getHMDMatrixPoseEye(JOpenVRLibrary.EVREye.EVREye_Eye_Left);
    	rightEyePose = getHMDMatrixPoseEye(JOpenVRLibrary.EVREye.EVREye_Eye_Right);
    }
	
	private static Matrix4f getHMDMatrixProjectionEye(int nEye){
    	HmdMatrix44_t mat = vrsystem.GetProjectionMatrix.apply(nEye, ZNEAR, ZFAR, JOpenVRLibrary.EGraphicsAPIConvention.EGraphicsAPIConvention_API_OpenGL);
    	return convertSteamVRMatrix4ToMatrix4f(mat);
    }
    
    private static Matrix4f getHMDMatrixPoseEye(int nEye){
    	if (vrsystem == null) return new Matrix4f();
    	HmdMatrix34_t matEyeRight = vrsystem.GetEyeToHeadTransform.apply(nEye);
    	Matrix4f m = convertSteamVRMatrix3ToMatrix4f(matEyeRight);
    	m.invert();
    	return m;
    }
	
	public static Vector2i getRenderSize() {
		Vector2i store = new Vector2i();
        if( vrsystem == null ) {
            // 1344x1512
            store.x = 1344;
            store.y = 1512;
        } else {
            IntBuffer x = IntBuffer.allocate(1);
            IntBuffer y = IntBuffer.allocate(1);
            vrsystem.GetRecommendedRenderTargetSize.apply(x, y);
            store.x = x.get(0)/2;
            store.y = y.get(0);
        }
        return store;
	}
	
	private static void initBuffers(int xl, int yl, int xr, int yr) {
		leftEyeTextureId = GL11.glGenTextures();
	    int leftEyeDepthId = GL11.glGenTextures();
	    fbos[EYE_LEFT] = GL30.glGenFramebuffers();
	    GL11.glBindTexture(GL11.GL_TEXTURE_2D, leftEyeTextureId);
	    GL11.glEnable(GL11.GL_TEXTURE_2D);
	    GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
	    GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
	    GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
	    GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
	    GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, xl, yl, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, (ByteBuffer)null);
	    texType0.handle = leftEyeTextureId;
	    texType0.write();
	    
	    GL11.glBindTexture(GL11.GL_TEXTURE_2D, leftEyeDepthId);
	    GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
	    GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
	    GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
	    GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
	    GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL14.GL_DEPTH_COMPONENT24, xl, yl, 0, GL11.GL_DEPTH_COMPONENT, GL11.GL_UNSIGNED_INT, (ByteBuffer)null);

	    GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, fbos[EYE_LEFT]);
	    GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL11.GL_TEXTURE_2D, leftEyeTextureId, 0);
	    GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT,  GL11.GL_TEXTURE_2D, leftEyeDepthId, 0);
	    GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
	    
	    
        
        
        
        
        
	    rightEyeTextureId = GL11.glGenTextures();
	    int rightEyeDepthId = GL11.glGenTextures();
	    fbos[EYE_RIGHT] = GL30.glGenFramebuffers();
	    GL11.glBindTexture(GL11.GL_TEXTURE_2D, rightEyeTextureId);
	    GL11.glEnable(GL11.GL_TEXTURE_2D);
	    GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
	    GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
	    GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
	    GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
	    GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, xr, yr, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, (ByteBuffer)null);
	    texType1.handle = rightEyeTextureId;
	    texType1.write();
	    
	    GL11.glBindTexture(GL11.GL_TEXTURE_2D, rightEyeDepthId);
	    GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
	    GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
	    GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
	    GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
	    GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL14.GL_DEPTH_COMPONENT24, xr, yr, 0, GL11.GL_DEPTH_COMPONENT, GL11.GL_UNSIGNED_INT, (ByteBuffer)null);

	    GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, fbos[EYE_RIGHT]);
	    GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL11.GL_TEXTURE_2D, rightEyeTextureId, 0);
	    GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT,  GL11.GL_TEXTURE_2D, rightEyeDepthId, 0);
	    GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
	}
	
	private static void initializeJOpenVR() throws Exception { 
		hmdErrorStore = IntBuffer.allocate(1);
		vrsystem = null;
		JOpenVRLibrary.VR_InitInternal(hmdErrorStore, JOpenVRLibrary.EVRApplicationType.EVRApplicationType_VRApplication_Scene);
		if( hmdErrorStore.get(0) == 0 ) {
			// ok, try and get the vrsystem pointer..
			vrsystem = new VR_IVRSystem_FnTable(JOpenVRLibrary.VR_GetGenericInterface(JOpenVRLibrary.IVRSystem_Version, hmdErrorStore));
		}
		if( vrsystem == null || hmdErrorStore.get(0) != 0 ) {
			throw new Exception(jopenvr.JOpenVRLibrary.VR_GetVRInitErrorAsEnglishDescription(hmdErrorStore.get(0)).getString(0));		
		} else {
			
			vrsystem.setAutoSynch(false);
			vrsystem.read();
			
			System.out.println("OpenVR initialized & VR connected.");
			
			tlastVsync = FloatBuffer.allocate(1);
			_tframeCount = LongBuffer.allocate(1);

			hmdDisplayFrequency = IntBuffer.allocate(1);
			hmdDisplayFrequency.put( (int) JOpenVRLibrary.ETrackedDeviceProperty.ETrackedDeviceProperty_Prop_DisplayFrequency_Float);
			hmdTrackedDevicePoseReference = new TrackedDevicePose_t.ByReference();
			hmdTrackedDevicePoses = (TrackedDevicePose_t[])hmdTrackedDevicePoseReference.toArray(JOpenVRLibrary.k_unMaxTrackedDeviceCount);
			poseMatrices = new Matrix4f[JOpenVRLibrary.k_unMaxTrackedDeviceCount];
			for(int i=0;i<poseMatrices.length;i++) poseMatrices[i] = new Matrix4f();

			timePerFrame = 1.0 / hmdDisplayFrequency.get(0);
			
			
			// disable all this stuff which kills performance
			hmdTrackedDevicePoseReference.setAutoRead(false);
			hmdTrackedDevicePoseReference.setAutoWrite(false);
			hmdTrackedDevicePoseReference.setAutoSynch(false);
			for(int i=0;i<JOpenVRLibrary.k_unMaxTrackedDeviceCount;i++) {
				hmdTrackedDevicePoses[i].setAutoRead(false);
				hmdTrackedDevicePoses[i].setAutoWrite(false);
				hmdTrackedDevicePoses[i].setAutoSynch(false);
			}

			initSuccess = true;
		}
	}
	
	private static void initOpenVRCompositor(boolean set) throws Exception {
        hmdErrorStore.put(0, 0); // clear the error store
		if( set && vrsystem.GetFloatTrackedDeviceProperty != null ) {
			vrCompositor = new VR_IVRCompositor_FnTable(JOpenVRLibrary.VR_GetGenericInterface(JOpenVRLibrary.IVRCompositor_Version, hmdErrorStore));
			if(vrCompositor != null && hmdErrorStore.get(0) == 0){                
				System.out.println("OpenVR Compositor initialized OK.");
				vrCompositor.setAutoSynch(false);
				vrCompositor.read();
				vrCompositor.SetTrackingSpace.apply(JOpenVRLibrary.ETrackingUniverseOrigin.ETrackingUniverseOrigin_TrackingUniverseStanding);
			} else {
				throw new Exception(jopenvr.JOpenVRLibrary.VR_GetVRInitErrorAsEnglishDescription(hmdErrorStore.get(0)).getString(0));			 
			}
		}
		if( vrCompositor == null ) {
			System.out.println("Skipping VR Compositor...");
			if( vrsystem != null ) {
				vsyncToPhotons = vrsystem.GetFloatTrackedDeviceProperty.apply(JOpenVRLibrary.k_unTrackedDeviceIndex_Hmd, JOpenVRLibrary.ETrackedDeviceProperty.ETrackedDeviceProperty_Prop_SecondsFromVsyncToPhotons_Float, hmdErrorStore);
			} else {
				vsyncToPhotons = 0f;
			}
			throw new Exception("Unable to initialize VR Compositor");
		}
	}
	
	
	private static void initTextureSubmitStructs(){
		
		// left eye
		texBounds.uMax = 1f;
		texBounds.uMin = 0f;
		texBounds.vMax = 1f;
		texBounds.vMin = 0f;
		texBounds.setAutoSynch(false);
		texBounds.setAutoRead(false);
		texBounds.setAutoWrite(false);
		texBounds.write();


		// texture type
		texType0.eColorSpace = JOpenVRLibrary.EColorSpace.EColorSpace_ColorSpace_Gamma;
		texType0.eType = JOpenVRLibrary.EGraphicsAPIConvention.EGraphicsAPIConvention_API_OpenGL;
		texType0.setAutoSynch(false);
		texType0.setAutoRead(false);
		texType0.setAutoWrite(false);
		texType0.handle = -1;
		texType0.write();

		
		// texture type
		texType1.eColorSpace = JOpenVRLibrary.EColorSpace.EColorSpace_ColorSpace_Gamma;
		texType1.eType = JOpenVRLibrary.EGraphicsAPIConvention.EGraphicsAPIConvention_API_OpenGL;
		texType1.setAutoSynch(false);
		texType1.setAutoRead(false);
		texType1.setAutoWrite(false);
		texType1.handle = -1;
		texType1.write();
		
		System.out.println("OpenVR Compositor initialized OK.");

	}
	/*
	private static RenderTextureSet createRenderTexture(int lwidth, int lheight){
		renderSize[0] = new Vector2f(lwidth, lheight);
		renderSize[1] = new Vector2f(lwidth, lheight);
		//----------------------------------------------------------------------------------------------------------------------------------
		//Buffer generation
		fbos[EYE_LEFT] = GL30.glGenFramebuffers();
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, fbos[EYE_LEFT]);
		
		
		//----------------------------------------------------------------------------------------------------------------------------------
		//Texture generation
		
		// generate left eye texture
		leftEyeTextureId = GL11.glGenTextures();
		int LeftEyeDepthId = GL11.glGenTextures();
		int boundTextureId = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, leftEyeTextureId);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, lwidth, lheight, 0, GL11.GL_RGBA, GL11.GL_INT, (java.nio.ByteBuffer) null);
		
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, LeftEyeDepthId);
		GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL14.GL_DEPTH_COMPONENT24, lwidth, lheight, 0, GL11.GL_DEPTH_COMPONENT, GL11.GL_INT, (ByteBuffer) null);
		
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, boundTextureId);

		texType0.handle = leftEyeTextureId;
		texType0.eColorSpace = JOpenVRLibrary.EColorSpace.EColorSpace_ColorSpace_Gamma;
		texType0.eType = JOpenVRLibrary.EGraphicsAPIConvention.EGraphicsAPIConvention_API_OpenGL;
		texType0.write();
		
		//----------------------------------------------------------------------------------------------------------------------------------
		//Buffer setup
		GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL11.GL_TEXTURE_2D, leftEyeTextureId, 0);
		GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT,  GL11.GL_TEXTURE_2D, LeftEyeDepthId, 0);
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
		
		//----------------------------------------------------------------------------------------------------------------------------------
		//Buffer generation
		fbos[EYE_RIGHT] = GL30.glGenFramebuffers();
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, fbos[EYE_RIGHT]);
		
		//----------------------------------------------------------------------------------------------------------------------------------
		//Texture generation
		
		// generate right eye texture
		rightEyeTextureId = GL11.glGenTextures();
		int RightEyeDepthId = GL11.glGenTextures();
		boundTextureId = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, rightEyeTextureId);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, lwidth, lheight, 0, GL11.GL_RGBA, GL11.GL_INT, (java.nio.ByteBuffer) null);
		
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, RightEyeDepthId);
		GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL14.GL_DEPTH_COMPONENT24, lwidth, lheight, 0, GL11.GL_DEPTH_COMPONENT, GL11.GL_INT, (ByteBuffer) null);
		
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, boundTextureId);
		texType1.handle = rightEyeTextureId;
		texType1.eColorSpace = JOpenVRLibrary.EColorSpace.EColorSpace_ColorSpace_Gamma;
		texType1.eType = JOpenVRLibrary.EGraphicsAPIConvention.EGraphicsAPIConvention_API_OpenGL;
		texType1.write();
		
		//----------------------------------------------------------------------------------------------------------------------------------
		//Buffer setup
		GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL11.GL_TEXTURE_2D, rightEyeTextureId, 0);
		GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT,  GL11.GL_TEXTURE_2D, RightEyeDepthId, 0);
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
		
		

		RenderTextureSet textureSet = new RenderTextureSet();
		textureSet.leftEyeTextureIds.add(leftEyeTextureId);
		textureSet.rightEyeTextureIds.add(rightEyeTextureId);
		return textureSet;
	}
	*/
	public static void setCurrentRenderEye(int renderEye){
		if(renderEye != EYE_CENTER){
			GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, fbos[renderEye]);
			GL11.glViewport(0, 0, rendersize.x, rendersize.y);
		}
		else{
			GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
			GL11.glViewport(0, 0, Display.getWidth(), Display.getHeight());
		}
	}
	/*
	private static void initProjectionsMatricesInfos(){
		FloatByReference lpfLeft = new FloatByReference();
		FloatByReference lpfRight = new FloatByReference();
		FloatByReference lpfTop = new FloatByReference();
		FloatByReference lpfBottom = new FloatByReference();
		
		vrsystem.GetProjectionRaw.apply(EYE_LEFT, lpfLeft, lpfRight, lpfTop, lpfBottom);
		float lfRight = lpfRight.getValue();
		float lfLeft = lpfLeft.getValue();
		float lfTop = lpfTop.getValue();
		float lfBottom = lpfBottom.getValue();
		eyeMatrixInfos[EYE_LEFT] = new float[]{lfLeft, lfRight, lfBottom, lfTop};
		
		
		FloatByReference rpfLeft = new FloatByReference();
		FloatByReference rpfRight = new FloatByReference();
		FloatByReference rpfTop = new FloatByReference();
		FloatByReference rpfBottom = new FloatByReference();
		
		vrsystem.GetProjectionRaw.apply(EYE_LEFT, rpfLeft, rpfRight, rpfTop, rpfBottom);
		float rfRight = lpfRight.getValue();
		float rfLeft = lpfLeft.getValue();
		float rfTop = lpfTop.getValue();
		float rfBottom = lpfBottom.getValue();
		eyeMatrixInfos[EYE_RIGHT] = new float[]{rfLeft, rfRight, rfBottom, rfTop};
		
		
		HmdMatrix44_t pm = vrsystem.GetProjectionMatrix.apply(EYE_LEFT, zNear, zFar, JOpenVRLibrary.EGraphicsAPIConvention.EGraphicsAPIConvention_API_OpenGL);
		
		System.out.println("GMatrix (eye left):");
	    System.out.println(pm.m[0]+" "+pm.m[1]+" "+pm.m[2]+" "+pm.m[3]);
	    System.out.println(pm.m[4]+" "+pm.m[5]+" "+pm.m[6]+" "+pm.m[7]);
	    System.out.println(pm.m[8]+" "+pm.m[9]+" "+pm.m[10]+" "+pm.m[11]);
	    System.out.println(pm.m[12]+" "+pm.m[13]+" "+pm.m[14]+" "+pm.m[15]);
	    
	    hmdProjectionLeftEyeBuffer = new Matrix4f(
	    		pm.m[0], pm.m[1], -pm.m[2], pm.m[3],
	    		pm.m[4], pm.m[5], -pm.m[6], pm.m[7],
	    		pm.m[8], pm.m[9], -pm.m[10], pm.m[11],
	    		pm.m[12], pm.m[13], -pm.m[14], pm.m[15]
	    ).toFloatBuffer();
	    
	    pm = vrsystem.GetProjectionMatrix.apply(EYE_RIGHT, zNear, zFar, JOpenVRLibrary.EGraphicsAPIConvention.EGraphicsAPIConvention_API_OpenGL);
		
		System.out.println("GMatrix (eye left):");
	    System.out.println(pm.m[0]+" "+pm.m[1]+" "+pm.m[2]+" "+pm.m[3]);
	    System.out.println(pm.m[4]+" "+pm.m[5]+" "+pm.m[6]+" "+pm.m[7]);
	    System.out.println(pm.m[8]+" "+pm.m[9]+" "+pm.m[10]+" "+pm.m[11]);
	    System.out.println(pm.m[12]+" "+pm.m[13]+" "+pm.m[14]+" "+pm.m[15]);
	    
	    hmdProjectionRightEyeBuffer = new Matrix4f(
	    		pm.m[0], pm.m[1], -pm.m[2], pm.m[3],
	    		pm.m[4], pm.m[5], -pm.m[6], pm.m[7],
	    		pm.m[8], pm.m[9], -pm.m[10], pm.m[11],
	    		pm.m[12], pm.m[13], -pm.m[14], pm.m[15]
	    ).toFloatBuffer();
	    float t = pm.m[0];
	    float Rad2Deg = (float) (180f / Math.PI);
	    float fov = (float) (Math.atan(1.0f / t ) * 2.0f * Rad2Deg);
	    System.out.println("fov: "+fov);
	    
	}
	
	private static float[] initProjectionMatrix(int eye){
		if (vrsystem == null) {
            return new float[16];
        }
        float nearClip = 0.1f, farClip = 30.0f;
        float[] m = vrsystem.GetProjectionMatrix.apply(eye, nearClip, farClip, JOpenVRLibrary.EGraphicsAPIConvention.EGraphicsAPIConvention_API_OpenGL).m;
        //printMatrix(toMatrix4f(m), "PROJECTION "+eye);
        //System.out.println(toMatrix4f(m).M[2][0]);
        return m;
        //return null;
	}
	*/
	/*
	private static float[] initProjectionMatrixOLD(int eye){
		float zNear = 0.01f;
		float zFar = 30f;
		FloatByReference pfLeft = new FloatByReference();
		FloatByReference pfRight = new FloatByReference();
		FloatByReference pfTop = new FloatByReference();
		FloatByReference pfBottom = new FloatByReference();
		vrsystem.GetProjectionRaw.apply(eye, pfLeft, pfRight, pfTop, pfBottom);
		//HmdMatrix44_t m2 = vrsystem.GetProjectionMatrix.apply(eye, zNear, zFar, 0);
		
		float[] m = new float[16];
		
		float fRight = pfRight.getValue();
		float fLeft = pfLeft.getValue();
		float fTop = pfTop.getValue();
		float fBottom = pfBottom.getValue();
	    /*
		float idx = 1.0f / (fRight - fLeft);
	    float idy = 1.0f / (fBottom - fTop);
	    float idz = 1.0f / (zFar - zNear);
	    float sx = fRight + fLeft;
	    float sy = fBottom + fTop;
		
	    m[0]  = 2*idx;  m[1]  = 0.0f;   m[2]  = sx*idx;     m[3]  = 0.0f;
	    
	    m[4]  = 0.0f;   m[5]  = 2*idy;  m[6]  = sy*idy;     m[7]  = 0.0f;
	    
	    m[8]  = 0.0f;   m[9]  = 0.0f;   m[10] = -zFar*idz;  m[11] = -zFar*zNear*idz;
	    
	    m[12] = 0.0f;   m[13] = 0.0f;   m[14] = -1.0f;      m[15] = 0.0f;
	    * /
	    
	    
	    
		int offset = 0;
		
		float r_width  = 1.0f / (fRight - fLeft);
	    //float r_height = 1.0f / (top - bottom);
		float r_height = 1.0f / (fBottom - fTop);
	    float r_depth  = 1.0f / (zFar - zNear);
	    float x =  2.0f * (r_width);
	    float y =  2.0f * (r_height);
	    float z =  2.0f * (r_depth);
	    float A = (fRight + fLeft) * r_width;
	    float B = (fTop + fBottom) * r_height;
	    float C = (zFar + zNear) * r_depth;
	    
	    m[offset + 0] = x;
	    m[offset + 3] = -A;
	    m[offset + 5] = y;
	    m[offset + 7] = -B;
	    m[offset + 10] = -z;
	    m[offset + 11] = -C;
	    m[offset +  1] = 0.0f;
	    m[offset +  2] = 0.0f;
	    m[offset +  4] = 0.0f;
	    m[offset +  6] = 0.0f;
	    m[offset +  8] = 0.0f;
	    m[offset +  9] = 0.0f;
	    m[offset + 12] = 0.0f;
	    m[offset + 13] = 0.0f;
	    m[offset + 14] = 0.0f;
	    m[offset + 15] = 1.0f;
	    
	    
	    /*
	    System.out.println("Matrix (eye "+(eye == EYE_LEFT ? "left" : "right")+"):");
	    System.out.println(left);
	    System.out.println(right);
	    System.out.println(bottom);
	    System.out.println(top);
		*/
	    /*
	    System.out.println("CMatrix (eye "+(eye == EYE_LEFT ? "left" : "right")+"):");
	    System.out.println(m[0]+" "+m[1]+" "+m[2]+" "+m[3]);
	    System.out.println(m[4]+" "+m[5]+" "+m[6]+" "+m[7]);
	    System.out.println(m[8]+" "+m[9]+" "+m[10]+" "+m[11]);
	    System.out.println(m[12]+" "+m[13]+" "+m[14]+" "+m[15]);
	    * /
	    
	    return m;
	}
	*/
	/**
	 * <b>MATRIXMODE</b> MUST BE SET TO <b>GL_PROJECTION</b> !
	 */
	/*
	public static void loadProjectionMatrix(int eye){
		GL11.glLoadIdentity();
    	FloatBuffer bufferl = BufferUtils.createFloatBuffer(4*4);
        bufferl.put(toFloatArray(eye == 0 ? leftEyeProjectionMatrix : rightEyeProjectionMatrix));
        bufferl.flip();
        GL11.glMultMatrix(bufferl);
	}
	
	public static void loadModelViewMatrix(int eye){
		GL11.glLoadIdentity();
    	FloatBuffer bufferl = BufferUtils.createFloatBuffer(4*4);
		Matrix4f mmul = new Matrix4f();
    	if(eye == EYE_LEFT)
    		 Matrix4f.mul(leftEyePose, hmdPose, mmul);
    	else Matrix4f.mul(rightEyePose, hmdPose, mmul);
        bufferl.put(toFloatArray(mmul));
        bufferl.flip();
        GL11.glMultMatrix(bufferl);
	}
	*/
	
	public static void loadProjectionMatrix(int eye){
		GL11.glLoadIdentity();
    	FloatBuffer bufferl = BufferUtils.createFloatBuffer(4*4);
        (eye == 0 ? leftEyeProjectionMatrix : rightEyeProjectionMatrix).store(bufferl);
        bufferl.flip();
        GL11.glMultMatrix(bufferl);
	}
	
	public static void loadModelViewMatrix(int eye){
		GL11.glLoadIdentity();
    	FloatBuffer bufferl = BufferUtils.createFloatBuffer(4*4);
		Matrix4f mmul = new Matrix4f();
    	if(eye == EYE_LEFT)
    		 Matrix4f.mul(leftEyePose, hmdPose, mmul);
    	else Matrix4f.mul(rightEyePose, hmdPose, mmul);
        mmul.store(bufferl);
        bufferl.flip();
        GL11.glMultMatrix(bufferl);
	}
	
	/*
	private static float[] toFloatArray(Matrix4f m) {
		return new float[]{
				m.M[0][0], m.M[0][1], m.M[0][2], m.M[0][3],
				m.M[1][0], m.M[1][1], m.M[1][2], m.M[1][3],
				m.M[2][0], m.M[2][1], m.M[2][2], m.M[2][3],
				m.M[3][0], m.M[3][1], m.M[3][2], m.M[3][3]
		};
	}
	*/
	/*
	private static float[] toFloatArray(Matrix4f m) {
		return new float[]{
				m.m00, m.m10, m.m20, m.m30,
				m.m01, m.m11, m.m21, m.m31,
				m.m02, m.m12, m.m22, m.m32,
				m.m03, m.m13, m.m23, m.m33
		};
	}
	*/
	/*
	private static Matrix4f toMatrix4f(float[] f) {
		return new Matrix4f(
				f[0], f[4], f[8] , f[12],
				f[1], f[5], f[9] , f[13],
				f[2], f[6], f[10], f[14],
				f[3], f[7], f[11], f[15]
		);
	}
	*/
	/*
	public static void loadProjectionMatrix(int eye){
		GL11.glLoadIdentity();
    	GL11.glFrustum(eyeMatrixInfos[eye][0], eyeMatrixInfos[eye][1], eyeMatrixInfos[eye][3], eyeMatrixInfos[eye][2], zNear, zFar);
	}
	*/
	
	static void sendFramesToCompositor() {
		if(vrCompositor.Submit == null)
			return;
		
		vrCompositor.Submit.apply(
				JOpenVRLibrary.EVREye.EVREye_Eye_Left,
				texType0, texBounds,
				JOpenVRLibrary.EVRSubmitFlags.EVRSubmitFlags_Submit_Default);

		vrCompositor.Submit.apply(
				JOpenVRLibrary.EVREye.EVREye_Eye_Right,
				texType1, texBounds,
				JOpenVRLibrary.EVRSubmitFlags.EVRSubmitFlags_Submit_Default);

		vrCompositor.PostPresentHandoff.apply();
	}
	
	public static void updatePose(){
        if(vrsystem == null) return;
        if(vrCompositor != null) {
           vrCompositor.WaitGetPoses.apply(hmdTrackedDevicePoseReference, JOpenVRLibrary.k_unMaxTrackedDeviceCount, null, 0);
        } else {
            // wait
            if( latencyWaitTime > 0 ) sleepNanos(latencyWaitTime);
                        
            vrsystem.GetTimeSinceLastVsync.apply(tlastVsync, _tframeCount);
            float fSecondsUntilPhotons = (float)timePerFrame - tlastVsync.get(0) + vsyncToPhotons;
            
            if( enableDebugLatency ) {
                if( frames == 10 ) {
                    System.out.println("Waited (nanos): " + Long.toString(latencyWaitTime));
                    System.out.println("Predict ahead time: " + Float.toString(fSecondsUntilPhotons));
                }
                frames = (frames + 1) % 60;            
            }            
            
            // handle skipping frame stuff
            long nowCount = _tframeCount.get(0);
            if( nowCount - frameCount > 1 ) {
                // skipped a frame!
                if( enableDebugLatency ) System.out.println("Frame skipped!");
                frameCountRun = 0;
                if( latencyWaitTime > 0 ) {
                    latencyWaitTime -= TimeUnit.MILLISECONDS.toNanos(1);
                    if( latencyWaitTime < 0 ) latencyWaitTime = 0;
                }
            } else if( latencyWaitTime < timePerFrame * 1000000000.0 ) {
                // didn't skip a frame, lets try waiting longer to improve latency
                frameCountRun++;
                latencyWaitTime += Math.round(Math.pow(frameCountRun / 10.0, 2.0));
            }

            frameCount = nowCount;
            
            vrsystem.GetDeviceToAbsoluteTrackingPose.apply(
                    JOpenVRLibrary.ETrackingUniverseOrigin.ETrackingUniverseOrigin_TrackingUniverseStanding,
                    fSecondsUntilPhotons, hmdTrackedDevicePoseReference, JOpenVRLibrary.k_unMaxTrackedDeviceCount);   
        }
        
        // deal with controllers being plugged in and out
        // causing an invalid memory crash... skipping for now
        /*boolean hasEvent = false;
        while( JOpenVRLibrary.VR_IVRSystem_PollNextEvent(OpenVR.getVRSystemInstance(), tempEvent) != 0 ) {
            // wait until the events are clear..
            hasEvent = true;
        }
        if( hasEvent ) {
            // an event probably changed controller state
            VRInput._updateConnectedControllers();
        }*/
        //update controllers pose information
        // TODO VRInput._updateControllerStates();
                
        // read pose data from native
        for (int nDevice = 0; nDevice < JOpenVRLibrary.k_unMaxTrackedDeviceCount; ++nDevice ){
            hmdTrackedDevicePoses[nDevice].readField("bPoseIsValid");
            if( hmdTrackedDevicePoses[nDevice].bPoseIsValid != 0 ){
                hmdTrackedDevicePoses[nDevice].readField("mDeviceToAbsoluteTracking");
                poseMatrices[nDevice] = convertSteamVRMatrix3ToMatrix4f(hmdTrackedDevicePoses[nDevice].mDeviceToAbsoluteTracking);
            }            
        }
        
        if ( hmdTrackedDevicePoses[JOpenVRLibrary.k_unTrackedDeviceIndex_Hmd].bPoseIsValid != 0 ){
            hmdPose = (Matrix4f) poseMatrices[JOpenVRLibrary.k_unTrackedDeviceIndex_Hmd].invert();
        } else {
            hmdPose = new Matrix4f();
        }
	}
	
	public static void sleepNanos(long nanoDuration) {
		final long end = System.nanoTime() + nanoDuration; 
		long timeLeft = nanoDuration; 
		do {
			try {
				if (timeLeft > SLEEP_PRECISION) {
					Thread.sleep(1);
				} else if (timeLeft > SPIN_YIELD_PRECISION) {
					Thread.sleep(0);
				}
			} catch(Exception e) {}
			timeLeft = end - System.nanoTime();
		} while (timeLeft > 0);
	}
	
	/*
	static void updatePose(){
		if ( vrsystem == null || vrCompositor == null || vrCompositor.WaitGetPoses == null)
			return;
		
		vrCompositor.WaitGetPoses.apply(hmdTrackedDevicePoseReference, JOpenVRLibrary.k_unMaxTrackedDeviceCount, null, 0);

		for (int nDevice = 0; nDevice < JOpenVRLibrary.k_unMaxTrackedDeviceCount; ++nDevice ){
			hmdTrackedDevicePoses[nDevice].read();
			if ( hmdTrackedDevicePoses[nDevice].bPoseIsValid != 0 ){
				poseMatrices[nDevice] = convertSteamVRMatrix3ToMatrix4f(hmdTrackedDevicePoses[nDevice].mDeviceToAbsoluteTracking);
				deviceVelocity[nDevice].x = hmdTrackedDevicePoses[nDevice].vVelocity.v[0];
				deviceVelocity[nDevice].y = hmdTrackedDevicePoses[nDevice].vVelocity.v[1];
				deviceVelocity[nDevice].z = hmdTrackedDevicePoses[nDevice].vVelocity.v[2];
			}
		}

		if ( hmdTrackedDevicePoses[JOpenVRLibrary.k_unTrackedDeviceIndex_Hmd].bPoseIsValid != 0 ){
			OpenVRUtil.Matrix4fCopy(poseMatrices[JOpenVRLibrary.k_unTrackedDeviceIndex_Hmd].inverted(), hmdPose);
			headIsTracking = true;
		}
		else{
			headIsTracking = false;
			OpenVRUtil.Matrix4fSetIdentity(hmdPose);
			//hmdPose.M[1][3] = 1.62f;
		}

		findControllerDevices();

		for (int c=0;c<2;c++){
			if (controllerDeviceIndex[c] != -1){
				controllerTracking[c] = true;
				OpenVRUtil.Matrix4fCopy(poseMatrices[controllerDeviceIndex[c]], controllerPose[c]);
			}
			else{
				controllerTracking[c] = false;
				if(controllerPose[c] == null) controllerPose[c] = new Matrix4f();
				OpenVRUtil.Matrix4fSetIdentity(controllerPose[c]);
			}
		}
	}
	
	private static void findControllerDevices(){
		controllerDeviceIndex[RIGHT_CONTROLLER] = -1;
		controllerDeviceIndex[LEFT_CONTROLLER] = -1;
		controllerDeviceIndex[LEFT_CONTROLLER]  = vrsystem.GetTrackedDeviceIndexForControllerRole.apply(JOpenVRLibrary.ETrackedControllerRole.ETrackedControllerRole_TrackedControllerRole_LeftHand);
		controllerDeviceIndex[RIGHT_CONTROLLER] = vrsystem.GetTrackedDeviceIndexForControllerRole.apply(JOpenVRLibrary.ETrackedControllerRole.ETrackedControllerRole_TrackedControllerRole_RightHand);
	}
	*/

	public static boolean iscloseRequested() {//TODO quit system
		return false;
	}


	public static void stop() {
		if (initialized){
			JOpenVRLibrary.VR_ShutdownInternal();
			initialized = false;
		}
	}
	
	public static org.lwjgl.util.vector.Vector3f getEyePose(int eye){
		return eye == EYE_LEFT ? leftEyeTransform : rightEyeTransform;
	}
	
	
	
	
	
	
	
	
	
	
	public static Matrix4f convertSteamVRMatrix3ToMatrix4f(HmdMatrix34_t hmdMatrix){
		Matrix4f s = new Matrix4f();
		s.m00 = hmdMatrix.m[0];
		s.m10 = hmdMatrix.m[1];
		s.m20 = hmdMatrix.m[2];
		s.m30 = hmdMatrix.m[3];
		
		s.m01 = hmdMatrix.m[4];
		s.m11 = hmdMatrix.m[5];
		s.m21 = hmdMatrix.m[6];
		s.m31 = hmdMatrix.m[7];
		
		s.m02 = hmdMatrix.m[8];
		s.m12 = hmdMatrix.m[9];
		s.m22 = hmdMatrix.m[10];
		s.m32 = hmdMatrix.m[11];
		
		s.m03 = 0;
		s.m13 = 0;
		s.m23 = 0;
		s.m33 = 1;
		return s;
	}
	
	public static Matrix4f convertSteamVRMatrix4ToMatrix4f(HmdMatrix44_t hmdMatrix){
		Matrix4f s = new Matrix4f();
		s.m00 = hmdMatrix.m[0];
		s.m10 = hmdMatrix.m[1];
		s.m20 = hmdMatrix.m[2];
		s.m30 = hmdMatrix.m[3];
		
		s.m01 = hmdMatrix.m[4];
		s.m11 = hmdMatrix.m[5];
		s.m21 = hmdMatrix.m[6];
		s.m31 = hmdMatrix.m[7];
		
		s.m02 = hmdMatrix.m[8];
		s.m12 = hmdMatrix.m[9];
		s.m22 = hmdMatrix.m[10];
		s.m32 = hmdMatrix.m[11];
		
		s.m03 = hmdMatrix.m[12];
		s.m13 = hmdMatrix.m[13];
		s.m23 = hmdMatrix.m[14];
		s.m33 = hmdMatrix.m[15];
		return s;
	}
	
	
	
	
}
