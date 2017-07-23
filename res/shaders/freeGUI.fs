#version 120

uniform vec3 colour;
uniform sampler2D textureDiffuse;

const float width = 0.5;
const float edge = 0.1;

const float borderWidth = 0.6;
const float borderEdge = 0.2;

const vec3 outlineColour = vec3(0.0, 0.0, 0.0);

uniform float textmode;
uniform float combomode;
uniform float invertColor;
uniform vec2 cbCuts;

void main(void){
	
	if(combomode > 0.5){
		vec4 p = gl_FragCoord;
		if(p.y > cbCuts.x){
			discard;
		}
		if(p.y < cbCuts.y){
			discard;
		}
	}
	
	if(textmode > 0.5){
		float distance = 1.0 - texture2D(textureDiffuse, gl_TexCoord[0].st).a;
		float alpha = 1.0 - smoothstep(width, width + edge, distance);
		
		float distance2 = 1.0 - texture2D(textureDiffuse, gl_TexCoord[0].st).a;
		float outlineAlpha = 1.0 - smoothstep(borderWidth, borderWidth + borderEdge, distance2);
		
		float overallAlpha = alpha + (1.0 - alpha) * outlineAlpha;
		vec3 overallColour = mix(outlineColour, colour, alpha / overallAlpha);
		
		gl_FragColor = vec4(overallColour, overallAlpha);
	}
	else{
		gl_FragColor = texture2D(textureDiffuse, gl_TexCoord[0].st);
	}
	if(invertColor > 0.5){
		gl_FragColor *= -1;
		gl_FragColor += vec4(1,1,1,1);
	}
	
}