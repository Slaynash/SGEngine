package slaynash.sgengine.gui.text2d.fontMeshCreator;

import java.io.File;

import slaynash.sgengine.gui.text2d.Text2d;
import slaynash.sgengine.textureUtils.TextureDef;

/**
 * Represents a font. It holds the font's texture atlas as well as having the
 * ability to create the quad vertices for any text using this font.
 * 
 * @author Karl
 *
 */
public class FontType {

	private TextureDef textureAtlas;
	private TextMeshCreator loader;
	private String fontPath;

	/**
	 * Creates a new font and loads up the data about each character from the
	 * font file.
	 * 
	 * @param textureAtlas
	 *            - the ID of the font atlas texture.
	 * @param string
	 *            - the font file containing information about each character in
	 *            the texture atlas.
	 */
	public FontType(TextureDef textureAtlas, String fontPath) {
		this.textureAtlas = textureAtlas;
		this.fontPath = fontPath;
		this.loader = new TextMeshCreator(new File(fontPath));
	}

	/**
	 * @return The font texture atlas.
	 */
	public TextureDef getTextureAtlas() {
		return textureAtlas;
	}

	/**
	 * Takes in an unloaded text and calculate all of the vertices for the quads
	 * on which this text will be rendered. The vertex positions and texture
	 * coords and calculated based on the information from the font file.
	 * 
	 * @param text
	 *            - the unloaded text.
	 * @return Information about the vertices of all the quads.
	 */
	public TextMeshData loadText(Text2d text) {
		return loader.createTextMesh(text);
	}
	
	public String getFontPath(){
		return fontPath;
	}

}
