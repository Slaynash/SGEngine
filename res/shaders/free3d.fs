#version 120

uniform sampler2D textureDiffuse;

void main(void){
	gl_FragColor = texture2D(textureDiffuse, gl_TexCoord[0].st);
}