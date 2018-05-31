/**
 * 
 */
package io.jenkins.x.eclipse.plugin;

import org.eclipse.core.runtime.Plugin;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * @author suren
 */
public class Activator extends AbstractUIPlugin {

	public static final String PLUGIN_ID = "jenkins-x";
	public static Plugin plugin;

	public Activator() {
	}

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	public static Plugin getDefault() {
		return plugin;
	}
}
