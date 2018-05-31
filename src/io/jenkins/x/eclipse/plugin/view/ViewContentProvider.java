/**
 * 
 */
package io.jenkins.x.eclipse.plugin.view;

import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;

import io.jenkins.x.client.tree.PipelineTreeModel;
import io.jenkins.x.client.tree.TreeItem;

/**
 * @author suren
 */
class ViewContentProvider implements ITreeContentProvider {
	private PipelineTreeModel treeModel;
	
	public ViewContentProvider(PipelineTreeModel treeModel) {
		this.treeModel = treeModel;
	}

	@Override
	public Object[] getElements(Object parent) {
		List<TreeItem> items = treeModel.getChildrenItems();
		if(items == null || items.size() == 0) {
			return new Object[] {};
		}
		
		return items.toArray();
	}
	
	@Override
	public Object getParent(Object child) {
		if (child instanceof TreeItem) {
			return ((TreeItem)child).getParent();
		}
		return null;
	}
	
	@Override
	public Object [] getChildren(Object parent) {
		if (parent instanceof TreeItem) {
			return ((TreeItem)parent).getChildrenItems().toArray();
		}
		return new Object[0];
	}

	@Override
	public boolean hasChildren(Object parent) {
		if (parent instanceof TreeItem)
			return ((TreeItem)parent).getChildrenItems().size() > 0;
		return false;
	}
}
