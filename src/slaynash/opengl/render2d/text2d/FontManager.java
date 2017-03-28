package slaynash.opengl.render2d.text2d;

import java.util.ArrayList;
import java.util.List;

import slaynash.opengl.Infos;
import slaynash.opengl.render2d.text2d.fontMeshCreator.FontType;
import slaynash.opengl.shaders.ShaderManager;
import slaynash.opengl.textureUtils.TextureManager;

public class FontManager {
	private static List<FontType> fontTypes = new ArrayList<FontType>();

	public static FontType createFont(String fontPath){
		for(FontType ft:fontTypes) if(ft.getFontPath().equals(fontPath)) return ft;
		FontType ft = new FontType(TextureManager.getTextureID(Infos.getFontPath()+"/"+fontPath+".png"), Infos.getFontPath()+"/"+fontPath+".fnt");
		fontTypes.add(ft);
		return ft;
	}

	public static void bind2DShaderAtlas(FontType font) {
		ShaderManager.bind2DShaderTextureID(font.getTextureAtlas());
	}
}
