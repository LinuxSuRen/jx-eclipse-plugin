package io.jenkins.x.eclipse.plugin.view;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.*;

import io.jenkins.x.client.PipelineClient;
import io.jenkins.x.client.kube.PipelineActivity;
import io.jenkins.x.client.tree.OwnerNode;
import io.jenkins.x.client.tree.PipelineTreeModel;
import io.jenkins.x.client.tree.TreeItem;

import org.eclipse.jface.viewers.*;
import org.eclipse.swt.graphics.Image;
import org.eclipse.jface.action.*;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.*;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.SWT;
import org.eclipse.core.runtime.IAdaptable;
import javax.inject.Inject;


/**
 * This sample class demonstrates how to plug-in a new
 * workbench view. The view shows data obtained from the
 * model. The sample creates a dummy model on the fly,
 * but a real implementation would connect to the model
 * available either in this or another plug-in (e.g. the workspace).
 * The view is connected to the model using a content provider.
 * <p>
 * The view uses a label provider to define how model
 * objects should be presented in the view. Each
 * view can present the same model objects using
 * different labels and icons, if needed. Alternatively,
 * a single label provider can be shared between views
 * in order to ensure that objects of the same type are
 * presented in the same way everywhere.
 * <p>
 */

public class JenkinsView extends ViewPart {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "io.jenkins.x.eclipse.plugin.view.JenkinsView";

	@Inject IWorkbench workbench;
	
	private TreeViewer viewer;
	private DrillDownAdapter drillDownAdapter;
	private Action startPipelineAction;

	private PipelineTreeModel model = PipelineTreeModel.newInstance();
	 
	class TreeObject extends io.jenkins.x.client.tree.TreeNode implements IAdaptable {
		public TreeObject(TreeItem parent, String label) {
			super(parent, label);
		}

		@Override
		public <T> T getAdapter(Class<T> adapter) {
			// TODO Auto-generated method stub
			return null;
		}
	}

	class ViewContentProvider implements ITreeContentProvider {
		private TreeItem invisibleRoot;

		public Object[] getElements(Object parent) {
//			if (parent.equals(getViewSite())) {
//				if (invisibleRoot==null) initialize();
//				return getChildren(invisibleRoot);
//			}
//			return getChildren(parent);
			
			List<TreeItem> items = model.getChildrenItems();
			if(items == null || items.size() == 0) {
				return new Object[] {};
			}
			
			for(TreeItem item : items) {
				if(item instanceof OwnerNode) {
					OwnerNode ownerNode = (OwnerNode) item;
				}
			}
			
			return items.toArray();
		}
		public Object getParent(Object child) {
			if (child instanceof TreeObject) {
				return ((TreeObject)child).getParent();
			}
			return null;
		}
		public Object [] getChildren(Object parent) {
			if (parent instanceof TreeItem) {
				return ((TreeItem)parent).getChildrenItems().toArray();
			}
			return new Object[0];
		}
		public boolean hasChildren(Object parent) {
			if (parent instanceof TreeItem)
				return ((TreeItem)parent).getChildrenItems().size() > 0;
			return false;
		}
	}

	class ViewLabelProvider extends LabelProvider {

		public String getText(Object obj) {
			if(obj instanceof TreeItem) {
				return ((TreeItem) obj).getLabel();
			}
			return obj.toString();
		}
		public Image getImage(Object obj) {
			String imageKey = ISharedImages.IMG_OBJ_ELEMENT;
			if (obj instanceof TreeItem)
			   imageKey = ISharedImages.IMG_OBJ_FOLDER;
			return workbench.getSharedImages().getImage(imageKey);
		}
	}

	@Override
	public void createPartControl(Composite parent) {
		viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		drillDownAdapter = new DrillDownAdapter(viewer);
		
		viewer.setContentProvider(new ViewContentProvider());
		viewer.setInput(getViewSite());
		viewer.setLabelProvider(new ViewLabelProvider());

		// Create the help context id for the viewer's control
		workbench.getHelpSystem().setHelp(viewer.getControl(), "jx-eclipse-plugin.viewer");
		getSite().setSelectionProvider(viewer);
		makeActions();
		hookContextMenu();
	}

	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				JenkinsView.this.fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, viewer);
	}

	private void fillContextMenu(IMenuManager manager) {
		manager.add(startPipelineAction);
		
		drillDownAdapter.addNavigationActions(manager);
		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	private void makeActions() {
		this.startPipelineAction = new Action() {
			public void run() {
				showMessage("Action 1 executed");
			}
		};
		startPipelineAction.setText("Start Pipeline");
		startPipelineAction.setToolTipText("Start Pipeline");
		startPipelineAction.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
			getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
	}

	private void showMessage(String message) {
		System.out.println(viewer.getSelection());
		MessageDialog.openInformation(
			viewer.getControl().getShell(),
			"Jenkins View",
			message);
	}

	@Override
	public void setFocus() {
		viewer.getControl().setFocus();
	}
}
