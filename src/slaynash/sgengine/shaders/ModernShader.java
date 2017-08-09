package slaynash.sgengine.shaders;

public abstract class ModernShader extends ShaderProgram {

	public ModernShader(String shaderPath, String vertexShaderName, String fragmentShaderName, int shaderType) {
		super(shaderPath, vertexShaderName, fragmentShaderName, shaderType);
	}
	
	public ModernShader(String shaderPath, String vertexShaderName, String fragmentShaderName, String geometryShaderName, int shaderType) {
		super(shaderPath, vertexShaderName, fragmentShaderName, geometryShaderName, shaderType);
	}

}
