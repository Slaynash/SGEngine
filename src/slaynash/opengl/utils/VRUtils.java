package slaynash.opengl.utils;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.concurrent.TimeUnit;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.FloatByReference;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.LongByReference;
import com.sun.jna.ptr.PointerByReference;

import de.fruitfly.ovr.structs.Vector2i;
import jopenvr.HmdMatrix34_t;
import jopenvr.HmdMatrix44_t;
import jopenvr.JOpenVRLibrary;
import jopenvr.JOpenVRLibrary.ETextureType;
import jopenvr.RenderModel_TextureMap_t;
import jopenvr.RenderModel_Vertex_t;
import jopenvr.RenderModel_t;
import jopenvr.Texture_t;
import jopenvr.TrackedDevicePose_t;
import jopenvr.VREvent_t;
import jopenvr.VRTextureBounds_t;
import jopenvr.VR_IVRCompositor_FnTable;
import jopenvr.VR_IVRRenderModels_FnTable;
import jopenvr.VR_IVRSystem_FnTable;
import slaynash.engine.Renderable3dModel;
import slaynash.engine.objloader.ObjLoader;
import slaynash.opengl.Configuration;
import slaynash.opengl.shaders.ShaderManager;
import slaynash.opengl.textureUtils.TextureDef;
import slaynash.opengl.utils.vr.VRController;

public class VRUtils {
	
	public static final int EYE_LEFT = 0;
	public static final int EYE_RIGHT = 1;
	public static final int EYE_CENTER = 2;
	
	
	public static String initStatus;
	private static boolean initialized;
	public static boolean initSuccess = false;
	
	
	private static VR_IVRSystem_FnTable vrsystem;
	private static VR_IVRCompositor_FnTable vrCompositor;
	
	
	private static IntBuffer hmdErrorStore;
	private static TrackedDevicePose_t.ByReference trackedDevicePosesReference = new TrackedDevicePose_t.ByReference();
	private static TrackedDevicePose_t[] trackedDevicePose = (TrackedDevicePose_t[]) trackedDevicePosesReference.toArray(JOpenVRLibrary.k_unMaxTrackedDeviceCount);
	
	private static Matrix4f leftEyeProjectionMatrix = new Matrix4f();
	private static Matrix4f rightEyeProjectionMatrix = new Matrix4f();
	private static Matrix4f leftEyePose = new Matrix4f();
	private static Matrix4f rightEyePose = new Matrix4f();
	

	private final static VRTextureBounds_t texBoundsLeft = new VRTextureBounds_t();
	private final static VRTextureBounds_t texBoundsRight = new VRTextureBounds_t();
	private final static Texture_t texType0 = new Texture_t();
	private final static Texture_t texType1 = new Texture_t();
	
	private static IntBuffer hmdDisplayFrequency;
	private static float tlastVsync;
	private static long _tframeCount;
	
	
	private static int[] fbos = new int[2];
	private static int[] rbos = new int[2];
	private static int leftEyeTextureId, rightEyeTextureId;
	private static Vector2i rendersize = new Vector2i();
	
	
	private static Matrix4f hmdPose = new Matrix4f();
	
	private static float znear = 0.01f, zfar = 30f;
	
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
	private static float rightX, rightY, rightZ;
	
	private static Matrix4f[] mat4DevicePose = new Matrix4f[JOpenVRLibrary.k_unMaxTrackedDeviceCount];
	private static Renderable3dModel controllerModel, basestationModel;
	private static boolean isCloseRequested = false;
	
	private static VRController[] controllers = new VRController[JOpenVRLibrary.k_unMaxTrackedDeviceCount];
	private static int nValidControllers;
	
	public static void setViewDistance(float znear, float zfar){
		if(VRUtils.znear == znear && VRUtils.zfar == zfar) return;
		VRUtils.znear = znear;
		VRUtils.zfar = zfar;
		setupCameras();
	}
	
	protected static boolean initVR() {
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
		
		System.out.println( "[VRUtils] OpenVR initialized & VR connected." );
		
		rendersize = getRenderSize();
		System.out.println("[VRUtils] Render size: "+rendersize.x+"x"+rendersize.y);
	    initFBOs(rendersize.x, rendersize.y, rendersize.x, rendersize.y);
	    initRBOs(rendersize.x, rendersize.y, rendersize.x, rendersize.y);
		System.out.println( "[VRUtils] Render buffers/textures created" );
		setupCameras();
		System.out.println( "[VRUtils] Matrices created" );
		initialized = true;
		return true;
	}
	
