package slaynash.sgengine.utils;

import java.util.ArrayList;
import java.util.List;

public abstract class EngineUpdateThread {
	
	private static long renderThread;
	private static List<Runnable> list = new ArrayList<Runnable>();
	
	protected static void setRenderthreadAsCurrent() {
		renderThread = Thread.currentThread().getId();
	}

	public static void invokeLater(Runnable runnable) {
		if(Thread.currentThread().getId() == renderThread) runnable.run();
		synchronized (list) {
			list.add(runnable);
		}
	}
	
	public static void runAll() {
		synchronized (list) {
			for(Runnable r:list) r.run();
			list.clear();
		}
	}
	
}
