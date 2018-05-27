/**
 * 
 */
package io.jenkins.x.eclipse.plugin;

import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleFactory;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.MessageConsole;

/**
 * @author suren
 *
 */
public class JenkinsConsole implements IConsoleFactory {
	private static MessageConsole msgConsole = new MessageConsole("Jenkins Console", null);
	private static boolean exists = false;
 
	@Override
	public void openConsole() {
		showConsole();
	}
 
	/**
	 *
	 */
	private static void showConsole() {
		if(msgConsole == null) {
			return;
		}
 
		IConsoleManager consoleMgr = ConsolePlugin.getDefault().getConsoleManager();
 
		IConsole[] existing = consoleMgr.getConsoles();
 
		for(IConsole console : existing) {
			if(msgConsole == console)
			{
				exists = true;
				break;
			}
		}
 
		if(!exists) {
			consoleMgr.addConsoles(new IConsole[]{msgConsole});
		}
	}
 
	public static void closeConsole()
	{
		if(msgConsole == null)
		{
			return;
		}
 
		IConsoleManager consoleMgr = ConsolePlugin.getDefault().getConsoleManager();
		consoleMgr.removeConsoles(new IConsole[]{msgConsole});
	}
 
	public static MessageConsole getConsole() {
		showConsole();
 
		return msgConsole;
	}

}
