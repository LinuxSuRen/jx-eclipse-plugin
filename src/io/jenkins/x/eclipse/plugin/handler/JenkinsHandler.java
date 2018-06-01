/**
 * 
 */
package io.jenkins.x.eclipse.plugin.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import io.jenkins.x.client.model.Jenkins;
import io.jenkins.x.eclipse.plugin.Startup;
import io.jenkins.x.eclipse.plugin.util.BrowserUtils;

/**
 * @author surenpi
 *
 */
public class JenkinsHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		Jenkins jenkins = Startup.getJenkins();
		if(jenkins == null) {
			return null;
		}
		
		BrowserUtils.open(jenkins.getUrl());
		
		return null;
	}

}
