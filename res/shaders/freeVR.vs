#version 120

uniform mat4 mMatrix;
uniform mat4 vMatrix;
uniform mat4 pMatrix;

void main(){
	gl_Position = pMatrix * vMatrix * (mMatrix * gl_Vertex);
	gl_TexCoord[0] = gl_MultiTexCoord0;
}