	private static void setupCameras(){
    	leftEyeProjectionMatrix = getHMDMatrixProjectionEye(JOpenVRLibrary.EVREye.EVREye_Eye_Left);
    	rightEyeProjectionMatrix = getHMDMatrixProjectionEye(JOpenVRLibrary.EVREye.EVREye_Eye_Right);
    	leftEyePose = getHMDMatrixPoseEye(JOpenVRLibrary.EVREye.EVREye_Eye_Left);
    	rightEyePose = getHMDMatrixPoseEye(JOpenVRLibrary.EVREye.EVREye_Eye_Right);
    }
	
	private static Matrix4f getHMDMatrixProjectionEye(int nEye){
    	HmdMatrix44_t mat = vrsystem.GetProjectionMatrix.apply(nEye, znear, zfar);
    	return convertSteamVRMatrix4ToMatrix4f(mat);
    }
    
    private static Matrix4f getHMDMatrixPoseEye(int nEye){
    	if (vrsystem == null) return new Matrix4f();
    	HmdMatrix34_t matEyeRight = vrsystem.GetEyeToHeadTransform.apply(nEye);
    	Matrix4f m = convertSteamVRMatrix3ToMatrix4f(matEyeRight);
    	m.invert();
    	return m;
    }
	
    private static Vector2i getRenderSize() {
		Vector2i store = new Vector2i();
        if( vrsystem == null ) {
            // 1344x1512
            store.x = 1344;
            store.y = 1512;
        } else {
        	IntByReference x = new IntByReference();
        	IntByReference y = new IntByReference();
            vrsystem.GetRecommendedRenderTargetSize.apply(x, y);
            store.x = x.getValue();
            store.y = y.getValue();
        }
        return store;
	}
    
    private static void initFBOs(int xl, int yl, int xr, int yr) {
    	//EYE LEFT
    	fbos[EYE_LEFT] = GL30.glGenFramebuffers();
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, fbos[EYE_LEFT]);
		GL11.glDrawBuffer(GL30.GL_COLOR_ATTACHMENT0);
		
