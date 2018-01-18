package slaynash.sgengine;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class DebugTimer {
	
	private static long startTime = 0;
	private static long baseTime = 0;
	
	private static List<DebugTime> times = new ArrayList<DebugTime>();
	private static List<DebugTime> savedTimes = new ArrayList<DebugTime>();
	private static TimingDebugFrame frame;
	private static float totalTime = 0;
	
	public static void outputAndUpdateTime(String string) {
		if(Configuration.isUsingTimingDebug()) {
			//GL11.glFinish();
			//LogSystem.out_println("[TIMING] VR Render time [Left][Defe]: "+((System.nanoTime()-startTime)/1e6f)+"ms");
			times.add(new DebugTime(((System.nanoTime()-startTime)/1e6f), string));
			startTime = System.nanoTime();
		}
	}

	public static void restart() {
		times.clear();
		baseTime = startTime = System.nanoTime();
	}
	
	private static class DebugTime{
		float duration;
		String name;
		
		public DebugTime(float duration, String name) {
			this.duration = duration;
			this.name = name;
		}
	}

	public static void finishUpdate() {
		synchronized (savedTimes) {
			savedTimes.clear();
			savedTimes.addAll(times);
			
			totalTime = ((System.nanoTime()-baseTime)/1e6f);
			if(Configuration.isUsingTimingDebug()) {
				LogSystem.out_println("");
				LogSystem.out_println("[DebugTimer] Total time: "+totalTime+"ms");
				for(int i=0;i<times.size();i++) {
					DebugTime dt = times.get(i);
					LogSystem.out_printf("[DebugTimer] %f%% - %f ms - %s\n", (dt.duration/totalTime*100), dt.duration, dt.name);
				}
				LogSystem.out_println("");
			}
			
		}
		
		if(frame != null) SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				frame.validate();
			}
		});
	}
	
	public static void showDebugFrame() {
		if(frame == null)
			frame = new TimingDebugFrame();
		if(!frame.isVisible())
			frame.setVisible(true);
	}
	
	private static class TimingDebugFrame extends JFrame {
		private static final long serialVersionUID = -7547608523892329109L;

		TimingDebugFrame(){
			super("SGE timing debug frame");
			setResizable(false);
			setSize(440, 440);
			setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		}
		
		@Override
		public void paintComponents(Graphics g) {
			synchronized (savedTimes) {
				super.paintComponents(g);
				
				Random rng= new Random(0);
				
				float lastAng = 0;
				float ang = 0;
				
				g.setColor(Color.BLACK);
				g.drawString(String.format("%.2f", totalTime)+"ms - "+(int)(1000/totalTime)+"fps", 0, 50);
				
				for(DebugTime t:savedTimes) {
					ang = t.duration/totalTime*360;
					g.setColor(new Color(rng.nextInt(256), rng.nextInt(256), rng.nextInt(256)));
					g.fillArc(20, 30, 400, 400, (int)lastAng, (int)ang);
					lastAng+=ang;
				}
				g.setColor(Color.WHITE);
				g.fillArc(20, 30, 400, 400, (int)lastAng, 360-(int)lastAng);
				
			}
			
		}
	}
}
