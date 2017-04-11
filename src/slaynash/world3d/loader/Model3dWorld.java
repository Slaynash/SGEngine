package slaynash.world3d.loader;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

import slaynash.opengl.shaders.ShaderManager;

public class Model3dWorld extends Entity{
	
	private TriangleFace[] faces;

	public Model3dWorld(TriangleFace[] faces) {
		super();
		this.faces = faces;
		setPosition(new Vector3f(2, 0, -10));
	}

	@Override
	public void update() {
		
	}

	@Override
	public void render() {
		GL11.glTranslatef(getPosition().x, getPosition().y, getPosition().z);
		GL11.glRotatef(getAngle().x, 1, 0, 0);
		GL11.glRotatef(getAngle().y, 0, 1, 0);
		GL11.glRotatef(getAngle().z, 0, 0, 1);
		//System.out.println("rendering "+faces.length+" faces");
		for(TriangleFace f:faces){
			float[] vs = f.getVertices();
			float[] vns = f.getNormals();
			float[] uvs = f.getUVs();
			if(f.getTextureColorID() != 0)
				ShaderManager.bind3DShaderColorTextureID(f.getTextureColorID());
			else
				ShaderManager.bind3DShaderDefaultColorTexture();
			
			if(f.getTextureNormalID() != 0)
				ShaderManager.bind3DShaderNormalTextureID(f.getTextureNormalID());
			else
				ShaderManager.bind3DShaderDefaultNormalTexture();
			
			if(f.getTextureSpecularID() != 0)
				ShaderManager.bind3DShaderSpecularTextureID(f.getTextureSpecularID());
			else
				ShaderManager.bind3DShaderDefaultSpecularTexture();
			
			ShaderManager.bind3DSpecularFactor(f.getSpecularFactor());
			//System.out.println("rendering face "+(i++));
			GL11.glBegin(GL11.GL_TRIANGLES);
			
			GL11.glTexCoord2f(uvs[0], uvs[1]);
			GL11.glNormal3f(vns[0], vns[1], vns[2]);
			GL11.glVertex3f(vs[0], vs[1], vs[2]);

			GL11.glTexCoord2f(uvs[2], uvs[3]);
			GL11.glNormal3f(vns[3], vns[4], vns[5]);
			GL11.glVertex3f(vs[3], vs[4], vs[5]);
			
			GL11.glTexCoord2f(uvs[4], uvs[5]);
			GL11.glNormal3f(vns[6], vns[7], vns[8]);
			GL11.glVertex3f(vs[6], vs[7], vs[8]);
			
			GL11.glEnd();
		}
		GL11.glRotatef(-getAngle().x, 1, 0, 0);
		GL11.glRotatef(-getAngle().y, 0, 1, 0);
		GL11.glRotatef(-getAngle().z, 0, 0, 1);
		GL11.glTranslatef(-getPosition().x, -getPosition().y, -getPosition().z);
	}
	


	@Override
	public void renderVR() {
		GL11.glTranslatef(getPosition().x, getPosition().y, getPosition().z);
		GL11.glRotatef(getAngle().x, 1, 0, 0);
		GL11.glRotatef(getAngle().y, 0, 1, 0);
		GL11.glRotatef(getAngle().z, 0, 0, 1);
		//System.out.println("rendering "+faces.length+" faces");
		for(TriangleFace f:faces){
			float[] vs = f.getVertices();
			float[] vns = f.getNormals();
			float[] uvs = f.getUVs();
			if(f.getTextureColorID() != 0)
				ShaderManager.bindVRShaderColorTextureID(f.getTextureColorID());
			else
				ShaderManager.bindVRShaderDefaultColorTexture();
			
			if(f.getTextureNormalID() != 0)
				ShaderManager.bindVRShaderNormalTextureID(f.getTextureNormalID());
			else
				ShaderManager.bindVRShaderDefaultNormalTexture();
			
			if(f.getTextureSpecularID() != 0)
				ShaderManager.bindVRShaderSpecularTextureID(f.getTextureSpecularID());
			else
				ShaderManager.bindVRShaderDefaultSpecularTexture();
			
			ShaderManager.bindVRSpecularFactor(f.getSpecularFactor());
			//System.out.println("rendering face "+(i++));
			GL11.glBegin(GL11.GL_TRIANGLES);
			
			GL11.glTexCoord2f(uvs[0], uvs[1]);
			GL11.glNormal3f(vns[0], vns[1], vns[2]);
			GL11.glVertex3f(vs[0], vs[1], vs[2]);

			GL11.glTexCoord2f(uvs[2], uvs[3]);
			GL11.glNormal3f(vns[3], vns[4], vns[5]);
			GL11.glVertex3f(vs[3], vs[4], vs[5]);
			
			GL11.glTexCoord2f(uvs[4], uvs[5]);
			GL11.glNormal3f(vns[6], vns[7], vns[8]);
			GL11.glVertex3f(vs[6], vs[7], vs[8]);
			
			GL11.glEnd();
		}
		GL11.glRotatef(-getAngle().x, 1, 0, 0);
		GL11.glRotatef(-getAngle().y, 0, 1, 0);
		GL11.glRotatef(-getAngle().z, 0, 0, 1);
		GL11.glTranslatef(-getPosition().x, -getPosition().y, -getPosition().z);
	}

	public TriangleFace[] getFaces() {
		return faces;
	}

}
