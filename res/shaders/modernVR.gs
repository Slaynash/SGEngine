#version 400 core

layout(triangles) in;
layout(triangle_strip, max_vertices = 3) out;

in vec2 pass_textureCoordinates_geom[];
in vec3 pass_normal[];
in vec3 pass_tangent[];

out vec3 toLightVector[8];
out vec3 toCameraVector;
out vec3 tang_out;
out vec2 pass_textureCoordinates;

uniform mat4 mMatrix;
uniform mat4 vMatrix;
uniform mat4 pMatrix;

uniform vec3 lightPosition[8];

void main() {
 	for(int i = 0; i < 3; i++) { // You used triangles, so it's always 3
		vec4 position = gl_in[i].gl_Position;
		
		
		
		vec4 worldPosition = mMatrix * position;
		mat4 modelViewMatrix = vMatrix * mMatrix;
		vec4 positionRelativeToCam = modelViewMatrix * position;
		gl_Position = pMatrix * positionRelativeToCam;
		
		
		pass_textureCoordinates = pass_textureCoordinates_geom[i];
		
		vec3 surfaceNormal = (modelViewMatrix * vec4(pass_normal[i],0.0)).xyz;
		
		vec3 norm = normalize(surfaceNormal);
		
		vec3 tang = normalize((modelViewMatrix * vec4(pass_tangent[i], 0.0)).xyz);
		vec3 bitang = normalize(cross(norm, tang));
		
		
		tang_out = pass_tangent[i];
		
		mat3 toTangentSpace = mat3(
			tang.x, bitang.x, norm.x,
			tang.y, bitang.y, norm.y,
			tang.z, bitang.z, norm.z
		);
		
		for(int i=0;i<8;i++){
			toLightVector[i] = toTangentSpace * (lightPosition[i] - positionRelativeToCam.xyz);
		}
		toCameraVector = toTangentSpace * (-positionRelativeToCam.xyz);
		
		EmitVertex();
	}
	EndPrimitive();
}