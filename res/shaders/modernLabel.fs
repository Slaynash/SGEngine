#version 140

in vec2 textureCoords;

out vec4 out_Color;

uniform sampler2D textureDiffuse;
uniform float visibility;

void main(void){
	
	out_Color = texture(textureDiffuse,textureCoords) * vec4(1,1,1,visibility);

}