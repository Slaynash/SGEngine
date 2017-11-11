package slaynash.sgengine.models;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import slaynash.sgengine.deferredRender.DeferredModelRenderer;
import slaynash.sgengine.models.utils.Vao;
import slaynash.sgengine.shaders.ShaderManager;
import slaynash.sgengine.textureUtils.TextureDef;
import slaynash.sgengine.textureUtils.TextureManager;

public class Renderable3dModel extends RenderableModel {

	//private int listId = 0;
	private TextureDef textureColor;
	private TextureDef textureNormal;
	private TextureDef textureSpecular;
	private Vao vao;
	private boolean renderable = true;
	
	/*
	public Renderable3dModel(float[] vertices, float[] textureCoords, float[] normals, float[] tangents, TextureDef textureColor, TextureDef textureNormal, TextureDef textureSpecular){
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
			vao = VaoManager.loadToVAO(vertices, textureCoords, normals, tangents, indices);
		}
		
		if(Configuration.isUsingDeferredRender() && !drRegistered){
			drRegistered = true;
			DeferredRenderer.registerModelRenderer(this, Renderable3dModelDeferredRender.class);
		}
		
	}
	
	public Renderable3dModel(float[] vertices, float[] textureCoords, float[] normals, float[] tangents, int[] indices, TextureDef textureColor, TextureDef textureNormal, TextureDef textureSpecular){
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
			vao = VaoManager.loadToVAO(vertices, textureCoords, normals, tangents, indices);
		}
		
		if(Configuration.isUsingDeferredRender() && !drRegistered){
			drRegistered = true;
			DeferredRenderer.registerModelRenderer(this, Renderable3dModelDeferredRender.class);
		}
		
	}
	*/
	public Renderable3dModel(Vao vao, TextureDef textureColor, TextureDef textureNormal, TextureDef textureSpecular){
		this.textureColor = textureColor != null ? textureColor : TextureManager.getDefaultTexture();
		this.textureNormal = textureNormal != null ? textureNormal : TextureManager.getDefaultNormalTexture();
		this.textureSpecular = textureSpecular != null ? textureSpecular : TextureManager.getDefaultSpecularTexture();
		
		this.vao = vao;
	}

	@Override
	protected void renderToScreen() {
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
			GL11.glDrawElements(GL11.GL_TRIANGLES, vao.getIndexCount(), GL11.GL_UNSIGNED_INT, 0);
		}
	}
	
	@Override
	public void renderVREye(int eye) {
		
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
			GL11.glDrawElements(GL11.GL_TRIANGLES, vao.getIndexCount(), GL11.GL_UNSIGNED_INT, 0);
		}
	}
	
	public void setTextureColor(TextureDef textureColor) {
		this.textureColor = textureColor != null ? textureColor : TextureManager.getDefaultTexture();
	}

	public void dispose() {
		vao.dispose();
		renderable = false;
	}
	
	public Vao getVao(){
		return vao;
	}

	public int[] getTextureIds() {
		return new int[]{textureColor.getTextureID(), textureNormal.getTextureID(), textureSpecular.getTextureID()};
	}

	@Override
	public Class<? extends DeferredModelRenderer> getDeferredRenderer() {
		return Renderable3dModelDeferredRender.class;
	}
	
	
	
}
