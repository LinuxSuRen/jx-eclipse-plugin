/**
 * 
 */
package io.jenkins.x.eclipse.plugin.view;

import io.jenkins.x.eclipse.plugin.util.BrowserUtils;

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
		
		BrowserUtils.open(getUrl());
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
