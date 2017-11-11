package slaynash.sgengine.gui.loader;

public class GUILoader {
	
	public GUIRenderablePage loadGUIFile(String path) {
		
		GUIRenderablePage page = new GUIRenderablePage();
		/* TODO GUI File loader
		File file = new File(Configuration.getAbsoluteInstallPath()+"/"+path);
		if(!file.exists()) {
			LogSystem.err_println("[GUILoader] Unable to load file "+path+": File not found.");
			return new GUIRenderablePage();
		}
		
		BufferedReader reader = new BufferedReader(new FileReader(file));
		String line = "";
		int tabs = 0;
		boolean rcheck = false;
		
		while(rcheck || (line = reader.readLine()) != null) {rcheck = false; tabs = line.split("\\t").length-1;
			if(tabs > 0) continue;
			
			if(line.equals("entities")) {
				while(rcheck || (line = reader.readLine()) != null) {rcheck = false; tabs = line.split("\\t").length-1;
					if(tabs > 1) continue; if(tabs < 1) {rcheck=true;break;};
					
					EntityDef entity = EntityManager.createEntityDef(line.trim());
					while(rcheck || (line = reader.readLine()) != null) {rcheck = false; tabs = line.split("\\t").length-1;
						if(tabs > 2) continue; if(tabs < 2) {rcheck=true;break;};
						line = line.trim();
						entity.addParameter(line);
					}
					EntityManager.registerEntityDef(entity);
					
				}
			}
			
		}
		
		reader.close();
		*/
		
		return page;
		
	}
	
}
