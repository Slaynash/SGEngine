#version 400 core

in vec2 position;
in vec2 textureCoordinates;

out vec2 textureCoords;

uniform mat4 mMatrix;
uniform mat4 vMatrix;
uniform mat4 pMatrix;
uniform float textmode;

uniform vec2 translation;

void main(){
	mat4 modelViewMatrix = vMatrix * mMatrix;
	if(textmode < 0.5){
		gl_Position = pMatrix * modelViewMatrix * vec4((position+translation), 0.0, 1.0);
	}
	else{
		gl_Position = pMatrix * modelViewMatrix * vec4((position+translation)*vec2(1,-1), 0.0, 1.0);
	}
	textureCoords = textureCoordinates;
}