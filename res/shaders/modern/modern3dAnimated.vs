#version 400 core

const int MAX_JOINTS = 50;//max joints allowed in a skeleton
const int MAX_WEIGHTS = 3;//max number of joints that can affect a vertex

in vec3 position;
in vec2 textureCoordinates;
in vec3 normal;
in vec3 tangent;
in ivec3 jointIndices;
in vec3 weights;

out vec2 pass_textureCoordinates;
out vec3 toLightVector[8];
out vec3 toCameraVector;
out vec3 tang_out;
out vec3 norm_out;
out vec3 worldPos;

uniform mat4 mMatrix;
uniform mat4 vMatrix;
uniform mat4 pMatrix;

uniform vec3 lightPosition[8];
uniform mat4 jointTransforms[MAX_JOINTS];

void main(void){

	vec4 totalLocalPos = vec4(0.0);
	vec4 totalNormal = vec4(0.0);
	vec4 totalTangent = vec4(0.0);
	
	
	for(int i=0;i<MAX_WEIGHTS;i++){
		mat4 jointTransform = jointTransforms[jointIndices[i]];
		vec4 posePosition = jointTransform * vec4(position, 1.0);
		totalLocalPos += posePosition * weights[i];
		
		vec4 worldNormal = jointTransform * vec4(normal, 0.0);
		totalNormal += worldNormal * weights[i];
		
		vec4 worldTangent = jointTransform * vec4(tangent, 0.0);
		totalTangent += worldNormal * weights[i];
	}
	
	

	vec4 worldPosition = mMatrix * totalLocalPos;
	worldPos = vec3(worldPosition);
	mat4 modelViewMatrix = vMatrix * mMatrix;
	vec4 positionRelativeToCam = modelViewMatrix * totalLocalPos;
	gl_Position = pMatrix * positionRelativeToCam;
	
	
	pass_textureCoordinates = textureCoordinates;
	
	vec3 surfaceNormal = (modelViewMatrix * totalNormal).xyz;
	
	vec3 norm = normalize(surfaceNormal);
	
	vec3 tang = normalize((modelViewMatrix * totalTangent).xyz);
	vec3 bitang = normalize(cross(norm, tang));
	
	
	tang_out = tangent;
	norm_out = norm;
	
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