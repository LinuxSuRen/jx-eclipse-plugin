/**
 * 
 */
package io.jenkins.x.eclipse.plugin;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
 
/**
 * @author suren 
 */
public class Logger {
	private static final ILog log = Activator.getDefault().getLog();
	private static final String ID = Activator.PLUGIN_ID;
	
	public static void info(String message) {
		info(message, null);
	}
	
	public static void info(String message, Throwable e) {
		log(IStatus.INFO, message, e);
	}
	
	public static void warning(String message) {
		warning(message, null);
	}
	
	public static void warning(String message, Throwable e) {
		log(IStatus.WARNING, message, e);
	}
	
	public static void error(String message) {
		error(message, null);
	}
	
	public static void error(String message, Throwable e) {
		log(IStatus.ERROR, message, e);
	}
	
	public static void ok(String message) {
		ok(message, null);
	}
	
	public static void ok(String message, Throwable e) {
		log(IStatus.OK, message, e);
	}
	
	public static void log(int level, String message) {
		log(level, message, null);
	}
	
	public static void log(int level, String message, Throwable e) {
		log(new Status(level, ID, message, e));
	}
	
	public static void log(IStatus status) {
		log.log(status);
	}
}