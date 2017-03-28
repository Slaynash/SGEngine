package slaynash.opengl.render2d.comboBox;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector2f;

import slaynash.opengl.render2d.GUIElement;
import slaynash.opengl.render2d.text2d.Text2d;
import slaynash.opengl.shaders.ShaderManager;
import slaynash.opengl.textureUtils.TextureManager;
import slaynash.opengl.utils.UserInputUtil;

public class GUIComboBox extends GUIElement{ //TODO create events for comboBox

	private int texBoxID;
	private int texMiddleID;
	private int texEndID;
	private int texExpandID;
	
	private List<?> list;
	private List<Text2d> listText;
	private Text2d selectedText;
	private int selectedIndex;

	public GUIComboBox(int x, int y, int width, List<?> list, GUIElement parent, int location) {
		super(x, y, width, 20, parent, false, location);
		this.list = list;
		
		texBoxID = TextureManager.getTextureID("res/textures/gui/comboBox/cb_box.png");
		texMiddleID = TextureManager.getTextureID("res/textures/gui/comboBox/cb_middle.png");
		texEndID = TextureManager.getTextureID("res/textures/gui/comboBox/cb_end.png");
		texExpandID = TextureManager.getTextureID("res/textures/gui/comboBox/cb_expand.png");
		
		listText = new ArrayList<Text2d>();
		
		int yp = 0;
		for(Object o:list){
			yp+=20;
			listText.add(new Text2d(o.toString(), "tahoma", 250, new Vector2f(0, yp+3), width/2, true, this));
		}
		
		selectedIndex = 0;
		selectedText = new Text2d(listText.get(selectedIndex).getTextString(), "tahoma", 250, new Vector2f(0, 3), width/2, true, this);
	}
	
	@Override
	public void render() {
		if(isFocused()){
			if(mouseIn && UserInputUtil.mouseLeftClicked()){
				if(height == 20){
					open();
				}
				else{
					Vector2f mousePos = UserInputUtil.getMousePos();
					if(mousePos.y <= getTopLeft().y+20)
						close();
					else{
						int selectedIndex = (int) ((mousePos.y-getTopLeft().y-20)/20);
						setSelected(selectedIndex);
					}
				}
			}
		}
		else{
			if(height != 20) close();
		}

		int err = 0;
		if((err = GL11.glGetError()) != 0) System.out.println("0:"+err);
		
		ShaderManager.setComboBoxMode(true);
		ShaderManager.loadComboBoxCuts(getTopLeft().y, getBottomRight().y);
		
		if((err = GL11.glGetError()) != 0) System.out.println("1:"+err);
		
		for(int i=0;i<listText.size();i++){
			if(i<listText.size()-1){
				ShaderManager.bind2DShaderTextureID(texMiddleID);
			}
			else{
				ShaderManager.bind2DShaderTextureID(texEndID);
			}
			GL11.glBegin(GL11.GL_QUADS);
				GL11.glTexCoord2f(0      , 0);
				GL11.glVertex2f  (getTopLeft().x, getTopLeft().y+(i+1)*20);
				GL11.glTexCoord2f(1      , 0);
				GL11.glVertex2f  (getBottomRight().x, getTopLeft().y+(i+1)*20);
				GL11.glTexCoord2f(1      , 1);
				GL11.glVertex2f  (getBottomRight().x, getTopLeft().y+(i+2)*20);
				GL11.glTexCoord2f(0      , 1);
				GL11.glVertex2f  (getTopLeft().x, getTopLeft().y+(i+2)*20);
			GL11.glEnd();
			listText.get(i).render();
		}
		
		if((err = GL11.glGetError()) != 0) System.out.println("2:"+err);
		
		ShaderManager.bind2DShaderTextureID(texBoxID);
		GL11.glBegin(GL11.GL_QUADS);
			GL11.glTexCoord2f(0      , 0);
			GL11.glVertex2f  (getTopLeft().x, getTopLeft().y);
			GL11.glTexCoord2f(1      , 0);
			GL11.glVertex2f  (getBottomRight().x, getTopLeft().y);
			GL11.glTexCoord2f(1      , 1);
			GL11.glVertex2f  (getBottomRight().x, getTopLeft().y+20);
			GL11.glTexCoord2f(0      , 1);
			GL11.glVertex2f  (getTopLeft().x, getTopLeft().y+20);
		GL11.glEnd();
		selectedText.render();
		
		ShaderManager.bind2DShaderTextureID(texExpandID);
		GL11.glBegin(GL11.GL_QUADS);
			GL11.glTexCoord2f(0      , 0);
			GL11.glVertex2f  (getBottomRight().x-20, getTopLeft().y);
			GL11.glTexCoord2f(1      , 0);
			GL11.glVertex2f  (getBottomRight().x, getTopLeft().y);
			GL11.glTexCoord2f(1      , 1);
			GL11.glVertex2f  (getBottomRight().x, getTopLeft().y+20);
			GL11.glTexCoord2f(0      , 1);
			GL11.glVertex2f  (getBottomRight().x-20, getTopLeft().y+20);
		GL11.glEnd();//TODO check for error

		if((err = GL11.glGetError()) != 0) System.out.println("3:"+err);
		
		ShaderManager.setComboBoxMode(false);
		

		if((err = GL11.glGetError()) != 0) System.out.println("4:"+err);
	}
	
	private void open(){
		height = list.size()*20+20;
	}
	
	private void close(){
		height = 20;
	}
	
	public void setSelected(int index){
		if(index > 0 && index < listText.size() && index != selectedIndex){
			selectedIndex = index;
			selectedText = listText.get(index);
			selectedText.release();
			selectedText = new Text2d(listText.get(index).getTextString(), "tahoma", 250, new Vector2f(0, 3), width/2, true, this);
			selectedText.setNumberOfLines(1);
		}
	}
	
	public Object getSelectedItem(){
		return list.get(selectedIndex);
	}
	
	@Override
	public void destroy(){
		super.destroy();
		for(Text2d t:listText) t.release();
		selectedText.release();
	}

	public boolean isExpanded() {
		if(height == 20) return false;
		return true;
	}
	
}
