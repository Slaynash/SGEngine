#version 150

in vec2 textureCoords;

out vec4 out_Colour;

uniform sampler2D colourTexture;
uniform sampler2D highlightTexture;

//uniform float exposure;
const float exposure = 1.0;

const float gamma = 1;

void main(void){
	vec3 sceneColour = texture(colourTexture, textureCoords).rgb;
	vec3 highlightColour = texture(highlightTexture, textureCoords).rgb;
	sceneColour += highlightColour;
	
	// tone mapping
	vec3 result = vec3(1.0) - exp(-sceneColour * exposure);
	// also gamma correct while we're at it       
	result = pow(result, vec3(1.0 / gamma));
	out_Colour = vec4(result, 1.0);
}