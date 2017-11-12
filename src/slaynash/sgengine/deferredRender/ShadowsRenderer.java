package slaynash.sgengine.deferredRender;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL32;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import slaynash.sgengine.Configuration;
import slaynash.sgengine.shaders.ShaderManager;
import slaynash.sgengine.shaders.ShaderProgram;
import slaynash.sgengine.utils.LightsManager;
import slaynash.sgengine.utils.MatrixUtils;
import slaynash.sgengine.utils.VRUtils;
import slaynash.sgengine.world3d.loader.Ent_PointLight;

public class ShadowsRenderer {
	
	public static final int SHADOW_WIDTH = 1024, SHADOW_HEIGHT = 1024;
	
	private static int[] depthCubemap;
	private static int[] depthMapFBO;
	
	public static void init() {
		depthCubemap = new int[Configuration.MAX_LIGHTS];
		depthMapFBO = new int[Configuration.MAX_LIGHTS*6];
		
		for(int l=0;l<Configuration.MAX_LIGHTS;l++) {
			depthCubemap[l] = GL11.glGenTextures();
			GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, depthCubemap[l]);
			for (int i=0; i<6; i++) {
		        GL11.glTexImage2D(GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL11.GL_DEPTH_COMPONENT, 
		                     SHADOW_WIDTH, SHADOW_HEIGHT, 0, GL11.GL_DEPTH_COMPONENT, GL11.GL_FLOAT, (FloatBuffer)null);
			}
			GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
			GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
			GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
			GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
			GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL12.GL_TEXTURE_WRAP_R, GL12.GL_CLAMP_TO_EDGE);
			
			depthMapFBO[l] = GL30.glGenFramebuffers();
			GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, depthMapFBO[l]);
			GL32.glFramebufferTexture(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, depthCubemap[l], 0);
			GL11.glDrawBuffer(GL11.GL_NONE);
			GL11.glReadBuffer(GL11.GL_NONE);
			GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
		}
	}
	
	public static void renderShadows(int eye, List<ShaderRenderlist> shaderLists) {
		
		Matrix4f projMat = MatrixUtils.createProjectionMatrix(Configuration.getLightsZNear(), Configuration.getLightsZFar(), 90f, (float)SHADOW_WIDTH/(float)SHADOW_HEIGHT);
		
		GL11.glViewport(0, 0, SHADOW_WIDTH, SHADOW_HEIGHT);
		for(int i=0;i<LightsManager.getPointlights().size();i++) {
			Ent_PointLight light = LightsManager.getPointlights().get(i);
			if(light.getColor()[0]+light.getColor()[1]+light.getColor()[2] < 0.0001f) continue;
			
			Matrix4f[] pMatrices = new Matrix4f[6];
			
			pMatrices[0] = Matrix4f.mul(
					projMat, MatrixUtils.createViewMatrix(light.getPosition(), Vector3f.add(new Vector3f(1,0,0), light.getPosition(), null), new Vector3f(0,-1,0)), null
			);
			pMatrices[1] = Matrix4f.mul(
					projMat, MatrixUtils.createViewMatrix(light.getPosition(), Vector3f.add(new Vector3f(-1,0,0), light.getPosition(), null), new Vector3f(0,-1,0)), null
			);
			pMatrices[2] = Matrix4f.mul(
					projMat, MatrixUtils.createViewMatrix(light.getPosition(), Vector3f.add(new Vector3f(0,1,0), light.getPosition(), null), new Vector3f(0,0,1)), null
			);
			pMatrices[3] = Matrix4f.mul(
					projMat, MatrixUtils.createViewMatrix(light.getPosition(), Vector3f.add(new Vector3f(0,-1,0), light.getPosition(), null), new Vector3f(0,0,-1)), null
			);
			pMatrices[4] = Matrix4f.mul(
					projMat, MatrixUtils.createViewMatrix(light.getPosition(), Vector3f.add(new Vector3f(0,0,1), light.getPosition(), null), new Vector3f(0,-1,0)), null
			);
			pMatrices[5] = Matrix4f.mul(
					projMat, MatrixUtils.createViewMatrix(light.getPosition(), Vector3f.add(new Vector3f(0,0,-1), light.getPosition(), null), new Vector3f(0,-1,0)), null
			);
			
			
			
			
			
			
			
			GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, depthMapFBO[i]);
			GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
			for(ShaderRenderlist map:shaderLists){//for each shader phase
				ShaderProgram shadowShader = map.getShader().getShadowShader();
				if(shadowShader == null) continue;
				shadowShader.useDirect();
				shadowShader.bindDataDirect("lightPos", light.getPosition());
				
				for(int j=0;j<6;j++) shadowShader.bindDataDirect("pMatrices["+j+"]", pMatrices[j]);
				
				for(Entry<Integer, ArrayList<DeferredModelRenderer>> entry:map.getObjectList().entrySet()){//for each models
					if(!entry.getValue().get(0).isCastingShadow()) break;
					shadowShader.bindModel(entry.getKey());
					for(DeferredModelRenderer dmr:entry.getValue()){
						shadowShader.bindDatasDirect(dmr.getShaderDatas());
						if(eye == VRUtils.EYE_CENTER) dmr.render(); else dmr.renderVR(eye);
					}
				}
				shadowShader.stop();
			}
			
			
		}
		
		
		for(int i=0;i<LightsManager.getPointlights().size();i++) {
			GL13.glActiveTexture(GL13.GL_TEXTURE0+ShaderManager.TEXTURE_SHADOWSMIN+i);
			GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, depthCubemap[i]);
		}
	}
}
