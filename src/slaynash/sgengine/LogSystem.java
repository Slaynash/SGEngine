package slaynash.sgengine;

import java.io.PrintStream;

public class LogSystem {
	
	private static CustomLogSystem clg = null;
	
	public static void out_print(Object arg){
		if(clg != null) clg.out_print(arg);
		else System.out.print(arg);
	}
	
	public static void out_println(Object arg){
		if(clg != null) clg.out_println(arg);
		else System.out.println(arg);
	}
	
	public static void err_print(Object arg){
		if(clg != null) clg.err_print(arg);
		else System.err.print(arg);
	}
	
	public static void err_println(Object arg){
		if(clg != null) clg.err_println(arg);
		else System.err.println(arg);
	}

	public static void out_printf(String arg0, Object... arg1) {
		if(clg != null) clg.out_printf(arg0, arg1);
		else System.out.printf(arg0, arg1);
	}

	public static void err_printf(String arg0, Object... arg1) {
		if(clg != null) clg.err_printf(arg0, arg1);
		else System.err.printf(arg0, arg1);
	}
	
	public static PrintStream getOutStream(){
		if(clg != null) return clg.getOutStream();
		else return System.out;
	}
	
	public static PrintStream getErrStream(){
		if(clg != null) return clg.getErrStream();
		else return System.err;
	}
	
	public static void setCustomLogSystem(CustomLogSystem clg){
		LogSystem.clg = clg;
	}
	
}
