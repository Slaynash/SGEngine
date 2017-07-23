#version 140

in vec2 position;
in vec2 textureCoordinates;

out vec2 textureCoords;

uniform vec2 screenSize;

void main(void){

	gl_Position = vec4(position/screenSize*vec2(2,-2)-vec2(1,-1), 0.0, 1.0);
	textureCoords = textureCoordinates;
}