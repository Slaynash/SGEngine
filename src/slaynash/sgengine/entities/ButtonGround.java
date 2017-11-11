package slaynash.sgengine.entities;

import java.util.List;

import org.lwjgl.util.vector.Vector3f;

import slaynash.sgengine.models.Renderable3dModel;
import slaynash.sgengine.models.utils.ModelManager;
import slaynash.sgengine.shaders.ShaderManager;
import slaynash.sgengine.utils.DisplayManager;
import slaynash.sgengine.utils.MatrixUtils;
import slaynash.sgengine.world3d.entities.InteractableObject;
import slaynash.sgengine.world3d.entities.Interaction;

public class ButtonGround extends Entity implements InteractableObject {

	private List<Interaction> interactions;
	private Vector3f position3d = new Vector3f();
	
	float buttonHeight = 0f;
	
	private Renderable3dModel border;
	private Renderable3dModel button;
	private boolean pressed;

	public ButtonGround(String id, float posX, float posY, float posZ, float angX, float angY, float angZ, List<Interaction> interactions) {
		super(id);
		this.interactions = interactions;
		setPosition(posX, posY, posZ);
		setRotation(angX, angY, angZ);
		border = ModelManager.loadObj("res/models/button_ground_border.obj", "res/textures/objects/button_ground_border.png", null, null);
		button = ModelManager.loadObj("res/models/button_ground_button.obj", "res/textures/objects/button_ground_button.png", null, null);
	}

	@Override
	public void update() {
		if(pressed) {
			if(buttonHeight > -0.03) buttonHeight -= 0.1f * DisplayManager.getFrameTimeSeconds();
			if(buttonHeight < -0.03) buttonHeight = -0.03f;
		}
		else {
			if(buttonHeight < 0) buttonHeight += 0.1f * DisplayManager.getFrameTimeSeconds();
			if(buttonHeight > 0) buttonHeight = 0f;
		}
	}

	@Override
	public void render() {
		ShaderManager.shader_loadTransformationMatrix(MatrixUtils.createTransformationMatrix(position3d, getAngX(), getAngY(), getAngZ(), 1));
		border.render();
		ShaderManager.shader_loadTransformationMatrix(MatrixUtils.createTransformationMatrix(new Vector3f(getPosX(), getPosY()+buttonHeight, getPosZ()), getAngX(), getAngY(), getAngZ(), 1));
		button.render();
	}

	@Override
	public void renderVR(int eye) {
		ShaderManager.shader_loadTransformationMatrix(MatrixUtils.createTransformationMatrix(position3d, getAngX(), getAngY(), getAngZ(), 1));
		border.renderVR(eye);
		ShaderManager.shader_loadTransformationMatrix(MatrixUtils.createTransformationMatrix(new Vector3f(getPosX(), getPosY()+buttonHeight, getPosZ()), getAngX(), getAngY(), getAngZ(), 1));
		button.renderVR(eye);
	}

	@Override
	public void destroy() {}

	@Override
	public void onAddedToWorldManager() {}

	@Override
	public void addInteractions(List<Interaction> interactions) {
		this.interactions.addAll(interactions);
	}

	@Override
	public void onInput(String command, String... args) {
		if(command.equals("setPressed")) {
			if(args[0].equals("true")) pressed = true;
			else if(args[0].equals("false")) pressed = false;
		}
	}
	
	@Override
	public void setPosition(float posX, float posY, float posZ) {
		super.setPosition(posX, posY, posZ);
		position3d.x = posX;
		position3d.y = posY;
		position3d.z = posZ;
	}

}
