#version 120

uniform float visibility;
uniform sampler2D textureDiffuse;

void main(void){
	gl_FragColor = vec4(texture2D(textureDiffuse, gl_TexCoord[0].st).rgb, visibility);
}