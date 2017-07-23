package slaynash.opengl.textureUtils;

import java.io.PrintStream;

import org.newdawn.slick.util.LogSystem;

public class TextureManagerLogSystem implements LogSystem {

	/** The output stream for dumping the log out on */
	public static PrintStream out = System.out;

	/**
	 * Log an error
	 * 
	 * @param message The message describing the error
	 * @param e The exception causing the error
	 */
	public void error(String message, Throwable e) {
		error(message);
		error(e);
	}

	/**
	 * Log an error
	 * 
	 * @param e The exception causing the error
	 */
	public void error(Throwable e) {
		out.println("[TextureManagerLogSystem] ERROR: " +e.getMessage());
		e.printStackTrace(out);
	}

	/**
	 * Log an error
	 * 
	 * @param message The message describing the error
	 */
	public void error(String message) {
		out.println("[TextureManagerLogSystem] ERROR: " +message);
	}

	/**
	 * Log a warning
	 * 
	 * @param message The message describing the warning
	 */
	public void warn(String message) {
		out.println("[TextureManagerLogSystem] WARN: " +message);
	}

	/**
	 * Log an information message
	 * 
	 * @param message The message describing the infomation
	 */
	public  void info(String message) {
		out.println("[TextureManagerLogSystem] INFO: " +message);
	}

	/**
	 * Log a debug message
	 * 
	 * @param message The message describing the debug
	 */
	public void debug(String message) {
		out.println("[TextureManagerLogSystem] DEBUG: " +message);
	}

	/**
	 * Log a warning with an exception that caused it
	 * 
	 * @param message The message describing the warning
	 * @param e The cause of the warning
	 */
	public void warn(String message, Throwable e) {
		warn(message);
		e.printStackTrace(out);
	}

}
