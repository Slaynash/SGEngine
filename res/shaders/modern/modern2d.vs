#version 140

in vec2 position;
in vec2 textureCoordinates;

out vec2 textureCoords;

uniform mat4 mMatrix;
uniform mat4 vMatrix;
uniform mat4 pMatrix;
uniform float zoom;
uniform float displayRatio;

void main(void){
	gl_Position = pMatrix * vMatrix * (mMatrix * vec4(position.xy, 0.0, 1.0)) * vec4(zoom,zoom*displayRatio,1,1);
	textureCoords = textureCoordinates;
}