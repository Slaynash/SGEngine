#version 120

uniform mat4 mMatrix;
uniform mat4 vMatrix;
uniform mat4 pMatrix;
uniform float zoom;
uniform float displayRatio:

void main(void){
	gl_Position = pMatrix * vMatrix * (mMatrix * vec4(gl_Vertex.xy * vec2(zoom, zoom*displayRatio), gl_Vertex.z, 1.0))
	gl_TexCoord[0] = gl_MultiTexCoord0;
}