#version 120

uniform float visibility;
uniform sampler2D texture;

void main(void){
	gl_FragColor = vec4(texture2D(texture, gl_TexCoord[0].st).rgb, visibility);
}