#version 400 core

in vec2 textureCoords;

out vec4 out_Color;

uniform vec3 colour;
uniform sampler2D textureDiffuse;
uniform float textmode;

const float width = 0.5;
const float edge = 0.1;

const float borderWidth = 0.0;
const float borderEdge = 0.2;

const vec3 outlineColour = vec3(0.0, 0.0, 0.0);

void main(void){
	
	if(textmode > 0.5){
		float distance = 1.0 - texture(textureDiffuse, textureCoords).a;
		float alpha = 1.0 - smoothstep(width, width + edge, distance);
		
		float distance2 = 1.0 - texture(textureDiffuse, textureCoords).a;
		float outlineAlpha = 1.0 - smoothstep(borderWidth, borderWidth + borderEdge, distance2);
		
		float overallAlpha = alpha + (1.0 - alpha) * outlineAlpha;
		vec3 overallColour = mix(outlineColour, colour, alpha / overallAlpha);
		
		vec4 colorOut = vec4(overallColour, overallAlpha);
		
		if(colorOut.a <= 0) discard;
		
		out_Color = colorOut;
	}
	else{
		out_Color = texture(textureDiffuse, textureCoords);
	}
}