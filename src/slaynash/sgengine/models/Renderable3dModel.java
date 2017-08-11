package slaynash.sgengine.models;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import slaynash.sgengine.Configuration;
import slaynash.sgengine.deferredRender.DeferredRenderer;
import slaynash.sgengine.shaders.ShaderManager;
import slaynash.sgengine.textureUtils.TextureDef;
import slaynash.sgengine.textureUtils.TextureManager;
import slaynash.sgengine.utils.VAO;
import slaynash.sgengine.utils.VOLoader;

public class Renderable3dModel extends RenderableModel {

	private int listId = 0;
	private TextureDef textureColor;
	private TextureDef textureNormal;
	private TextureDef textureSpecular;
	private VAO vao;
	private boolean isIndexed = false;
	private boolean renderable = true;
	private boolean drRegistered = false;
	
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
		
		if(Configuration.isUsingDeferredRender() && !drRegistered){
			drRegistered = true;
			DeferredRenderer.registerModelRenderer(this, Renderable3dModelDeferredRender.class);
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
		
		if(Configuration.isUsingDeferredRender() && !drRegistered){
			drRegistered = true;
			DeferredRenderer.registerModelRenderer(this, Renderable3dModelDeferredRender.class);
		}
		
	}
	
	@Override
	protected void renderFree() {
		if(!renderable) return;
		else{
			//TODO add ShaderManager.shader_bindShineDamper(shineDamper);
			//TODO add ShaderManager.shader_bindReflectivity(reflectivity);
			ShaderManager.shader_bindTextureID(textureColor.getTextureID(), ShaderManager.TEXTURE_COLOR);
			ShaderManager.shader_bindTextureID(textureNormal.getTextureID(), ShaderManager.TEXTURE_NORMAL);
			ShaderManager.shader_bindTextureID(textureSpecular.getTextureID(), ShaderManager.TEXTURE_SPECULAR);
			GL11.glCallList(listId);
		}
	}

	@Override
	protected void renderModern() {
		if(!renderable) return;
		else{
			GL30.glBindVertexArray(vao.getVaoID());
			GL20.glEnableVertexAttribArray(0);
			GL20.glEnableVertexAttribArray(1);
			GL20.glEnableVertexAttribArray(2);
			GL20.glEnableVertexAttribArray(3);
			//TODO add ShaderManager.shader_bindShineDamper(shineDamper);
			//TODO add ShaderManager.shader_bindReflectivity(reflectivity);
			ShaderManager.shader_bindTextureID(textureColor.getTextureID(), ShaderManager.TEXTURE_COLOR);
			ShaderManager.shader_bindTextureID(textureNormal.getTextureID(), ShaderManager.TEXTURE_NORMAL);
			ShaderManager.shader_bindTextureID(textureSpecular.getTextureID(), ShaderManager.TEXTURE_SPECULAR);
			if(isIndexed) GL11.glDrawElements(GL11.GL_TRIANGLES, vao.getVertexCount(), GL11.GL_UNSIGNED_INT, 0); else GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, vao.getVertexCount());
		}
	}
	
	@Override
	public void renderVR() {
		
		if(!renderable) return;
		else{
			if(Configuration.getRenderMethod() == Configuration.RENDER_FREE){
				ShaderManager.shader_bindTextureID(textureColor.getTextureID(), ShaderManager.TEXTURE_COLOR);
				ShaderManager.shader_bindTextureID(textureNormal.getTextureID(), ShaderManager.TEXTURE_NORMAL);
				ShaderManager.shader_bindTextureID(textureSpecular.getTextureID(), ShaderManager.TEXTURE_SPECULAR);
				GL11.glCallList(listId);
			}else{
				GL30.glBindVertexArray(vao.getVaoID());
				GL20.glEnableVertexAttribArray(0);
				GL20.glEnableVertexAttribArray(1);
				GL20.glEnableVertexAttribArray(2);
				GL20.glEnableVertexAttribArray(3);
				//TODO add ShaderManager.shader_bindShineDamper(shineDamper);
				//TODO add ShaderManager.shader_bindReflectivity(reflectivity);
				ShaderManager.shader_bindTextureID(textureColor.getTextureID(), ShaderManager.TEXTURE_COLOR);
				ShaderManager.shader_bindTextureID(textureNormal.getTextureID(), ShaderManager.TEXTURE_NORMAL);
				ShaderManager.shader_bindTextureID(textureSpecular.getTextureID(), ShaderManager.TEXTURE_SPECULAR);
				if(isIndexed) GL11.glDrawElements(GL11.GL_TRIANGLES, vao.getVertexCount(), GL11.GL_UNSIGNED_INT, 0); else GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, vao.getVertexCount());
			}
		}
	}
	
	public void setTextureColor(TextureDef textureColor) {
		this.textureColor = textureColor != null ? textureColor : TextureManager.getDefaultTexture();
	}

	public void dispose() {
		vao.dispose();
		renderable = false;
	}

	public boolean isIndexed() {
		return isIndexed;
	}
	
	public VAO getVao(){
		return vao;
	}

	public int[] getTextureIds() {
		return new int[]{textureColor.getTextureID(), textureNormal.getTextureID(), textureSpecular.getTextureID()};
	}

	public int getListId() {
		return listId;
	}
	
	
	
}
