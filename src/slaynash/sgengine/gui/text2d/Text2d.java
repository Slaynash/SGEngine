package slaynash.sgengine.gui.text2d;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import slaynash.sgengine.models.Renderable2dModel;
import slaynash.sgengine.models.utils.VaoManager;
import slaynash.sgengine.gui.GUIElement;
import slaynash.sgengine.gui.text2d.fontMeshCreator.FontType;
import slaynash.sgengine.gui.text2d.fontMeshCreator.TextMeshData;
import slaynash.sgengine.shaders.ShaderManager;
import slaynash.sgengine.utils.DisplayManager;

public class Text2d {
	
	private static List<Text2d> text2ds = new ArrayList<Text2d>();
	
	private String textString;
	private float fontSize;
	
	private float[] vertices, uvs;
	private int vertexCount;
	private Vector3f colour = new Vector3f(1f, 1f, 1f);

	private Vector2f position;
	private float lineMaxSize;
	private int numberOfLines;

	private FontType font;

	private boolean centerText = false;
	private GUIElement parent;
	
	private FontType fontType;
	
	private Renderable2dModel textModel;
	
	public Text2d(String text, String fontLocation, float fontSize, Vector2f position, float maxLineLength, boolean centered, GUIElement parent){
		fontType = FontManager.createFont(fontLocation);
		
		this.textString = text;
		this.fontSize = fontSize;
		this.font = fontType;
		this.position = position;
		this.lineMaxSize = maxLineLength;
		this.centerText = centered;
		
		this.parent = parent;
		
		TextMeshData data = fontType.loadText(this);
		vertices = data.getVertexPositions();
		uvs = data.getTextureCoords();
		textModel = new Renderable2dModel(VaoManager.loadToVao2d(vertices, uvs), font.getTextureAtlas());
		
		text2ds.add(this);
	}
	
	
	public void render(){
		ShaderManager.shader_setTextMode();
		ShaderManager.shader_loadColor(getColour());
		float px = position.x;
		float py = position.y;
		if(parent != null){
			px += parent.getTopLeft().x;
			py += parent.getTopLeft().y;
		}
		ShaderManager.shader_loadTranslation(new Vector2f(px, -DisplayManager.getHeight()+py));
		/*
		GL11.glBegin(GL11.GL_TRIANGLES);
		for(int i=0;i<vertices.length;i+=2){
			GL11.glTexCoord2f(uvs[i], uvs[i+1]);
			GL11.glVertex2f(vertices[i]+px, vertices[i+1]-(DisplayManager.getHeight()-py));
		}
		GL11.glEnd();
		*/
		textModel.render();
		ShaderManager.shader_loadTranslation(new Vector2f());
		ShaderManager.shader_exitTextMode();
	}
	
	public Renderable2dModel getModel() {
		return textModel;
	}
	
	
	
	
	
	
	/**
	 * Creates a new text, loads the text's quads into a VAO, and adds the text
	 * to the screen.
	 * 
	 * @param text
	 *            - the text.
	 * @param fontSize
	 *            - the font size of the text, where a font size of 1 is the
	 *            default size.
	 * @param font
	 *            - the font that this text should use.
	 * @param position
	 *            - the position on the screen where the top left corner of the
	 *            text should be rendered. The top left corner of the screen is
	 *            (0, 0) and the bottom right is (1, 1).
	 * @param maxLineLength
	 *            - basically the width of the virtual page in terms of screen
	 *            width (1 is full screen width, 0.5 is half the width of the
	 *            screen, etc.) Text cannot go off the edge of the page, so if
	 *            the text is longer than this length it will go onto the next
	 *            line. When text is centered it is centered into the middle of
	 *            the line, based on this line length value.
	 * @param centered
	 *            - whether the text should be centered or not.
	 */

	/**
	 * @return The font used by this text.
	 */
	public FontType getFont() {
		return font;
	}

	/**
	 * Set the colour of the text.
	 * 
	 * @param r
	 *            - red value, between 0 and 1.
	 * @param g
	 *            - green value, between 0 and 1.
	 * @param b
	 *            - blue value, between 0 and 1.
	 */
	public void setColour(float r, float g, float b) {
		colour.set(r, g, b);
	}

	/**
	 * @return the colour of the text.
	 */
	public Vector3f getColour() {
		return colour;
	}

	/**
	 * @return The number of lines of text. This is determined when the text is
	 *         loaded, based on the length of the text and the max line length
	 *         that is set.
	 */
	public int getNumberOfLines() {
		return numberOfLines;
	}

	/**
	 * @return The position of the top-left corner of the text in screen-space.
	 *         (0, 0) is the top left corner of the screen, (1, 1) is the bottom
	 *         right.
	 */
	public Vector2f getPosition() {
		return position;
	}
	/*
	public void setPosition(Vector2f position) {
		this.position = position;
	}
	*/
	public float[] getVertices(){
		return vertices;
	}
	
	public float[] getUVs(){
		return uvs;
	}

	/**
	 * @return The total number of vertices of all the text's quads.
	 */
	public int getVertexCount() {
		return this.vertexCount;
	}

	/**
	 * @return the font size of the text (a font size of 1 is normal).
	 */
	public float getFontSize() {
		return fontSize;
	}

	/**
	 * Sets the number of lines that this text covers (method used only in
	 * loading).
	 * 
	 * @param number
	 */
	public void setNumberOfLines(int number) {
		this.numberOfLines = number;
	}

	/**
	 * @return {@code true} if the text should be centered.
	 */
	public boolean isCentered() {
		return centerText;
	}

	/**
	 * @return The maximum length of a line of this text.
	 */
	public float getMaxLineSize() {
		return lineMaxSize;
	}

	/**
	 * @return The string of text.
	 */
	public String getTextString() {
		return textString;
	}


	public static void reload() {
		for(Text2d t:text2ds){
			TextMeshData data = t.fontType.loadText(t);
			t.vertices = data.getVertexPositions();
			t.uvs = data.getTextureCoords();
			t.textModel = new Renderable2dModel(VaoManager.loadToVao2d(t.vertices, t.uvs), t.font.getTextureAtlas());
		}
	}
	
	public void release(){
		text2ds.remove(this);
		textModel.getVao().dispose();
	}
}
