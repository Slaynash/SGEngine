#version 120

uniform float renderMode;

uniform mat4 mvpMatrix;
uniform mat4 mMatrix;

void main(void){
	if(renderMode < 0.5){
		gl_Position = ftransform();
		gl_TexCoord[0] = gl_MultiTexCoord0;
	}
	else if(renderMode < 1.5){
		gl_Position = ftransform();
		gl_TexCoord[0] = gl_MultiTexCoord0;
	}
	else if(renderMode < 2.5){
  	 	gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;
		gl_TexCoord[0] = gl_MultiTexCoord0;
	}
	else{
		gl_Position = mvpMatrix * (mMatrix * gl_Vertex);
		gl_TexCoord[0] = gl_MultiTexCoord0;
	}
}