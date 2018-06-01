/**
 * 
 */
package io.jenkins.x.eclipse.plugin;

import org.eclipse.ui.IStartup;

import io.jenkins.x.client.ServiceClient;
import io.jenkins.x.client.model.Jenkins;

/**
 * @author suren
 *
 */
public class Startup implements IStartup {
	private static Jenkins jenkins = null;
	private static String nexus = null;
	private static String monocular = null;

	@Override
	public void earlyStartup() {
		new Thread() {

			@Override
			public void run() {
				ServiceClient client = new ServiceClient();
				
				jenkins = client.getJenkins();
				nexus = client.getNexusUrl();
				monocular = client.getMonocularUrl();
			}
		}.start();;
	}

	public static Jenkins getJenkins() {
		return jenkins;
	}

	public static String getNexus() {
		return nexus;
	}

	public static String getMonocular() {
		return monocular;
	}
}
