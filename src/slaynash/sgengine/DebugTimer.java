package slaynash.sgengine;

public class DebugTimer {
	
	private static long startTime = 0;

	public static void start() {
		startTime = System.nanoTime();
	}
	
	public static void outputAndUpdateTime(String string) {
		if(Configuration.isUsingTimingDebug()) LogSystem.out_printf(string, ((System.nanoTime()-startTime)/1e6f));
		startTime = System.nanoTime();
	}
	
}
