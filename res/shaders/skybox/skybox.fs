#version 400

in vec3 textureCoords;
out vec4 out_Color;

uniform float transition;

uniform samplerCube textureDiffuse1;
uniform samplerCube textureDiffuse2;

void main(void){
    out_Color = mix(texture(textureDiffuse1, textureCoords), texture(textureDiffuse2, textureCoords), transition);
}