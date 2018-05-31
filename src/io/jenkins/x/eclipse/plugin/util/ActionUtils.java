/**
 * 
 */
package io.jenkins.x.eclipse.plugin.util;

import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IContributionManager;
import io.fabric8.zjsonpatch.internal.guava.Strings;
import io.jenkins.x.eclipse.plugin.view.OpenBrowseAction;

/**
 * @author suren
 */
public abstract class ActionUtils {
	/**
	 * Add OpenBrowseAction to IMenuManager, when the target text is null or empty will do nothing
	 * @see OpenBrowseAction
	 * @param manager menu manager
	 * @param url target url
	 * @param target target action text
	 */
	public static void addOpenBrowseAction(IContributionManager manager, String url, String target) {
		if(!Strings.isNullOrEmpty(target)) {
			manager.add(new OpenBrowseAction(target, url));
			manager.update(true);
		}
	}
	
	public static void removeOpenBrowseActions(IContributionManager manager) {
		IContributionItem[] items = manager.getItems();
		if(items == null) {
			return;
		}

		int count = items.length;
		for(int i = 0; i < count; i++) {
			IContributionItem item = items[i];
			if(item instanceof ActionContributionItem) {
				ActionContributionItem actionItem = (ActionContributionItem) item;
				
				IAction action = actionItem.getAction();
				if(action instanceof OpenBrowseAction) {
					manager.remove(item);
				}
			}
		}
		
		manager.update(true);
	}
	
	public static void removeItemByTarget(IContributionManager manager, String target) {
		IContributionItem[] items = manager.getItems();
		if(items == null) {
			return;
		}

		for(IContributionItem item : items) {
			if(item instanceof ActionContributionItem) {
				ActionContributionItem actionItem = (ActionContributionItem) item;
				
				IAction action = actionItem.getAction();
				if(action instanceof OpenBrowseAction) {
					if(target.equals(((OpenBrowseAction) action).getTarget())) {
						manager.remove(item);
						break;
					}
				}
			}
		}
	}
}
