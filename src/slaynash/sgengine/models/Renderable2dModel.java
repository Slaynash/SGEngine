package slaynash.sgengine.models;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import slaynash.sgengine.Configuration;
import slaynash.sgengine.shaders.ShaderManager;
import slaynash.sgengine.textureUtils.TextureDef;
import slaynash.sgengine.textureUtils.TextureManager;
import slaynash.sgengine.utils.VAO;
import slaynash.sgengine.utils.VOLoader;

public class Renderable2dModel extends RenderableModel {
	
	private int listId = 0;
	private TextureDef texture;
	private VAO vao;

	public Renderable2dModel(float[] vertices, float[] textureCoords, TextureDef texture){
		
		this.texture = texture != null ? texture : TextureManager.getDefaultTexture();
		
		if(Configuration.getRenderMethod() == Configuration.RENDER_FREE){
			int err = 0; if((err = GL11.glGetError()) != 0) System.out.println("Renderable2dModel 1: OpenGL Error "+err);
			listId = GL11.glGenLists(1);
			GL11.glNewList(listId, GL11.GL_COMPILE);
			GL11.glBegin(GL11.GL_TRIANGLES);
			for(int i=0;i<vertices.length/2;i++){
				GL11.glTexCoord2f(textureCoords[i*2], textureCoords[i*2+1]);
				GL11.glVertex2f(vertices[i*2], vertices[i*2+1]);
			}
			GL11.glEnd();
			GL11.glEndList();
			if((err = GL11.glGetError()) != 0) System.out.println("Renderable2dModel 2: OpenGL Error "+err);
		}else{
			vao = VOLoader.loadToVAO(vertices, textureCoords);
		}
		
	}

	@Override
	protected void renderFree() {
		ShaderManager.shaderGUI_bindTextureID(texture.getTextureID(), ShaderManager.TEXTURE_COLOR);
		GL11.glCallList(listId);
	}

	@Override
	protected void renderModern() {
		GL30.glBindVertexArray(vao.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		ShaderManager.shaderGUI_bindTextureID(texture.getTextureID(), ShaderManager.TEXTURE_COLOR);
		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, vao.getVertexCount());
	}

	public void setTexture(TextureDef texture) {
		this.texture = texture;
	}

}
