package slaynash.sgengine;

import java.io.PrintStream;

public interface CustomLogSystem {
	
	public abstract void out_print(Object arg);
	
	public abstract void out_println(Object arg);
	
	public abstract void err_print(Object arg);
	
	public abstract void err_println(Object arg);

	public abstract void out_printf(String arg0, Object... arg1);

	public abstract void err_printf(String arg0, Object... arg1);
	
	public abstract PrintStream getOutStream();
	
	public abstract PrintStream getErrStream();
	
}
