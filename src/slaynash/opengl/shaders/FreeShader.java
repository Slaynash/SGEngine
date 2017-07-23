package slaynash.opengl.shaders;

public abstract class FreeShader extends ShaderProgram{

	public FreeShader(String shaderPath, String vertexShaderName, String fragmentShaderName, int shaderType) {
		super(shaderPath, vertexShaderName, fragmentShaderName, shaderType);
	}

}
