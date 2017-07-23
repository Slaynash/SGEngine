#version 120

uniform vec2 translation;
uniform vec2 screenSize;

void main(void){
	gl_Position = vec4((gl_Vertex.xy+translation)/screenSize*vec2(2,-2)-vec2(1,-1), 0.0, 1.0);
	gl_TexCoord[0] = gl_MultiTexCoord0;
}