package slaynash.sgengine.deferredRender;

import java.util.ArrayList;
import java.util.Map;

import slaynash.sgengine.shaders.ShaderProgram;

public class ShaderRenderlist {

	private ShaderProgram shader;
	private Map<Integer, ArrayList<DeferredModelRenderer>> objectMap;

	public ShaderRenderlist(ShaderProgram shader, Map<Integer, ArrayList<DeferredModelRenderer>> objectMap) {
		this.shader = shader;
		this.objectMap = objectMap;
	}

	public ShaderProgram getShader() {
		return shader;
	}

	public Map<Integer, ArrayList<DeferredModelRenderer>> getObjectList() {
		return objectMap;
	}

}
