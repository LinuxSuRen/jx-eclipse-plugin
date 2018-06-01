/**
 * 
 */
package io.jenkins.x.eclipse.plugin.util;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * @author suren
 */
public abstract class BrowserUtils {
	public static void open(String url) {
		if(url == null) {
			return;
		}
		
		try {
			URI uri = new URI(url);
			Desktop.getDesktop().browse(uri);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}
}
