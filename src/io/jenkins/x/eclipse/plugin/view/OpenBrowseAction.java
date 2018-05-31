/**
 * 
 */
package io.jenkins.x.eclipse.plugin.view;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * @author suren
 */
public class OpenBrowseAction extends BeAction {
	private String target;
	
	public OpenBrowseAction(String target, String url) {
		setTarget(target);
		setUrl(url);
	}
	
	public void run() {
		if(getUrl() == null) {
			return;
		}
		
		try {
			URI uri = new URI(this.getUrl());
			
			Desktop.getDesktop().browse(uri);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void setTarget(String target) {
		this.target = target;
		setText("Open " + target);
		setToolTipText(getText());
	}

	public String getTarget() {
		return target;
	}
}
