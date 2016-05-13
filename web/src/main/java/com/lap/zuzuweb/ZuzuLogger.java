package com.lap.zuzuweb;

public class ZuzuLogger {

	/**
	 * Instance of the real logger object.
	 */
	private final org.slf4j.Logger realLogger;
	private final String className;

	public static ZuzuLogger getLogger(Class<?> clazz) {
		return new ZuzuLogger(clazz);
	}

	public ZuzuLogger(Class<?> clazz) {
		realLogger = org.slf4j.LoggerFactory.getLogger(clazz);
		className = clazz.getSimpleName();
	}


	// Entering methods
	
	public void entering() {
		realLogger.info(String.format("Enter %s::", className));
	}

	public void entering(String sourceMethod) {
		realLogger.info(String.format("Enter %s.%s::", className, sourceMethod));
	}

	public void entering(String sourceMethod, String message) {
		realLogger.info(String.format("Enter %s.%s:: %s", className, sourceMethod, message));
	}

	public void entering(String sourceMethod, String format, Object... args) {
		String s1 = String.format("Enter %s.%s:: ", className, sourceMethod);
		String s2 = String.format(format, args);
		realLogger.info(s1 + s2);
	}
	
	// Exit methods
	
	public void exit() {
		realLogger.info(String.format("Exit %s.", className));
	}

	public void exit(String sourceMethod) {
		realLogger.info(String.format("Exit %s.%s.", className, sourceMethod));
	}
	
	public void exit(String sourceMethod, String message) {
		realLogger.info(String.format("Exit %s.%s >>> %s", className, sourceMethod, message));
	}
	
	public void exit(String sourceMethod, String format, Object... args) {
		String s1 = String.format("Exit %s.%s >>> ", className, sourceMethod);
		String s2 = String.format(format, args);
		realLogger.info(s1 + s2);
	}

	public void error(String message) {
		realLogger.error(message);
	}

	public void error(String format, Object... args) {
		realLogger.error(String.format(format, args));
	}

	public void error(String message, Throwable t) {
		realLogger.error(message, t);
	}

	public void warn(String message) {
		realLogger.warn(message);
	}

	public void warn(String format, Object... args) {
		realLogger.warn(String.format(format, args));
	}

	public void info(String message) {
		realLogger.info(message);
	}

	public void info(String format, Object... args) {
		realLogger.info(String.format(format, args));
	}

	public void debug(String message) {
		realLogger.debug(message);
	}

	public void debug(String format, Object... args) {
		realLogger.debug(String.format(format, args));
	}

	public void trace(String message) {
		realLogger.trace(message);
	}

	public void trace(String format, Object... args) {
		realLogger.trace(String.format(format, args));
	}
}
