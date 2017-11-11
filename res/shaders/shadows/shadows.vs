#version 330 core

in vec3 position;

uniform mat4 mMatrix;

void main()
{
    gl_Position = mMatrix * vec4(position, 1.0);
} 