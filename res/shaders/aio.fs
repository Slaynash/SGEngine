#version 120

uniform vec3 colour;

uniform float textmode;
uniform float combomode;
uniform float invertColor;
uniform vec2 cbCuts;
uniform float visibility;

uniform float renderMode;


uniform sampler2D texture;
uniform sampler2D textureNormal;
uniform sampler2D textureSpecular;
uniform float specularFactor;

const float width = 0.5;
const float edge = 0.1;

const float borderWidth = 0.8;
const float borderEdge = 0.2;

const vec2 offset = vec2(0, 0);

const vec3 outlineColour = vec3(1.0, 0.0, 0.0);










void main(void){
	if(renderMode < 0.5){
		gl_FragColor = vec4(texture2D(texture, gl_TexCoord[0].st).rgb, visibility);
	}
	else if(renderMode < 1.5){
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
			gl_FragColor = vec4(1,0,0,1);
			float distance = 1.0 - texture2D(texture, gl_TexCoord[0].st).a;
			float alpha = 1.0 - smoothstep(width, width + edge, distance);
			
			float distance2 = 1.0 - texture2D(texture, gl_TexCoord[0].st).a;
			float outlineAlpha = 1.0 - smoothstep(borderWidth, borderWidth + borderEdge, distance2);
			
			float overallAlpha = alpha + (1.0 - alpha) * outlineAlpha;
			vec3 overallColour = mix(outlineColour, colour, alpha / overallAlpha);
			
			gl_FragColor = vec4(overallColour, overallAlpha);
			//if(gl_FragColor.rgb != overallColour){
			//	discard;
			//}
		}
		else{
			gl_FragColor = texture2D(texture, gl_TexCoord[0].st);
			//gl_FragColor = vec4(1,0,0,1);
		}
		if(invertColor > 0.5){
			gl_FragColor *= -1;
			gl_FragColor += vec4(1,1,1,1);
		}
	}
	else if(renderMode < 2.5){
		gl_FragColor = texture2D(texture, gl_TexCoord[0].st);
	}
	else{
		gl_FragColor = texture2D(texture, gl_TexCoord[0].st);
	}
}