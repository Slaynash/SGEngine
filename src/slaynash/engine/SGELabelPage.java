package slaynash.engine;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import slaynash.engine.objloader.ObjLoader;
import slaynash.opengl.Configuration;
import slaynash.opengl.shaders.ShaderManager;
import slaynash.opengl.textureUtils.TextureDef;
import slaynash.opengl.textureUtils.TextureManager;
import slaynash.opengl.utils.DisplayManager;
import slaynash.opengl.utils.MatrixUtils;
import slaynash.opengl.utils.RenderablePage;
import slaynash.opengl.utils.VRUtils;
import slaynash.opengl.utils.vr.VRController;
import slaynash.world3d.loader.PointLight;

public class SGELabelPage extends RenderablePage {
	
	private float startTime;
	private TextureDef background;
	private float elapsedTime;
	private boolean doneRendering = false;
	private Renderable2dModel backgroundModel;
	private Renderable3dModel vrplateform;
	private List<PointLight> lights = new ArrayList<PointLight>();
	
	private VRController mainHand;
	private PointLight controllerLight;
	private boolean check;

	@Override
	public void init() {
		ShaderManager.initLabelShader();
		if(Configuration.isVR()) ShaderManager.initVRShader();
		
		if(Configuration.getRenderMethod() == Configuration.RENDER_FREE){
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			GL11.glEnable(GL11.GL_BLEND);
		
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			GL11.glDisable(GL11.GL_DEPTH_TEST);
			GL11.glMatrixMode(GL11.GL_PROJECTION);
			GL11.glLoadIdentity();
			GL11.glOrtho(0, DisplayManager.getWidth(), DisplayManager.getHeight(), 0, 1, -1);
			GL11.glMatrixMode(GL11.GL_MODELVIEW);
			
		}
		else{
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		}
		
		background = TextureManager.getTextureDef("res/textures/menu/label.png", TextureManager.COLOR);
		
		if(Configuration.isVR()){
			vrplateform = ObjLoader.loadRenderable3dModel(new File(Configuration.getAbsoluteInstallPath()+"/res/models/label3d.obj"), "/res/models/label3d.png", null, null);
		}
		
		
		
		
		float[] vertices = new float[2*3*2];
		float[] textCoords = new float[2*3*2];
		
		
		
		
		
		float imageWidth = background.getTexture().getImageWidth();
		float imageHeight = background.getTexture().getImageHeight();
		
		float aspectRatio = DisplayManager.getWidth()/((float)DisplayManager.getHeight());
		float imgAspectRatio = imageWidth/imageHeight;
		
		float maxX = (imageWidth/(background.getTexture().getTextureWidth()));
		float maxY = (imageHeight/(background.getTexture().getTextureHeight()));
		float centerx = maxX*0.5f;
		float centery = maxY*0.5f;
		
		if(imgAspectRatio > aspectRatio){
			float hs = (aspectRatio/imgAspectRatio)/2*maxX;
			textCoords[0] = centerx-hs;
			textCoords[1] = 0;
			vertices[0] = 0;
			vertices[1] = 0;
				
			textCoords[2] = centerx+hs;
			textCoords[3] = 0;
			vertices[2] = Display.getWidth();
			vertices[3] = 0;
			
			textCoords[4] = centerx+hs;
			textCoords[5] = maxY;
			vertices[4] = Display.getWidth();
			vertices[5] = Display.getHeight();
			
			textCoords[6] = centerx+hs;
			textCoords[7] = maxY;
			vertices[6] = Display.getWidth();
			vertices[7] = Display.getHeight();
			
			textCoords[8] = centerx-hs;
			textCoords[9] = maxY;
			vertices[8] = 0;
			vertices[9] = Display.getHeight();
			
			textCoords[10] = centerx-hs;
			textCoords[11] = 0;
			vertices[10] = 0;
			vertices[11] = 0;
		}else{
			float hs = (imgAspectRatio/aspectRatio)/2*maxY;
			
			textCoords[0] = 0;
			textCoords[1] = centery-hs;
			vertices[0] = 0;
			vertices[1] = 0;
				
			textCoords[2] = maxX;
			textCoords[3] = centery-hs;
			vertices[2] = Display.getWidth();
			vertices[3] = 0;
			
			textCoords[4] = maxX;
			textCoords[5] = centery+hs;
			vertices[4] = Display.getWidth();
			vertices[5] = Display.getHeight();
			
			textCoords[6] = maxX;
			textCoords[7] = centery+hs;
			vertices[6] = Display.getWidth();
			vertices[7] = Display.getHeight();
			
			textCoords[8] = 0;
			textCoords[9] = centery+hs;
			vertices[8] = 0;
			vertices[9] = Display.getHeight();
			
			textCoords[10] = 0;
			textCoords[11] = centery-hs;
			vertices[10] = 0;
			vertices[11] = 0;
		}
		
		
		
		

		
		
		
		backgroundModel = new Renderable2dModel(vertices, textCoords, background);
		int err = 0; if((err = GL11.glGetError()) != 0) System.out.println("LabelInit 1: OpenGL Error "+err);
	}

