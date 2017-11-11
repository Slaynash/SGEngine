#version 400 core

in vec2 pass_textureCoordinates;
in vec3 toLightVector[8];
in vec3 toCameraVector;
in vec3 tang_out;
in vec3 worldPos;

out vec4 out_Color;

uniform sampler2D textureDiffuse;
uniform sampler2D textureNormal;
uniform sampler2D textureSpecular;
uniform vec3 lightColour[8];
uniform vec3 attenuation[8];

uniform float shineDamper;
uniform float reflectivity;

uniform float far_plane;
uniform samplerCube lightShadows[8];

uniform vec3 lightPosition[8];

const float bias = 0.05;

float ShadowCalculation(int id)
{
    vec3 fragToLight = worldPos - lightPosition[id];
    float closestDepth = texture(lightShadows[id], fragToLight).r;
    closestDepth *= far_plane;
    float currentDepth = length(fragToLight);
    float shadow = currentDepth - bias > closestDepth ? 1.0 : 0.0;

    return shadow;
}

void main(void){
	
	vec4 normalMapTexture = 2.0 * texture(textureNormal, pass_textureCoordinates) - 1.0;
	
	vec3 unitNormal = normalize(normalMapTexture.rgb);
	vec3 unitVectorToCamera = normalize(toCameraVector);
	
	vec3 totalDiffuse = vec3(0.0);
	vec3 totalSpecular = vec3(0.0);
	
	for(int i=0;i<8;i++){
		if(length(lightColour[i]) < 0.0001) continue;
		float distance = length(toLightVector[i]);
		float attFactor = attenuation[i].x + (attenuation[i].y * distance) + (attenuation[i].z * distance * distance);
		vec3 unitLightVector = normalize(toLightVector[i]);	
		float nDotl = dot(unitNormal,unitLightVector);
		float brightness = max(nDotl,0.0);
		vec3 lightDirection = -unitLightVector;
		vec3 reflectedLightDirection = reflect(lightDirection,unitNormal);
		float specularFactor = dot(reflectedLightDirection , unitVectorToCamera);
		specularFactor = max(specularFactor,0.0);
		
		float shadow = ShadowCalculation(i);
		float dampedFactor = pow(specularFactor,shineDamper);
		totalDiffuse = totalDiffuse + (1.0 - shadow) * (brightness * lightColour[i])/attFactor;
		totalSpecular = totalSpecular + (1.0 - shadow) * (dampedFactor * reflectivity * lightColour[i])/attFactor;
		//totalDiffuse = totalDiffuse + (brightness * lightColour[i])/attFactor;
		//totalSpecular = totalSpecular + (dampedFactor * reflectivity * lightColour[i])/attFactor;
	}
	totalDiffuse = max(totalDiffuse, 0.0);
	
	vec4 textureColour = texture(textureDiffuse,pass_textureCoordinates);
	
	vec4 mapInfo = texture(textureSpecular, pass_textureCoordinates);
	totalSpecular *= mapInfo.r;
	if(mapInfo.g > 0.5){
		totalDiffuse = vec3(1.0);
	}
	
	out_Color = vec4(totalDiffuse,1.0) * textureColour + min(vec4(totalSpecular,1),0);
	
}