package slaynash.sgengine.textureUtils;

import org.newdawn.slick.util.LogSystem;

public class TextureManagerLogSystem implements LogSystem {

	/**
	 * Log an error
	 * 
	 * @param message The message describing the error
	 * @param e The exception causing the error
	 */
	@Override
	public void error(String message, Throwable e) {
		error(message);
		error(e);
	}

	/**
	 * Log an error
	 * 
	 * @param e The exception causing the error
	 */
	@Override
	public void error(Throwable e) {
		slaynash.sgengine.LogSystem.getOutStream().println("[TextureManagerLogSystem] ERROR: " +e.getMessage());
		e.printStackTrace(slaynash.sgengine.LogSystem.getOutStream());
	}

	/**
	 * Log an error
	 * 
	 * @param message The message describing the error
	 */
	@Override
	public void error(String message) {
		slaynash.sgengine.LogSystem.getOutStream().println("[TextureManagerLogSystem] ERROR: " +message);
	}

	/**
	 * Log a warning
	 * 
	 * @param message The message describing the warning
	 */
	@Override
	public void warn(String message) {
		slaynash.sgengine.LogSystem.getOutStream().println("[TextureManagerLogSystem] WARN: " +message);
	}

	/**
	 * Log an information message
	 * 
	 * @param message The message describing the infomation
	 */
	@Override
	public  void info(String message) {
		slaynash.sgengine.LogSystem.getOutStream().println("[TextureManagerLogSystem] INFO: " +message);
	}

	/**
	 * Log a debug message
	 * 
	 * @param message The message describing the debug
	 */
	@Override
	public void debug(String message) {
		slaynash.sgengine.LogSystem.getOutStream().println("[TextureManagerLogSystem] DEBUG: " +message);
	}

	/**
	 * Log a warning with an exception that caused it
	 * 
	 * @param message The message describing the warning
	 * @param e The cause of the warning
	 */
	public void warn(String message, Throwable e) {
		warn(message);
		e.printStackTrace(slaynash.sgengine.LogSystem.getOutStream());
	}

}
