package slaynash.sgengine.gui.text2d;

import java.util.ArrayList;
import java.util.List;

import slaynash.sgengine.Configuration;
import slaynash.sgengine.gui.text2d.fontMeshCreator.FontType;
import slaynash.sgengine.shaders.ShaderManager;
import slaynash.sgengine.textureUtils.TextureManager;

public class FontManager {
	private static List<FontType> fontTypes = new ArrayList<FontType>();

	public static FontType createFont(String fontPath){
		for(FontType ft:fontTypes) if(ft.getFontPath().equals(fontPath)) return ft;
		FontType ft = new FontType(TextureManager.getTextureDef(Configuration.getFontPath()+"/"+fontPath+".png", TextureManager.COLOR), Configuration.getFontPath()+"/"+fontPath+".fnt");
		fontTypes.add(ft);
		return ft;
	}
	
	public static void bind2DShaderAtlas(FontType font) {
		ShaderManager.shader_bindTextureID(font.getTextureAtlas().getTextureID(), ShaderManager.TEXTURE_COLOR);
	}
}
