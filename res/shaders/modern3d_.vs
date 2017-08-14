#version 400 core

in vec3 position;
in vec2 textureCoordinates;
in vec3 normal;
in vec3 tangent;

out vec2 pass_textureCoordinates_geom;
out vec3 pass_normal;
out vec3 pass_tangent;

void main(void){
	gl_Position = vec4(position, 1.0);
	pass_textureCoordinates_geom = textureCoordinates;
	pass_normal = normal;
	pass_tangent = tangent;
}