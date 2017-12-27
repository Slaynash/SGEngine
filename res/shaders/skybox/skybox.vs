#version 400

in vec3 position;
out vec3 textureCoords;

uniform mat4 pMatrix;
uniform mat4 vMatrix;

void main(void){
	
	gl_Position = pMatrix * vMatrix * vec4(position, 1.0); 
	textureCoords = position;
	
}