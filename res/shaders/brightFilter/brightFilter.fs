#version 150

in vec2 textureCoords;

out vec4 out_Colour;

uniform sampler2D colourTexture;

void main(void){
	
	vec4 colour = texture(colourTexture, textureCoords);
	float brightness = dot(colour.rgb, vec3(0.2126, 0.7152, 0.0722));
	if(brightness > 1.0){
		out_Colour = colour - 1.0;
	}
	else{
		out_Colour = vec4(0.0);
	}
	
}