package slaynash.opengl.shaders;

public abstract class ModernShader extends ShaderProgram {

	public ModernShader(String shaderPath, String vertexShaderName, String fragmentShaderName, int shaderType) {
		super(shaderPath, vertexShaderName, fragmentShaderName, shaderType);
	}

}