		int colourBufferLeft = GL30.glGenRenderbuffers();
		GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, colourBufferLeft);
		GL30.glRenderbufferStorageMultisample(GL30.GL_RENDERBUFFER, 4, GL11.GL_RGBA8, xl, yl);
		GL30.glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL30.GL_RENDERBUFFER,
				colourBufferLeft);
		
		int depthBufferLeft = GL30.glGenRenderbuffers();
		GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, depthBufferLeft);
		GL30.glRenderbufferStorageMultisample(GL30.GL_RENDERBUFFER, 4, GL14.GL_DEPTH_COMPONENT24, xl, yl);
		GL30.glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, GL30.GL_RENDERBUFFER,
				depthBufferLeft);
		
		
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
		
		
		
		
		
		
		fbos[EYE_RIGHT] = GL30.glGenFramebuffers();
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, fbos[EYE_RIGHT]);
		GL11.glDrawBuffer(GL30.GL_COLOR_ATTACHMENT0);
		
		int colourBufferRight = GL30.glGenRenderbuffers();
		GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, colourBufferRight);
		GL30.glRenderbufferStorageMultisample(GL30.GL_RENDERBUFFER, 4, GL11.GL_RGBA8, xr, yr);
		GL30.glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL30.GL_RENDERBUFFER,
				colourBufferRight);
		
		int depthBufferRight = GL30.glGenRenderbuffers();
		GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, depthBufferRight);
		GL30.glRenderbufferStorageMultisample(GL30.GL_RENDERBUFFER, 4, GL14.GL_DEPTH_COMPONENT24, xr, yr);
		GL30.glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, GL30.GL_RENDERBUFFER,
				depthBufferRight);
		
		
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
		GL11.glViewport(0, 0, Display.getWidth(), Display.getHeight());
	}
    
	
	private static void initRBOs(int xl, int yl, int xr, int yr) {
		leftEyeTextureId = GL11.glGenTextures();
	    int leftEyeDepthId = GL11.glGenTextures();
	    rbos[EYE_LEFT] = GL30.glGenFramebuffers();
	    
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

	    GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, rbos[EYE_LEFT]);
	    GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL11.GL_TEXTURE_2D, leftEyeTextureId, 0);
	    GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT,  GL11.GL_TEXTURE_2D, leftEyeDepthId, 0);
	    GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
	    
	    
        
        
        
        
        
	    rightEyeTextureId = GL11.glGenTextures();
	    int rightEyeDepthId = GL11.glGenTextures();
	    rbos[EYE_RIGHT] = GL30.glGenFramebuffers();
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

	    GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, rbos[EYE_RIGHT]);
	    GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL11.GL_TEXTURE_2D, rightEyeTextureId, 0);
	    GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT,  GL11.GL_TEXTURE_2D, rightEyeDepthId, 0);
	    GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
	}
	
	private static void initializeJOpenVR() throws Exception { 
		hmdErrorStore = IntBuffer.allocate(1);
		vrsystem = null;
		JOpenVRLibrary.VR_InitInternal(hmdErrorStore, JOpenVRLibrary.EVRApplicationType.EVRApplicationType_VRApplication_Scene);
		if( hmdErrorStore.get(0) == 0 ) {
			vrsystem = new VR_IVRSystem_FnTable(JOpenVRLibrary.VR_GetGenericInterface(JOpenVRLibrary.IVRSystem_Version, hmdErrorStore));
		}
		if( vrsystem == null || hmdErrorStore.get(0) != 0 ) {
			throw new Exception(jopenvr.JOpenVRLibrary.VR_GetVRInitErrorAsEnglishDescription(hmdErrorStore.get(0)).getString(0));		
		} else {
			
			vrsystem.setAutoSynch(false);
			vrsystem.read();
			
			System.out.println("[VRUtils] OpenVR initialized & VR connected.");

			hmdDisplayFrequency = IntBuffer.allocate(1);
			hmdDisplayFrequency.put( (int) JOpenVRLibrary.ETrackedDeviceProperty.ETrackedDeviceProperty_Prop_DisplayFrequency_Float);
			for(int i=0;i<mat4DevicePose.length;i++) mat4DevicePose[i] = new Matrix4f();
			for(int i=0;i<controllers.length;i++) controllers[i] = new VRController(i);

			timePerFrame = 1.0 / hmdDisplayFrequency.get(0);
			
			initSuccess = true;
		}
	}
	
	private static void initOpenVRCompositor(boolean set) throws Exception {
        hmdErrorStore.put(0, 0); // clear the error store
		if( set && vrsystem.GetFloatTrackedDeviceProperty != null ) {
			vrCompositor = new VR_IVRCompositor_FnTable(JOpenVRLibrary.VR_GetGenericInterface(JOpenVRLibrary.IVRCompositor_Version, hmdErrorStore));
			if(vrCompositor != null && hmdErrorStore.get(0) == 0){                
				System.out.println("[VRUtils] OpenVR Compositor initialized OK.");
				vrCompositor.setAutoSynch(false);
				vrCompositor.read();
				vrCompositor.SetTrackingSpace.apply(JOpenVRLibrary.ETrackingUniverseOrigin.ETrackingUniverseOrigin_TrackingUniverseStanding);
			} else {
				throw new Exception(JOpenVRLibrary.VR_GetVRInitErrorAsEnglishDescription(hmdErrorStore.get(0)).getString(0));			 
			}
		}
		if( vrCompositor == null ) {
			System.out.println("[VRUtils] Skipping VR Compositor...");
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
		texType0.eType = ETextureType.ETextureType_TextureType_OpenGL;
		texType0.setAutoSynch(false);
		texType0.setAutoRead(false);
		texType0.setAutoWrite(false);
		texType1.eColorSpace = JOpenVRLibrary.EColorSpace.EColorSpace_ColorSpace_Gamma;
		texType1.eType = ETextureType.ETextureType_TextureType_OpenGL;
		texType1.setAutoSynch(false);
		texType1.setAutoRead(false);
		texType1.setAutoWrite(false);
		
		System.out.println("[VRUtils] OpenVR Compositor initialized OK.");

	}
	
	protected static void updatePose(){
        if(vrsystem == null) return;
        if(vrCompositor != null) {
           vrCompositor.WaitGetPoses.apply(trackedDevicePosesReference, JOpenVRLibrary.k_unMaxTrackedDeviceCount, null, 0);
        } else {
            // wait
            if( latencyWaitTime > 0 ) sleepNanos(latencyWaitTime);
            FloatByReference fbatLastVsync = new FloatByReference();
            LongByReference fba_tframeCount = new LongByReference();
            vrsystem.GetTimeSinceLastVsync.apply(fbatLastVsync, fba_tframeCount);
            tlastVsync = fbatLastVsync.getValue();
            _tframeCount = fba_tframeCount.getValue();
            float fSecondsUntilPhotons = (float)timePerFrame - tlastVsync + vsyncToPhotons;
            
            if( enableDebugLatency ) {
                if( frames == 10 ) {
                    System.out.println("[VRUtils] Waited (nanos): " + Long.toString(latencyWaitTime));
                    System.out.println("[VRUtils] Predict ahead time: " + Float.toString(fSecondsUntilPhotons));
                }
                frames = (frames + 1) % 60;            
            }            
            
            // handle skipping frame stuff
            long nowCount = _tframeCount;
            if( nowCount - frameCount > 1 ) {
                // skipped a frame!
                if( enableDebugLatency ) System.out.println("[VRUtils] Frame skipped!");
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
                    fSecondsUntilPhotons, trackedDevicePosesReference, JOpenVRLibrary.k_unMaxTrackedDeviceCount);   
        }
        nValidControllers = 0;
        for (int nDevice = 0; nDevice < JOpenVRLibrary.k_unMaxTrackedDeviceCount; nDevice++ ){
        	if (trackedDevicePose[nDevice].bPoseIsValid == 1) {
        		MatrixUtils.copy(trackedDevicePose[nDevice].mDeviceToAbsoluteTracking, mat4DevicePose[nDevice]);
            }
        	if(vrsystem.GetTrackedDeviceClass.apply(nDevice) == JOpenVRLibrary.ETrackedDeviceClass.ETrackedDeviceClass_TrackedDeviceClass_Controller){
        		controllers[nDevice].setPose(trackedDevicePose[nDevice].bPoseIsValid == 1 ? mat4DevicePose[nDevice] : null);
        		nValidControllers++;
        	}
        	else{
        		controllers[nDevice].unValid();
        	}
        }
        handleInput();
        
        if ( trackedDevicePose[JOpenVRLibrary.k_unTrackedDeviceIndex_Hmd].bPoseIsValid == 1 ){
            MatrixUtils.inverse((Matrix4f) mat4DevicePose[JOpenVRLibrary.k_unTrackedDeviceIndex_Hmd], hmdPose);
            
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
			rightX = viewMatrix.m00;
			rightY = viewMatrix.m10;
			rightZ = viewMatrix.m20;
			
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
			GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, fbos[renderEye]);
			GL11.glViewport(0, 0, rendersize.x, rendersize.y);
		}
		else{
			GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
			GL11.glViewport(0, 0, Display.getWidth(), Display.getHeight());
		}
	}
	
	protected static void sendFramesToCompositor() {
		if(vrCompositor.Submit == null)
			return;
		
		GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, rbos[EYE_LEFT]);
		GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, fbos[EYE_LEFT]);
		GL30.glBlitFramebuffer(0, 0, rendersize.x, rendersize.y, 0, 0, rendersize.x, rendersize.y, GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT, GL11.GL_NEAREST);
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
		
		vrCompositor.Submit.apply(
				JOpenVRLibrary.EVREye.EVREye_Eye_Left,
				texType0, texBoundsLeft,
				JOpenVRLibrary.EVRSubmitFlags.EVRSubmitFlags_Submit_Default);
		
		GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, rbos[EYE_RIGHT]);
		GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, fbos[EYE_RIGHT]);
		GL30.glBlitFramebuffer(0, 0, rendersize.x, rendersize.y, 0, 0, rendersize.x, rendersize.y, GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT, GL11.GL_NEAREST);
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
		
		vrCompositor.Submit.apply(
				JOpenVRLibrary.EVREye.EVREye_Eye_Right,
				texType1, texBoundsRight,
				JOpenVRLibrary.EVRSubmitFlags.EVRSubmitFlags_Submit_Default);

		vrCompositor.PostPresentHandoff.apply();//require ?
	}
	
	private static void sleepNanos(long nanoDuration) {
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

	protected static boolean isCloseRequested() {
		return isCloseRequested;
	}


	protected static void stop() {
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
	
	public static Matrix4f getProjectionMatrix(int eye){
		return eye == EYE_LEFT ? leftEyeProjectionMatrix : rightEyeProjectionMatrix;
	}
	
	public static Matrix4f getViewMatrix(int eye){
		if(eye == EYE_LEFT)
			return Matrix4f.mul(leftEyePose, hmdPose, new Matrix4f());
		else if(eye == EYE_RIGHT)
			return Matrix4f.mul(rightEyePose, hmdPose, new Matrix4f());
		else return Matrix4f.transpose(hmdPose, new Matrix4f());
	}
	
	/*
	public static Matrix4f getCurrentViewProjectionMatrix(int nEye){
    	Matrix4f matMVP = new Matrix4f();
    	
    	if (nEye == JOpenVRLibrary.EVREye.EVREye_Eye_Left)
    		Matrix4f.mul(leftEyeProjectionMatrix, leftEyePose, matMVP);
    	else if (nEye == JOpenVRLibrary.EVREye.EVREye_Eye_Right)
    		Matrix4f.mul(rightEyeProjectionMatrix, rightEyePose, matMVP);
		Matrix4f.mul(matMVP, hmdPose, matMVP);

    	return matMVP;
    }
    */
	
	public static Vector3f getPosition(){
		return new Vector3f(posX, posY, posZ);
	}
	
	public static Vector3f getForward(){
		return new Vector3f(dirX, dirY, dirZ);
	}
	
	public static Vector3f getUpVector(){
		return new Vector3f(upX, upY, upZ);
	}
	
	public static Vector3f getRightVector(){
		return new Vector3f(rightX, rightY, rightZ);
	}
	
	public static Matrix4f getHmdPose(){
		return hmdPose;
	}
	
	
	
	
	
	
	private static void handleInput() {

        // Process SteamVR events
        VREvent_t event = new VREvent_t();
        while (vrsystem.PollNextEvent.apply(event, event.size()) != 0) {
            processVREvent(event);
        }
        
        /*
        // Process SteamVR controller state
        for (int device = 0; device < JOpenVRLibrary.k_unMaxTrackedDeviceCount; device++) {

            VRControllerState_t state = new VRControllerState_t();

            if (vrsystem.GetControllerState.apply(device, state, state.size()) != 0) {

                //rbShowTrackedDevice[device] = state.ulButtonPressed == 0;

                // let's test haptic impulse too..
                if (state.ulButtonPressed != 0) {
                    // apparently only axis ID 0 works, maximum duration value is 3999
                	vrsystem.TriggerHapticPulse.apply(device, 0, (short) 3999);
                }

            }
        }
        */
    }

    private static void processVREvent(VREvent_t event) {
    	
    	if(event.eventType >= 700 && event.eventType <= 704 && event.eventType != 701){
    		System.out.println("[VRUtils] Close requested by SteamVR... (id: "+event.eventType+")");
    		isCloseRequested = true;
    	}
    	
    	if(event.trackedDeviceIndex > 0) controllers[event.trackedDeviceIndex].throwEvent(event.trackedDeviceIndex, event.eventType, event.data);
    	
        switch (event.eventType) {
            case JOpenVRLibrary.EVREventType.EVREventType_VREvent_TrackedDeviceActivated:
                System.out.printf("[VRUtils] Device %d attached. Setting up render model.\n", event.trackedDeviceIndex);
                break;

            case JOpenVRLibrary.EVREventType.EVREventType_VREvent_TrackedDeviceDeactivated:
                System.out.printf("[VRUtils] Device %d detached.\n", event.trackedDeviceIndex);
                break;

            case JOpenVRLibrary.EVREventType.EVREventType_VREvent_TrackedDeviceUpdated:
                System.out.printf("[VRUtils] Device %d updated.\n", event.trackedDeviceIndex);
                break;
        }
    }
	
	
	
	
	
	
	
	
	
	

	public static Matrix4f[] getPoseMatrices() {
		return mat4DevicePose;
	}
	
	public static void renderControllers(){
		if(controllerModel == null) controllerModel = ObjLoader.loadRenderable3dModel(new File(Configuration.getAbsoluteInstallPath()+"/res/vr/controller.obj"), "res/vr/controller_texture.png", null, "res/vr/controller_spec.png");
		for(int i=0;i<trackedDevicePose.length;i++){
			if(trackedDevicePose[i].bPoseIsValid != 1) continue;
			switch(vrsystem.GetTrackedDeviceClass.apply(i)){
				case JOpenVRLibrary.ETrackedDeviceClass.ETrackedDeviceClass_TrackedDeviceClass_Controller:
					ShaderManager.shaderVR_loadTransformationMatrix(mat4DevicePose[i]);
					controllerModel.renderVR();
					break;
			}
		}
	}
	
	public static void renderBaseStations(){
		if(basestationModel == null) basestationModel = ObjLoader.loadRenderable3dModel(new File(Configuration.getAbsoluteInstallPath()+"/res/vr/basestation.obj"), "res/vr/basestation.tga", null, null);
		for(int i=0;i<trackedDevicePose.length;i++){
			if(trackedDevicePose[i].bPoseIsValid != 1) continue;
			switch(vrsystem.GetTrackedDeviceClass.apply(i)){
				case JOpenVRLibrary.ETrackedDeviceClass.ETrackedDeviceClass_TrackedDeviceClass_TrackingReference:
					ShaderManager.shaderVR_loadTransformationMatrix(mat4DevicePose[i]);
					basestationModel.renderVR();
					break;
			}
		}
	}
	
	public static void renderControllers3D(){
		if(controllerModel == null) controllerModel = ObjLoader.loadRenderable3dModel(new File(Configuration.getAbsoluteInstallPath()+"/res/vr/controller.obj"), "res/vr/controller_texture.png", null, "res/vr/controller_spec.png");
		for(int i=0;i<trackedDevicePose.length;i++){
			if(trackedDevicePose[i].bPoseIsValid != 1) continue;
			switch(vrsystem.GetTrackedDeviceClass.apply(i)){
				case JOpenVRLibrary.ETrackedDeviceClass.ETrackedDeviceClass_TrackedDeviceClass_Controller:
					ShaderManager.shader3d_loadTransformationMatrix(mat4DevicePose[i]);
					controllerModel.render();
					break;
			}
		}
	}
	
	public static void renderBaseStations3D(){
		if(basestationModel == null) basestationModel = ObjLoader.loadRenderable3dModel(new File(Configuration.getAbsoluteInstallPath()+"/res/vr/basestation.obj"), "res/vr/basestation.tga", null, null);
		for(int i=0;i<trackedDevicePose.length;i++){
			if(trackedDevicePose[i].bPoseIsValid != 1) continue;
			switch(vrsystem.GetTrackedDeviceClass.apply(i)){
				case JOpenVRLibrary.ETrackedDeviceClass.ETrackedDeviceClass_TrackedDeviceClass_TrackingReference:
					ShaderManager.shader3d_loadTransformationMatrix(mat4DevicePose[i]);
					basestationModel.render();
					break;
			}
		}
	}
	
    public static VRController[] getValidControllers(){
    	VRController[] validControllers = new VRController[nValidControllers];
    	int i=0;
    	for(VRController c:controllers) if(c.isValid()){
    		validControllers[i] = c;
    		i++;
    	}
    	return validControllers;
    }
    
    public static VRController[] getAllControllers(){
    	return controllers;
    }
    

	
	public static VR_IVRSystem_FnTable getVRSystem(){
		return vrsystem;
	}
	
	public static Renderable3dModel loadDeviceModel(int trackedDeviceIndex){
		
		if (vrsystem == null) {
            return null;
        }
		
		 String modelName = getTrackedDeviceString(trackedDeviceIndex, JOpenVRLibrary.ETrackedDeviceProperty.ETrackedDeviceProperty_Prop_RenderModelName_String);
		 
		 IntBuffer errorBuffer = IntBuffer.allocate(1);
         VR_IVRRenderModels_FnTable renderModels = new VR_IVRRenderModels_FnTable(
                 JOpenVRLibrary.VR_GetGenericInterface(JOpenVRLibrary.IVRRenderModels_Version, errorBuffer));
         
         if (errorBuffer.get(0) != JOpenVRLibrary.EVRRenderModelError.EVRRenderModelError_VRRenderModelError_None) {
             return null;
         }

         int error;
         PointerByReference modelPtr = new PointerByReference();
         while (true) {
        	 System.out.println("Pass");
             Pointer stringPointer = new Memory(modelName.length() + 1);
             stringPointer.setString(0, modelName);
             
             System.out.println("renderModels: "+renderModels);
             System.out.println("LoadRenderModel_Async: "+renderModels.LoadRenderModel_Async);

             error = renderModels.LoadRenderModel_Async.apply(stringPointer, modelPtr);

             if (error != JOpenVRLibrary.EVRRenderModelError.EVRRenderModelError_VRRenderModelError_Loading) {
                 break;
             }

             try {
                 Thread.sleep(1);
             } catch (InterruptedException ex) {
                 System.err.println("[VRUtils] [loadControllerModel]"+ex);
             }
         }
         
         RenderModel_t renderModel = new RenderModel_t(modelPtr.getValue());

         if (error != JOpenVRLibrary.EVRRenderModelError.EVRRenderModelError_VRRenderModelError_None) {
             System.err.println("Unable to load render model " + modelName + " - "
                     + renderModels.GetRenderModelErrorNameFromEnum.apply(error));
             return null; // move on to the next tracked device
         }
         
         PointerByReference texturePtr = new PointerByReference();

         while (true) {

             error = renderModels.LoadTexture_Async.apply(renderModel.diffuseTextureId, texturePtr);

             if (error != JOpenVRLibrary.EVRRenderModelError.EVRRenderModelError_VRRenderModelError_Loading) {
                 break;
             }

             try {
                 Thread.sleep(1);
             } catch (InterruptedException ex) {
            	 System.err.println("[VRUtils] [loadControllerModel]"+ex);
             }
         }
         RenderModel_TextureMap_t renderModelTexture = new RenderModel_TextureMap_t(texturePtr.getValue());

         if (error != JOpenVRLibrary.EVRRenderModelError.EVRRenderModelError_VRRenderModelError_None) {
             System.err.println("Unable to load render texture id " + renderModel.diffuseTextureId
                     + " for render model " + modelName);
             return null; // move on to the next tracked device
         }
         
         RenderModel_Vertex_t[] vertexList = (RenderModel_Vertex_t[]) renderModel.rVertexData.toArray(renderModel.unVertexCount);
         short[] elements = renderModel.rIndexData.getPointer().getShortArray(0, renderModel.unTriangleCount * 3);
         
         float[] vertices = new float[renderModel.unVertexCount*3];
         float[] normals = new float[renderModel.unVertexCount*3];
         float[] uvs = new float[renderModel.unVertexCount*2];
         int[] indices = new int[renderModel.unTriangleCount*3];
         
         for(int i=0;i<renderModel.unVertexCount;i++){
        	 vertices[i*3+0] = vertexList[i].vPosition.v[0];
        	 vertices[i*3+1] = vertexList[i].vPosition.v[1];
        	 vertices[i*3+2] = vertexList[i].vPosition.v[2];

        	 normals[i*3+0] = vertexList[i].vNormal.v[0];
        	 normals[i*3+1] = vertexList[i].vNormal.v[1];
        	 normals[i*3+2] = vertexList[i].vNormal.v[2];
        	 
        	 uvs[i*2+0] = vertexList[i].rfTextureCoord[0];
        	 uvs[i*2+1] = vertexList[i].rfTextureCoord[1];
         }
         
         for(int i=0;i<renderModel.unTriangleCount*3;i++){
        	 indices[i] = elements[i];
         }
         
         TextureDef texture = TextureDef.createTextureFromData(renderModelTexture.rubTextureMapData.getByteArray(0, renderModelTexture.unWidth * renderModelTexture.unHeight * 4 * Byte.BYTES), modelName, renderModelTexture.unWidth, renderModelTexture.unHeight);
         
         renderModels.FreeRenderModel.apply(renderModel);
         renderModels.FreeTexture.apply(renderModelTexture);
         
         return new Renderable3dModel(vertices, uvs, normals, ShapeHelper.calculateTangents(vertices, uvs), indices, texture, null, null);
		
	}
	
	private static String getTrackedDeviceString(int device, int prop) {
		hmdErrorStore.put(0, 0);
		int requiredBufferLen = vrsystem.GetStringTrackedDeviceProperty.apply(device, prop, Pointer.NULL, 0, hmdErrorStore);
		
		if (requiredBufferLen == 0) {
			return "";
		}
		
		Pointer stringPointer = new Memory(requiredBufferLen);
		vrsystem.GetStringTrackedDeviceProperty.apply(device, prop, stringPointer, requiredBufferLen, hmdErrorStore);
		
		return stringPointer.getString(0);
	}
}
