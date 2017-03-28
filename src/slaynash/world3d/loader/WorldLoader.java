package slaynash.world3d.loader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class WorldLoader {
	
	private static boolean error = false;
	private static String errorMessage = "";
	
	public static String mv = "0.0";//mapversion
	public static String mn = "null";//mapname
	public static String mcn = "unknown";//mapcreator
	public static String wspwn = "0 1 0";//worldspawn
	
	
	private static List<Entity> entities = new ArrayList<Entity>();
	private static List<PointLight> lights = new ArrayList<PointLight>();
	
	
	
	public static void loadMap(String mapPath){
		System.out.println("Start loading 3d map \""+mapPath+"\"...");
		File mapFile = new File(mapPath);
		BufferedReader reader = null;
		
		try {
			reader = new BufferedReader(new FileReader(mapFile));
		} catch (FileNotFoundException e) {error = true; errorMessage = e.getMessage(); return;}
		
		try {
			boolean found = false;
			String ln = "";
			String command = "";
			String args = "";
				
			while(!found && (ln=reader.readLine()) != null) {
				if(ln.split(" ", 2)[0].equals("MapVersion")){
					System.out.println("Map version: "+ln.split(" ", 2)[1]);
					mv = ln.split(" ", 2)[1];
					found = true;
				}
			}
			if(mv.equals("0.0")){
				error = true; errorMessage = "Bad map file ! (version not found)"; reader.close(); return;
			}
			else if(mv.equals("1.3"))
				readMap1_3(ln, command, args, reader);
			else{
				error = true; errorMessage = "Version not readable. Version: "+mv; reader.close(); return;
			}
			
			
			command = null;
			args = null;
		} catch (IOException e) {error = true; errorMessage = e.getMessage(); return;}
		System.out.println("Load finished. map properties:");
		System.out.println("------------------------------");
		System.out.println("Map \""+mn+"\" for loader v"+mv+", created by \""+mcn+"\".");
		System.out.println("World spawn placed at "+wspwn.split(" ")[0]+" "+wspwn.split(" ")[1]+" "+wspwn.split(" ")[2]);
		System.out.println("------------------------------");
	}
	
	private static void readMap1_3(String ln, String command, String args, BufferedReader reader) throws IOException {
		while((ln=reader.readLine()) != null) {
			ln = ln.trim();
			if(ln.startsWith("#")) continue;
			command = ln.split(" ", 2)[0].toLowerCase();
			if(command.equals("")) continue;
			System.out.println("ln="+ln);
			args = ln.split(" ", 2)[1];
			if(command.equals("spawn")) wspwn = args;
			else if(command.equals("name")) mn = args;
			else if(command.equals("creator")) mcn = args;
			else if(command.equals("worldpart")){
				//System.out.println("creating a world part...");
				String[] ag = args.split(" ");
				TriangleFace[] faces = new TriangleFace[Integer.parseInt(ag[0])];
				float[] vs = null;
				float[] vns = null;
				float[] uvs = null;
				String texC = "";
				String texN = "";
				String texS = "";
				float sf = 0;
				
				int vn = 0;
				
				int fn = 0;
				while((ln=reader.readLine()) != null){
					ln = ln.trim();
					if(ln.startsWith("#")) continue;
					command = ln.split(" ", 2)[0].toLowerCase();
					if(command.equals("")) continue;
					ag = ln.split(" ");
					if(command.equals("endworldpart")){
						Model3dWorld m3dw = new Model3dWorld(faces);
						entities.add(m3dw);
						System.out.println("m3dw created with "+faces.length+" faces !");
						break;
					}
					if(command.equals("face")){
						vs = new float[Integer.parseInt(ag[1])*3];
						vns = new float[Integer.parseInt(ag[1])*3];
						uvs = new float[Integer.parseInt(ag[1])*2];
					}
					else if(command.equals("endface")){
						faces[fn] = new TriangleFace(vs, vns, uvs, texC, texN, texS, sf);
						texC = "";
						texN = "";
						texS = "";
						sf = 0;
						fn++;
						vn = 0;
					}
					else if(command.equals("v")){//vertices*3 normals*3 uvs*2
						//System.out.println("loading vertices at point nb "+vn);
						 vs[vn*3+0] = Float.parseFloat(ag[1]);
						 vs[vn*3+1] = Float.parseFloat(ag[2]);
						 vs[vn*3+2] = Float.parseFloat(ag[3]);
						
						vns[vn*3+0] = Float.parseFloat(ag[4]);
						vns[vn*3+1] = Float.parseFloat(ag[5]);
						vns[vn*3+2] = Float.parseFloat(ag[6]);
						
						uvs[vn*2+0] = Float.parseFloat(ag[7]);
						uvs[vn*2+1] = Float.parseFloat(ag[8]);
						vn++;
						
					}
					else if(command.equals("texc")){//color texture
						texC = ln.split(" ", 2)[1];
					}
					else if(command.equals("texn")){//normal texture
						texN = ln.split(" ", 2)[1];
					}
					else if(command.equals("texs")){//specular texture
						texS = ln.split(" ", 2)[1];
					}
					else if(command.equals("sf")){//specular factor
						sf = Float.parseFloat(ln.split(" ", 2)[1]);
					}
					else{
						System.out.println("Unknown worldpart line: "+ln);
					}
				}
			}
			else if(command.equals("model3d")){
				//TODO model loading
			}
			else if(command.equals("pointlight")){
				String[] ags = args.split(" ");
				PointLight l = new PointLight(
						Float.parseFloat(ags[0]), Float.parseFloat(ags[1]), Float.parseFloat(ags[2]),
						Float.parseFloat(ags[3]), Float.parseFloat(ags[4]), Float.parseFloat(ags[5]),
						Float.parseFloat(ags[6]), Float.parseFloat(ags[7]), Float.parseFloat(ags[8])
				);
				lights.add(l);
			}
		}
	}

	public static String getError(){
		String error = errorMessage;
		errorMessage = "";
		return error;
	}
	
	public static boolean isErrored(){
		return error;
	}

	public static List<Entity> getEntities() {
		return entities;
	}

	public static List<PointLight> getLights() {
		return lights;
	}
}