	@Override
	public void start() {
		
		controllerLight = new PointLight(2, 1, 0, 0f, 1, 0f, 0f, 0f, 2f);
		lights.add(controllerLight);
		
		startTime = System.nanoTime()/1E9f;
	}

	@Override
	public void render() {
		
		if(Configuration.isVR()) VRUtils.setCurrentRenderEye(VRUtils.EYE_CENTER);
		
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		GL11.glClearColor(0, 0, 0, 1);
		
		if(Configuration.getRenderMethod() == Configuration.RENDER_FREE){
			GL11.glMatrixMode(GL11.GL_PROJECTION);
			GL11.glLoadIdentity();
			GL11.glOrtho(0, DisplayManager.getWidth(), DisplayManager.getHeight(), 0, 1, -1);
			GL11.glMatrixMode(GL11.GL_MODELVIEW);
			
			GL11.glLoadIdentity();
		}
		
		ShaderManager.startLabelShader();
		
		elapsedTime = (System.nanoTime()/1E9f)-startTime;
		if(Configuration.isVR()){//Default: 15s
			if(elapsedTime < 1)
				ShaderManager.shaderLabel_setVisibility(elapsedTime/1f);
			else if (elapsedTime < 2)
				ShaderManager.shaderLabel_setVisibility(1);
			else if (elapsedTime < 3){
				ShaderManager.shaderLabel_setVisibility((-elapsedTime+3)/1f);
			}
			else doneRendering = true;
		}
		else{
			if(elapsedTime < 1)
				ShaderManager.shaderLabel_setVisibility(elapsedTime/1f);
			else if (elapsedTime < 2)
				ShaderManager.shaderLabel_setVisibility(1);
			else if (elapsedTime < 3){
				ShaderManager.shaderLabel_setVisibility((-elapsedTime+3)/1f);
			}
			else doneRendering = true;
		}
		
		
		
		backgroundModel.render();
		
		int err; if((err = GL11.glGetError()) != 0) System.out.println("LabelRender: OpenGL Error "+err);
		ShaderManager.stopShader();
	}

	@Override
	public void renderVR() {

		if(!check){
			VRController[] controllers = VRUtils.getValidControllers();
			if(controllers.length != 0) mainHand = controllers[0];
			System.out.println(mainHand == null ? "[SMLabelPage] Controller not found" : "[SMLabelPage] Main hand is device "+mainHand.getId());
			check = true;
		}
		
		
		
		if(mainHand != null){
			Vector3f pos = new Vector3f(mainHand.getPose().m30, mainHand.getPose().m31, mainHand.getPose().m32);
			controllerLight.setPosition(pos);
		}
		
		ShaderManager.startVRShader();
		
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		
		renderEye(VRUtils.EYE_LEFT);
		renderEye(VRUtils.EYE_RIGHT);
		
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		
		int err = 0; if((err = GL11.glGetError()) != 0) System.out.println(err);
		ShaderManager.stopShader();
	}

	private void renderEye(int eye) {
		VRUtils.setCurrentRenderEye(eye);
		
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		GL11.glClearColor(0, 0, 0, 1);
		
		ShaderManager.shaderVR_loadViewMatrix(VRUtils.getViewMatrix(eye));
		ShaderManager.shaderVR_loadProjectionMatrix(VRUtils.getProjectionMatrix(eye));
		ShaderManager.shaderVR_loadTransformationMatrix(new Matrix4f());
		ShaderManager.shaderVR_loadLights(lights, VRUtils.getViewMatrix(eye));

		
		if(Configuration.getRenderMethod() == Configuration.RENDER_FREE){
	    	GL11.glLoadIdentity();
		}
		
		ShaderManager.shaderVR_loadTransformationMatrix(
				MatrixUtils.createTransformationMatrix(new Vector3f(0, 1.6f, -2f), 0, 0, 0, 1f)
		);
		
		vrplateform.renderVR();
		
		VRUtils.renderBaseStations();
		VRUtils.renderControllers();
	}

	@Override
	public void stop() {
		
	}

	@Override
	public void resize() {
		
	}
	
	public boolean isRenderingDone(){
		return doneRendering;
	}
}
