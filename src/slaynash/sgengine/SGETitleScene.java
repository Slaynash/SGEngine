package slaynash.sgengine;

import org.lwjgl.Sys;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import slaynash.sgengine.gui.GUIManager;
import slaynash.sgengine.gui.GUIText;
import slaynash.sgengine.models.Renderable2dModel;
import slaynash.sgengine.models.Renderable3dModel;
import slaynash.sgengine.models.utils.ModelManager;
import slaynash.sgengine.models.utils.VaoManager;
import slaynash.sgengine.shaders.ShaderManager;
import slaynash.sgengine.textureUtils.TextureDef;
import slaynash.sgengine.textureUtils.TextureManager;
import slaynash.sgengine.utils.DisplayManager;
import slaynash.sgengine.utils.MatrixUtils;
import slaynash.sgengine.utils.Scene;
import slaynash.sgengine.utils.VRUtils;
import slaynash.sgengine.utils.vr.VRController;
import slaynash.sgengine.world3d.loader.Ent_PointLight;

public class SGETitleScene extends Scene {
	
	private float startTime;
	private TextureDef background;
	private float elapsedTime;
	private boolean doneRendering = false;
	private Renderable2dModel backgroundModel;
	private Renderable3dModel vrplateform;
	
	private VRController mainHand;
	private Ent_PointLight controllerLight;
	private boolean check;
	

	private GUIText fpsText;
	private int fpsCount = 0;
	private long lastFPS = 0;

	@Override
	public void init() {
		ShaderManager.initLabelShader();
		if(Configuration.isVR()) ShaderManager.initVRShader();
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		
		background = TextureManager.getTextureDef("res/textures/menu/label.png", TextureManager.TEXTURE_DIFFUSE);
		
		if(Configuration.isVR()){
			vrplateform = ModelManager.loadObj("res/models/label3d.obj", "/res/models/label3d.png", null, "/res/textures/fullbright.png");
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
		
		
		
		

		
		
		
		backgroundModel = new Renderable2dModel(VaoManager.loadToVao2d(vertices, textCoords), background);
		int err = 0; if((err = GL11.glGetError()) != 0) LogSystem.out_println("LabelInit 1: OpenGL Error "+err);
	}

	@Override
	public void start() {
		
		controllerLight = new Ent_PointLight("l1", 2, 1, 0, 0f, 1, 0f, 0f, 0f, 2f);
		//LightsManager.addPointlight(controllerLight);
		
		startTime = System.nanoTime()/1E9f;

		fpsText = new GUIText("FPS: ?", 5, 5, 400, null, GUIManager.ELEMENT_GAME);
		lastFPS = (Sys.getTime() * 1000) / Sys.getTimerResolution();
	}
	
	@Override
	public void update() {
		updateFPS();
		
		if(Configuration.isVR()){
			if(!check){
				VRController[] controllers = VRUtils.getValidControllers();
				if(controllers.length != 0) mainHand = controllers[0];
				LogSystem.out_println(mainHand == null ? "[SMLabelPage] Controller not found" : "[SMLabelPage] Main hand is device "+mainHand.getId());
				check = true;
			}
			
			if(mainHand != null){
				Vector3f pos = new Vector3f(mainHand.getPose().m30, mainHand.getPose().m31, mainHand.getPose().m32);
				controllerLight.setPosition(pos.x, pos.y, pos.z);
			}
		}
	}

	private void updateFPS() {
		long ct = (Sys.getTime() * 1000) / Sys.getTimerResolution();
		
		if (ct - lastFPS > 1000) {
			fpsText.setText("FPS: "+fpsCount);
			fpsCount = 0;
			lastFPS += 1000;
		}
		fpsCount++;
		
	}

	@Override
	public void render() {
		
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		GL11.glClearColor(0, 0, 0, 1);
		
		ShaderManager.startLabelShader();
		
		elapsedTime = (System.nanoTime()/1E9f)-startTime;
		if(Configuration.isVR()){//Default: 15s
			if(elapsedTime < 1)
				ShaderManager.shader_setVisibility(elapsedTime/1f);
			else if (elapsedTime < 9)
				ShaderManager.shader_setVisibility(1);
			else if (elapsedTime < 10){
				ShaderManager.shader_setVisibility((-elapsedTime+10)/1f);
			}
			else doneRendering = true;
		}
		else{
			if(elapsedTime < 1)
				ShaderManager.shader_setVisibility(elapsedTime/1f);
			else if (elapsedTime < 2)
				ShaderManager.shader_setVisibility(1);
			else if (elapsedTime < 3){
				ShaderManager.shader_setVisibility((-elapsedTime+3)/1f);
			}
			else doneRendering = true;
		}
		
		
		
		backgroundModel.render();
		
		int err; if((err = GL11.glGetError()) != 0) LogSystem.out_println("LabelRender: OpenGL Error "+err);
		ShaderManager.stopShader();
	}

	@Override
	public void renderVR(int eye) {
		
		ShaderManager.startVRShader();
		
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		GL11.glClearColor(0, 0, 0, 1);
		
		ShaderManager.shader_loadViewMatrix(VRUtils.getViewMatrix(eye));
		ShaderManager.shader_loadProjectionMatrix(VRUtils.getProjectionMatrix(eye));
		ShaderManager.shader_loadTransformationMatrix(new Matrix4f());
		//ShaderManager.shader_loadLights(lights);
		
		ShaderManager.shader_loadTransformationMatrix(
				MatrixUtils.createTransformationMatrix(new Vector3f(0, 1.6f, -2f), 0, 0, 0, 1f)
		);
		
		vrplateform.renderVR(eye);
		
		VRUtils.renderBaseStations(eye);
		VRUtils.renderControllers(eye);
		
		
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		
		int err = 0; if((err = GL11.glGetError()) != 0) LogSystem.out_println(err);
		ShaderManager.stopShader();
	}

	@Override
	public void stop() {
		//LightsManager.removePointlight(controllerLight);
	}

	@Override
	public void resize() {
		
	}
	
	public boolean isRenderingDone(){
		return doneRendering;
	}
}
