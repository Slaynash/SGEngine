package slaynash.opengl.utils;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.util.concurrent.TimeUnit;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

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
	
	private static Matrix4f[] poseMatrices;
	
	private static Matrix4f leftEyeProjectionMatrix = new Matrix4f();
	static Matrix4f rightEyeProjectionMatrix = new Matrix4f();
	static Matrix4f leftEyePose = new Matrix4f();
	static Matrix4f rightEyePose = new Matrix4f();
	

	final static VRTextureBounds_t texBoundsLeft = new VRTextureBounds_t();
	final static VRTextureBounds_t texBoundsRight = new VRTextureBounds_t();
	final static Texture_t texType0 = new Texture_t();
	final static Texture_t texType1 = new Texture_t();
	
	private static IntBuffer hmdDisplayFrequency;
	private static FloatBuffer tlastVsync;
	private static LongBuffer _tframeCount;
	
	
	private static int[] fbos = new int[2];
	private static int leftEyeTextureId, rightEyeTextureId;
	private static Vector2i rendersize = new Vector2i();
	
	
	private static Matrix4f hmdPose = new Matrix4f();
	
	private static float znear = 0.1f, zfar = 30f;
	
	private static final long SLEEP_PRECISION = TimeUnit.MILLISECONDS.toNanos(4);
	private static final long SPIN_YIELD_PRECISION = TimeUnit.MILLISECONDS.toNanos(2);
	private static long latencyWaitTime;
	private static boolean enableDebugLatency;
	private static int frames;
	private static float vsyncToPhotons;
    private static double timePerFrame, frameCountRun;
    private static long frameCount;
    
	private static float posX, posY, posZ;
	private static float dirX, dirY, dirZ;
	private static float upX, upY, upZ;
	
	public static void setViewDistance(float znear, float zfar){
		if(VRUtils.znear == znear && VRUtils.zfar == zfar) return;
		VRUtils.znear = znear;
		VRUtils.zfar = zfar;
		setupCameras();
	}
	
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
		
		rendersize = getRenderSize();
	    initBuffers(rendersize.x, rendersize.y, rendersize.x, rendersize.y);
		System.out.println( "Render buffers/textures created" );
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
    	HmdMatrix44_t mat = vrsystem.GetProjectionMatrix.apply(nEye, znear, zfar, JOpenVRLibrary.EGraphicsAPIConvention.EGraphicsAPIConvention_API_OpenGL);
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
		texBoundsLeft.uMax = 1f;
		texBoundsLeft.uMin = 0f;
		texBoundsLeft.vMax = 1f;
		texBoundsLeft.vMin = 0f;
		texBoundsLeft.setAutoSynch(false);
		texBoundsLeft.setAutoRead(false);
		texBoundsLeft.setAutoWrite(false);
		texBoundsLeft.write();
		// right eye
		texBoundsRight.uMax = 1f;
		texBoundsRight.uMin = 0f;
		texBoundsRight.vMax = 1f;
		texBoundsRight.vMin = 0f;
		texBoundsRight.setAutoSynch(false);
		texBoundsRight.setAutoRead(false);
		texBoundsRight.setAutoWrite(false);
		texBoundsRight.write();
		// texture type
		texType0.eColorSpace = JOpenVRLibrary.EColorSpace.EColorSpace_ColorSpace_Gamma;
		texType0.eType = JOpenVRLibrary.EGraphicsAPIConvention.EGraphicsAPIConvention_API_OpenGL;
		texType0.setAutoSynch(false);
		texType0.setAutoRead(false);
		texType0.setAutoWrite(false);
		texType0.handle = -1;
		texType1.eColorSpace = JOpenVRLibrary.EColorSpace.EColorSpace_ColorSpace_Gamma;
		texType1.eType = JOpenVRLibrary.EGraphicsAPIConvention.EGraphicsAPIConvention_API_OpenGL;
		texType1.setAutoSynch(false);
		texType1.setAutoRead(false);
		texType1.setAutoWrite(false);
		texType1.handle = -1;
		
		System.out.println("OpenVR Compositor initialized OK.");

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
            Matrix4f viewMatrix = new Matrix4f();
			hmdPose.transpose(viewMatrix);
			viewMatrix.invert();
			posX = viewMatrix.m03;
			posY = viewMatrix.m13;
			posZ = viewMatrix.m23;
			dirX = -viewMatrix.m02;
			dirY = -viewMatrix.m12;
			dirZ = -viewMatrix.m22;
			upX = viewMatrix.m01;
			upY = viewMatrix.m11;
			upZ = viewMatrix.m21;
        } else {
            hmdPose = new Matrix4f();
        }
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/***
	 * <b>WARNING:</b> you must pass the MVP matrix manually after this function !
	 * @param renderEye the eyeId you want to render to
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
	
	static void sendFramesToCompositor() {
		if(vrCompositor.Submit == null)
			return;
		
		vrCompositor.Submit.apply(
				JOpenVRLibrary.EVREye.EVREye_Eye_Left,
				texType0, texBoundsLeft,
				JOpenVRLibrary.EVRSubmitFlags.EVRSubmitFlags_Submit_Default);

		vrCompositor.Submit.apply(
				JOpenVRLibrary.EVREye.EVREye_Eye_Right,
				texType1, texBoundsRight,
				JOpenVRLibrary.EVRSubmitFlags.EVRSubmitFlags_Submit_Default);

		vrCompositor.PostPresentHandoff.apply();//require ?
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

	public static boolean isCloseRequested() {//TODO quit system
		return false;
	}


	public static void stop() {
		if (initialized){
			JOpenVRLibrary.VR_ShutdownInternal();
			initialized = false;
		}
	}
	
	private static Matrix4f convertSteamVRMatrix3ToMatrix4f(HmdMatrix34_t hmdMatrix){
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
	
	private static Matrix4f convertSteamVRMatrix4ToMatrix4f(HmdMatrix44_t hmdMatrix){
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
	
	
	public static Matrix4f getCurrentModelViewProjectionMatrix(int nEye){
    	Matrix4f matMVP = new Matrix4f();
    	
    	if (nEye == JOpenVRLibrary.EVREye.EVREye_Eye_Left)
    		Matrix4f.mul(leftEyeProjectionMatrix, leftEyePose, matMVP);
    	else if (nEye == JOpenVRLibrary.EVREye.EVREye_Eye_Right)
    		Matrix4f.mul(rightEyeProjectionMatrix, rightEyePose, matMVP);
		Matrix4f.mul(matMVP, hmdPose, matMVP);

    	return matMVP;
    }
	
	public static Vector3f getPosition(){
		return new Vector3f(posX, posY, posZ);
	}
	
	public static Vector3f getForward(){
		return new Vector3f(dirX, dirY, dirZ);
	}
	
	public static Vector3f getUpVector(){
		return new Vector3f(upX, upY, upZ);
	}
	
	public static Matrix4f getHmdPose(){
		return hmdPose;
	}
	
}
