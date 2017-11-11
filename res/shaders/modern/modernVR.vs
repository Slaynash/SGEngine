#version 400 core

in vec3 position;
in vec2 textureCoordinates;
in vec3 normal;
in vec3 tangent;

out vec2 pass_textureCoordinates;
out vec3 toLightVector[8];
out vec3 toCameraVector;
out vec3 tang_out;
out vec3 worldPos;

uniform mat4 mMatrix;
uniform mat4 vMatrix;
uniform mat4 pMatrix;

uniform vec3 lightPosition[8];

void main(void){
	vec4 worldPosition = mMatrix * vec4(position,1.0);
	worldPos = vec3(worldPosition);
	mat4 modelViewMatrix = vMatrix * mMatrix;
	vec4 positionRelativeToCam = modelViewMatrix * vec4(position,1.0);
	gl_Position = pMatrix * positionRelativeToCam;
	
	
	pass_textureCoordinates = textureCoordinates;
	
	vec3 surfaceNormal = (modelViewMatrix * vec4(normal,0.0)).xyz;
	
	vec3 norm = normalize(surfaceNormal);
	
	vec3 tang = normalize((modelViewMatrix * vec4(tangent, 0.0)).xyz);
	vec3 bitang = normalize(cross(norm, tang));
	
	
	tang_out = tangent;
	
	mat3 toTangentSpace = mat3(
		tang.x, bitang.x, norm.x,
		tang.y, bitang.y, norm.y,
		tang.z, bitang.z, norm.z
	);
	
	for(int i=0;i<8;i++){
		toLightVector[i] = toTangentSpace * ( (vMatrix*vec4(lightPosition[i],1.0)).xyz - positionRelativeToCam.xyz );
	}
	toCameraVector = toTangentSpace * (-positionRelativeToCam.xyz);
}