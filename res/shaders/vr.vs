#version 120

uniform mat4 mvpMatrix;
uniform mat4 mMatrix;

void main(){
	gl_Position = mvpMatrix * (mMatrix * gl_Vertex);
	gl_TexCoord[0] = gl_MultiTexCoord0;
}