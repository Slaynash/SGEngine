package slaynash.sgengine.utils;

import java.util.ArrayList;

import slaynash.sgengine.Configuration;
import slaynash.sgengine.world3d.loader.Ent_PointLight;

public class LightsManager {
	
	private static ArrayList<Ent_PointLight> pointlights = new ArrayList<Ent_PointLight>();
	//Upcoming updates
	//private static ArrayList<SpotLight> spotlights = new ArrayList<SpotLight>();
	//private static DirectionalLight directionalLight;
	
	public static ArrayList<Ent_PointLight> getPointlights(){
		synchronized (pointlights) {
			while(pointlights.size() > Configuration.MAX_LIGHTS) pointlights.remove(Configuration.MAX_LIGHTS);
			return pointlights;
		}
	}
	
	public static void addPointlight(Ent_PointLight pointlight) {
		synchronized (pointlights) {
			if(pointlights.size() >= Configuration.MAX_LIGHTS) return;
			pointlights.add(pointlight);
		}
	}
	
	public static void removePointlight(Ent_PointLight pointlight) {
		synchronized (pointlights) {
			pointlights.remove(pointlight);
		}
	}
	
	public static void removePointlight(int pointlightIndex) {
		synchronized (pointlights) {
			pointlights.remove(pointlightIndex);
		}
	}
	
	/* Upcoming updates
	public static ArrayList<PointLight> getPointlights(){
		synchronized (spotlights) {
			return spotlights;
		}
	}
	
	public static void addPointlight(PointLight spotlight) {
		synchronized (spotlights) {
			pointlights.add(spotlight);
		}
	}
	
	public static void removePointlight(PointLight spotlight) {
		synchronized (spotlights) {
			pointlights.remove(spotlight);
		}
	}
	
	public static void removePointlight(int spotlightIndex) {
		synchronized (spotlights) {
			pointlights.remove(spotlightIndex);
		}
	}
	
	public static DirectionalLight getDirectionalLight(){
		return directionalLight;
	}
	
	public static void setDirectionalLight(DirectionalLight directionalLight){
		LightsManager.directionalLight = directionalLight;
	}
	*/
}
