package slaynash.sgengine.gui.comboBox;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector2f;

import slaynash.sgengine.models.Renderable2dModel;
import slaynash.sgengine.gui.GUIElement;
import slaynash.sgengine.gui.text2d.Text2d;
import slaynash.sgengine.shaders.ShaderManager;
import slaynash.sgengine.textureUtils.TextureDef;
import slaynash.sgengine.textureUtils.TextureManager;
import slaynash.sgengine.utils.UserInputUtil;

public class GUIComboBox extends GUIElement{ //TODO create events for comboBox

	private TextureDef texBox;
	private TextureDef texMiddle;
	private TextureDef texEnd;
	private TextureDef texExpand;
	
	private List<?> list;
	private List<Text2d> listText;
	private Text2d selectedText;
	private int selectedIndex;
	
	private Renderable2dModel modelBox;
	private Renderable2dModel modelExp;
	private static float[] uvs = new float[]{0,0,1,0,1,1,1,1,0,1,0,0};

	public GUIComboBox(int x, int y, int width, List<?> list, GUIElement parent, int location) {
		super(x, y, width, 20, parent, false, location);
		this.list = list;
		
		texBox = TextureManager.getTextureDef("res/textures/gui/comboBox/cb_box.png", TextureManager.COLOR);
		texMiddle = TextureManager.getTextureDef("res/textures/gui/comboBox/cb_middle.png", TextureManager.COLOR);
		texEnd = TextureManager.getTextureDef("res/textures/gui/comboBox/cb_end.png", TextureManager.COLOR);
		texExpand = TextureManager.getTextureDef("res/textures/gui/comboBox/cb_expand.png", TextureManager.COLOR);
		
		listText = new ArrayList<Text2d>();
		
		int yp = 0;
		for(Object o:list){
			yp+=20;
			listText.add(new Text2d(o.toString(), "tahoma", 250, new Vector2f(0, yp+3), width/2, true, this));
		}
		
		selectedIndex = 0;
		selectedText = new Text2d(listText.get(selectedIndex).getTextString(), "tahoma", 250, new Vector2f(0, 3), width/2, true, this);
		
		float[] verticesBox = new float[]{0,0,width,0,width,20,width,20,0,20,0,0};
		modelBox = new Renderable2dModel(verticesBox, uvs, texBox);
		
		float[] verticesExp = new float[]{-20,0,0,0,0,20,0,20,-20,20,-20,0};
		modelExp = new Renderable2dModel(verticesExp, uvs, texExpand);
	}
	
	@Override
	public void render() {
		if(isFocused()){
			if(mouseIn && UserInputUtil.mouseLeftClicked()){
				if(getHeight() == 20){
					open();
				}
				else{
					Vector2f mousePos = UserInputUtil.getMousePos();
					System.out.println("[GUICombobox] "+mousePos.y);
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
			if(getHeight() != 20) close();
		}

		int err = 0;
		if((err = GL11.glGetError()) != 0) System.out.println("0:"+err);
		
		ShaderManager.shaderGUI_setComboBoxMode(true);
		ShaderManager.shaderGUI_loadComboBoxCuts(getTopLeft().y, getBottomRight().y);
		
		if((err = GL11.glGetError()) != 0) System.out.println("1:"+err);
		
		for(int i=0;i<listText.size();i++){
			if(i<listText.size()-1) modelBox.setTexture(texMiddle);
			else modelBox.setTexture(texEnd);
			
			ShaderManager.shaderGUI_loadTranslation(new Vector2f(getTopLeft().x, getTopLeft().y+(i+1)*20));
			modelBox.render();
			ShaderManager.shaderGUI_loadTranslation(new Vector2f());
			/*
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
			*/
			listText.get(i).render();
		}
		
		ShaderManager.shaderGUI_loadTranslation(getTopLeft());
		modelBox.setTexture(texBox);
		modelBox.render();
		/*
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
		*/
		selectedText.render();
		
		ShaderManager.shaderGUI_loadTranslation(new Vector2f(getBottomRight().x, getTopLeft().y));
		modelExp.render();
		/*
		GL11.glBegin(GL11.GL_QUADS);
			GL11.glTexCoord2f(0      , 0);
			GL11.glVertex2f  (getBottomRight().x-20, getTopLeft().y);
			GL11.glTexCoord2f(1      , 0);
			GL11.glVertex2f  (getBottomRight().x, getTopLeft().y);
			GL11.glTexCoord2f(1      , 1);
			GL11.glVertex2f  (getBottomRight().x, getTopLeft().y+20);
			GL11.glTexCoord2f(0      , 1);
			GL11.glVertex2f  (getBottomRight().x-20, getTopLeft().y+20);
		GL11.glEnd();
		*/
		ShaderManager.shaderGUI_loadTranslation(new Vector2f());
		
		ShaderManager.shaderGUI_setComboBoxMode(false);
		

		if((err = GL11.glGetError()) != 0) System.out.println("[GUIComboBox] Opengl error "+err);
	}
	
	private void open(){
		setHeight(list.size()*20+20);
	}
	
	private void close(){
		setHeight(20);
	}
	
	public void setSelected(int index){
		if(index >= 0 && index < listText.size() && index != selectedIndex){
			selectedIndex = index;
			selectedText = listText.get(index);
			selectedText.release();
			selectedText = new Text2d(listText.get(index).getTextString(), "tahoma", 250, new Vector2f(0, 3), getWidth()/2, true, this);
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
		if(getHeight() == 20) return false;
		return true;
	}
	
}
