package io.jenkins.x.eclipse.plugin.view;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.action.Action;

public class BeAction extends Action {
	private Map<String, String> data = new HashMap<String, String>();
	
	public static final String URL = "url";
	
	public void put(String key, String value) {
		data.put(key, value);
	}
	
	public void setUrl(String url) {
		put(URL, url);
	}
	
	public String getUrl() {
		return data.get(URL);
	}
}
