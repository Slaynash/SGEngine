package slaynash.engine;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import slaynash.opengl.Configuration;
import slaynash.opengl.shaders.ShaderManager;
import slaynash.opengl.textureUtils.TextureDef;
import slaynash.opengl.textureUtils.TextureManager;
import slaynash.opengl.utils.VAO;
import slaynash.opengl.utils.VOLoader;

public class Renderable3dModel extends RenderableModel {

	private int listId = 0;
	private TextureDef textureColor;
	private TextureDef textureNormal;
	private TextureDef textureSpecular;
	private VAO vao;
	private boolean isIndexed = false;
	
	public Renderable3dModel(float[] vertices, float[] textureCoords, float[] normals, float[] tangents, TextureDef textureColor, TextureDef textureNormal, TextureDef textureSpecular){
		isIndexed = true;
		this.textureColor = textureColor != null ? textureColor : TextureManager.getDefaultTexture();
		this.textureNormal = textureNormal != null ? textureNormal : TextureManager.getDefaultNormalTexture();
		this.textureSpecular = textureSpecular != null ? textureSpecular : TextureManager.getDefaultSpecularTexture();
		
		if(Configuration.getRenderMethod() == Configuration.RENDER_FREE){
			listId = GL11.glGenLists(1);
			GL11.glNewList(listId, GL11.GL_COMPILE);
			GL11.glBegin(GL11.GL_TRIANGLES);
			for(int i=0;i<vertices.length/3;i++){
				GL11.glNormal3f(normals[i*3], normals[i*3+1], normals[i*3+2]);
				GL11.glTexCoord2f(textureCoords[i*2], textureCoords[i*2+1]);
				GL11.glVertex3f(vertices[i*3], vertices[i*3+1], vertices[i*3+2]);
			}
			GL11.glEnd();
			GL11.glEndList();
		}else{
			int[] indices = new int[vertices.length/3];
			for(int i=0;i<indices.length;i++) indices[i] = i;
			vao = VOLoader.loadToVAO(vertices, textureCoords, normals, tangents, indices);
		}
		
	}
	
	public Renderable3dModel(float[] vertices, float[] textureCoords, float[] normals, float[] tangents, int[] indices, TextureDef textureColor, TextureDef textureNormal, TextureDef textureSpecular){
		isIndexed = true;
		this.textureColor = textureColor != null ? textureColor : TextureManager.getDefaultTexture();
		this.textureNormal = textureNormal != null ? textureNormal : TextureManager.getDefaultNormalTexture();
		this.textureSpecular = textureSpecular != null ? textureSpecular : TextureManager.getDefaultSpecularTexture();
		
		if(Configuration.getRenderMethod() == Configuration.RENDER_FREE){
			listId = GL11.glGenLists(1);
			GL11.glNewList(listId, GL11.GL_COMPILE);
			GL11.glBegin(GL11.GL_TRIANGLES);
			for(int i=0;i<indices.length;i++){
				GL11.glNormal3f(normals[indices[i]*3], normals[indices[i]*3+1], normals[indices[i]*3+2]);
				GL11.glTexCoord2f(textureCoords[indices[i]*2], textureCoords[indices[i]*2+1]);
				GL11.glVertex3f(vertices[indices[i]*3], vertices[indices[i]*3+1], vertices[indices[i]*3+2]);
			}
			GL11.glEnd();
			GL11.glEndList();
		}else{
			vao = VOLoader.loadToVAO(vertices, textureCoords, normals, tangents, indices);
		}
		
	}
	
	@Override
	protected void renderFree() {
		//TODO add ShaderManager.shader3d_bindShineDamper(shineDamper);
		//TODO add ShaderManager.shader3d_bindReflectivity(reflectivity);
		ShaderManager.shader3d_bindTextureID(textureColor.getTextureID(), ShaderManager.TEXTURE_COLOR);
		ShaderManager.shader3d_bindTextureID(textureNormal.getTextureID(), ShaderManager.TEXTURE_NORMAL);
		ShaderManager.shader3d_bindTextureID(textureSpecular.getTextureID(), ShaderManager.TEXTURE_SPECULAR);
		GL11.glCallList(listId);
	}

	@Override
	protected void renderModern() {
		GL30.glBindVertexArray(vao.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);
		GL20.glEnableVertexAttribArray(3);
		//TODO add ShaderManager.shader3d_bindShineDamper(shineDamper);
		//TODO add ShaderManager.shader3d_bindReflectivity(reflectivity);
		ShaderManager.shader3d_bindTextureID(textureColor.getTextureID(), ShaderManager.TEXTURE_COLOR);
		ShaderManager.shader3d_bindTextureID(textureNormal.getTextureID(), ShaderManager.TEXTURE_NORMAL);
		ShaderManager.shader3d_bindTextureID(textureSpecular.getTextureID(), ShaderManager.TEXTURE_SPECULAR);
		if(isIndexed) GL11.glDrawElements(GL11.GL_TRIANGLES, vao.getVertexCount(), GL11.GL_UNSIGNED_INT, 0); else GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, vao.getVertexCount());
	}
	
	@Override
	public void renderVR() {
		if(Configuration.getRenderMethod() == Configuration.RENDER_FREE){
			ShaderManager.shaderVR_bindTextureID(textureColor.getTextureID(), ShaderManager.TEXTURE_COLOR);
			ShaderManager.shaderVR_bindTextureID(textureNormal.getTextureID(), ShaderManager.TEXTURE_NORMAL);
			ShaderManager.shaderVR_bindTextureID(textureSpecular.getTextureID(), ShaderManager.TEXTURE_SPECULAR);
			GL11.glCallList(listId);
		}else{
			GL30.glBindVertexArray(vao.getVaoID());
			GL20.glEnableVertexAttribArray(0);
			GL20.glEnableVertexAttribArray(1);
			GL20.glEnableVertexAttribArray(2);
			GL20.glEnableVertexAttribArray(3);
			//TODO add ShaderManager.shader3d_bindShineDamper(shineDamper);
			//TODO add ShaderManager.shader3d_bindReflectivity(reflectivity);
			ShaderManager.shader3d_bindTextureID(textureColor.getTextureID(), ShaderManager.TEXTURE_COLOR);
			ShaderManager.shader3d_bindTextureID(textureNormal.getTextureID(), ShaderManager.TEXTURE_NORMAL);
			ShaderManager.shader3d_bindTextureID(textureSpecular.getTextureID(), ShaderManager.TEXTURE_SPECULAR);
			if(isIndexed) GL11.glDrawElements(GL11.GL_TRIANGLES, vao.getVertexCount(), GL11.GL_UNSIGNED_INT, 0); else GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, vao.getVertexCount());
		}
	}
	
}
