/**
 * 
 */
package io.jenkins.x.eclipse.plugin.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import io.jenkins.x.eclipse.plugin.Startup;
import io.jenkins.x.eclipse.plugin.util.BrowserUtils;

/**
 * @author surenpi
 */
public class NexusHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		BrowserUtils.open(Startup.getNexus());
		
		return null;
	}

}
